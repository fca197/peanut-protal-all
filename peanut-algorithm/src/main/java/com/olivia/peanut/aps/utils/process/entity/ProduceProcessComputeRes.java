package com.olivia.peanut.aps.utils.process.entity;

import cn.hutool.core.collection.CollUtil;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ProduceProcessComputeRes {

  private long maxUseSecond;
  private List<ProduceProcessComputeOrderRes> processComputeOrderRes;

  public List<ProduceProcessComputeOrderRes> getProcessComputeOrderRes() {
    return CollUtil.isEmpty(processComputeOrderRes) ? List.of() : processComputeOrderRes;
  }
}
