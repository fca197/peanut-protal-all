package com.olivia.sdk.service.impl;

import static com.olivia.sdk.utils.FieldUtils.getField;
import static com.olivia.sdk.utils.Str.tenantId;

import cn.hutool.core.util.ReflectUtil;
import com.olivia.sdk.enums.DataPermissionRetType;
import com.olivia.sdk.exception.NotenantException;
import com.olivia.sdk.exception.RunException;
import com.olivia.sdk.filter.LoginUser;
import com.olivia.sdk.filter.LoginUserContext;
import com.olivia.sdk.service.DataPermissionService;
import com.olivia.sdk.utils.Str;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 用户租户数据权限服务实现类 基于租户ID实现数据权限过滤，确保用户只能访问其所属租户的数据
 */
@Slf4j
@Component
public class UserTenantDataPermissionServiceImpl implements DataPermissionService {

  /**
   * 权限类型：租户ID
   */
  private static final String PERMISSION_TYPE = Str.TENANT_ID;

  @Override
  public String getType() {
    return PERMISSION_TYPE;
  }

  @Override
  public DataPermissionRetType getRetType() {
    return DataPermissionRetType.NUMBER;
  }

  @Override
  public List<Serializable> filterValueList() {
    // 获取当前登录用户
    LoginUser loginUser = LoginUserContext.getLoginUser();
    if (loginUser == null) {
      log.error("获取登录用户信息失败，无法获取租户ID");
      throw new RunException("获取登录用户信息失败，无法获取租户ID");
    }

    // 获取租户ID字段
    Field tenantIdField = getField(loginUser.getClass(), tenantId);
    if (tenantIdField == null) {
      String errorMsg = String.format("登录用户类 %s 中未找到租户ID字段: %s", loginUser.getClass().getName(), tenantId);
      log.error(errorMsg);
      throw new RunException(errorMsg);
    }

    // 获取租户ID值
    Object tenantIdValue = ReflectUtil.getFieldValue(loginUser, tenantIdField);
    if (Objects.isNull(tenantIdValue)) {
      log.error("登录用户的租户ID为空，用户信息: {}", loginUser);
      throw new NotenantException("租户ID获取异常，租户ID为空");
    }

    // 验证租户ID类型
    if (!(tenantIdValue instanceof Long)) {
      String errorMsg = String.format("租户ID字段类型错误，期望Long，实际: %s", tenantIdValue.getClass().getSimpleName());
      log.error(errorMsg);
      throw new RunException(errorMsg);
    }

    // 返回租户ID列表（单租户场景）
    return Collections.singletonList((Serializable) tenantIdValue);
  }
}
