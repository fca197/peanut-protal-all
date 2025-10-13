package com.olivia.peanut.aps.utils.forecast.model;


import com.olivia.sdk.utils.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 配置占比
 */
@Setter
@Getter
@Accessors(chain = true)
public class SaleItemConfig {

  private Long id;
  private Long parentId;
  private String saleCode;
  private double target;
  private Integer targetCount;

  private Boolean isMax;

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
