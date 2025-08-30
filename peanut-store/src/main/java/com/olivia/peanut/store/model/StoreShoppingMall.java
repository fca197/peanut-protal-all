package com.olivia.peanut.store.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.olivia.sdk.mybatis.type.impl.ListStringTypeHandler;
import com.olivia.sdk.utils.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 门店 商场(StoreShoppingMall)表实体类
 *
 * @author admin
 * @since 2025-08-29 15:54:25
 */
@Accessors(chain = true)
@Getter
@Setter
//@SuppressWarnings("serial")
@TableName(value = "store_shopping_mall")
public class StoreShoppingMall extends BaseEntity<StoreShoppingMall> {

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
   *  所属最新商区
   */
  @TableField(value = "belong_district_id")
  private Long belongDistrictId;
  /***
   *  所属商区 List<Long>
   */
  @TableField(value = "belong_district_id_list", typeHandler = ListStringTypeHandler.class)
  private List<String> belongDistrictIdList;
  /***
   *  地址
   */
  @TableField(value = "shopping_mall_address")
  private String shoppingMallAddress;
  /***
   *  经度
   */
  @TableField(value = "shopping_mall_location_lng")
  private BigDecimal shoppingMallLocationLng;
  /***
   *  纬度
   */
  @TableField(value = "shopping_mall_location_lat")
  private BigDecimal shoppingMallLocationLat;
  /***
   *  商场名称
   */
  @TableField(value = "shopping_mall_name")
  private String shoppingMallName;
  /***
   *  商场别称
   */
  @TableField(value = "business_alias")
  private String businessAlias;
  /***
   *  营业开始时间
   */
  @TableField(value = "business_open_time_today_open")
  private LocalTime businessOpenTimeTodayOpen;
  /***
   *  营业结束时间
   */
  @TableField(value = "business_open_time_today_close")
  private LocalTime businessOpenTimeTodayClose;
  /***
   *  评分
   */
  @TableField(value = "business_rating")
  private BigDecimal businessRating;
  /***
   *  标签List<String>
   */
  @TableField(value = "business_tag", typeHandler = ListStringTypeHandler.class)
  private List<String> businessTag;
  /***
   *  联系电话可多个List<String>
   */
  @TableField(value = "business_tel", typeHandler = ListStringTypeHandler.class)
  private List<String> businessTel;
  /***
   *  入口经纬度经度
   */
  @TableField(value = "enter_location_lng")
  private BigDecimal enterLocationLng;
  /***
   *  入口经纬度纬度
   */
  @TableField(value = "enter_location_lat")
  private BigDecimal enterLocationLat;
  /***
   *  图片： List<String>
   */
  @TableField(value = "photos", typeHandler = ListStringTypeHandler.class)
  private List<String> photos;

}

