package com.olivia.sdk.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * JSON 工具类 提供 JSON 序列化和反序列化功能，配置了自定义的序列化器和反序列化器 使用 JDK 21+ 特性进行优化
 */
public class JSONUtils {

  /**
   * Jackson ObjectMapper 实例，用于 JSON 序列化和反序列化 -- GETTER -- 获取 ObjectMapper 实例
   *
   * @return ObjectMapper 实例
   */
  @Getter
  private static final ObjectMapper mapper = new ObjectMapper();

  static {
    // 用于生成空键的唯一索引
    var nullKeyIndex = new AtomicLong(0);

    // 1. 基础配置
    configureBaseSettings();

    // 2. 注册JavaTimeModule (处理LocalDateTime等Java 8日期时间类型)
    registerJavaTimeModule();

    // 3. 注册自定义模块 (处理Long, BigDecimal等特殊类型)
    registerCustomModule();

    // 4. 配置空键序列化器
    configureNullKeySerializer(nullKeyIndex);
  }

  /**
   * 配置ObjectMapper基础设置
   */
  private static void configureBaseSettings() {
    // 配置日期格式化
    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    // 禁用将日期序列化为时间戳
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    // 配置反序列化时忽略未知属性
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // 禁用空对象序列化失败
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
  }

  /**
   * 注册JavaTimeModule
   */
  private static void registerJavaTimeModule() {
    var javaTimeModule = new JavaTimeModule();
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 注册 LocalDateTime 序列化器和反序列化器
    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
    javaTimeModule.addDeserializer(String.class, new PeanutStringDeserializer());

    mapper.registerModule(javaTimeModule);
  }

  /**
   * 注册自定义模块
   */
  private static void registerCustomModule() {
    var module = new SimpleModule();

    // 注册 Long 和 long 的序列化器和反序列化器
    module.addSerializer(Long.class, JSLongSerializer.instance).addSerializer(long.class, JSLongSerializer.instance)
        .addDeserializer(Long.class, new CustomLongDeserializer()).addDeserializer(long.class, new CustomLongDeserializer())
        .addSerializer(BigDecimal.class, ToStringSerializer.instance);

    mapper.registerModule(module);
  }

  /**
   * 配置空键序列化器
   */
  private static void configureNullKeySerializer(AtomicLong nullKeyIndex) {
    mapper.getSerializerProvider().setNullKeySerializer(new JsonSerializer<>() {
      @Override
      public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("NULL_KEY_" + nullKeyIndex.incrementAndGet());
      }
    });
  }

  /**
   * 将 JSON 字符串反序列化为指定类型的对象
   *
   * @param content   JSON 字符串
   * @param valueType 目标对象类型
   * @param <T>       泛型类型
   * @return 反序列化后的对象，如果输入为空则返回 null
   */
  @SneakyThrows
  public static <T> T readValue(String content, Class<T> valueType) {
    if (isBlank(content)) {
      return null;
    }
    return mapper.readValue(content, valueType);
  }

  public static <T> Optional<T> readOptionalValue(String content, Class<T> valueType) {
    if (isBlank(content)) {
      return Optional.empty();
    }
    return Optional.ofNullable(readValue(content, valueType));
  }


  /**
   * 将 JSON 字符串反序列化为泛型类型对象
   *
   * @param content JSON 字符串
   * @param <T>     泛型类型
   * @return 反序列化后的对象，如果输入为空则返回 null
   */
  @SneakyThrows
  public static <T> T readValue(String content) {
    if (isBlank(content)) {
      return null;
    }
    return mapper.readValue(content, new TypeReference<>() {
    });
  }

  /**
   * 将 JSON 字符串反序列化为指定类型引用的对象
   *
   * @param content       JSON 字符串
   * @param typeReference 类型引用
   * @param <T>           泛型类型
   * @return 反序列化后的对象
   */
  @SneakyThrows
  public static <T> T readValue(String content, TypeReference<T> typeReference) {
    return mapper.readValue(content, typeReference);
  }

  /**
   * 将对象序列化为 JSON 字符串
   *
   * @param object 要序列化的对象
   * @return JSON 字符串
   */
  @SneakyThrows
  public static String toJSONString(Object object) {
    return mapper.writeValueAsString(object);
  }

  /**
   * 将 JSON 字符串反序列化为指定类型的列表
   *
   * @param content JSON 字符串
   * @param clazz   列表元素类型
   * @param <T>     泛型类型
   * @return 反序列化后的列表，如果输入为空则返回空列表
   */
  @SneakyThrows
  public static <T> List<T> readList(String content, Class<T> clazz) {
    if (isBlank(content)) {
      return List.of();
    }
    var collectionType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
    return mapper.readValue(content, collectionType);
  }

  /**
   * 将对象转换为指定类型
   *
   * @param object    源对象
   * @param valueType 目标类型
   * @param <T>       泛型类型
   * @return 转换后的对象
   */
  @SneakyThrows
  public static <T> T convertValue(Object object, Class<T> valueType) {
    return mapper.convertValue(object, valueType);
  }

  /**
   * 将对象转换为指定类型引用
   *
   * @param object        源对象
   * @param typeReference 类型引用
   * @param <T>           泛型类型
   * @return 转换后的对象
   */
  @SneakyThrows
  public static <T> T convertValue(Object object, TypeReference<T> typeReference) {
    return mapper.convertValue(object, typeReference);
  }

  /**
   * 判断字符串是否为空或空白
   *
   * @param str 待检查字符串
   * @return 如果为空或空白返回true，否则返回false
   */
  private static boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }
}
