package com.olivia.peanut.store.api.impl;

import static com.olivia.peanut.store.converter.StoreBusinessDistrictConverter.INSTANCE;

import com.olivia.peanut.store.api.StoreBusinessDistrictApi;
import com.olivia.peanut.store.api.entity.storeBusinessDistrict.*;
import com.olivia.peanut.store.api.impl.listener.StoreBusinessDistrictImportListener;
import com.olivia.peanut.store.model.StoreBusinessDistrict;
import com.olivia.peanut.store.service.StoreBusinessDistrictService;
import com.olivia.sdk.utils.DynamicsPage;
import com.olivia.sdk.utils.PoiExcelUtil;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商圈(StoreBusinessDistrict)表服务实现类
 *
 * @author admin
 * @since 2025-08-24 21:01:54
 */
@RestController
public class StoreBusinessDistrictApiImpl implements StoreBusinessDistrictApi {

  private @Resource StoreBusinessDistrictService storeBusinessDistrictService;

  /****
   * insert
   *
   */
  public @Override StoreBusinessDistrictInsertRes insert(StoreBusinessDistrictInsertReq req) {

    this.storeBusinessDistrictService.save(req);
    return new StoreBusinessDistrictInsertRes().setCount(1);
  }

  /****
   * deleteByIds
   *
   */
  public @Override StoreBusinessDistrictDeleteByIdListRes deleteByIdList(StoreBusinessDistrictDeleteByIdListReq req) {
    storeBusinessDistrictService.removeByIds(req.getIdList());
    return new StoreBusinessDistrictDeleteByIdListRes();
  }

  /****
   * queryList
   *
   */
  public @Override StoreBusinessDistrictQueryListRes queryList(StoreBusinessDistrictQueryListReq req) {
    return storeBusinessDistrictService.queryList(req);
  }

  /****
   * updateById
   *
   */
  public @Override StoreBusinessDistrictUpdateByIdRes updateById(StoreBusinessDistrictUpdateByIdReq req) {
    storeBusinessDistrictService.updateById(INSTANCE.updateReq(req));
    return new StoreBusinessDistrictUpdateByIdRes();

  }

  public @Override DynamicsPage<StoreBusinessDistrictExportQueryPageListInfoRes> queryPageList(StoreBusinessDistrictExportQueryPageListReq req) {
    return storeBusinessDistrictService.queryPageList(req);
  }

  public @Override void queryPageListExport(StoreBusinessDistrictExportQueryPageListReq req) {
    DynamicsPage<StoreBusinessDistrictExportQueryPageListInfoRes> page = queryPageList(req);
    List<StoreBusinessDistrictExportQueryPageListInfoRes> list = page.getDataList();
    // 类型转换，  更换枚举 等操作
    PoiExcelUtil.export(StoreBusinessDistrictExportQueryPageListInfoRes.class, list, "商圈");
  }

  public @Override StoreBusinessDistrictImportRes importData(@RequestParam("file") MultipartFile file) {
    List<StoreBusinessDistrictImportReq> reqList = PoiExcelUtil.readData(file, new StoreBusinessDistrictImportListener(), StoreBusinessDistrictImportReq.class);
    // 类型转换，  更换枚举 等操作
    List<StoreBusinessDistrict> readList = INSTANCE.importReq(reqList);
    boolean bool = storeBusinessDistrictService.saveBatch(readList);
    int c = bool ? readList.size() : 0;
    return new StoreBusinessDistrictImportRes().setCount(c);
  }

  public @Override StoreBusinessDistrictQueryByIdListRes queryByIdListRes(StoreBusinessDistrictQueryByIdListReq req) {

    List<StoreBusinessDistrict> list = this.storeBusinessDistrictService.listByIds(req.getIdList());
    List<StoreBusinessDistrictDto> dataList = INSTANCE.queryListRes(list);
    this.storeBusinessDistrictService.setName(dataList);
    return new StoreBusinessDistrictQueryByIdListRes().setDataList(dataList);
  }
}
