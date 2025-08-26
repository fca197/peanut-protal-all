package com.olivia.peanut.store.service;

import com.github.yulichang.base.MPJBaseService;
import com.olivia.peanut.store.api.entity.storeBusinessDistrict.*;
import com.olivia.peanut.store.model.StoreBusinessDistrict;
import com.olivia.sdk.utils.DynamicsPage;
import java.util.List;

/**
 * 商圈(StoreBusinessDistrict)表服务接口
 *
 * @author admin
 * @since 2025-08-24 21:01:55
 */
public interface StoreBusinessDistrictService extends MPJBaseService<StoreBusinessDistrict> {

  StoreBusinessDistrictQueryListRes queryList(StoreBusinessDistrictQueryListReq req);

  DynamicsPage<StoreBusinessDistrictExportQueryPageListInfoRes> queryPageList(StoreBusinessDistrictExportQueryPageListReq req);


  void setName(List<? extends StoreBusinessDistrictDto> storeBusinessDistrictDtoList);

  void save(StoreBusinessDistrictInsertReq req);
}

