package com.olivia.peanut.aps.utils.constrained.model.sub.constrained;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 字段配置
 */
@Setter
@Getter
@Accessors(chain = true)
public class FieldConfig {

  /***
   *属性名称
   */
  private String fieldName;
  /**
   * 显示名称
   */
  private String showName;
  /***
   *
   */
  private List<Operator> operator;

  /***
   * 值类型
   */
  private ValueType valueType;

  /****
   * 值
   */
  private List<ValueItem> valueItemList;
}
