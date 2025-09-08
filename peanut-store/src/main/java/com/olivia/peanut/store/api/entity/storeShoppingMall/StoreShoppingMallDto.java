package com.olivia.peanut.store.api.entity.storeShoppingMall;

import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * 门店 商场(StoreShoppingMall)查询对象返回
 *
 * @author admin
 * @since 2025-08-29 15:54:26
 */
//@Accessors(chain=true)
@Getter
@Setter
//@SuppressWarnings("serial")
public class StoreShoppingMallDto extends BaseEntityDto {

  /***
   *  国家编码
   */
  @NotBlank(message = "国家编码不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String countryCode;
  /***
   *  城市编码
   */
  @NotBlank(message = "城市编码不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String provinceCode;
  /***
   *  城市编码
   */
  @NotBlank(message = "城市编码不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String cityCode;
  /***
   *  城市编码
   */
  @NotBlank(message = "城市编码不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String areaCode;
  /***
   *  国家名称
   */
  @NotBlank(message = "国家名称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String countryName;
  /***
   *  省份名称
   */
  @NotBlank(message = "省份名称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String provinceName;
  /***
   *  城市名称
   */
  @NotBlank(message = "城市名称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String cityName;
  /***
   *  区县名称
   */
  @NotBlank(message = "区县名称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String areaName;
  /***
   *  所属最新商区
   */
  @NotNull(message = "所属最新商区不能为空")
  private Long belongDistrictId;
  /***
   *  所属商区 List<Long>
   */
  @NotNull(message = "所属商区 List<Long>不能为空")
  private List<String> belongDistrictIdList;
  /***
   *  地址
   */
  @NotBlank(message = "地址不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String shoppingMallAddress;
  /***
   *  经度
   */
  @NotNull(message = "经度不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private BigDecimal shoppingMallLocationLng;
  /***
   *  纬度
   */
  @NotNull(message = "纬度不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private BigDecimal shoppingMallLocationLat;
  /***
   *  商场名称
   */
  @NotBlank(message = "商场名称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String shoppingMallName;
  /***
   *  商场别称
   */
  @NotBlank(message = "商场别称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String businessAlias;
  /***
   *  营业开始时间
   */
  @NotNull(message = "营业开始时间不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private LocalTime businessOpenTimeTodayOpen;
  /***
   *  营业结束时间
   */
  @NotNull(message = "营业结束时间不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private LocalTime businessOpenTimeTodayClose;
  /***
   *  评分
   */
  @NotNull(message = "评分不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private BigDecimal businessRating;
  /***
   *  标签List<String>
   */
  @NotNull(message = "标签List<String>不能为空")
  private List<String> businessTag;
  /***
   *  联系电话可多个List<String>
   */
  @NotNull(message = "联系电话可多个List<String>不能为空")
  private List<String> businessTel;
  /***
   *  入口经纬度经度
   */
  @NotNull(message = "入口经纬度经度不能为空")
  private BigDecimal enterLocationLng;
  /***
   *  入口经纬度纬度
   */
  @NotNull(message = "入口经纬度纬度不能为空")
  private BigDecimal enterLocationLat;
  /***
   *  图片： List<String>
   */
  @NotNull(message = "图片： List<String>不能为空")
  private List<String> photos;

}


