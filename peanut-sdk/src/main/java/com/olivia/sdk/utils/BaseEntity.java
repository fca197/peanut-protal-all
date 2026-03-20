package com.olivia.sdk.utils;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 基础实体类，封装所有实体的通用字段和功能 继承MyBatis-Plus的Model，支持ActiveRecord模式
 *
 * @param <T> 实体类泛型，需继承当前BaseEntity
 */
@Setter
@Getter
@Accessors(chain = true)
public class BaseEntity<T extends BaseEntity<T>> extends Model<T> {

  /**
   * 主键ID，采用雪花算法自动生成
   */
  @TableId(type = IdType.ASSIGN_ID, value = "id")
  private Long id;

  /**
   * 逻辑删除标识：0-未删除，1-已删除 由MyBatis-Plus自动维护，查询时会过滤已删除数据
   */
  @TableLogic(value = "0",delval = "UNIX_TIMESTAMP()")
  @TableField(fill = FieldFill.INSERT, value = "is_delete")
  @JsonIgnore
  private Integer isDelete = 0;


  /***
   * 创建用户ID
   */
  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER, value = "create_by")
  private Long createBy;

  /***
   * 创建用户姓名
   */
  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER, value = "create_user_name")
  private String createUserName;

  /**
   * 创建时间 插入时自动填充，不可更新
   */
  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER, value = "create_time")
  private LocalDateTime createTime;

  /**
   * 修改人姓名
   */
  @TableField(fill = FieldFill.INSERT_UPDATE, updateStrategy = FieldStrategy.NEVER, value = "update_user_name")
  private String updateUserName;

  /**
   * 更新时间 插入和更新时自动填充
   */
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_time")
  private LocalDateTime updateTime;

  /**
   * 更新人ID 插入和更新时自动填充
   */
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "update_by")
  private Long updateBy;

  /**
   * 分布式追踪ID，用于链路追踪 插入和更新时自动填充
   */
  @TableField(fill = FieldFill.INSERT_UPDATE, value = "trace_id")
  private String traceId;

  /**
   * 租户ID，用于多租户隔离 插入时自动填充，不可更新
   */
  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER, value = "tenant_id")
  private Long tenantId;


  /**
   * 乐观锁版本号，用于并发控制 默认为1，更新时自动递增
   */
  @Version
  @TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER, value = "version_num")
  private Integer versionNum;

  /**
   * 行总计，非数据库字段
   */
  @TableField(exist = false)
  private Long rowTotal;

  /**
   * 总计日期，非数据库字段
   */
  @TableField(exist = false)
  private LocalDate totalDate;


  /**
   * 重写toString方法，使用JSON格式输出
   *
   * @return 实体的JSON字符串表示
   */
  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
