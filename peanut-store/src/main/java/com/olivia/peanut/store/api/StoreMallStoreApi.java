package com.olivia.peanut.store.api;

import org.springframework.validation.annotation.Validated;
import com.olivia.sdk.utils.DynamicsPage;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.olivia.peanut.store.api.entity.storeMallStore.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * store 商场门店(StoreMallStore)对外API
 *
 * @author admin
 * @since 2025-08-31 15:37:01
 */
// @FeignClient(value = "",contextId = "storeMallStore-api",url = "${ portal..center.endpoint:}")
public interface StoreMallStoreApi {

  /**
   * 保存 store 商场门店
   */
  @PostMapping("/storeMallStore/insert")
  StoreMallStoreInsertRes insert(@RequestBody @Validated(InsertCheck.class) StoreMallStoreInsertReq req);

  /**
   * 根据ID 删除 store 商场门店
   */
  @PostMapping("/storeMallStore/deleteByIdList")
  StoreMallStoreDeleteByIdListRes deleteByIdList(@RequestBody @Valid StoreMallStoreDeleteByIdListReq req);

  /**
   * 查询 store 商场门店
   */
  @PostMapping("/storeMallStore/queryList")
  StoreMallStoreQueryListRes queryList(@RequestBody @Valid StoreMallStoreQueryListReq req);

  /**
   * 根据ID 更新 store 商场门店
   */
  @PostMapping("/storeMallStore/updateById")
  StoreMallStoreUpdateByIdRes updateById(@RequestBody @Validated(UpdateCheck.class) StoreMallStoreUpdateByIdReq req);

  /**
   * 分页查询 store 商场门店
   */
  @PostMapping("/storeMallStore/queryPageList")
  DynamicsPage<StoreMallStoreExportQueryPageListInfoRes> queryPageList(@RequestBody @Valid StoreMallStoreExportQueryPageListReq req);

  /**
   * 导出 store 商场门店
   */
  @PostMapping("/storeMallStore/exportQueryPageList")
  void queryPageListExport(@RequestBody @Valid StoreMallStoreExportQueryPageListReq req);

  /**
   * 导入
   */
  @PostMapping("/storeMallStore/importData")
  StoreMallStoreImportRes importData(@RequestParam("file") MultipartFile file);


  /**
   * 根据ID 批量查询
   */
  @PostMapping("/storeMallStore/queryByIdList")
  StoreMallStoreQueryByIdListRes queryByIdListRes(@RequestBody @Valid StoreMallStoreQueryByIdListReq req);


}
