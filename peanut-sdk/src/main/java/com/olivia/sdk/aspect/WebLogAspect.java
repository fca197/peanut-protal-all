package com.olivia.sdk.aspect;

import com.olivia.sdk.ann.MethodExt;
import com.olivia.sdk.filter.LoginUser;
import com.olivia.sdk.filter.LoginUserContext;
import com.olivia.sdk.utils.JSONUtils;
import com.olivia.sdk.utils.ReqResUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Web请求日志切面，用于记录REST接口的请求和响应信息。
 * <p>
 * 通过AOP环绕通知拦截所有标注了@RestController的类， 记录请求URL、方法信息、登录用户、请求参数和响应结果， 便于接口调试和问题排查。
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class WebLogAspect {

  /**
   * 定义切入点：匹配所有标注了@RestController注解的类中的方法
   */
  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  public void webLogPointcut() {
    // 切入点定义，无具体实现
  }

  /**
   * 环绕通知：记录请求和响应日志
   *
   * @param joinPoint 连接点对象，包含目标方法信息
   * @return 目标方法的执行结果
   * @throws Throwable 目标方法可能抛出的异常
   */
  @Around("webLogPointcut()")
  public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
    // 仅在DEBUG级别开启日志记录
    if (log.isDebugEnabled()) {
      return logRequestAndResponse(joinPoint);
    }

    // 非DEBUG级别直接执行方法
    return joinPoint.proceed();
  }

  /**
   * 记录请求和响应日志的核心逻辑
   *
   * @param joinPoint 连接点对象
   * @return 目标方法的执行结果
   * @throws Throwable 目标方法可能抛出的异常
   */
  private Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取请求信息
    HttpServletRequest request = ReqResUtils.getRequest();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    LoginUser loginUser = LoginUserContext.getLoginUser();
    long startTime = System.currentTimeMillis();
    // 过滤并获取请求参数
    List<Object> filteredArgs = ReqResUtils.filterReqArgs(joinPoint.getArgs());

    // 记录请求日志
    log.debug("请求日志 - URL: {}, 方法: {}.{}, 用户: {}, 参数: {}", request.getRequestURI(), methodSignature.getDeclaringType().getSimpleName(),
        methodSignature.getMethod().getName(), JSONUtils.toJSONString(loginUser), JSONUtils.toJSONString(filteredArgs));

    // 执行目标方法
    Object result = null;
    try {
      result = joinPoint.proceed();
    } finally {
      // 根据注解配置决定是否记录响应日志
      MethodExt methodExt = methodSignature.getMethod().getAnnotation(MethodExt.class);
      if (Objects.isNull(methodExt) || methodExt.printResult()) {
        log.debug("响应日志 - 耗时: {} ms, 结果: {}", (System.currentTimeMillis() - startTime), JSONUtils.toJSONString(result));
      }
    }

    return result;
  }
}
