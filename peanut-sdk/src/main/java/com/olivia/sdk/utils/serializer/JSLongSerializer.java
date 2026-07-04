package com.olivia.sdk.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Objects;

/**
 * JavaScript Long 类型序列化器 用于处理 Long 类型在 JavaScript 中的安全表示 当 Long 值超出 JavaScript 安全整数范围时，将其序列化为字符串
 */
public class JSLongSerializer extends JsonSerializer<Long> {

  /**
   * 序列化器实例
   */
  public static JSLongSerializer instance = new JSLongSerializer();

  /**
   * JavaScript 安全整数最大值 JavaScript 中 Number 类型的安全整数范围是 -2^53 到 2^53，即 -9007199254740991 到 9007199254740991
   */
  private final Long MAX = 9007199254740991L;

  /**
   * JavaScript 安全整数最小值
   */
  private final Long MIN = -9007199254740991L;

  /**
   * 序列化 Long 值为 JSON
   *
   * @param value              要序列化的 Long 值
   * @param jsonGenerator      JSON 生成器
   * @param serializerProvider 序列化提供者
   * @throws IOException IO 异常
   */
  @Override
  public void serialize(Long value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    // 处理 null 值
    if (Objects.isNull(value)) {
      jsonGenerator.writeNull();
      return;
    }
    // 检查值是否超出 JavaScript 安全整数范围
    if (value < MIN || value > MAX) {
      // 超出范围时序列化为字符串
      jsonGenerator.writeString(String.valueOf(value));
    } else {
      // 在范围内时序列化为数字
      jsonGenerator.writeNumber(value);
    }
  }
}