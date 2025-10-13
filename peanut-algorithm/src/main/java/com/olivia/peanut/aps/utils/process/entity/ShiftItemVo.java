package com.olivia.peanut.aps.utils.process.entity;

import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ShiftItemVo extends BaseEntityDto {

  private Long shiftId;
  private LocalTime beginTime;
  private LocalTime endTime;
  private Long factoryId;
}
