package com.olivia.peanut.base.api.entity.baseUserInfo;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户信息(BaseUserInfo)查询对象入参
 *
 * @author admin
 * @since 2026-07-04 01:25:45
 */
@Accessors(chain = true)
@Getter
@Setter
@SuppressWarnings("serial")
public class BaseUserInfoQueryByIdListReq {

  private List<Long> idList;

}

