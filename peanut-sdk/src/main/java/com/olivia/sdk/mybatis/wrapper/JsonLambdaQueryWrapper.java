package com.olivia.sdk.mybatis.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import java.util.Objects;
import lombok.Setter;

@Setter
public class JsonLambdaQueryWrapper<T> extends LambdaQueryWrapper<T> {

  private LambdaQueryWrapper<T> wrapper;

  // 判断JSON数组包含指定值
  public JsonLambdaQueryWrapper<T> jsonContains(SFunction<T, ?> column, Object value) {
    if (Objects.isNull(value)) {
      return this;
    }
//    Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(wrapper.getEntityClass());
    String columnName = columnToString(column);
    String valueStr = value.toString();
    this.wrapper.apply("JSON_CONTAINS(" + columnName + ", " + valueStr + ",'$')");
    return this;
  }

  // 判断JSON对象指定路径的值等于目标值
  public JsonLambdaQueryWrapper<T> jsonEq(SFunction<T, ?> column, String path, Object value) {
    String columnName = columnToString(column);
    this.wrapper.apply("JSON_EXTRACT(" + columnName + ", '" + path + "') = '" + value + "'");
    return this;
  }
}

// 使用示例
//public List<Product> findUsingJsonWrapper() {
//  JsonLambdaQueryWrapper<Product> wrapper = new JsonLambdaQueryWrapper<>();
//  wrapper.jsonContains(Product::getCategories, "手机")
//      .jsonEq(Product::getAttributes, "$.brand", "华为");
//  return productMapper.selectList(wrapper);
//}