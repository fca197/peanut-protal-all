package com.olivia.peanut.store.converter;

import com.olivia.peanut.store.api.entity.storeShoppingMall.*;
import com.olivia.peanut.store.model.StoreShoppingMall;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreShoppingMallConverter {

  StoreShoppingMallConverter INSTANCE = Mappers.getMapper(StoreShoppingMallConverter.class);

  StoreShoppingMall insertReq(StoreShoppingMallInsertReq req);

  StoreShoppingMall updateReq(StoreShoppingMallUpdateByIdReq req);

  List<StoreShoppingMallDto> queryListRes(List<StoreShoppingMall> list);

  List<StoreShoppingMallExportQueryPageListInfoRes> queryPageListRes(List<StoreShoppingMall> list);

  List<StoreShoppingMall> importReq(List<StoreShoppingMallImportReq> reqList);
}

