package com.olivia.peanut.store.api.entity.storeMallStore;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * store 商场门店(StoreMallStore)根据ID删除多个入参
 *
 * @author admin
 * @since 2025-08-31 15:37:01
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreMallStoreDeleteByIdListReq {

  /***
   * 要删除的ID
   */
  @NotEmpty(message = "请选择删除对象")
  private List<Long> idList;

}

