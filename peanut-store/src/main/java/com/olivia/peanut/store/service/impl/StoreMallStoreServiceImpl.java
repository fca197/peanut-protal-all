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
import com.olivia.peanut.store.mapper.StoreMallStoreMapper;
import com.olivia.peanut.store.model.StoreMallStore;
import com.olivia.peanut.store.converter.StoreMallStoreConverter;
import com.olivia.peanut.store.service.StoreMallStoreService;
import cn.hutool.core.collection.CollUtil;
import com.olivia.peanut.base.service.BaseTableHeaderService;
import com.olivia.sdk.utils.BaseEntity;
import com.olivia.peanut.store.api.entity.storeMallStore.*;
import com.olivia.sdk.service.SetNameService;

/**
 * store 商场门店(StoreMallStore)表服务实现类
 *
 * @author admin
 * @since 2025-08-31 15:37:02
 */
@Service("storeMallStoreService")
@Transactional
public class StoreMallStoreServiceImpl extends MPJBaseServiceImpl<StoreMallStoreMapper, StoreMallStore> implements StoreMallStoreService {

  // final static Cache<String, Map<String, String>> cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES).build();

  @Resource
  BaseTableHeaderService tableHeaderService;
  @Resource
  SetNameService setNameService;


  public @Override StoreMallStoreQueryListRes queryList(StoreMallStoreQueryListReq req) {

    MPJLambdaWrapper<StoreMallStore> q = getWrapper(req.getData());
    List<StoreMallStore> list = this.list(q);

    List<StoreMallStoreDto> dataList = StoreMallStoreConverter.INSTANCE.queryListRes(list);
    ((StoreMallStoreService) AopContext.currentProxy()).setName(dataList);
    return new StoreMallStoreQueryListRes().setDataList(dataList);
  }


  public @Override DynamicsPage<StoreMallStoreExportQueryPageListInfoRes> queryPageList(StoreMallStoreExportQueryPageListReq req) {

    DynamicsPage<StoreMallStore> page = new DynamicsPage<>();
    page.setCurrent(req.getPageNum()).setSize(req.getPageSize());
    setQueryListHeader(page);
    MPJLambdaWrapper<StoreMallStore> q = getWrapper(req.getData());
    List<StoreMallStoreExportQueryPageListInfoRes> records;
    if (Boolean.TRUE.equals(req.getQueryPage())) {
      IPage<StoreMallStore> list = this.page(page, q);
      IPage<StoreMallStoreExportQueryPageListInfoRes> dataList = list.convert(t -> $.copy(t, StoreMallStoreExportQueryPageListInfoRes.class));
      records = dataList.getRecords();
    } else {
      records = StoreMallStoreConverter.INSTANCE.queryPageListRes(this.list(q));
    }

    // 类型转换，  更换枚举 等操作 

    ((StoreMallStoreService) AopContext.currentProxy()).setName(records);
    return DynamicsPage.init(page, records);
  }

  // 以下为私有对象封装

  public @Override void setName(List<? extends StoreMallStoreDto> list) {

    //   setNameService.setName(list, SetNamePojoUtils.FACTORY, SetNamePojoUtils.OP_USER_NAME);

  }


  // @SuppressWarnings("unchecked")
  private MPJLambdaWrapper<StoreMallStore> getWrapper(StoreMallStoreDto obj) {
    MPJLambdaWrapper<StoreMallStore> q = new MPJLambdaWrapper<>();

    LambdaQueryUtil.lambdaQueryWrapper(q, obj, StoreMallStore.class
        // 查询条件
        , BaseEntity::getId // id
        , StoreMallStore::getShoppingMallId // store_shopping_mall ID  商场ID
        , StoreMallStore::getMallStoreFloor // 楼层
        , StoreMallStore::getMallStoreRoomNo // 房间号
        , StoreMallStore::getMallStoreStatus // 状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，
        , StoreMallStore::getMallStoreStatusMark // 状态备注
        , StoreMallStore::getCreateUserName // 创建人姓名
        , StoreMallStore::getUpdateUserName // 修改人姓名
    );

    q.orderByDesc(StoreMallStore::getId);
    return q;

  }

  private void setQueryListHeader(DynamicsPage<StoreMallStore> page) {

    tableHeaderService.listByBizKey(page, "StoreMallStoreService#queryPageList");

  }


}

