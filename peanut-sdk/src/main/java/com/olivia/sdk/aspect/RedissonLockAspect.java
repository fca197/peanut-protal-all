package com.olivia.sdk.aspect;

import com.olivia.sdk.ann.RedissonLockAnn;
import com.olivia.sdk.exception.CanIgnoreException;
import com.olivia.sdk.exception.RunException;
import com.olivia.sdk.utils.RedisLockUtils;
import com.olivia.sdk.utils.SpelUtils;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Redisson分布式锁切面，用于为标注了{@link RedissonLockAnn}的方法自动添加分布式锁。
 * <p>
 * 实现逻辑： 1. 解析注解参数生成分布式锁键 2. 使用Redisson获取分布式锁 3. 执行目标方法 4. 根据配置自动释放锁或等待超时释放
 */
@Slf4j
@Aspect
@Component
@Order(-1)
public class RedissonLockAspect {

  /**
   * 定义切入点：匹配所有标注了{@link RedissonLockAnn}注解的方法
   */
  @Pointcut("@annotation(com.olivia.sdk.ann.RedissonLockAnn)")
  public void redissonLockPointcut() {
    // 切入点定义，无具体实现
  }

  /**
   * 环绕通知：实现分布式锁的获取、执行方法和释放逻辑
   *
   * @param joinPoint 连接点对象，包含目标方法信息
   * @return 目标方法的执行结果
   * @throws Throwable 目标方法可能抛出的异常
   */
  @Around("redissonLockPointcut()")
  public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = methodSignature.getMethod();
    RedissonLockAnn lockAnnotation = targetMethod.getAnnotation(RedissonLockAnn.class);

    // 处理注解为空的异常情况
    if (lockAnnotation == null) {
      log.warn("RedissonLockAnn注解不存在 - 方法: {}, 类: {}", targetMethod.getName(), targetMethod.getDeclaringClass().getSimpleName());
      return joinPoint.proceed();
    }

    // 生成分布式锁键
    String lockKey = generateLockKey(joinPoint, targetMethod, lockAnnotation);

    if (log.isDebugEnabled()) {
      log.debug("获取分布式锁 - 方法: {}, 锁键: {}, 超时时间: {}ms", targetMethod.getName(), lockKey, lockAnnotation.lockTimeOut());
    }

    // 执行带锁的方法调用
    return RedisLockUtils.lockCall(lockKey, lockAnnotation.lockTimeOut(), lockAnnotation.afterDeleteKey(), lockAnnotation.isWait(), () -> executeTargetMethod(joinPoint));
  }

  /**
   * 生成分布式锁的完整键名
   *
   * @param joinPoint  连接点
   * @param method     目标方法
   * @param annotation 锁注解
   * @return 生成的锁键字符串
   */
  private String generateLockKey(ProceedingJoinPoint joinPoint, Method method, RedissonLockAnn annotation) {
    // 获取注解配置的基础参数
    String lockPrefix = annotation.lockPrefix();
    String bizKeyFlag = annotation.lockBizKeyFlag();
    String keyExpression = annotation.keyExpression();

    // 解析SpEL表达式获取业务ID
    String bizId = resolveBizId(joinPoint, method, keyExpression);

    // 处理前缀和标识的格式，确保正确拼接
    String formattedBizKeyFlag = formatKeySegment(bizKeyFlag);
    String formattedBizId = formatKeySegment(bizId);

    // 组合生成完整锁键
    return lockPrefix + formattedBizKeyFlag + formattedBizId;
  }

  /**
   * 解析SpEL表达式获取业务ID
   *
   * @param joinPoint     连接点
   * @param method        目标方法
   * @param keyExpression SpEL表达式
   * @return 解析后的业务ID，默认"all"
   */
  private String resolveBizId(ProceedingJoinPoint joinPoint, Method method, String keyExpression) {
    if (StringUtils.isBlank(keyExpression)) {
      return "all";
    }

    try {
      return SpelUtils.parseKey(keyExpression, method, joinPoint.getArgs());
    } catch (Exception e) {
      log.error("解析锁键SpEL表达式失败: {}", keyExpression, e);
      throw new RunException("分布式锁键表达式解析失败", e);
    }
  }

  /**
   * 格式化键片段，确保统一以冒号开头
   *
   * @param segment 键的片段
   * @return 格式化后的键片段
   */
  private String formatKeySegment(String segment) {
    if (StringUtils.isBlank(segment)) {
      return "";
    }
    return segment.startsWith(":") ? segment : ":" + segment;
  }

  /**
   * 执行目标方法并处理异常
   *
   * @param joinPoint 连接点
   * @return 目标方法的执行结果
   */
  private Object executeTargetMethod(ProceedingJoinPoint joinPoint) {
    try {
      return joinPoint.proceed();
    } catch (CanIgnoreException e) {
      // 已知异常直接抛出
      log.info("CanIgnoreException error message {}", e.getMessage(), e);
      return null;
    } catch (Throwable e) {
      // 未知异常包装为RunException
      log.error("Throwable error message {}", e.getMessage(), e);
      throw new RunException("分布式锁保护的方法执行失败", e);
    }
  }
}
