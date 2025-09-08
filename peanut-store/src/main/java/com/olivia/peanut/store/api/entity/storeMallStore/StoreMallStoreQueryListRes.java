package com.olivia.peanut.store.api.entity.storeMallStore;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * store 商场门店(StoreMallStore)查询对象返回
 *
 * @author admin
 * @since 2025-08-31 15:37:01
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreMallStoreQueryListRes {

  /***
   * 返回对象列表
   */
  private List<StoreMallStoreDto> dataList;


}

