package com.olivia.peanut.store.converter;

import com.olivia.peanut.store.api.entity.storeBusinessDistrictLevel.*;
import com.olivia.peanut.store.model.StoreBusinessDistrictLevel;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreBusinessDistrictLevelConverter {

  StoreBusinessDistrictLevelConverter INSTANCE = Mappers.getMapper(StoreBusinessDistrictLevelConverter.class);

  StoreBusinessDistrictLevel insertReq(StoreBusinessDistrictLevelInsertReq req);

  StoreBusinessDistrictLevel updateReq(StoreBusinessDistrictLevelUpdateByIdReq req);

  List<StoreBusinessDistrictLevelDto> queryListRes(List<StoreBusinessDistrictLevel> list);

  List<StoreBusinessDistrictLevelExportQueryPageListInfoRes> queryPageListRes(List<StoreBusinessDistrictLevel> list);

  List<StoreBusinessDistrictLevel> importReq(List<StoreBusinessDistrictLevelImportReq> reqList);

  StoreBusinessDistrictLevelDto entityToDto(StoreBusinessDistrictLevel entity);
}

