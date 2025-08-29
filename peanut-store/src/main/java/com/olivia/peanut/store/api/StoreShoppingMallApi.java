package com.olivia.peanut.store.api;

import org.springframework.validation.annotation.Validated;
import com.olivia.sdk.utils.DynamicsPage;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.olivia.peanut.store.api.entity.storeShoppingMall.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * 门店 商场(StoreShoppingMall)对外API
 *
 * @author admin
 * @since 2025-08-29 15:54:25
 */
// @FeignClient(value = "",contextId = "storeShoppingMall-api",url = "${ portal..center.endpoint:}")
public interface StoreShoppingMallApi {

  /**
   * 保存 门店 商场
   */
  @PostMapping("/storeShoppingMall/insert")
  StoreShoppingMallInsertRes insert(@RequestBody @Validated(InsertCheck.class) StoreShoppingMallInsertReq req);

  /**
   * 根据ID 删除 门店 商场
   */
  @PostMapping("/storeShoppingMall/deleteByIdList")
  StoreShoppingMallDeleteByIdListRes deleteByIdList(@RequestBody @Valid StoreShoppingMallDeleteByIdListReq req);

  /**
   * 查询 门店 商场
   */
  @PostMapping("/storeShoppingMall/queryList")
  StoreShoppingMallQueryListRes queryList(@RequestBody @Valid StoreShoppingMallQueryListReq req);

  /**
   * 根据ID 更新 门店 商场
   */
  @PostMapping("/storeShoppingMall/updateById")
  StoreShoppingMallUpdateByIdRes updateById(@RequestBody @Validated(UpdateCheck.class) StoreShoppingMallUpdateByIdReq req);

  /**
   * 分页查询 门店 商场
   */
  @PostMapping("/storeShoppingMall/queryPageList")
  DynamicsPage<StoreShoppingMallExportQueryPageListInfoRes> queryPageList(@RequestBody @Valid StoreShoppingMallExportQueryPageListReq req);

  /**
   * 导出 门店 商场
   */
  @PostMapping("/storeShoppingMall/exportQueryPageList")
  void queryPageListExport(@RequestBody @Valid StoreShoppingMallExportQueryPageListReq req);

  /**
   * 导入
   */
  @PostMapping("/storeShoppingMall/importData")
  StoreShoppingMallImportRes importData(@RequestParam("file") MultipartFile file);


  /**
   * 根据ID 批量查询
   */
  @PostMapping("/storeShoppingMall/queryByIdList")
  StoreShoppingMallQueryByIdListRes queryByIdListRes(@RequestBody @Valid StoreShoppingMallQueryByIdListReq req);


}
