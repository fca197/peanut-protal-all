package com.olivia.peanut.aps.utils.process.entity;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

public class ProductProcessDto {

  @Data
  public static class DayPower {

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private double maxPower;
  }

  @Data
  public static class OrderMachine {

    private int machineId;
    private int goodsStatusId;
    private int useTime; // 使用时间（秒）
    private int produceProcessId;
    private String goodsStatusName;
    private int machineWorkstationId;
    private String machineWorkstationName;
    private String machineName;
    private double minPower;
    private double maxPower;
    private int factoryId;
    private int sortIndex;
  }

  @Data
  public static class ProcessResult {

    private int orderId;
    private int machineId;
    private LocalDateTime beginLocalDateTime;
    private LocalDateTime endLocalDateTime;
    private int startSecond; // 相对于开始时间的秒数
    private int endSecond;   // 相对于开始时间的秒数
    private int statusId;
    private int useTime;
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
  }


  @Data
  public static class ProduceOrder {

    private int orderId;
    private int urgentLevel; // 紧急程度，越大越优先
    private List<OrderMachine> orderMachineList;
  }

  @Data
  public static class ProductionInput {

    private List<DayPower> dayPowerList;
    private LocalDateTime produceStartTime;
    private List<ProduceOrder> produceOrderList;
  }

  @Data
  public static class ProductionOutput {

    private int maxUseSecond; // 总生产时间（秒）
    private List<ProcessResult> processComputeOrderRes;
  }

}
