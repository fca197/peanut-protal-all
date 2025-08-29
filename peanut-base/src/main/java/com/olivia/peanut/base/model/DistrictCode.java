package com.olivia.peanut.base.model;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.olivia.sdk.utils.BaseEntity;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * (DistrictCode)表实体类
 *
 * @author peanut
 * @since 2024-04-09 13:19:07
 */
@Accessors(chain = true)
@Getter
@Setter
////@SuppressWarnings("serial")
@TableName("t_district_code")
public class DistrictCode extends BaseEntity<DistrictCode> {

  @TableField(exist = false)
  public static final String MAX_PARENT_CODE = "000000";

  @TableField(value = "code")
  private String code;
  /***
   *  城市编码
   */
  @TableField(value = "city_code")
  private String cityCode;
  @TableField(value = "name")
  private String name;
  @TableField(value = "parent_code")
  private String parentCode;
  /***
   *  路径
   */
  @TableField(value = "path")
  private String path;
  @TableField(value = "level")
  private Integer level;



  // 子节点
  @TableField(exist = false)
  private List<DistrictCode> children;
}

