package com.olivia.peanut.store.api.entity.storeMallStore;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * store 商场门店(StoreMallStore)保存返回
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
@Accessors(chain = true)
@Getter
@Setter
public class StoreMallStoreImportRes {

  /****
   * 写入行数
   */
  private int count;
  /**
   * 错误信息
   */
  private List<String> errorMsg;
}

