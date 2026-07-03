package com.olivia.peanut.base.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.olivia.peanut.base.model.BaseUserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息(BaseUserInfo)表数据库访问层
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
@Mapper
public interface BaseUserInfoMapper extends MPJBaseMapper<BaseUserInfo> {

}

