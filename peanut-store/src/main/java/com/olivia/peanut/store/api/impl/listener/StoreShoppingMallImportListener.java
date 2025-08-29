package com.olivia.peanut.store.api.impl.listener;


import com.olivia.peanut.store.model.StoreShoppingMall;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.olivia.peanut.store.api.entity.storeShoppingMall.*;
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
    log.info("StoreShoppingMallImportListener invoke data:{}", JSON.toJSONString(data));
    checkData(data, analysisContext);

  }

}
