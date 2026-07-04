package com.olivia.peanut.base.api.impl;

import static com.olivia.peanut.base.converter.BaseUserInfoConverter.INSTANCE;

import com.olivia.peanut.base.api.BaseUserInfoApi;
import com.olivia.peanut.base.api.entity.baseUserInfo.*;
import com.olivia.peanut.base.api.impl.listener.BaseUserInfoImportListener;
import com.olivia.peanut.base.model.BaseUserInfo;
import com.olivia.peanut.base.service.BaseUserInfoService;
import com.olivia.sdk.utils.DynamicsPage;
import com.olivia.sdk.utils.PoiExcelUtil;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户信息(BaseUserInfo)表服务实现类
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
@RestController
public class BaseUserInfoApiImpl implements BaseUserInfoApi {

  private @Resource BaseUserInfoService baseUserInfoService;

  /****
   * insert
   *
   */
  public @Override BaseUserInfoInsertRes insert(BaseUserInfoInsertReq req) {

    this.baseUserInfoService.save(req);
    return new BaseUserInfoInsertRes().setCount(1);
  }

  /****
   * deleteByIds
   *
   */
  public @Override BaseUserInfoDeleteByIdListRes deleteByIdList(BaseUserInfoDeleteByIdListReq req) {
    baseUserInfoService.removeByIds(req.getIdList());
    return new BaseUserInfoDeleteByIdListRes();
  }

  /****
   * queryList
   *
   */
  public @Override BaseUserInfoQueryListRes queryList(BaseUserInfoQueryListReq req) {
    return baseUserInfoService.queryList(req);
  }

  /****
   * updateById
   *
   */
  public @Override BaseUserInfoUpdateByIdRes updateById(BaseUserInfoUpdateByIdReq req) {
    baseUserInfoService.updateById(INSTANCE.updateReq(req));
    return new BaseUserInfoUpdateByIdRes();

  }

  public @Override DynamicsPage<BaseUserInfoExportQueryPageListInfoRes> queryPageList(BaseUserInfoExportQueryPageListReq req) {
    return baseUserInfoService.queryPageList(req);
  }

  public @Override void queryPageListExport(BaseUserInfoExportQueryPageListReq req) {
    DynamicsPage<BaseUserInfoExportQueryPageListInfoRes> page = queryPageList(req);
    List<BaseUserInfoExportQueryPageListInfoRes> list = page.getDataList();
    // 类型转换，  更换枚举 等操作
    PoiExcelUtil.export(BaseUserInfoExportQueryPageListInfoRes.class, list, "用户信息");
  }

  public @Override BaseUserInfoImportRes importData(@RequestParam("file") MultipartFile file) {
    List<BaseUserInfoImportReq> reqList = PoiExcelUtil.readData(file, new BaseUserInfoImportListener(), BaseUserInfoImportReq.class);
    // 类型转换，  更换枚举 等操作
    List<BaseUserInfo> readList = INSTANCE.importReq(reqList);
    boolean bool = baseUserInfoService.saveBatch(readList);
    int c = bool ? readList.size() : 0;
    return new BaseUserInfoImportRes().setCount(c);
  }

  public @Override BaseUserInfoQueryByIdListRes queryByIdListRes(BaseUserInfoQueryByIdListReq req) {

    List<BaseUserInfo> list = this.baseUserInfoService.listByIds(req.getIdList());
    List<BaseUserInfoDto> dataList = INSTANCE.queryListRes(list);
    this.baseUserInfoService.setName(dataList);
    return new BaseUserInfoQueryByIdListRes().setDataList(dataList);
  }

  @Override
  public BaseUserInfoDto loginPwd(BaseUserInfoDto req) {
    return this.baseUserInfoService.loginPwd(req);
  }
}
