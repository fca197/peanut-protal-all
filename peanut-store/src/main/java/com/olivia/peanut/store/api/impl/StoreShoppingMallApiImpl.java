package com.olivia.peanut.store.api.impl;

import static com.olivia.peanut.store.converter.StoreShoppingMallConverter.INSTANCE;

import com.olivia.peanut.store.api.StoreShoppingMallApi;
import com.olivia.peanut.store.api.entity.storeShoppingMall.*;
import com.olivia.peanut.store.api.impl.listener.StoreShoppingMallImportListener;
import com.olivia.peanut.store.model.StoreShoppingMall;
import com.olivia.peanut.store.service.StoreShoppingMallService;
import com.olivia.sdk.utils.DynamicsPage;
import com.olivia.sdk.utils.PoiExcelUtil;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 门店 商场(StoreShoppingMall)表服务实现类
 *
 * @author admin
 * @since 2025-08-29 15:54:25
 */
@RestController
public class StoreShoppingMallApiImpl implements StoreShoppingMallApi {

  private @Resource StoreShoppingMallService storeShoppingMallService;

  /****
   * insert
   *
   */
  public @Override StoreShoppingMallInsertRes insert(StoreShoppingMallInsertReq req) {
    StoreShoppingMall storeShoppingMall = INSTANCE.insertReq(req);
    this.storeShoppingMallService.saveStoreShoppingMall(storeShoppingMall);
    return new StoreShoppingMallInsertRes().setCount(1);
  }

  /****
   * deleteByIds
   *
   */
  public @Override StoreShoppingMallDeleteByIdListRes deleteByIdList(StoreShoppingMallDeleteByIdListReq req) {
    storeShoppingMallService.removeByIds(req.getIdList());
    return new StoreShoppingMallDeleteByIdListRes();
  }

  /****
   * queryList
   *
   */
  public @Override StoreShoppingMallQueryListRes queryList(StoreShoppingMallQueryListReq req) {
    return storeShoppingMallService.queryList(req);
  }

  /****
   * updateById
   *
   */
  public @Override StoreShoppingMallUpdateByIdRes updateById(StoreShoppingMallUpdateByIdReq req) {
    StoreShoppingMall storeShoppingMall = INSTANCE.updateReq(req);
    this.storeShoppingMallService.checkStoreShoppingMall(storeShoppingMall);
    storeShoppingMallService.updateById(storeShoppingMall);
    return new StoreShoppingMallUpdateByIdRes();

  }

  public @Override DynamicsPage<StoreShoppingMallExportQueryPageListInfoRes> queryPageList(StoreShoppingMallExportQueryPageListReq req) {
    return storeShoppingMallService.queryPageList(req);
  }

  public @Override void queryPageListExport(StoreShoppingMallExportQueryPageListReq req) {
    DynamicsPage<StoreShoppingMallExportQueryPageListInfoRes> page = queryPageList(req);
    List<StoreShoppingMallExportQueryPageListInfoRes> list = page.getDataList();
    // 类型转换，  更换枚举 等操作
    PoiExcelUtil.export(StoreShoppingMallExportQueryPageListInfoRes.class, list, "门店 商场");
  }

  public @Override StoreShoppingMallImportRes importData(@RequestParam("file") MultipartFile file) {
    List<StoreShoppingMallImportReq> reqList = PoiExcelUtil.readData(file, new StoreShoppingMallImportListener(), StoreShoppingMallImportReq.class);
    // 类型转换，  更换枚举 等操作
    List<StoreShoppingMall> readList = INSTANCE.importReq(reqList);
    boolean bool = storeShoppingMallService.saveBatch(readList);
    int c = bool ? readList.size() : 0;
    return new StoreShoppingMallImportRes().setCount(c);
  }

  public @Override StoreShoppingMallQueryByIdListRes queryByIdListRes(StoreShoppingMallQueryByIdListReq req) {

    List<StoreShoppingMall> list = this.storeShoppingMallService.listByIds(req.getIdList());
    List<StoreShoppingMallDto> dataList = INSTANCE.queryListRes(list);
    this.storeShoppingMallService.setName(dataList);
    return new StoreShoppingMallQueryByIdListRes().setDataList(dataList);
  }
}
