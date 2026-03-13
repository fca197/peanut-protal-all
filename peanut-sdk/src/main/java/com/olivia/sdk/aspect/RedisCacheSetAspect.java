package com.olivia.sdk.aspect;

import com.olivia.sdk.ann.RedissonCacheAnn;
import com.olivia.sdk.utils.SpelUtils;
import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis缓存设置切面，用于自动为标注了{@link RedissonCacheAnn}的方法添加缓存功能。
 * <p>
 * 实现逻辑： 1. 方法执行前检查缓存，存在则直接返回缓存值 2. 缓存不存在则执行方法，将结果存入缓存 3. 支持SpEL表达式动态生成缓存键，配置缓存过期时间
 */
@Slf4j
@Aspect
@Component
public class RedisCacheSetAspect {

  @Resource
  private RedisTemplate<String, Object> redisTemplate;

  /**
   * 定义切入点：匹配所有标注了{@link RedissonCacheAnn}注解的方法
   */
  @Pointcut("@annotation(com.olivia.sdk.ann.RedissonCacheAnn)")
  public void redisCachePointcut() {
    // 切入点定义，无具体实现
  }

  /**
   * 环绕通知：实现缓存的读取和设置逻辑
   *
   * @param joinPoint 连接点对象，包含目标方法信息
   * @return 目标方法的执行结果（可能来自缓存或方法本身）
   * @throws Throwable 目标方法可能抛出的异常
   */
  @Around("redisCachePointcut()")
  public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = methodSignature.getMethod();
    String methodName = targetMethod.getName();

    // 处理无返回值方法
    if (targetMethod.getReturnType().equals(Void.TYPE)) {
      log.warn("Redis缓存注解应用于无返回值方法: {}", methodName);
      return joinPoint.proceed(joinPoint.getArgs());
    }

    RedissonCacheAnn cacheAnnotation = targetMethod.getAnnotation(RedissonCacheAnn.class);
    String cacheKey = generateCacheKey(joinPoint, targetMethod, cacheAnnotation);

    if (log.isDebugEnabled()) {
      log.debug("Redis缓存处理 - 方法: {}, 缓存键: {}", methodName, cacheKey);
    }

    // 尝试从缓存获取数据
    Object cachedValue = getValueFromCache(cacheKey, methodName);
    if (cachedValue != null) {
      return cachedValue;
    }

    // 缓存未命中，执行目标方法
    Object methodResult = joinPoint.proceed();

    // 将结果存入缓存
    setValueToCache(cacheKey, methodResult, cacheAnnotation, methodName);

    return methodResult;
  }

  /**
   * 生成缓存键
   *
   * @param joinPoint  连接点
   * @param method     目标方法
   * @param annotation 缓存注解
   * @return 生成的缓存键字符串
   */
  private String generateCacheKey(ProceedingJoinPoint joinPoint, Method method, RedissonCacheAnn annotation) {
    String spelKey = annotation.key();

    // 解析SpEL表达式
    if (StringUtils.isNotBlank(spelKey) && spelKey.startsWith("#")) {
      try {
        spelKey = SpelUtils.parseKey(spelKey, method, joinPoint.getArgs());
      } catch (Exception e) {
        log.error("解析缓存键SpEL表达式失败: {}", spelKey, e);
        throw new RuntimeException("缓存键表达式解析失败", e);
      }
    }

    // 组合完整缓存键
    return "cache:" + annotation.group() + ":" + spelKey;
  }

  /**
   * 从缓存获取值
   *
   * @param cacheKey   缓存键
   * @param methodName 方法名，用于日志
   * @return 缓存中的值，null表示未命中或获取失败
   */
  private Object getValueFromCache(String cacheKey, String methodName) {
    try {
      Object value = redisTemplate.opsForValue().get(cacheKey);
      if (value != null && log.isDebugEnabled()) {
        log.debug("Redis缓存命中 - 方法: {}, 缓存键: {}", methodName, cacheKey);
      }
      return value;
    } catch (Exception e) {
      log.error("Redis缓存获取失败 - 方法: {}, 缓存键: {}", methodName, cacheKey, e);
      return null;
    }
  }

  /**
   * 将值存入缓存
   *
   * @param cacheKey   缓存键
   * @param value      要缓存的值
   * @param annotation 缓存注解，包含过期时间配置
   * @param methodName 方法名，用于日志
   */
  private void setValueToCache(String cacheKey, Object value, RedissonCacheAnn annotation, String methodName) {
    try {
      long ttl = annotation.ttl();
      TimeUnit timeUnit = annotation.unit();
      redisTemplate.opsForValue().set(cacheKey, value, ttl, timeUnit);

      if (log.isDebugEnabled()) {
        log.debug("Redis缓存设置成功 - 方法: {}, 缓存键: {}, 过期时间: {} {}", methodName, cacheKey, ttl, timeUnit);
      }
    } catch (Exception e) {
      log.error("Redis缓存设置失败 - 方法: {}, 缓存键: {}", methodName, cacheKey, e);
    }
  }
}
