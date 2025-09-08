package com.olivia.peanut.store.service;

import com.github.yulichang.base.MPJBaseService;
import com.olivia.peanut.store.api.entity.storeShoppingMall.*;
import com.olivia.peanut.store.model.StoreShoppingMall;
import com.olivia.sdk.utils.DynamicsPage;
import java.util.List;
import lombok.NonNull;

/**
 * 门店 商场(StoreShoppingMall)表服务接口
 *
 * @author admin
 * @since 2025-08-29 15:54:26
 */
public interface StoreShoppingMallService extends MPJBaseService<StoreShoppingMall> {

  StoreShoppingMallQueryListRes queryList(StoreShoppingMallQueryListReq req);

  void checkStoreShoppingMall(@NonNull StoreShoppingMall storeShoppingMall);

  DynamicsPage<StoreShoppingMallExportQueryPageListInfoRes> queryPageList(StoreShoppingMallExportQueryPageListReq req);


  void setName(List<? extends StoreShoppingMallDto> storeShoppingMallDtoList);

  void saveStoreShoppingMall(StoreShoppingMall storeShoppingMall);
}

