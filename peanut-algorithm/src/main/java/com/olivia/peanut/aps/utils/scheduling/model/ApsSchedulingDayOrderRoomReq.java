package com.olivia.peanut.aps.utils.scheduling.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApsSchedulingDayOrderRoomReq {

  private Long schedulingDayId;
  private List<ApsSchedulingIssueItemDto> issueItemList;

  private ApsSchedulingDayConfigDto schedulingDayConfigDto;
}
