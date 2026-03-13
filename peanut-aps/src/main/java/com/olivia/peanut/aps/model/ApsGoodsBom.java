package com.olivia.peanut.aps.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.olivia.sdk.utils.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * APS 商品 BOM 实体类
 * 用于表示商品的物料清单（Bill of Materials）信息，包括零件编号、名称、用量、成本等
 *
 * @author peanut
 * @since 2024-04-03 22:28:56
 */
@Accessors(chain = true)
@Getter
@Setter
////@SuppressWarnings("serial")
@TableName("aps_goods_bom")
public class ApsGoodsBom extends BaseEntity<ApsGoodsBom> {

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
   * BOM 名称
   */
  private String bomName;

  /**
   * 用量
   */
  private BigDecimal bomUsage;

  /**
   * 规格
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