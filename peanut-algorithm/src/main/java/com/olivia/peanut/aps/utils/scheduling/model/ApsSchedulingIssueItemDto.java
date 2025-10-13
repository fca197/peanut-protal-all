package com.olivia.peanut.aps.utils.scheduling.model;

import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApsSchedulingIssueItemDto {


  private Long schedulingVersionId;
  /***
   *  当前日期
   */
  private String currentDay;
  /***
   *  订单ID
   */
  private Long orderId;
  private String orderNo;
  /***
   *  商品ID
   */
  private Long goodsId;
  /***
   *  生产序号
   */
  private Long numberIndex;
  /***
   *  工厂id
   */
  private Long factoryId;


  private List<Long> projectConfigIdList;
  private List<Long> saleConfigIdList;
  private List<Long> bomIdList;

  public List<Long> getBomIdList() {
    return Objects.nonNull(bomIdList) ? bomIdList : List.of();
  }

  public List<Long> getSaleConfigIdList() {
    return Objects.nonNull(saleConfigIdList) ? saleConfigIdList : List.of();
  }

  public List<Long> getProjectConfigIdList() {
    return Objects.nonNull(projectConfigIdList) ? projectConfigIdList : List.of();
  }

}
