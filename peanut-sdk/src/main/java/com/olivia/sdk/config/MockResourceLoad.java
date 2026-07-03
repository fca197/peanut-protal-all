package com.olivia.sdk.config;

import cn.hutool.extra.spring.SpringUtil;
import com.olivia.sdk.utils.JSONUtils;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 模拟资源加载器 负责从配置文件加载模拟数据，并映射到对应的类和方法
 */
@Slf4j
//@Component
public class MockResourceLoad {

  /**
   * 存储模拟资源的映射表 key: 类名.方法名 value: 模拟返回值 使用ConcurrentHashMap确保并发安全
   */
  public static final Map<String, Object> MOCK_RESOURCE_MAP = new ConcurrentHashMap<>();

  /**
   * 模拟资源文件名
   */
  private static final String MOCK_RESOURCE_FILE = "mock.txt";

  /**
   * 获取模拟资源
   *
   * @param classMethod 类名.方法名
   * @return 模拟资源对象，不存在则返回null
   */
  public static Object getMockResource(String classMethod) {
    return MOCK_RESOURCE_MAP.get(classMethod);
  }

  /**
   * 获取不可修改的模拟资源映射表，用于安全的遍历
   *
   * @return 不可修改的映射表
   */
  public static Map<String, Object> getImmutableMockResources() {
    return Collections.unmodifiableMap(MOCK_RESOURCE_MAP);
  }

  /**
   * 初始化方法，在Spring容器启动后加载模拟资源 使用@PostConstruct确保在依赖注入完成后执行
   */
  @PostConstruct
  public void loadResource() {
    // 使用try-with-resources自动关闭输入流
    try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(MOCK_RESOURCE_FILE)) {
      // 检查资源是否存在
      Objects.requireNonNull(inputStream, "模拟资源文件不存在: " + MOCK_RESOURCE_FILE);

      // 读取文件内容并处理每一行
      List<String> lines = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
      log.info("开始加载模拟资源，共 {} 行内容", lines.size());

      lines.forEach(this::processMockLine);

      log.info("模拟资源加载完成，共加载 {} 条有效配置", MOCK_RESOURCE_MAP.size());
    } catch (Exception e) {
      log.error("加载模拟资源失败", e);
      // 根据业务需求决定是否抛出异常中断启动
      // throw new RuntimeException("加载模拟资源失败", e);
    }
  }

  /**
   * 处理单行模拟配置
   *
   * @param line 配置行内容
   */
  private void processMockLine(String line) {
    // 修剪空白字符
    String trimmedLine = line.trim();

    // 跳过注释和空行
    if (trimmedLine.startsWith("#") || StringUtils.isBlank(trimmedLine)) {
      return;
    }

    try {
      // 解析类名.方法名和内容
      int separatorIndex = trimmedLine.indexOf(": ");
      if (separatorIndex == -1) {
        log.warn("无效的模拟配置格式，缺少分隔符 ': '，行内容: {}", trimmedLine);
        return;
      }

      String classMethod = trimmedLine.substring(0, separatorIndex);
      String content = trimmedLine.substring(separatorIndex + 2);

      // 解析类名和方法名
      String[] classMethodParts = classMethod.split("\\.", 2);
      if (classMethodParts.length != 2) {
        log.warn("无效的类名.方法名格式: {}", classMethod);
        return;
      }

      String className = classMethodParts[0];
      String methodName = classMethodParts[1];

      // 获取Spring Bean并查找对应方法
      Object bean = SpringUtil.getBean(className);
      Method method = findMethod(bean.getClass(), methodName);

      if (method != null) {
        // 解析JSON内容为方法返回类型的对象
        Object mockValue = JSONUtils.readValue(content, method.getReturnType());
        MOCK_RESOURCE_MAP.put(classMethod, mockValue);
        log.debug("已加载模拟配置: {} -> {}", classMethod, content);
      } else {
        log.warn("未找到方法: {} 中的 {}", className, methodName);
      }
    } catch (Exception e) {
      log.error("处理模拟配置行失败，行内容: {}", trimmedLine, e);
    }
  }

  /**
   * 查找类中指定名称的方法
   *
   * @param clazz      目标类
   * @param methodName 方法名
   * @return 找到的方法，未找到则返回null
   */
  private Method findMethod(Class<?> clazz, String methodName) {
    // 使用JDK 8+的Stream API查找方法
    return Arrays.stream(clazz.getMethods())
        .filter(method -> method.getName().equals(methodName))
        .findFirst()
        .orElse(null);
  }
}
