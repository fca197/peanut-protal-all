package com.olivia.sdk.mybatis.type;

import com.olivia.sdk.utils.JSONUtils;
import java.sql.*;
import java.util.Objects;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

/**
 * 集合类型处理器基类 用于MyBatis与数据库之间的集合类型字段映射，通过JSON格式进行转换
 *
 * @param <T> 集合类型，通常为List或其他可序列化的集合
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
public abstract class ListMyBaseTypeHandler<T> extends BaseTypeHandler<T> {

  /**
   * 将JSON字符串解析为集合对象
   *
   * @param json JSON字符串
   * @return 解析后的集合对象，如果JSON为空则返回空集合
   */
  public abstract T parse(String json);

  /**
   * 将集合对象转换为JSON字符串
   *
   * @param obj 集合对象
   * @return 转换后的JSON字符串，如果对象为空则返回"[]"
   */
  public String toJson(T obj) {
    if (Objects.isNull(obj)) {
      return "[]";
    }
    return JSONUtils.toJSONString(obj);
  }

  /**
   * 设置非空参数到PreparedStatement
   *
   * @param ps        PreparedStatement对象
   * @param i         参数索引
   * @param parameter 集合参数
   * @param jdbcType  JDBC类型
   * @throws SQLException SQL异常
   */
  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
    ps.setString(i, toJson(parameter));
  }

  /**
   * 从ResultSet中按列名获取结果
   *
   * @param rs         ResultSet对象
   * @param columnName 列名
   * @return 解析后的集合对象
   * @throws SQLException SQL异常
   */
  @Override
  public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return parseJsonString(rs.getString(columnName));
  }

  /**
   * 从ResultSet中按列索引获取结果
   *
   * @param rs          ResultSet对象
   * @param columnIndex 列索引
   * @return 解析后的集合对象
   * @throws SQLException SQL异常
   */
  @Override
  public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return parseJsonString(rs.getString(columnIndex));
  }

  /**
   * 从CallableStatement中按列索引获取结果
   *
   * @param cs          CallableStatement对象
   * @param columnIndex 列索引
   * @return 解析后的集合对象
   * @throws SQLException SQL异常
   */
  @Override
  public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return parseJsonString(cs.getString(columnIndex));
  }

  /**
   * 解析JSON字符串，处理空值情况
   *
   * @param jsonString JSON字符串
   * @return 解析后的集合对象
   */
  private T parseJsonString(String jsonString) {
    if (jsonString == null || jsonString.trim().isEmpty() || "null".equalsIgnoreCase(jsonString.trim())) {
      return parse("[]");
    }
    return parse(jsonString);
  }
}
