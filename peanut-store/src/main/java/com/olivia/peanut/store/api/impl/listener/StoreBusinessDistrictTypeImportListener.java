package com.olivia.peanut.store.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.store.api.entity.storeBusinessDistrictType.StoreBusinessDistrictTypeImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 商圈类型(StoreBusinessDistrictType)文件导入监听
 *
 * @author admin
 * @since 2025-08-24 21:10:18
 */
@Slf4j
public class StoreBusinessDistrictTypeImportListener extends AbstractImportListener<StoreBusinessDistrictTypeImportReq> {

  @Override
  public void invoke(StoreBusinessDistrictTypeImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("StoreBusinessDistrictTypeImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
