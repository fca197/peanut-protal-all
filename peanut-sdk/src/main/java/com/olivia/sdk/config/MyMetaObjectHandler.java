package com.olivia.sdk.config;

import static com.olivia.sdk.utils.MDCUtils.MDC_KEY_TRACE_ID;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.olivia.sdk.filter.LoginUser;
import com.olivia.sdk.filter.LoginUserContext;
import com.olivia.sdk.utils.BaseEntity;
import com.olivia.sdk.utils.BaseEntityUtils;
import com.olivia.sdk.utils.MDCUtils;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * MyBatis 元对象处理器 负责自动填充实体类的公共字段，如创建时间、更新时间、创建人、更新人等
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  /**
   * 插入操作时自动填充字段 填充创建时间、删除标记、版本号、租户ID、创建人等信息
   *
   * @param metaObject 元对象
   */
  @Override
  public void insertFill(MetaObject metaObject) {
    LocalDateTime now = LocalDateTime.now();
    LoginUser loginUser = LoginUserContext.getLoginUser();

    // 确保登录用户不为null，避免空指针异常
    if (loginUser == null) {
      log.warn("插入操作时未获取到登录用户信息，使用默认值填充");
    }
    if (metaObject.getOriginalObject() instanceof BaseEntity<?> m) {
      BaseEntityUtils.clearCreateInfo(m);
    }

    // 填充创建时间
    this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
    this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
    // 填充删除标记（0表示未删除）
    this.strictInsertFill(metaObject, "isDelete", Integer.class, 0);

    // 填充版本号（乐观锁）
    this.strictInsertFill(metaObject, "versionNum", Integer.class, 0);

    // 填充租户ID
    Optional.ofNullable(loginUser).map(LoginUser::getTenantId).ifPresent(tenantId -> {
      this.strictInsertFill(metaObject, "tenantId", Long.class, tenantId);
    });
    // 填充创建人
    Optional.ofNullable(loginUser).map(LoginUser::getId).ifPresent(userId -> {
      this.strictInsertFill(metaObject, "createBy", Long.class, userId);
      this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);

    });
    Optional.ofNullable(loginUser).map(LoginUser::getUserName).ifPresent(un -> {
      this.strictInsertFill(metaObject, "createUserName", String.class, un);
      this.strictUpdateFill(metaObject, "updateUserName", String.class, un);
    });

    // 填充追踪ID
    setTraceId(metaObject);
  }

  /**
   * 更新操作时自动填充字段 填充更新时间、更新人等信息
   *
   * @param metaObject 元对象
   */
  @Override
  public void updateFill(MetaObject metaObject) {
    // 填充更新时间
    setUpdateUserAdnTime(metaObject);
    LocalDateTime now = LocalDateTime.now();
    this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
    // 填充追踪ID
    setTraceId(metaObject);
  }

  private void setUpdateUserAdnTime(MetaObject metaObject) {
    if (metaObject.getOriginalObject() instanceof BaseEntity<?> m) {
      BaseEntityUtils.clearAllInfo(m);
    }
    // 填充更新人
    LoginUser loginUser = LoginUserContext.getLoginUser();
    Optional.ofNullable(loginUser).map(LoginUser::getId).ifPresent(userId -> {
      this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
    });
    Optional.ofNullable(loginUser).map(LoginUser::getUserName).ifPresent(loginUserName -> {
      this.strictUpdateFill(metaObject, "updateUserName", String.class, loginUserName);
    });

  }

  /**
   * 填充追踪ID（用于分布式追踪）
   *
   * @param metaObject 元对象
   */
  private void setTraceId(MetaObject metaObject) {
    String traceId = MDCUtils.getTraceId();
    if (traceId != null) {
      this.strictInsertFill(metaObject, MDC_KEY_TRACE_ID, String.class, traceId);
    } else {
      log.trace("未获取到追踪ID，不填充追踪字段");
    }
  }
}
