package com.olivia.sdk.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.olivia.sdk.listener.BaseExportDataPoiListener;
import com.olivia.sdk.utils.JSONUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 基础Excel导出控制器抽象类 提供通用的Excel导出功能，支持通过请求参数构建查询条件
 *
 * @param <T> 导出数据的实体类型
 * @param <L> 导出监听器类型
 */
@Setter
@Getter
@Accessors(chain = true)
@SuppressWarnings("unchecked")
public abstract class BaseExportDataPoiController<T, L extends BaseExportDataPoiListener<T>> implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(BaseExportDataPoiController.class);
  private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String FILE_EXTENSION = ".xlsx";

  /**
   * 导出Excel文件
   *
   * @param request  HTTP请求对象，包含查询参数
   * @param response HTTP响应对象，用于输出Excel文件
   * @throws IOException 如果IO操作失败时抛出
   */
  @RequestMapping(value = "/excel/export", method = {RequestMethod.GET, RequestMethod.POST})
  public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // 获取导出监听器
    L listener = getExportDataPoiListener();
    Objects.requireNonNull(listener, "导出监听器不能为null，请检查Spring配置");

    // 配置响应头
    configureResponse(response, listener.getFileName());

    // 获取查询条件和导出数据
    Wrapper<T> queryWrapper = getQueryWrapper(request);
    List<T> dataList = listener.getExportData(queryWrapper);

    // 使用EasyExcel写入数据
    EasyExcel.write(response.getOutputStream(), getEntityClass()).sheet("数据列表").doWrite(dataList);
  }

  /**
   * 配置响应头信息
   *
   * @param response 响应对象
   * @param fileName 原始文件名（不含扩展名）
   * @throws IOException 如果编码操作失败时抛出
   */
  private void configureResponse(HttpServletResponse response, String fileName) throws IOException {
    // 确保文件名不为空
    String safeFileName = Objects.requireNonNullElse(fileName, "导出数据");

    // 编码文件名，解决中文乱码问题
    String encodedFileName = URLEncoder.encode(safeFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

    // 设置响应头
    response.setContentType(EXCEL_CONTENT_TYPE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    response.setHeader("Content-disposition", "attachment;filename*=UTF-8''" + encodedFileName + FILE_EXTENSION);
    response.setHeader("Pragma", "public");
    response.setHeader("Cache-Control", "no-store");
    response.addHeader("Cache-Control", "max-age=0");
  }

  /**
   * 获取实体类的Class对象
   *
   * @return 实体类的Class对象
   */
  protected Class<T> getEntityClass() {
    try {
      ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
      return (Class<T>) type.getActualTypeArguments()[0];
    } catch (Exception e) {
      logger.error("获取实体类Class对象失败", e);
      throw new RuntimeException("获取导出实体类型失败", e);
    }
  }

  /**
   * 获取导出监听器实例
   *
   * @return 导出监听器实例
   */
  protected L getExportDataPoiListener() {
    try {
      ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
      Class<L> listenerClass = (Class<L>) type.getActualTypeArguments()[1];
      return SpringUtil.getBean(listenerClass);
    } catch (Exception e) {
      logger.error("初始化导出监听器失败", e);
      return null;
    }
  }

  /**
   * 从请求中构建查询条件
   *
   * @param request HTTP请求对象
   * @return MyBatis-Plus的查询条件包装器
   */
  protected Wrapper<T> getQueryWrapper(HttpServletRequest request) {
    try (InputStream inputStream = request.getInputStream()) {
      // 读取请求体
      String requestBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

      // 解析请求参数为Map
      Map<String, Object> params = parseRequestParams(requestBody);

      // 构建查询条件
      QueryWrapper<T> queryWrapper = new QueryWrapper<>();
      params.forEach((key, value) -> buildQueryCondition(queryWrapper, key, value));

      return queryWrapper;
    } catch (Exception e) {
      logger.error("构建查询条件失败", e);
      throw new RuntimeException("获取导出查询条件异常", e);
    }
  }

  /**
   * 解析请求参数为Map
   *
   * @param requestBody 请求体字符串
   * @return 解析后的参数Map
   */
  private Map<String, Object> parseRequestParams(String requestBody) {
    if (requestBody == null || requestBody.trim().isEmpty()) {
      return new HashMap<>(0);
    }
    return JSONUtils.readValue(requestBody, new TypeReference<Map<String, Object>>() {
    });
  }

  /**
   * 构建单个查询条件
   *
   * @param queryWrapper 查询包装器
   * @param key          字段名
   * @param value        字段值
   */
  private void buildQueryCondition(QueryWrapper<T> queryWrapper, String key, Object value) {
    if (value == null || key == null || key.trim().isEmpty()) {
      return;
    }

    // 根据值的类型构建不同的查询条件
    if (value instanceof Iterable<?> iterable) {
      queryWrapper.in(key, iterable);
    } else if (value.getClass().isArray()) {
      queryWrapper.in(key, (Object[]) value);
    } else {
      queryWrapper.eq(key, value);
    }
  }
}
