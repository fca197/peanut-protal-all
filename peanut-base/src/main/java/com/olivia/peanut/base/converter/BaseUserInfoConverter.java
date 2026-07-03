package com.olivia.peanut.base.converter;

import com.olivia.peanut.base.api.entity.baseUserInfo.*;
import com.olivia.peanut.base.model.BaseUserInfo;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseUserInfoConverter {

  BaseUserInfoConverter INSTANCE = Mappers.getMapper(BaseUserInfoConverter.class);

  BaseUserInfo insertReq(BaseUserInfoInsertReq req);

  BaseUserInfo updateReq(BaseUserInfoUpdateByIdReq req);

  List<BaseUserInfoDto> queryListRes(List<BaseUserInfo> list);

  List<BaseUserInfoExportQueryPageListInfoRes> queryPageListRes(List<BaseUserInfo> list);

  List<BaseUserInfo> importReq(List<BaseUserInfoImportReq> reqList);


  BaseUserInfoDto entity2Dto(BaseUserInfo req);

  BaseUserInfo dto2Entity(BaseUserInfoDto req);
}

