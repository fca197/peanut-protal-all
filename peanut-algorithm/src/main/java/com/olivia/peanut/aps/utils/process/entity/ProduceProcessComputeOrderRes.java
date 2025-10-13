package com.olivia.peanut.aps.utils.process.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ProduceProcessComputeOrderRes {

  private Long orderWorkId;
  private Long orderId;
  private Long machineId;
  private LocalDateTime beginLocalDateTime;
  private LocalDateTime endLocalDateTime;
  private Long startSecond;
  private Long endSecond;
  private Long statusId;
  private Long useTime;

  private int goodsStatusId;
  private int produceProcessId;
  private String goodsStatusName;
  private int machineWorkstationId;
  private String machineWorkstationName;
  private String machineName;
  private double minPower;
  private double maxPower;
  private int factoryId;
  private int sortIndex;
//  private List<ProduceProcessComputeOrderMachineTimeRes> orderMachineTimeResList;
}
