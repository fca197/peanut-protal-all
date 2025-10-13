package com.olivia.peanut.aps.utils.constrained.model.sub;

import cn.hutool.core.util.NumberUtil;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 过滤配置
 */
@Setter
@Getter
@Accessors(chain = true)
public class Filter {

  /****
   * 字段名称
   */
  private String fieldName;
  /***
   * 显示名称
   */
  private String showName;
  private OperatorEnum operator;
  private List<String> valueList;

  private Double min;
  private Double max;

  public Filter setValueList(List<String> valueList) {
    this.valueList = valueList;
    this.min = NumberUtil.parseDouble(valueList.getFirst(), Double.MIN_VALUE);
    if (valueList.size() > 1) {
      this.max = NumberUtil.parseDouble(valueList.get(1), Double.MAX_VALUE);
    } else {
      this.max = Double.MAX_VALUE;
    }
    return this;
  }
}
