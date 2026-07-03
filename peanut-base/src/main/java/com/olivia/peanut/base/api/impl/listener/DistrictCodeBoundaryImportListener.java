package com.olivia.peanut.base.api.impl.listener;


import com.alibaba.excel.context.AnalysisContext;
import com.olivia.peanut.base.api.entity.districtCodeBoundary.DistrictCodeBoundaryImportReq;
import com.olivia.sdk.listener.AbstractImportListener;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 地区边界(DistrictCodeBoundary)文件导入监听
 *
 * @author admin
 * @since 2025-08-22 13:33:38
 */
@Slf4j
public class DistrictCodeBoundaryImportListener extends AbstractImportListener<DistrictCodeBoundaryImportReq> {

  @Override
  public void invoke(DistrictCodeBoundaryImportReq data, AnalysisContext analysisContext) {
    //  文件校验
    log.info("DistrictCodeBoundaryImportListener invoke data:{}", JSONUtils.toJSONString(data));
    checkData(data, analysisContext);

  }

}
