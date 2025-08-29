package com.olivia.peanut.store.service.impl;

import static com.olivia.peanut.store.converter.StoreBusinessDistrictConverter.INSTANCE;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.olivia.peanut.base.model.Brand;
import com.olivia.peanut.base.model.DistrictCode;
import com.olivia.peanut.base.service.BaseTableHeaderService;
import com.olivia.peanut.base.service.BrandService;
import com.olivia.peanut.base.service.DistrictCodeService;
import com.olivia.peanut.store.api.entity.storeBusinessDistrict.*;
import com.olivia.peanut.store.converter.StoreBusinessDistrictConverter;
import com.olivia.peanut.store.converter.StoreBusinessDistrictLevelConverter;
import com.olivia.peanut.store.mapper.StoreBusinessDistrictMapper;
import com.olivia.peanut.store.model.StoreBusinessDistrict;
import com.olivia.peanut.store.model.StoreBusinessDistrictLevel;
import com.olivia.peanut.store.model.StoreBusinessDistrictType;
import com.olivia.peanut.store.service.StoreBusinessDistrictLevelService;
import com.olivia.peanut.store.service.StoreBusinessDistrictService;
import com.olivia.peanut.store.service.StoreBusinessDistrictTypeService;
import com.olivia.sdk.filter.LoginUserContext;
import com.olivia.sdk.service.SetNameService;
import com.olivia.sdk.utils.*;
import jakarta.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商圈(StoreBusinessDistrict)表服务实现类
 *
 * @author admin
 * @since 2025-08-24 21:01:55
 */
@Slf4j
@Service("storeBusinessDistrictService")
@Transactional
public class StoreBusinessDistrictServiceImpl extends MPJBaseServiceImpl<StoreBusinessDistrictMapper, StoreBusinessDistrict> implements StoreBusinessDistrictService {

  // final static Cache<String, Map<String, String>> cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES).build();

  @Resource
  BaseTableHeaderService tableHeaderService;
  @Resource
  SetNameService setNameService;
  @Resource
  BrandService brandService;

  @Resource
  StoreBusinessDistrictLevelService storeBusinessDistrictLevelService;
  @Resource
  StoreBusinessDistrictTypeService storeBusinessDistrictTypeService;
  @Resource
  DistrictCodeService districtCodeService;
  @Resource
  SqlSession sqlSession;

  private void updateLocationGeo(StoreBusinessDistrict req) {
    String centerLng = req.getCenterLng();
    String centerLat = req.getCenterLat();
    Long id = req.getId();
    if (StringUtils.isNotBlank(centerLng) && StringUtils.isNotBlank(centerLat)) {
      String wkt = String.format("POINT(%s , %s)", centerLng, centerLat);
      log.info("updateLocationGeo id:{} wkt {}", id, wkt);
      // 构建更新条件
      UpdateWrapper<StoreBusinessDistrict> updateWrapper = new UpdateWrapper<>();
      updateWrapper.setSql("location_geo = " + wkt   // 修正为纬度
          ).eq("is_delete", 0).eq("id", id)//
          .eq("tenant_id", LoginUserContext.getLoginUser().getTenantId());

      // 执行更新
      this.update(updateWrapper);
    } else {
      this.update(new LambdaUpdateWrapper<StoreBusinessDistrict>().set(StoreBusinessDistrict::getLocationGeo, null).eq(BaseEntity::getId, id));
    }
  }

  public @Override StoreBusinessDistrictQueryListRes queryList(StoreBusinessDistrictQueryListReq req) {

    MPJLambdaWrapper<StoreBusinessDistrict> q = getWrapper(req.getData());
    List<StoreBusinessDistrict> list = this.list(q);

    List<StoreBusinessDistrictDto> dataList = StoreBusinessDistrictConverter.INSTANCE.queryListRes(list);
    ((StoreBusinessDistrictService) AopContext.currentProxy()).setName(dataList);
    return new StoreBusinessDistrictQueryListRes().setDataList(dataList);
  }

  public @Override DynamicsPage<StoreBusinessDistrictExportQueryPageListInfoRes> queryPageList(StoreBusinessDistrictExportQueryPageListReq req) {

    DynamicsPage<StoreBusinessDistrict> page = new DynamicsPage<>();
    page.setCurrent(req.getPageNum()).setSize(req.getPageSize());
    setQueryListHeader(page);
    MPJLambdaWrapper<StoreBusinessDistrict> q = getWrapper(req.getData());
    List<StoreBusinessDistrictExportQueryPageListInfoRes> records;
    if (Boolean.TRUE.equals(req.getQueryPage())) {
      IPage<StoreBusinessDistrict> list = this.page(page, q);
      IPage<StoreBusinessDistrictExportQueryPageListInfoRes> dataList = list.convert(t -> $.copy(t, StoreBusinessDistrictExportQueryPageListInfoRes.class));
      records = dataList.getRecords();
    } else {
      records = StoreBusinessDistrictConverter.INSTANCE.queryPageListRes(this.list(q));
    }

    // 类型转换，  更换枚举 等操作

    ((StoreBusinessDistrictService) AopContext.currentProxy()).setName(records);
    return DynamicsPage.init(page, records);
  }

