package com.olivia.peanut.aps.utils.scheduling.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

/***
 *
 */

@Getter
@AllArgsConstructor
public enum ApsSchedulingDayConfigItemConfigBizTypeEnum {


  sale("sale", "销售"),//
  project("project", "工程"), //
  bom("bom", "零件"), //
  sleep("sleep", "休眠"), //
  def("def", "default");

  static Map<String, ApsSchedulingDayConfigItemConfigBizTypeEnum> VALUES_MAP = new HashMap<>();

  static {
    for (ApsSchedulingDayConfigItemConfigBizTypeEnum bizTypeEnum : ApsSchedulingDayConfigItemConfigBizTypeEnum.values()) {
      VALUES_MAP.put(bizTypeEnum.getCode(), bizTypeEnum);
    }
  }

  private final String code;
  private final String desc;

  public static ApsSchedulingDayConfigItemConfigBizTypeEnum of(String configBizType) {

    ApsSchedulingDayConfigItemConfigBizTypeEnum configItemConfigBizTypeEnum = VALUES_MAP.get(configBizType);
    Optional.ofNullable(configItemConfigBizTypeEnum).orElseThrow((() -> new RuntimeException("配置方式不存在,期望值:" + configBizType)));
    return configItemConfigBizTypeEnum;
  }
}
