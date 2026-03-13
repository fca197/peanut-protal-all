package com.olivia.peanut.aps.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * APS 排程日配置版本产品类型枚举
 * 定义了生产过程中的不同路径类型，包括制造路径和工艺路径
 */
@Getter
@AllArgsConstructor
public enum ApsSchedulingDayConfigVersionProductType {
  /**
   * 制造路径 - 表示产品的制造过程路径
   */
  MAKE("制造路径"),
  /**
   * 工艺路径 - 表示产品的工艺处理路径
   */
  PROCESS("工艺路径");

  /**
   * 描述信息
   */
  private final String desc;
}