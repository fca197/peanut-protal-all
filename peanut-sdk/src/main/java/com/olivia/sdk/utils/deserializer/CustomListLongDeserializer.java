package com.olivia.sdk.utils.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义的 Long 列表反序列化器 用于将 JSON 数组反序列化为 List<Long>，支持大数字转换
 */
@Slf4j
public class CustomListLongDeserializer extends JsonDeserializer<List<Long>> {

  /**
   * 反序列化 JSON 数组为 List<Long>
   *
   * @param p    JSON 解析器
   * @param ctxt 反序列化上下文
   * @return 反序列化后的 Long 列表
   * @throws IOException IO 异常
   */
  @Override
  public List<Long> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    List<Long> result = new ArrayList<>();

    // 使用Jackson的TreeTraversingParser安全遍历数组元素
    try (JsonParser arrayParser = p.getCodec().treeAsTokens(p.getCodec().readTree(p))) {
      // 移动到数组开始标记
      if (arrayParser.nextToken() != JsonToken.START_ARRAY) {
        return result;
      }

      // 遍历数组元素
      while (arrayParser.nextToken() != JsonToken.END_ARRAY) {
        // 获取当前元素的文本值
        String value = arrayParser.getText();
        try {
          // 使用 BigInteger 处理可能的大数字，然后转换为 long
          result.add(new BigInteger(value).longValue());
        } catch (NumberFormatException e) {
          // 记录错误或跳过无效值
          log.error("Error parsing list long value . value: {} error: {}", value, e.getMessage(), e);
        }
      }
    }

    return result;
  }
}