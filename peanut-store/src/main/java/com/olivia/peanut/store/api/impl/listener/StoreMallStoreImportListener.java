package com.olivia.peanut.store.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.store.api.entity.storeMallStore.StoreMallStoreImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * store 商场门店(StoreMallStore)文件导入监听
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
@Slf4j
public class StoreMallStoreImportListener extends AbstractImportListener<StoreMallStoreImportReq> {

  @Override
  public void invoke(StoreMallStoreImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("StoreMallStoreImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
