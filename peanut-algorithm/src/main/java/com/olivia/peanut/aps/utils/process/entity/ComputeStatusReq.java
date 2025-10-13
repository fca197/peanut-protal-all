package com.olivia.peanut.aps.utils.process.entity;

import com.olivia.sdk.utils.model.WeekInfo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ComputeStatusReq {

  private List<ApsProduceProcessItemPojo> apsProduceProcessItemPojoList;

  private Long currentGoodsStatusId;

  private List<WeekInfo> weekInfoList;

  private LocalDateTime beginLocalDateTime;
  /***
   * 当天剩余工作时长
   */
  private Long dayWorkLastSecond;
  /***
   * 工作时长
   */
  private Long dayWorkSecond;

  private Boolean isBegin;
}
