package com.olivia.peanut.aps.utils.scheduling.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApsSchedulingDayConfigItemDto {

  private Long id;
  /***
   *  排程版本ID
   */
  private Long schedulingDayId;
  /***
   *  工艺路径ID
   */
  private Long processId;
  /***
   *  车间ID
   */
  private Long roomId;
  /***
   *  状态ID
   */
  private Long statusId;
  /***
   *  配置类型 sale,part,bom ,sleep
   */
  private String configBizType;
  /***
   *  配置业务ID
   */
  private Long configBizId;
  /***
   *  配置业务名称
   */
  private String configBizName;
  /***
   *  配置业务数量
   */
  private Long configBizNum;
  /***
   *  配置业务耗时(秒)
   */
  private Long configBizTime;
  /***
   *  是否默认 0 否,1 是
   */
  private Boolean isDefault;


}
