package com.olivia.peanut.store.service;

import com.olivia.sdk.utils.DynamicsPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.olivia.peanut.store.model.StoreShoppingMall;
import java.util.List;
import com.github.yulichang.base.MPJBaseService;

import com.olivia.peanut.store.api.entity.storeShoppingMall.*;

/**
 * 门店 商场(StoreShoppingMall)表服务接口
 *
 * @author admin
 * @since 2025-08-29 15:54:26
 */
public interface StoreShoppingMallService extends MPJBaseService<StoreShoppingMall> {

  StoreShoppingMallQueryListRes queryList(StoreShoppingMallQueryListReq req);

  DynamicsPage<StoreShoppingMallExportQueryPageListInfoRes> queryPageList(StoreShoppingMallExportQueryPageListReq req);


  void setName(List<? extends StoreShoppingMallDto> storeShoppingMallDtoList);
}

