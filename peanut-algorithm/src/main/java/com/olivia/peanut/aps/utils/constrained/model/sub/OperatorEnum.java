package com.olivia.peanut.aps.utils.constrained.model.sub;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * 对比类型
 */
@Getter
@AllArgsConstructor
public enum OperatorEnum {
  EQ("等于", "EQ"),
  NE("不等于", "NE"),
  GT("大于", "GT"),
  GE("大于等于", "GE"),
  LT("小于", "LT"),
  LE("小于等于", "LE"),
  LIKE("模糊匹配", "LIKE"),
  IN("包含", "IN"),
  NOT_IN("不包含", "NOT_IN"),
  BETWEEN("区间", "BETWEEN"),
  NOT_BETWEEN("不区间", "NOT_BETWEEN"),
  IS_NULL("为空", "IS_NULL"),
  ;
  private final String name;
  private final String value;
}
