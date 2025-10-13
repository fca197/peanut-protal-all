package com.olivia.peanut.aps.utils.forecast.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class DivisionRes {

  private List<SkuCombineInfo> skuCombineInfoList;
  private List<OnlySkuInfo> onlySkuInfoList;
}
