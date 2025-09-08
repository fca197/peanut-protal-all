package com.olivia.peanut.store.api.entity.storeMallStore;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * store 商场门店(StoreMallStore)查询对象入参
 *
 * @author admin
 * @since 2025-08-31 15:37:01
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreMallStoreExportQueryPageListReq {

  private int pageNum;
  private int pageSize;
  private Boolean queryPage = true;
  private StoreMallStoreDto data;
}

