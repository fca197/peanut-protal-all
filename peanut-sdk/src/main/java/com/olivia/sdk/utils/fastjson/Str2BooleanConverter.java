package com.olivia.sdk.utils.fastjson;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符串转 Boolean 类型转换器 用于 Excel 导入时将字符串值转换为 Boolean 类型 支持多种布尔值表示形式，如 "true"、"是"、"yes"、"y"、"1"、"t"
 */
public class Str2BooleanConverter implements Converter<Boolean> {

  /**
   * 将 Excel 单元格数据转换为 Java Boolean 类型
   *
   * @param cellData            单元格数据
   * @param contentProperty     Excel 内容属性
   * @param globalConfiguration 全局配置
   * @return 转换后的 Boolean 值
   * @throws Exception 转换异常
   */
  @Override
  public Boolean convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
    // 检查单元格字符串值是否匹配常见的布尔值表示形式（忽略大小写）
    return StringUtils.equalsAnyIgnoreCase(cellData.getStringValue(), "true", "是", "yes", "y", "1", "t");
  }
}