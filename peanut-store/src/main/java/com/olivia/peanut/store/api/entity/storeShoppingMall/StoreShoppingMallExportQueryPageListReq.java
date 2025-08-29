package com.olivia.peanut.store.api.entity.storeShoppingMall;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门店 商场(StoreShoppingMall)查询对象入参
 *
 * @author admin
 * @since 2025-08-29 15:54:25
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreShoppingMallExportQueryPageListReq {

  private int pageNum;
  private int pageSize;
  private Boolean queryPage = true;
  private StoreShoppingMallDto data;
}

