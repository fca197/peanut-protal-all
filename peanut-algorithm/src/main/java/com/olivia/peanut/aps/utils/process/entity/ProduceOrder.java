package com.olivia.peanut.aps.utils.process.entity;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
public class ProduceOrder {

  private Long orderId;
  /***
   * 越大越晋级
   */

//  private Integer urgencyLevel;
  private Integer urgencyLevel;
  private List<ProduceOrderMachine> orderMachineList;


}
