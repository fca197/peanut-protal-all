package com.olivia.peanut.aps.utils.forecast.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class OnlySkuInfo {

  private String groupName;
  private String sku;
  private Double originalProportion;
  private Double resProportion;
}
