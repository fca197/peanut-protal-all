package com.olivia.peanut.store.api.entity.storeBusinessDistrict;

import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import com.olivia.peanut.store.api.entity.storeBusinessDistrictLevel.StoreBusinessDistrictLevelDto;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 商圈(StoreBusinessDistrict)查询对象返回
 *
 * @author admin
 * @since 2025-08-24 21:01:56
 */
//@Accessors(chain=true)
@Getter
@Setter
//@SuppressWarnings("serial")
public class StoreBusinessDistrictDto extends BaseEntityDto {

  /***
   *  品牌ID
   */
  @NotNull(message = "品牌ID不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Long brandId;
  /***
   *  编码
   */
  @NotNull(message = "编码不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String businessDistrictCode;
  /***
   *  名称
   */
  @NotBlank(message = "名称不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String businessDistrictName;
  /***
   *  描述
   */
  private String businessDistrictDesc;
  /***
   *  地址
   */
  @NotBlank(message = "地址不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String businessDistrictAddress;
  /***
   *  国家编码
   */
  private String countryCode;
  /***
   *  城市编码
   */
  private String provinceCode;
  /***
   *  城市编码
   */
  private String cityCode;
  /***
   *  城市编码
   */
  private String areaCode;
  /***
   *  国家名称
   */
  private String countryName;
  /***
   *  省份名称
   */
  private String provinceName;
  /***
   *  城市名称
   */
  private String cityName;
  /***
   *  区县名称
   */
  private String areaName;
  /***
   *  半径
   */
  @NotNull(message = "半径不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Long businessDistrictRadius;
  /***
   *  商圈级别ID
   */
  @NotNull(message = "商圈级别ID不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Long businessDistrictLevelId;
  /***
   *  商圈类别ID
   */
  @NotNull(message = "商圈类别ID不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Long businessDistrictTypeId;
  /***
   *  纬度
   */
  @NotBlank(message = "纬度不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String centerLat;
  /***
   *  经度
   */
  @NotBlank(message = "经度不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String centerLng;

  private String brandName;
  private String businessDistrictTypeName;
  private StoreBusinessDistrictLevelDto businessDistrictLevelInfo;
}


