package com.olivia.peanut.base.service;

import com.github.yulichang.base.MPJBaseService;
import com.olivia.peanut.base.api.entity.baseUserInfo.*;
import com.olivia.peanut.base.model.BaseUserInfo;
import com.olivia.sdk.utils.DynamicsPage;
import java.util.List;

/**
 * 用户信息(BaseUserInfo)表服务接口
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
public interface BaseUserInfoService extends MPJBaseService<BaseUserInfo> {

  BaseUserInfoQueryListRes queryList(BaseUserInfoQueryListReq req);

  DynamicsPage<BaseUserInfoExportQueryPageListInfoRes> queryPageList(BaseUserInfoExportQueryPageListReq req);

  void setName(List<? extends BaseUserInfoDto> baseUserInfoDtoList);

  BaseUserInfoDto loginPwd(BaseUserInfoDto req);

  void save(BaseUserInfoInsertReq req);
}

