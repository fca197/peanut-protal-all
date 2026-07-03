package com.olivia.peanut.store.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.store.api.entity.storeBusinessDistrict.StoreBusinessDistrictImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 商圈(StoreBusinessDistrict)文件导入监听
 *
 * @author admin
 * @since 2025-08-24 21:01:55
 */
@Slf4j
public class StoreBusinessDistrictImportListener extends AbstractImportListener<StoreBusinessDistrictImportReq> {

  @Override
  public void invoke(StoreBusinessDistrictImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("StoreBusinessDistrictImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
