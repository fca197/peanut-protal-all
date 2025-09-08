package com.olivia.peanut.store.converter;

import com.olivia.peanut.store.api.entity.storeMallStore.*;
import com.olivia.peanut.store.model.StoreMallStore;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreMallStoreConverter {

  StoreMallStoreConverter INSTANCE = Mappers.getMapper(StoreMallStoreConverter.class);

  StoreMallStore insertReq(StoreMallStoreInsertReq req);

  StoreMallStore updateReq(StoreMallStoreUpdateByIdReq req);

  List<StoreMallStoreDto> queryListRes(List<StoreMallStore> list);

  List<StoreMallStoreExportQueryPageListInfoRes> queryPageListRes(List<StoreMallStore> list);

  List<StoreMallStore> importReq(List<StoreMallStoreImportReq> reqList);
}

