package com.olivia.peanut.store.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.store.api.entity.storePoi.StorePoiImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * store poi(StorePoi)文件导入监听
 *
 * @author admin
 * @since 2025-08-22 16:10:27
 */
@Slf4j
public class StorePoiImportListener extends AbstractImportListener<StorePoiImportReq> {

  @Override
  public void invoke(StorePoiImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("StorePoiImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
