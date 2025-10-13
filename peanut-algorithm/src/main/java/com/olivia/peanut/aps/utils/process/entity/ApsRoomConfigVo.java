package com.olivia.peanut.aps.utils.process.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApsRoomConfigVo {

  private Long roomId;
  private Long sectionId;
  private Long stationId;
  private Long statusId;
  private Integer executeTime;
  private Long factoryId;
  private Integer sortIndex;
}
