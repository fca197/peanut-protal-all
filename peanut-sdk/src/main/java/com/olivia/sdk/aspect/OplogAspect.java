package com.olivia.sdk.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.olivia.sdk.ann.Oplog;
import com.olivia.sdk.filter.LoginUser;
import com.olivia.sdk.filter.LoginUserContext;
import com.olivia.sdk.mapper.OpLogEntityMapper;
import com.olivia.sdk.model.OpLogEntity;
import com.olivia.sdk.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.IntStream;
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
 * 操作日志切面，用于记录系统操作日志。
 * <p>
 * 通过AOP环绕通知拦截标注了{@link Oplog}注解的方法， 自动记录操作详情、请求参数、执行结果等信息并持久化。
 */
@Aspect
@Order(-1)
@Component
@Slf4j
public class OplogAspect {

  /**
   * 定义切入点：匹配所有标注了{@link Oplog}注解的方法
   */
  @Pointcut("@annotation(com.olivia.sdk.ann.Oplog)")
  public void oplogPointcut() {
    // 切入点定义，无具体实现
  }

  /**
   * 环绕通知：在方法执行前后记录操作日志
   *
   * @param joinPoint 连接点对象，包含目标方法信息
   * @return 目标方法的执行结果
   * @throws Throwable 目标方法可能抛出的异常
   */
  @Around("oplogPointcut()")
  public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {

    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    Method targetMethod = methodSignature.getMethod();
    Oplog oplogAnnotation = targetMethod.getAnnotation(Oplog.class);
    LoginUser currentUser = LoginUserContext.getLoginUser();

    long startTime = System.currentTimeMillis();
    Object result = null;
    StringBuilder errorMsg = new StringBuilder();

    try {
      // 执行目标方法
      result = joinPoint.proceed();
      return result;
    } catch (Exception e) {
      errorMsg.append(e.getMessage());
      throw e;
    } finally {
      // 异步保存操作日志
      saveOperationLog(joinPoint, targetMethod, oplogAnnotation, currentUser, startTime, result, errorMsg.toString());
    }
  }

  /**
   * 异步保存操作日志
   *
   * @param joinPoint 连接点
   * @param method    目标方法
   * @param oplog     日志注解
   * @param loginUser 当前登录用户
   * @param startTime 方法开始时间
   * @param result    方法执行结果
   * @param errorMsg  错误信息
   */
  private void saveOperationLog(ProceedingJoinPoint joinPoint, Method method, Oplog oplog, LoginUser loginUser, long startTime, Object result, String errorMsg) {

    RunUtils.run("save-operation-log", () -> {
      try {
        // 计算方法执行时间
        long executionTime = System.currentTimeMillis() - startTime;

        // 获取HTTP请求信息
        HttpServletRequest request = ReqResUtils.getRequest();
        String contextPath = request.getContextPath();
        String requestUri = request.getRequestURI();
        String reqUrl = requestUri.substring(contextPath.length());

        // 解析日志注解参数
        String businessKey = parseSpelExpression(joinPoint, oplog.businessKey(), method);
        String content = oplog.content();
        String businessType = oplog.businessType();
        List<String> businessTypeSegments = splitBusinessType(businessType);
        String url = StringUtils.firstNonEmpty(oplog.url(), reqUrl);
        String channel = request.getHeader(Str.ReqHeader.USER_CHANNEL);
        String remark = oplog.remark();
        String paramName = oplog.paramName();

        // 处理请求和响应数据
        String requestBody = JSON.toJSONString(ReqResUtils.filterReqArgs(joinPoint.getArgs()));
        String resultStr = buildResultString(result, errorMsg);

        // 获取用户信息
        String userName = loginUser.getUserName();
        String loginPhone = loginUser.getLoginPhone();
        Long tenantId = determineTenantId(joinPoint, oplog, method, loginUser);

        // 构建日志实体
        OpLogEntity logEntity = new OpLogEntity().setBusinessKey(businessKey).setUrl(url).setUseTime(executionTime).setIsSuccess(StringUtils.isEmpty(errorMsg))
            .setMethodName(method.getName()).setContent(content).setBusinessType(businessType).setChannel(channel).setRemark(remark).setReqBody(requestBody)
            .setResultStr(resultStr).setTraceId(MDCUtils.getTraceId()).setCreateUserName(userName).setLoginPhone(loginPhone).setTenantId(tenantId)
            .setParamName(paramName);
        logEntity.setId(IdUtils.getId());
        // 设置业务类型细分字段
        setBusinessTypeSegments(logEntity, businessTypeSegments);

        // 保存日志
        SpringUtil.getBean(OpLogEntityMapper.class).insert(logEntity);

      } catch (Exception e) {
        log.error("保存操作日志失败", e);
      }
    });
  }

  /**
   * 解析SpEL表达式
   *
   * @param joinPoint  连接点
   * @param expression SpEL表达式
   * @param method     目标方法
   * @return 解析后的字符串
   */
  private String parseSpelExpression(ProceedingJoinPoint joinPoint, String expression, Method method) {
    if (StringUtils.isEmpty(expression)) {
      return null;
    }
    try {
      return SpelUtils.parseKey(expression, method, joinPoint.getArgs());
    } catch (Exception e) {
      log.warn("解析SpEL表达式失败: {}", expression, e);
      return null;
    }
  }

  /**
   * 拆分业务类型为多个片段
   *
   * @param businessType 业务类型字符串
   * @return 拆分后的片段列表
   */
  private List<String> splitBusinessType(String businessType) {
    if (StringUtils.isBlank(businessType)) {
      return new ArrayList<>();
    }
    return Arrays.stream(businessType.split("\\.")).filter(StringUtils::isNotBlank).toList();
  }

  /**
   * 构建结果字符串
   *
   * @param result   方法执行结果
   * @param errorMsg 错误信息
   * @return 结果字符串
   */
  private String buildResultString(Object result, String errorMsg) {
    if (Objects.nonNull(result)) {
      return JSON.toJSONString(result);
    }
    return StringUtils.isNotBlank(errorMsg) ? "errorMsg: " + errorMsg : "no result";
  }

  /**
   * 确定租户ID
   *
   * @param joinPoint 连接点
   * @param oplog     日志注解
   * @param method    目标方法
   * @param loginUser 当前登录用户
   * @return 租户ID
   */
  private Long determineTenantId(ProceedingJoinPoint joinPoint, Oplog oplog, Method method, LoginUser loginUser) {

    String tenantIdField = oplog.reqTenantIdFieldName();
    if (StringUtils.isNotBlank(tenantIdField)) {
      String tenantIdStr = parseSpelExpression(joinPoint, tenantIdField, method);
      if (StringUtils.isNotBlank(tenantIdStr)) {
        return Long.parseLong(tenantIdStr);
      }
    }

    return Objects.nonNull(loginUser.getTenantId()) ? loginUser.getTenantId() : 0L;
  }

  /**
   * 设置业务类型细分字段
   *
   * @param logEntity 日志实体
   * @param segments  业务类型片段列表
   */
  private void setBusinessTypeSegments(OpLogEntity logEntity, List<String> segments) {
    if (CollUtil.isEmpty(segments)) {
      return;
    }

    // 最多设置5个细分字段
    IntStream.range(0, Math.min(5, segments.size())).forEach(index -> {
      try {
        Field field = FieldUtils.getField(logEntity, "businessType" + index);
        if (Objects.nonNull(field)) {
          ReflectUtil.setFieldValue(logEntity, field, segments.get(index));
        }
      } catch (Exception e) {
        log.warn("设置业务类型细分字段失败, index: {}", index, e);
      }
    });
  }
}
