package com.olivia.peanut.store.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olivia.sdk.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 商圈(StoreBusinessDistrict)表实体类
 *
 * @author admin
 * @since 2025-08-24 21:01:55
 */
@Accessors(chain = true)
@Getter
@Setter
//@SuppressWarnings("serial")
@TableName(value = "store_business_district", autoResultMap = true)
public class StoreBusinessDistrict extends BaseEntity<StoreBusinessDistrict> {

  /***
   *  品牌ID
   */
  @TableField(value = "brand_id")
  private Long brandId;
  /***
   *  编码
   */
  @TableField(value = "business_district_code")
  private String businessDistrictCode;
  /***
   *  名称
   */
  @TableField(value = "business_district_name")
  private String businessDistrictName;
  /***
   *  描述
   */
  @TableField(value = "business_district_desc")
  private String businessDistrictDesc;
  /***
   *  地址
   */
  @TableField(value = "business_district_address")
  private String businessDistrictAddress;
  /***
   *  国家编码
   */
  @TableField(value = "country_code")
  private String countryCode;
  /***
   *  城市编码
   */
  @TableField(value = "province_code")
  private String provinceCode;
  /***
   *  城市编码
   */
  @TableField(value = "city_code")
  private String cityCode;
  /***
   *  城市编码
   */
  @TableField(value = "area_code")
  private String areaCode;
  /***
   *  国家名称
   */
  @TableField(value = "country_name")
  private String countryName;
  /***
   *  省份名称
   */
  @TableField(value = "province_name")
  private String provinceName;
  /***
   *  城市名称
   */
  @TableField(value = "city_name")
  private String cityName;
  /***
   *  区县名称
   */
  @TableField(value = "area_name")
  private String areaName;
  /***
   *  半径
   */
  @TableField(value = "business_district_radius")
  private Long businessDistrictRadius;
  /***
   *  商圈级别ID
   */
  @TableField(value = "business_district_level_id")
  private Long businessDistrictLevelId;
  /***
   *  商圈类别ID
   */
  @TableField(value = "business_district_type_id")
  private Long businessDistrictTypeId;
  /***
   *  纬度
   */
  @TableField(value = "center_lat")
  private String centerLat;
  /***
   *  经度
   */
  @TableField(value = "center_lng")
  private String centerLng;

  @TableField(value = "location_geo", select = false, insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
  private Object locationGeo;
}

