package com.olivia.peanut.aps.utils.process.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ProduceOrderMachine {

  private Long orderId;
  private Long orderWorkId;
  private Long orderStepIndex;
  private Long machineId;
  private Long goodsStatusId;
  private Long useTime;


  /***
   *  生产路径 Id aps_produce_process
   */
  private Long produceProcessId;

  private String goodsStatusName;


  /***
   *  工作站id
   */

  private Long machineWorkstationId;
  /***
   *
   */
  private String machineWorkstationName;


  private String machineName;

  /***
   *  最大功率
   */
  private Integer maxPower;

  /***
   *  工厂ID
   */

  private Long factoryId;
  /***
   *  排序索引
   */

  private Long sortIndex;

}
