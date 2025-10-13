package com.olivia.peanut.aps.utils.constrained.model.sub.constrained;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 约束字段
 */
@Setter
@Getter
@Accessors(chain = true)
public class ConstrainedField {

  /***
   * 约束名称
   */
  private String name;
  /***
   * 约束编码
   */
  private String code;
  /***
   * 约束配置列表
   */
  private List<FieldConfig> values;
}
