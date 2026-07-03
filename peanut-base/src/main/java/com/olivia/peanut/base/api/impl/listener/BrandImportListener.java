package com.olivia.peanut.base.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.base.api.entity.brand.BrandImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 品牌表(Brand)文件导入监听
 *
 * @author admin
 * @since 2025-08-25 15:03:19
 */
@Slf4j
public class BrandImportListener extends AbstractImportListener<BrandImportReq> {

  @Override
  public void invoke(BrandImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("BrandImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
