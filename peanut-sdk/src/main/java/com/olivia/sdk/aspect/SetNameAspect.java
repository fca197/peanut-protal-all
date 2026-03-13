package com.olivia.sdk.aspect;

import static com.olivia.sdk.utils.FieldUtils.getField;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.olivia.sdk.ann.SetUserName;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 自动设置用户名切面，用于为标注了{@link SetUserName}的方法自动填充创建者和更新者用户名。
 * <p>
 * 实现逻辑： 1. 拦截标注了注解的方法，获取方法参数 2. 提取参数中的用户ID（创建者ID和更新者ID） 3. 批量查询用户名称 4. 反射设置对象中的用户名字段
 */
@Aspect
@Component
@Slf4j
public class SetNameAspect {

  /**
   * 定义切入点：匹配所有标注了{@link SetUserName}注解的方法
   */
  @Pointcut("@annotation(com.olivia.sdk.ann.SetUserName)")
  public void setUserNamePointcut() {
    // 切入点定义，无具体实现
  }

  /**
   * 前置通知：在方法执行前自动填充用户名
   *
   * @param joinPoint 连接点对象，包含目标方法信息
   */
  @Before("setUserNamePointcut()")
  public void doBefore(JoinPoint joinPoint) {
    Object[] methodArgs = joinPoint.getArgs();
    if (ArrayUtil.isEmpty(methodArgs)) {
      log.debug("方法参数为空，无需设置用户名");
      return;
    }

    // 获取注解信息和第一个参数对象
    Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
    SetUserName annotation = method.getAnnotation(SetUserName.class);
    Object firstArg = methodArgs[0];

    // 处理集合类型参数
    if (firstArg instanceof Iterable<?> dataList) {
      processIterableData(dataList, annotation);
    } else if (annotation.isList()) {
      log.warn("注解指定为列表类型，但实际参数类型为: {}", firstArg.getClass().getSimpleName());
    }
  }

  /**
   * 处理可迭代的数据集合，批量设置用户名
   *
   * @param dataList   数据集合
   * @param annotation 注解配置
   */
  private void processIterableData(Iterable<?> dataList, SetUserName annotation) {
    // 收集所有需要查询的用户ID
    Set<Long> userIds = collectUserIds(dataList, annotation);
    if (CollUtil.isEmpty(userIds)) {
      log.debug("未收集到有效用户ID，无需设置用户名");
      return;
    }

    // 批量查询用户名映射关系
    Map<Long, String> userIdToNameMap = queryUserNames(userIds);
    if (CollUtil.isEmpty(userIdToNameMap)) {
      log.warn("未查询到任何用户信息，用户ID列表: {}", userIds);
      return;
    }

    // 为每个对象设置用户名
    setUserNamesToObjects(dataList, annotation, userIdToNameMap);
  }

  /**
   * 从数据集合中收集所有用户ID
   *
   * @param dataList   数据集合
   * @param annotation 注解配置
   * @return 用户ID集合
   */
  private Set<Long> collectUserIds(Iterable<?> dataList, SetUserName annotation) {
    Set<Long> userIds = new HashSet<>();
    String createByField = annotation.createBy();
    String updateByField = annotation.updateBy();

    for (Object data : dataList) {
      // 获取创建者ID
      Long createId = getFieldValueAsLong(data, createByField);
      if (createId != null) {
        userIds.add(createId);
      }

      // 获取更新者ID
      Long updateId = getFieldValueAsLong(data, updateByField);
      if (updateId != null) {
        userIds.add(updateId);
      }
    }

    return userIds;
  }

  /**
   * 批量查询用户名称
   *
   * @param userIds 用户ID集合
   * @return 用户ID到用户名的映射
   */
  private Map<Long, String> queryUserNames(Set<Long> userIds) {
    try {
      IService<Object> userService = SpringUtil.getBean("loginAccountServiceImpl");
      List<Object> userList = userService.listByIds(userIds);

      return userList.stream().collect(
          Collectors.toMap(user -> (Long) ReflectUtil.getFieldValue(user, "id"), user -> (String) ReflectUtil.getFieldValue(user, getField(user, "userName")),
              (existing, replacement) -> existing // 处理ID重复的情况
          ));
    } catch (Exception e) {
      log.error("查询用户名称失败", e);
      return Collections.emptyMap();
    }
  }

  /**
   * 为对象集合设置用户名
   *
   * @param dataList        数据集合
   * @param annotation      注解配置
   * @param userIdToNameMap 用户ID到用户名的映射
   */
  private void setUserNamesToObjects(Iterable<?> dataList, SetUserName annotation, Map<Long, String> userIdToNameMap) {
    String createByField = annotation.createBy();
    String createNameField = annotation.createUserName();
    String updateByField = annotation.updateBy();
    String updateNameField = annotation.updateUserName();

    for (Object data : dataList) {
      // 设置创建者用户名
      setUserNameForField(data, createByField, createNameField, userIdToNameMap);

      // 设置更新者用户名
      setUserNameForField(data, updateByField, updateNameField, userIdToNameMap);
    }
  }

  /**
   * 为特定字段设置用户名
   *
   * @param data            数据对象
   * @param idField         ID字段名
   * @param nameField       目标用户名字段名
   * @param userIdToNameMap 用户ID到用户名的映射
   */
  private void setUserNameForField(Object data, String idField, String nameField, Map<Long, String> userIdToNameMap) {
    Long userId = getFieldValueAsLong(data, idField);
    if (userId != null) {
      String userName = userIdToNameMap.get(userId);
      if (userName != null) {
        ReflectUtil.setFieldValue(data, getField(data, nameField), userName);
      } else if (log.isDebugEnabled()) {
        log.debug("未找到用户ID[{}]对应的用户名", userId);
      }
    }
  }

  /**
   * 获取对象字段值并转换为Long类型
   *
   * @param obj       数据对象
   * @param fieldName 字段名
   * @return 字段值（Long类型），null表示获取失败或值为null
   */
  private Long getFieldValueAsLong(Object obj, String fieldName) {
    try {
      Field field = getField(obj, fieldName);
      Object value = ReflectUtil.getFieldValue(obj, field);
      return value instanceof Number number ? number.longValue() : null;
    } catch (Exception e) {
      log.warn("获取字段[{}]的值失败，对象类型: {}", fieldName, obj.getClass().getSimpleName(), e);
      return null;
    }
  }
}
