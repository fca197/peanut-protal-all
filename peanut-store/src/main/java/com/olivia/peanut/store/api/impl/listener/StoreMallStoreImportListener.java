package com.olivia.peanut.store.api.impl.listener;


import com.olivia.peanut.store.model.StoreMallStore;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.olivia.peanut.store.api.entity.storeMallStore.*;
import com.alibaba.excel.context.AnalysisContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import com.olivia.sdk.listener.AbstractImportListener;

import com.olivia.sdk.utils.JSON;
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
    log.info("StoreMallStoreImportListener invoke data:{}", JSON.toJSONString(data));
    checkData(data, analysisContext);

  }

}
