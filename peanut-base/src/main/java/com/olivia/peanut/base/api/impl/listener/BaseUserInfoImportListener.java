package com.olivia.peanut.base.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.base.api.entity.baseUserInfo.BaseUserInfoImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户信息(BaseUserInfo)文件导入监听
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
@Slf4j
public class BaseUserInfoImportListener extends AbstractImportListener<BaseUserInfoImportReq> {

  @Override
  public void invoke(BaseUserInfoImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("BaseUserInfoImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
