package com.olivia.sdk.aspect;

import com.olivia.sdk.utils.JSONUtils;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
//@Aspect
//@Component
public class RedisLogAspect {

  //@Pointcut(" org.springframework.data.redis.core.")
  @Pointcut("execution(* org.springframework.data.redis.core.RedisTemplate.*(..))")
  public void redisLogger() {
    // 切入点定义，无具体实现
  }

  @Around("redisLogger()")
  public Object aroundMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = methodSignature.getMethod();
    String methodName = targetMethod.getName();
    Object[] joinPointArgs = joinPoint.getArgs();
    log.info("redisLogger methodName:{} args: {}", methodName, JSONUtils.toJSONString(joinPointArgs));
    return joinPoint.proceed();
  }
}
