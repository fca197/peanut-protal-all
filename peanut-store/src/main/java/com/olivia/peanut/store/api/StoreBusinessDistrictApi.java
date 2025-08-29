package com.olivia.peanut.store.api;

import com.olivia.peanut.store.api.entity.storeBusinessDistrict.*;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import com.olivia.sdk.utils.DynamicsPage;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


/**
 * 商圈(StoreBusinessDistrict)对外API
 *
 * @author admin
 * @since 2025-08-24 21:01:54
 */
// @FeignClient(value = "",contextId = "storeBusinessDistrict-api",url = "${ portal..center.endpoint:}")
public interface StoreBusinessDistrictApi {

  /**
   * 保存 商圈
   */
  @PostMapping("/storeBusinessDistrict/insert")
  StoreBusinessDistrictInsertRes insert(@RequestBody @Validated(InsertCheck.class) StoreBusinessDistrictInsertReq req);
  @PostMapping("/storeBusinessDistrict/insertAll")
  StoreBusinessDistrictInsertRes insertAll(@RequestBody @Validated(InsertCheck.class) StoreBusinessDistrictInsertReq req);

  /**
   * 根据ID 删除 商圈
   */
  @PostMapping("/storeBusinessDistrict/deleteByIdList")
  StoreBusinessDistrictDeleteByIdListRes deleteByIdList(@RequestBody @Valid StoreBusinessDistrictDeleteByIdListReq req);

  /**
   * 查询 商圈
   */
  @PostMapping("/storeBusinessDistrict/queryList")
  StoreBusinessDistrictQueryListRes queryList(@RequestBody @Valid StoreBusinessDistrictQueryListReq req);

  /**
   * 根据ID 更新 商圈
   */
  @PostMapping("/storeBusinessDistrict/updateById")
  StoreBusinessDistrictUpdateByIdRes updateById(@RequestBody @Validated(UpdateCheck.class) StoreBusinessDistrictUpdateByIdReq req);

  /**
   * 分页查询 商圈
   */
  @PostMapping("/storeBusinessDistrict/queryPageList")
  DynamicsPage<StoreBusinessDistrictExportQueryPageListInfoRes> queryPageList(@RequestBody @Valid StoreBusinessDistrictExportQueryPageListReq req);

  /**
   * 导出 商圈
   */
  @PostMapping("/storeBusinessDistrict/exportQueryPageList")
  void queryPageListExport(@RequestBody @Valid StoreBusinessDistrictExportQueryPageListReq req);

  /**
   * 导入
   */
  @PostMapping("/storeBusinessDistrict/importData")
  StoreBusinessDistrictImportRes importData(@RequestParam("file") MultipartFile file);


  /**
   * 根据ID 批量查询
   */
  @PostMapping("/storeBusinessDistrict/queryByIdList")
  StoreBusinessDistrictQueryByIdListRes queryByIdListRes(@RequestBody @Valid StoreBusinessDistrictQueryByIdListReq req);


}
