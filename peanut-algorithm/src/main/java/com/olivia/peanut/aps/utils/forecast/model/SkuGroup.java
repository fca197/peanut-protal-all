package com.olivia.peanut.aps.utils.forecast.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
// SKU分组定义
public class SkuGroup {

  String groupName;
  Map<String, Double> ratios = new HashMap<>();

  public SkuGroup(String groupName, Map<String, Double> ratios) {
    this.groupName = groupName;
    this.ratios.putAll(ratios);
  }
}
