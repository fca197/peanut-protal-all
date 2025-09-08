package com.olivia.peanut.store.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.olivia.sdk.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;

/**
 * store 商场门店(StoreMallStore)表实体类
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
@Accessors(chain = true)
@Getter
@Setter
//@SuppressWarnings("serial")
@TableName(value = "store_mall_store")
public class StoreMallStore extends BaseEntity<StoreMallStore> {

  /***
   *  store_shopping_mall ID  商场ID
   */
  @TableField(value = "shopping_mall_id")
  private Long shoppingMallId;
  /***
   *  楼层
   */
  @TableField(value = "mall_store_floor")
  private Integer mallStoreFloor;
  /***
   *  房间号
   */
  @TableField(value = "mall_store_room_no")
  private Integer mallStoreRoomNo;
  /***
   *  状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，
   */
  @TableField(value = "mall_store_status")
  private Integer mallStoreStatus;
  /***
   *  状态备注
   */
  @TableField(value = "mall_store_status_mark")
  private String mallStoreStatusMark;

}

