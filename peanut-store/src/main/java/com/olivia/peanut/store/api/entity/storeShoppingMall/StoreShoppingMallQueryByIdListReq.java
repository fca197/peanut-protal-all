package com.olivia.peanut.store.api.entity.storeShoppingMall;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门店 商场(StoreShoppingMall)查询对象入参
 *
 * @author admin
 * @since 2025-08-29 15:54:26
 */
@Accessors(chain = true)
@Getter
@Setter
@SuppressWarnings("serial")
public class StoreShoppingMallQueryByIdListReq {

  private List<Long> idList;

}

