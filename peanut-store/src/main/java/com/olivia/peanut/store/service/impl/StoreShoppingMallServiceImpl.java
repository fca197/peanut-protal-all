package com.olivia.peanut.store.service.impl;

import org.springframework.aop.framework.AopContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Resource;
import com.olivia.sdk.utils.$;
import com.olivia.sdk.utils.LambdaQueryUtil;
import com.olivia.sdk.utils.DynamicsPage;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.olivia.peanut.store.mapper.StoreShoppingMallMapper;
import com.olivia.peanut.store.model.StoreShoppingMall;
import com.olivia.peanut.store.converter.StoreShoppingMallConverter;
import com.olivia.peanut.store.service.StoreShoppingMallService;
import cn.hutool.core.collection.CollUtil;
import com.olivia.peanut.base.service.BaseTableHeaderService;
import com.olivia.sdk.utils.BaseEntity;
import com.olivia.peanut.store.api.entity.storeShoppingMall.*;
import com.olivia.sdk.service.SetNameService;

/**
 * 门店 商场(StoreShoppingMall)表服务实现类
 *
 * @author admin
 * @since 2025-08-29 15:54:26
 */
@Service("storeShoppingMallService")
@Transactional
public class StoreShoppingMallServiceImpl extends MPJBaseServiceImpl<StoreShoppingMallMapper, StoreShoppingMall> implements StoreShoppingMallService {

  // final static Cache<String, Map<String, String>> cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES).build();

  @Resource
  BaseTableHeaderService tableHeaderService;
  @Resource
  SetNameService setNameService;


  public @Override StoreShoppingMallQueryListRes queryList(StoreShoppingMallQueryListReq req) {

    MPJLambdaWrapper<StoreShoppingMall> q = getWrapper(req.getData());
    List<StoreShoppingMall> list = this.list(q);

    List<StoreShoppingMallDto> dataList = StoreShoppingMallConverter.INSTANCE.queryListRes(list);
    ((StoreShoppingMallService) AopContext.currentProxy()).setName(dataList);
    return new StoreShoppingMallQueryListRes().setDataList(dataList);
  }


  public @Override DynamicsPage<StoreShoppingMallExportQueryPageListInfoRes> queryPageList(StoreShoppingMallExportQueryPageListReq req) {

    DynamicsPage<StoreShoppingMall> page = new DynamicsPage<>();
    page.setCurrent(req.getPageNum()).setSize(req.getPageSize());
    setQueryListHeader(page);
    MPJLambdaWrapper<StoreShoppingMall> q = getWrapper(req.getData());
    List<StoreShoppingMallExportQueryPageListInfoRes> records;
    if (Boolean.TRUE.equals(req.getQueryPage())) {
      IPage<StoreShoppingMall> list = this.page(page, q);
      IPage<StoreShoppingMallExportQueryPageListInfoRes> dataList = list.convert(t -> $.copy(t, StoreShoppingMallExportQueryPageListInfoRes.class));
      records = dataList.getRecords();
    } else {
      records = StoreShoppingMallConverter.INSTANCE.queryPageListRes(this.list(q));
    }

    // 类型转换，  更换枚举 等操作 

    ((StoreShoppingMallService) AopContext.currentProxy()).setName(records);
    return DynamicsPage.init(page, records);
  }

  // 以下为私有对象封装

  public @Override void setName(List<? extends StoreShoppingMallDto> list) {

    //   setNameService.setName(list, SetNamePojoUtils.FACTORY, SetNamePojoUtils.OP_USER_NAME);

  }


  // @SuppressWarnings("unchecked")
  private MPJLambdaWrapper<StoreShoppingMall> getWrapper(StoreShoppingMallDto obj) {
    MPJLambdaWrapper<StoreShoppingMall> q = new MPJLambdaWrapper<>();

    LambdaQueryUtil.lambdaQueryWrapper(q, obj, StoreShoppingMall.class
        // 查询条件
        , BaseEntity::getId // id
        , StoreShoppingMall::getCountryCode // 国家编码
        , StoreShoppingMall::getProvinceCode // 城市编码
        , StoreShoppingMall::getCityCode // 城市编码
        , StoreShoppingMall::getAreaCode // 城市编码
        , StoreShoppingMall::getCountryName // 国家名称
        , StoreShoppingMall::getProvinceName // 省份名称
        , StoreShoppingMall::getCityName // 城市名称
        , StoreShoppingMall::getAreaName // 区县名称
        , StoreShoppingMall::getBelongDistrictId // 所属最新商区
        , StoreShoppingMall::getBelongDistrictIdList // 所属商区 List<Long>
        , StoreShoppingMall::getShoppingMallAddress // 地址
        , StoreShoppingMall::getShoppingMallLocationLng // 经度
        , StoreShoppingMall::getShoppingMallLocationLat // 纬度
        , StoreShoppingMall::getShoppingMallName // 商场名称
        , StoreShoppingMall::getBusinessAlias // 商场别称
        , StoreShoppingMall::getBusinessOpenTimeTodayOpen // 营业开始时间
        , StoreShoppingMall::getBusinessOpenTimeTodayClose // 营业结束时间
        , StoreShoppingMall::getBusinessRating // 评分
        , StoreShoppingMall::getBusinessTag // 标签List<String>
        , StoreShoppingMall::getBusinessTel // 联系电话可多个List<String>
        , StoreShoppingMall::getEnterLocationLng // 入口经纬度经度
        , StoreShoppingMall::getEnterLocationLat // 入口经纬度纬度
        , StoreShoppingMall::getPhotos // 图片： List<String>
        , StoreShoppingMall::getCreateUserName // 创建人姓名
        , StoreShoppingMall::getUpdateUserName // 修改人姓名
    );

    q.orderByDesc(StoreShoppingMall::getId);
    return q;

  }

  private void setQueryListHeader(DynamicsPage<StoreShoppingMall> page) {

    tableHeaderService.listByBizKey(page, "StoreShoppingMallService#queryPageList");

  }


}

