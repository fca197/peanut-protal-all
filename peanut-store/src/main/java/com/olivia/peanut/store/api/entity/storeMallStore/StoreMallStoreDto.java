package com.olivia.peanut.store.api.entity.storeMallStore;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import java.math.BigDecimal;
import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import com.alibaba.excel.annotation.ExcelProperty;
import com.olivia.sdk.utils.fastjson.Str2BooleanConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

/**
 * store 商场门店(StoreMallStore)查询对象返回
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
//@Accessors(chain=true)
@Getter
@Setter
//@SuppressWarnings("serial")
public class StoreMallStoreDto extends BaseEntityDto {

  /***
   *  store_shopping_mall ID  商场ID
   */
  @NotNull(message = "store_shopping_mall ID  商场ID不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Long shoppingMallId;
  /***
   *  楼层
   */
  @NotNull(message = "楼层不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Integer mallStoreFloor;
  /***
   *  房间号
   */
  @NotNull(message = "房间号不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Integer mallStoreRoomNo;
  /***
   *  状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，
   */
  @NotNull(message = "状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private Integer mallStoreStatus;
  /***
   *  状态备注
   */
  @NotBlank(message = "状态备注不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String mallStoreStatusMark;

}


