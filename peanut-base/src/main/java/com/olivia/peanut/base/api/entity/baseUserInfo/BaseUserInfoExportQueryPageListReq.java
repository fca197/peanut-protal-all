package com.olivia.peanut.base.api.entity.baseUserInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户信息(BaseUserInfo)查询对象入参
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
@Accessors(chain = true)
@Getter
@Setter
public class BaseUserInfoExportQueryPageListReq {

  private int pageNum;
  private int pageSize;
  private Boolean queryPage = true;
  private BaseUserInfoDto data;
}

