package com.olivia.peanut.aps.model;


import com.baomidou.mybatisplus.annotation.TableName;
import com.olivia.sdk.utils.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * BOM 清单(ApsBom)表实体类
 * 用于表示物料清单（Bill of Materials）的基本信息，包括编码、名称、成本、库存、供应方式等
 *
 * @author peanut
 * @since 2024-06-06 11:27:34
 */
@Accessors(chain = true)
@Getter
@Setter
////@SuppressWarnings("serial")
@TableName("aps_bom")
public class ApsBom extends BaseEntity<ApsBom> {

  /**
   * 集团ID
   */
  private Long groupId;

  /**
   * BOM 编码
   */
  private String bomCode;

  /**
   * BOM 名称
   */
  private String bomName;

  /**
   * 成本价
   */
  private BigDecimal bomCostPrice;

  /**
   * 成本价单位
   */
  private String bomCostPriceUnit;

  /**
   * 库存数量
   */
  private BigDecimal bomInventory;

  /**
   * 供应方式，make (自制) 或 buy (外购)
   */
  private String supplyMode;

  /**
   * 使用规格
   */
  private String useUnit;

  /**
   * 规格描述，例如：100个*6
   */
  private String bomUnit;

  /**
   * 生产工艺ID
   */
  private Long produceProcessId;

  /**
   * BOM供应商ID
   */
  private Long apsBomSupplierId;

  /**
   * 交付周期（天）
   */
  private Integer deliveryCycleDay;

}

