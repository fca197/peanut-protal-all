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
public class ApsSchedulingDayConfigDto {

  private Long id;
  /***
   *  工厂ID
   */
  private Long factoryId;
  private String factoryName;
  /***
   *  工艺路径ID
   */
  private Long processId;
  private String processName;
  /***
   *  排程版本号
   */
  private String schedulingDayNo;
  /***
   *  排程版本名称
   */
  private String schedulingDayName;
  /***
   *  是否默认 0 否,1 是
   */
  private Boolean isDefault;

  private List<ApsSchedulingDayConfigItemDto> schedulingDayConfigItemDtoList;
}
