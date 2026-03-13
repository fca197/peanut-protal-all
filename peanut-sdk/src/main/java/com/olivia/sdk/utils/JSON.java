package com.olivia.sdk.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.olivia.sdk.utils.deserializer.CustomLongDeserializer;
import com.olivia.sdk.utils.deserializer.PeanutStringDeserializer;
import com.olivia.sdk.utils.serializer.JSLongSerializer;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.SneakyThrows;

/**
 * JSON 工具类
 * 提供 JSON 序列化和反序列化功能，配置了自定义的序列化器和反序列化器
 * 使用 JDK 25 特性进行优化
 */

public class JSON {

  /**
   * Jackson ObjectMapper 实例，用于 JSON 序列化和反序列化
   */
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    // 用于生成空键的索引
    var index = new AtomicLong(0);

    // 配置日期格式化
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    // 注册Java 8日期时间模块
    var javaTimeModule = new JavaTimeModule();

    // 定义日期时间格式
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 注册 LocalDateTime 序列化器
    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));

    // 注册 LocalDateTime 反序列化器
    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
    javaTimeModule.addDeserializer(String.class, new PeanutStringDeserializer());

    mapper.registerModule(javaTimeModule);

    // 禁用将日期序列化为时间戳
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 配置反序列化时忽略未知属性
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 禁用空对象序列化失败
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    // 创建一个 SimpleModule 并注册自定义序列化器和反序列化器
    var module = new SimpleModule();

    // 构建List<Long>的JavaType
    var type = TypeFactory.defaultInstance().constructCollectionType(List.class, Long.class);

    // 构建通配符List<?>类型
    var wildCardType = TypeFactory.defaultInstance()
        .constructCollectionType(List.class, Long.class);

    // 注册序列化器和反序列化器
    module.addSerializer(Long.class, JSLongSerializer.instance)
        .addSerializer(long.class, JSLongSerializer.instance)
        .addDeserializer(Long.class, new CustomLongDeserializer())
        .addDeserializer(long.class, new CustomLongDeserializer())
        .addSerializer(BigDecimal.class, ToStringSerializer.instance);

    // 注册空键序列化器
    mapper.getSerializerProvider().setNullKeySerializer(new JsonSerializer<>() {
      @Override
      public void serialize(Object o, JsonGenerator jsonGenerator,
          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("NULL_KEY_"+index.incrementAndGet());
      }
    });

    // 注册模块
    mapper.registerModules(List.of(module, javaTimeModule));
  }

  /**
   * 获取 ObjectMapper 实例
   * @return ObjectMapper 实例
   */
  public static ObjectMapper getMapper() {
    return JSON.mapper;
  }

  /**
   * 将 JSON 字符串反序列化为指定类型的对象
   * @param content JSON 字符串
   * @param valueType 目标对象类型
   * @param <T> 泛型类型
   * @return 反序列化后的对象
   */
  @SneakyThrows
  public static <T> T readValue(String content, Class<T> valueType) {
    if (content == null || content.trim().isEmpty()) {
      return null;
    }
    return mapper.readValue(content, valueType);
  }

  /**
   * 将 JSON 字符串反序列化为泛型类型对象
   * @param content JSON 字符串
   * @param <T> 泛型类型
   * @return 反序列化后的对象
   */
  @SneakyThrows
  public static <T> T readValue(String content) {
    if (content == null || content.trim().isEmpty()) {
      return null;
    }
    return mapper.readValue(content, new TypeReference<>() {});
  }

  /**
   * 将 JSON 字符串反序列化为指定类型引用的对象
   * @param content JSON 字符串
   * @param typeReference 类型引用
   * @param <T> 泛型类型
   * @return 反序列化后的对象
   */
  @SneakyThrows
  public static <T> T readValue(String content, TypeReference<T> typeReference) {
    return mapper.readValue(content, typeReference);
  }

  /**
   * 将对象序列化为 JSON 字符串
   * @param object 要序列化的对象
   * @return JSON 字符串
   */
  @SneakyThrows
  public static String toJSONString(Object object) {
    return mapper.writeValueAsString(object);
  }

  /**
   * 将 JSON 字符串反序列化为指定类型的列表
   * @param content JSON 字符串
   * @param clazz 列表元素类型
   * @param <T> 泛型类型
   * @return 反序列化后的列表
   */
  @SneakyThrows
  public static <T> List<T> readList(String content, Class<T> clazz) {
    if (content == null || content.trim().isEmpty()) {
      return List.of();
    }
    var collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
    return mapper.readValue(content, collectionType);
  }

  /**
   * 将对象转换为指定类型
   * @param object 源对象
   * @param valueType 目标类型
   * @param <T> 泛型类型
   * @return 转换后的对象
   */
  @SneakyThrows
  public static <T> T convertValue(Object object, Class<T> valueType) {
    return mapper.convertValue(object, valueType);
  }

  /**
   * 将对象转换为指定类型引用
   * @param object 源对象
   * @param typeReference 类型引用
   * @param <T> 泛型类型
   * @return 转换后的对象
   */
  @SneakyThrows
  public static <T> T convertValue(Object object, TypeReference<T> typeReference) {
    return mapper.convertValue(object, typeReference);
  }

}