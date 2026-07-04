package com.olivia.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.olivia.sdk.config.entity.LoggingRedisTemplate;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类 配置 RedisTemplate 实例，自定义序列化器以优化 Redis 数据存储和读取
 */
@Configuration
public class RedisBeanConfig {

  /**
   * 配置 RedisTemplate 实例 自定义 key 和 value 的序列化方式，支持复杂对象和 Java 8 日期时间类型
   *
   * @param redisConnectionFactory Redis 连接工厂
   * @return 配置好的 RedisTemplate 实例
   */
  @Bean
  public RedisTemplate<String, Object> redisTemplate(@NonNull RedisConnectionFactory redisConnectionFactory) {
    // 创建 RedisTemplate 实例
    RedisTemplate<String, Object> redisTemplate = new LoggingRedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);

    // 配置 key 序列化器 - 使用 String 序列化
    RedisSerializer<String> keySerializer = new StringRedisSerializer();
    redisTemplate.setKeySerializer(keySerializer);
    redisTemplate.setHashKeySerializer(keySerializer);

    // 配置 value 序列化器 - 使用 JSON 序列化，支持 Java 8 日期时间类型
    RedisSerializer<Object> valueSerializer = createJsonSerializer();
    redisTemplate.setValueSerializer(valueSerializer);
    redisTemplate.setHashValueSerializer(valueSerializer);

    // 初始化 RedisTemplate
    redisTemplate.afterPropertiesSet();

    return redisTemplate;
  }


  /**
   * 创建 JSON 序列化器 配置支持 Java 8 日期时间类型（LocalDateTime 等）的序列化和反序列化
   *
   * @return 配置好的 JSON 序列化器
   */
  private RedisSerializer<Object> createJsonSerializer() {
    // 创建自定义 ObjectMapper 以支持 Java 8 日期时间类型
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule()); // 注册 Java 时间模块

    // 创建并返回 JSON 序列化器
    return new GenericJackson2JsonRedisSerializer(objectMapper);
  }
}
