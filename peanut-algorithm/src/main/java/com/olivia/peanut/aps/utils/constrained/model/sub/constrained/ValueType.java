package com.olivia.peanut.aps.utils.constrained.model.sub.constrained;

import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 * 值类型枚举
 */
@Getter
@AllArgsConstructor
public enum ValueType {
  DATE("日期"),

  TEXT("文本框"),
  SELECT("下拉框");
  final String name;

}
