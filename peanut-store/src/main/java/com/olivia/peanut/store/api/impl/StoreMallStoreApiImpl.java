package com.olivia.peanut.store.api.impl;

import java.time.LocalDateTime;

import com.olivia.peanut.store.model.StoreMallStore;
import com.olivia.sdk.utils.$;
import com.olivia.sdk.utils.DynamicsPage;
import com.olivia.sdk.utils.PoiExcelUtil;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import com.olivia.peanut.store.api.entity.storeMallStore.*;
import com.olivia.peanut.store.service.StoreMallStoreService;
import com.olivia.peanut.store.model.*;
import com.baomidou.mybatisplus.core.conditions.query.*;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.web.bind.annotation.*;
import com.olivia.peanut.store.api.StoreMallStoreApi;

import static com.olivia.peanut.store.converter.StoreMallStoreConverter.*;

import com.olivia.peanut.store.api.impl.listener.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.Resource;

/**
 * store 商场门店(StoreMallStore)表服务实现类
 *
 * @author admin
 * @since 2025-08-31 15:37:01
 */
@RestController
public class StoreMallStoreApiImpl implements StoreMallStoreApi {

  private @Resource StoreMallStoreService storeMallStoreService;

  /****
   * insert
   *
   */
  public @Override StoreMallStoreInsertRes insert(StoreMallStoreInsertReq req) {
    StoreMallStore storeMallStore = INSTANCE.insertReq(req);
    this.storeMallStoreService.save(storeMallStore);
    return new StoreMallStoreInsertRes().setCount(1);
  }

  /****
   * deleteByIds
   *
   */
  public @Override StoreMallStoreDeleteByIdListRes deleteByIdList(StoreMallStoreDeleteByIdListReq req) {
    storeMallStoreService.removeByIds(req.getIdList());
    return new StoreMallStoreDeleteByIdListRes();
  }

  /****
   * queryList
   *
   */
  public @Override StoreMallStoreQueryListRes queryList(StoreMallStoreQueryListReq req) {
    return storeMallStoreService.queryList(req);
  }

  /****
   * updateById
   *
   */
  public @Override StoreMallStoreUpdateByIdRes updateById(StoreMallStoreUpdateByIdReq req) {
    storeMallStoreService.updateById(INSTANCE.updateReq(req));
    return new StoreMallStoreUpdateByIdRes();

  }

  public @Override DynamicsPage<StoreMallStoreExportQueryPageListInfoRes> queryPageList(StoreMallStoreExportQueryPageListReq req) {
    return storeMallStoreService.queryPageList(req);
  }

  public @Override void queryPageListExport(StoreMallStoreExportQueryPageListReq req) {
    DynamicsPage<StoreMallStoreExportQueryPageListInfoRes> page = queryPageList(req);
    List<StoreMallStoreExportQueryPageListInfoRes> list = page.getDataList();
    // 类型转换，  更换枚举 等操作
    PoiExcelUtil.export(StoreMallStoreExportQueryPageListInfoRes.class, list, "store 商场门店");
  }

  public @Override StoreMallStoreImportRes importData(@RequestParam("file") MultipartFile file) {
    List<StoreMallStoreImportReq> reqList = PoiExcelUtil.readData(file, new StoreMallStoreImportListener(), StoreMallStoreImportReq.class);
    // 类型转换，  更换枚举 等操作
    List<StoreMallStore> readList = INSTANCE.importReq(reqList);
    boolean bool = storeMallStoreService.saveBatch(readList);
    int c = bool ? readList.size() : 0;
    return new StoreMallStoreImportRes().setCount(c);
  }

  public @Override StoreMallStoreQueryByIdListRes queryByIdListRes(StoreMallStoreQueryByIdListReq req) {

    List<StoreMallStore> list = this.storeMallStoreService.listByIds(req.getIdList());
    List<StoreMallStoreDto> dataList = INSTANCE.queryListRes(list);
    this.storeMallStoreService.setName(dataList);
    return new StoreMallStoreQueryByIdListRes().setDataList(dataList);
  }
}
