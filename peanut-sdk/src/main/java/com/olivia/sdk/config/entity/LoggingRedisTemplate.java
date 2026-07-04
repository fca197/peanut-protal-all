package com.olivia.sdk.config.entity;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class LoggingRedisTemplate<K, V> extends RedisTemplate<K, V> {

  public LoggingRedisTemplate() {
    super();
  }

  @Override
  public <T> T execute(RedisCallback<T> action, boolean exposeConnection, boolean pipeline) {
    // 1. 记录操作开始日志
    log.debug("[Redis 操作开始] 线程: {} | 暴露连接: {} | 管道模式: {}",
        Thread.currentThread().getName(), exposeConnection, pipeline);

    long startTime = System.currentTimeMillis();
    try {
      // 2. 执行实际的 Redis 操作
      T result = super.execute(action, exposeConnection, pipeline);

      // 3. 记录操作成功日志及耗时
      long costTime = System.currentTimeMillis() - startTime;
      log.debug("[Redis 操作成功] 耗时: {}ms", costTime);
      return result;
    } catch (Exception e) {
      // 4. 记录操作异常日志
      long costTime = System.currentTimeMillis() - startTime;
      log.error("[Redis 操作异常] 耗时: {}ms | 异常信息: {}", costTime, e.getMessage(), e);
      throw e;
    }
  }
}
