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
public class ApsSchedulingDayConfigVersionDetailDto {

  private Long schedulingDayId;
  /***
   *  配置类型 sale,part,bom
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

  private Long roomId;
  private Long statusId;
  private Integer sortIndex;
  /***
   *  订单ID
   */
  private Long orderId;
  /***
   *  订单编号
   */
  private String orderNo;
  /***
   *  是否匹配 0 否,1 是
   */
  private Boolean isMatch;
  /***
   *  循环次数
   */
  private Integer loopIndex;
  /***
   *  是否满足 0 否,1 是
   */
  private Boolean loopEnough;
}
