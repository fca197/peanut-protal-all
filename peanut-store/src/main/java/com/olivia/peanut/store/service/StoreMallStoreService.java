package com.olivia.peanut.store.service;

import com.olivia.sdk.utils.DynamicsPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.olivia.peanut.store.model.StoreMallStore;
import java.util.List;
import com.github.yulichang.base.MPJBaseService;

import com.olivia.peanut.store.api.entity.storeMallStore.*;

/**
 * store 商场门店(StoreMallStore)表服务接口
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
public interface StoreMallStoreService extends MPJBaseService<StoreMallStore> {

  StoreMallStoreQueryListRes queryList(StoreMallStoreQueryListReq req);

  DynamicsPage<StoreMallStoreExportQueryPageListInfoRes> queryPageList(StoreMallStoreExportQueryPageListReq req);


  void setName(List<? extends StoreMallStoreDto> storeMallStoreDtoList);
}

