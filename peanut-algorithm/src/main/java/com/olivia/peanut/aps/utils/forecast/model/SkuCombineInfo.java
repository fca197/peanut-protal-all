package com.olivia.peanut.aps.utils.forecast.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 返回结果数量
 */
@Setter
@Getter
@Accessors(chain = true)
public class SkuCombineInfo {

  /****
   *  配置信息
   */
  private String key;
  /***
   * 数量
   */
  private Long count;
}
