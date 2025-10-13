package com.olivia.peanut.aps.utils.process.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProduceProcessComputeReq {

  private List<DayPower> dayPowerList;
  private LocalDateTime produceStartTime;
  private List<ProduceOrder> produceOrderList;
}
