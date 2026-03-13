package com.olivia.peanut.aps.utils.bom.model;

import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * APS 商品 BOM（物料清单）视图对象
 * 用于表示商品的物料清单信息，包括物料编号、名称、用量、成本等
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApsGoodsBomVo extends BaseEntityDto {

  /**
   * 商品ID
   */
  private Long goodsId;

  /**
   * BOM ID
   */
  private Long bomId;

  /**
   * 零件编号
   */
  private String bomCode;

  /**
   * 名称
   */
  private String bomName;

  /**
   * 用量
   */
  private BigDecimal bomUsage;

  /**
   * 单位
   */
  private String bomUnit;

  /**
   * 成本价
   */
  private BigDecimal bomCostPrice;

  /**
   * 成本价单位
   */
  private String bomCostPriceUnit;

  /**
   * 使用工位
   */
  private Long bomUseWorkStation;

  /**
   * 使用表达式：工程值，格式为所有工序的逻辑组合，例如：(AA001&&AC002)&&(AB001||AB002)
   */
  private String bomUseExpression;

  /**
   * 工厂ID
   */
  private Long factoryId;

  /**
   * 是否关注
   */
  private Boolean isFollow;
}
