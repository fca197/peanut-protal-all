package com.olivia.peanut.portal.api.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 基础实体 DTO 类 包含通用的实体字段，如 id、租户信息、创建和更新信息等
 */
@Setter
@Getter
@Accessors(chain = true)
public class BaseEntityDto {


  /**
   * 主键 ID
   */
  @ExcelIgnore
  private Long id;
  /**
   * 所属租户 ID
   */
  @ExcelIgnore
  private Long tenantId;

  /**
   * 所属租户名称
   */
  @ExcelIgnore
  @ExcelProperty("所属租户Name")
  private String tenantName;

  /**
   * 创建时间
   */
  @ExcelProperty("创建时间")
  private LocalDateTime createTime;

  /**
   * 创建人 ID
   */
  @ExcelIgnore
  private Long createBy;

  /**
   * 更新人 ID
   */
  @ExcelIgnore
  private Long updateBy;

  /**
   * 更新时间
   */
  @ExcelProperty("更新时间")
  private LocalDateTime updateTime;

  /**
   * 版本号（用于乐观锁）
   */
  @ExcelIgnore
  private Integer versionNum;

  /**
   * 创建人名称
   */
  @ExcelProperty("创建人")
  private String createUserName;

  /**
   * 更新人名称
   */
  @ExcelProperty("更新人")
  private String updateUserName;


  /**
   * 行索引（用于Excel导入导出）
   */
  @ExcelIgnore
  private Integer rowIndex;

}