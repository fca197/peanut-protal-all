package com.olivia.peanut.store.api.entity.storeShoppingMall;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门店 商场(StoreShoppingMall)根据ID删除多个入参
 *
 * @author admin
 * @since 2025-08-29 15:54:25
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreShoppingMallDeleteByIdListReq {

  /***
   * 要删除的ID
   */
  @NotEmpty(message = "请选择删除对象")
  private List<Long> idList;

}

