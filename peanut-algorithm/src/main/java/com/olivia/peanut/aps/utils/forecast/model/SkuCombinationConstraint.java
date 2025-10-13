package com.olivia.peanut.aps.utils.forecast.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

// 特定SKU组合约束类

@Getter
@Setter
public class SkuCombinationConstraint {

  private String combination; // 组合字符串，格式如 颜色=红,轮毂=19
  private double ratio;       // 目标比例

  public SkuCombinationConstraint() {

  }

  public SkuCombinationConstraint(String combination, double ratio) {
    this.combination = combination;
    this.ratio = ratio;
  }

  public String getCombination() {
    return combination;
  }

  public double getRatio() {
    return ratio;
  }

  // 将组合字符串解析为Map
  public Map<String, String> getCombinationMap() {
    Map<String, String> map = new HashMap<>();
    String[] parts = combination.split(",");
    for (String part : parts) {
      String[] kv = part.split(":");
      if (kv.length == 2) {
        map.put(kv[0].trim(), kv[1].trim());
      }
    }
    return map;
  }
}