  @Override
  public void saveDto(StoreBusinessDistrictInsertReq req) {
    StoreBusinessDistrict reqTmp = INSTANCE.insertReq(req);
    setDistrictCodeName(reqTmp);
    reqTmp.setId(IdWorker.getId());
    this.save(reqTmp);
    updateLocationGeo(reqTmp);
  }

  private void setDistrictCodeName(StoreBusinessDistrict storeBusinessDistrict) {
//    DistrictCode codeServiceOne = districtCodeService.getOne(new LambdaQueryWrapper<DistrictCode>().eq(DistrictCode::getCode, storeBusinessDistrict.getAreaCode()));
//    $.requireNonNullCanIgnoreException(codeServiceOne, "行政区域不存在");
//    String regex = "[.]";
//    String[] pathArr = codeServiceOne.getPath().split(regex);
//    String[] pathNameArr = codeServiceOne.getPathName().split(regex);
//    storeBusinessDistrict
//        //.setCountryCode(pathArr[0]).setCountryName(pathNameArr[0])  //
//        .setProvinceCode(pathArr[0]).setProvinceName(pathNameArr[0])   //
//        .setCityCode(pathArr[1]).setCityName(pathNameArr[1])  //
//        .setAreaCode(pathArr[2]).setAreaName(pathNameArr[2]);  //

    updateLocationGeo(storeBusinessDistrict);
  }

  @Override
  public boolean updateById(StoreBusinessDistrict entity) {
    setDistrictCodeName(entity);
    super.updateById(entity);
    updateLocationGeo(entity);
    return true;
  }
  // 以下为私有对象封装

  public @Override void setName(List<? extends StoreBusinessDistrictDto> list) {

    //   setNameService.setName(list, SetNamePojoUtils.FACTORY, SetNamePojoUtils.OP_USER_NAME);

    if (CollUtil.isEmpty(list)) {
      return;
    }
    Set<Long> brandIdSet = new HashSet<>();
    Set<Long> levelIdSet = new HashSet<>();
    Set<Long> typeIdSet = new HashSet<>();
    list.forEach(item -> {
      brandIdSet.add(item.getBrandId());
      levelIdSet.add(item.getBusinessDistrictLevelId());
      typeIdSet.add(item.getBusinessDistrictTypeId());
    });
    Map<Long, String> bnMap = this.brandService.listByIds(brandIdSet).stream().collect(Collectors.toMap(BaseEntity::getId, Brand::getBrandName));
    Map<Long, StoreBusinessDistrictLevel> levelMap = this.storeBusinessDistrictLevelService.listByIds(levelIdSet).stream()
        .collect(Collectors.toMap(BaseEntity::getId, Function.identity()));

    Map<Long, String> tyMap = storeBusinessDistrictTypeService.listByIds(typeIdSet).stream()
        .collect(Collectors.toMap(BaseEntity::getId, StoreBusinessDistrictType::getBusinessDistrictTypeName));
    list.forEach(item -> {
      item.setBusinessDistrictLevelInfo(StoreBusinessDistrictLevelConverter.INSTANCE.entityToDto(levelMap.get(item.getBusinessDistrictLevelId())));
      item.setBrandName(bnMap.get(item.getBrandId()));
      item.setBusinessDistrictTypeName(tyMap.get(item.getBusinessDistrictTypeId()));
    });
  }


  // @SuppressWarnings("unchecked")
  private MPJLambdaWrapper<StoreBusinessDistrict> getWrapper(StoreBusinessDistrictDto obj) {
    MPJLambdaWrapper<StoreBusinessDistrict> q = new MPJLambdaWrapper<>();

    LambdaQueryUtil.lambdaQueryWrapper(q, obj, StoreBusinessDistrict.class
        // 查询条件
        , BaseEntity::getId   // id
        , StoreBusinessDistrict::getBrandId   // 品牌ID
        , StoreBusinessDistrict::getBusinessDistrictCode   // 编码
        , StoreBusinessDistrict::getBusinessDistrictName   // 名称
        , StoreBusinessDistrict::getBusinessDistrictDesc   // 描述
        , StoreBusinessDistrict::getBusinessDistrictAddress   // 地址
        , StoreBusinessDistrict::getCountryCode   // 国家编码
        , StoreBusinessDistrict::getProvinceCode   // 城市编码
        , StoreBusinessDistrict::getCityCode   // 城市编码
        , StoreBusinessDistrict::getAreaCode   // 城市编码
        , StoreBusinessDistrict::getBusinessDistrictLevelId   // 商圈级别ID
        , StoreBusinessDistrict::getBusinessDistrictTypeId   // 商圈类别ID
        , StoreBusinessDistrict::getCenterLat   // 纬度
        , StoreBusinessDistrict::getCenterLng   // 经度
        , StoreBusinessDistrict::getCreateUserName   // 创建人姓名
        , StoreBusinessDistrict::getUpdateUserName   // 修改人姓名
    );

    q.orderByDesc(StoreBusinessDistrict::getId);
    return q;

  }

  private void setQueryListHeader(DynamicsPage<StoreBusinessDistrict> page) {

    tableHeaderService.listByBizKey(page, "StoreBusinessDistrictService#queryPageList");

  }


}

