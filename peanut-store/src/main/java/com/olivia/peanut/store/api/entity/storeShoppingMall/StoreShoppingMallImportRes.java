package com.olivia.peanut.store.api.entity.storeShoppingMall;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 门店 商场(StoreShoppingMall)保存返回
 *
 * @author admin
 * @since 2025-08-29 15:54:25
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreShoppingMallImportRes {

  /****
   * 写入行数
   */
  private int count;
  /**
   * 错误信息
   */
  private List<String> errorMsg;
}

