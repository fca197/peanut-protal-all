package com.olivia.peanut.store.api.entity.storeMallStore;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * store 商场门店(StoreMallStore)查询对象入参
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
@Accessors(chain = true)
@Getter
@Setter
@SuppressWarnings("serial")
public class StoreMallStoreQueryByIdListReq {

  private List<Long> idList;

}

