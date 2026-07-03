package com.olivia.peanut.store.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.store.api.entity.storeShoppingMall.StoreShoppingMallImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 门店 商场(StoreShoppingMall)文件导入监听
 *
 * @author admin
 * @since 2025-08-29 15:54:26
 */
@Slf4j
public class StoreShoppingMallImportListener extends AbstractImportListener<StoreShoppingMallImportReq> {

  @Override
  public void invoke(StoreShoppingMallImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("StoreShoppingMallImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
