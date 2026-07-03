package com.olivia.peanut.base.api;

import com.olivia.peanut.base.api.entity.baseUserInfo.*;
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
 * 用户信息(BaseUserInfo)对外API
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
// @FeignClient(value = "",contextId = "baseUserInfo-api",url = "${ portal..center.endpoint:}")
public interface BaseUserInfoApi {


  @PostMapping("/loginAccount/login/pwd")
  BaseUserInfoDto loginPwd(@RequestBody @Validated(LoginCheck.class) BaseUserInfoDto req);

  /**
   * 保存 用户信息
   *
   */
  @PostMapping("/baseUserInfo/insert")
  BaseUserInfoInsertRes insert(@RequestBody @Validated(InsertCheck.class) BaseUserInfoInsertReq req);

  /**
   * 根据ID 删除 用户信息
   *
   */
  @PostMapping("/baseUserInfo/deleteByIdList")
  BaseUserInfoDeleteByIdListRes deleteByIdList(@RequestBody @Valid BaseUserInfoDeleteByIdListReq req);

  /**
   * 查询 用户信息
   *
   */
  @PostMapping("/baseUserInfo/queryList")
  BaseUserInfoQueryListRes queryList(@RequestBody @Valid BaseUserInfoQueryListReq req);

  /**
   * 根据ID 更新 用户信息
   *
   */
  @PostMapping("/baseUserInfo/updateById")
  BaseUserInfoUpdateByIdRes updateById(@RequestBody @Validated(UpdateCheck.class) BaseUserInfoUpdateByIdReq req);

  /**
   * 分页查询 用户信息
   */
  @PostMapping("/baseUserInfo/queryPageList")
  DynamicsPage<BaseUserInfoExportQueryPageListInfoRes> queryPageList(@RequestBody @Valid BaseUserInfoExportQueryPageListReq req);

  /**
   * 导出 用户信息
   */
  @PostMapping("/baseUserInfo/exportQueryPageList")
  void queryPageListExport(@RequestBody @Valid BaseUserInfoExportQueryPageListReq req);

  /**
   * 导入
   */
  @PostMapping("/baseUserInfo/importData")
  BaseUserInfoImportRes importData(@RequestParam("file") MultipartFile file);


  /**
   * 根据ID 批量查询
   */
  @PostMapping("/baseUserInfo/queryByIdList")
  BaseUserInfoQueryByIdListRes queryByIdListRes(@RequestBody @Valid BaseUserInfoQueryByIdListReq req);


}
