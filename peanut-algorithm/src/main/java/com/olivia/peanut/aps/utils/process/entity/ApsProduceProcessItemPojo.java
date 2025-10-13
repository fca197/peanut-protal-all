package com.olivia.peanut.aps.utils.process.entity;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ApsProduceProcessItemPojo {

  private Long id;
  /***
   *  生产路径 Id aps_produce_process
   */
  private Long produceProcessId;
  /***
   *  机器ID
   */
  private Long machineId;

  /***
   * 状态
   */
  private Long statusId;

  /***
   *  耗时（秒）
   */
  private Long machineUseTimeSecond;

  private LocalDate statusLocalDate;
}
