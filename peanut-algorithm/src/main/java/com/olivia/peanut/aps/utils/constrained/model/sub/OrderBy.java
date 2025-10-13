package com.olivia.peanut.aps.utils.constrained.model.sub;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 排序信息
 */
@Setter
@Getter
@Accessors(chain = true)
public class OrderBy {

  /****
   * 排序字段
   */
  private String fieldName;
  /***
   * 排序类型
   */
  private OrderByEnum orderType;

}
