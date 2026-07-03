package com.olivia.peanut.store.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.store.api.entity.storeBusinessDistrictLevel.StoreBusinessDistrictLevelImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 商圈级别(StoreBusinessDistrictLevel)文件导入监听
 *
 * @author admin
 * @since 2025-08-24 21:10:16
 */
@Slf4j
public class StoreBusinessDistrictLevelImportListener extends AbstractImportListener<StoreBusinessDistrictLevelImportReq> {

  @Override
  public void invoke(StoreBusinessDistrictLevelImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("StoreBusinessDistrictLevelImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
