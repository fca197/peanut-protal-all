package com.olivia.sdk.utils.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.math.BigInteger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 自定义的 Long 类型反序列化器
 * 用于将 JSON 中的字符串值转换为 Long 类型，支持大数字转换
 */
@Slf4j
public class CustomLongDeserializer extends JsonDeserializer<Long> {

  /**
   * 反序列化 JSON 字符串为 Long
   * @param p JSON 解析器
   * @param ctxt 反序列化上下文
   * @return 反序列化后的 Long 值
   * @throws IOException  IO 异常
   */
  @Override
  public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    // 读取 JSON 值作为字符串
    String value = p.readValueAs(String.class);
    try {
      // 处理空值情况
      if (StringUtils.isEmpty(value)) {
        return null;
      }
      // 先尝试作为普通 Long 处理
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      // 超出范围时，使用 BigInteger 处理
      log.error("Error converting long value to Long  value {} message: {}", value, e.getMessage(), e);
      return new BigInteger(value).longValue();
    }
  }
}
