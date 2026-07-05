package com.olivia.peanut.base.service.impl;

import static com.olivia.peanut.base.converter.BaseUserInfoConverter.INSTANCE;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.olivia.peanut.base.api.entity.baseUserInfo.*;
import com.olivia.peanut.base.converter.BaseUserInfoConverter;
import com.olivia.peanut.base.mapper.BaseUserInfoMapper;
import com.olivia.peanut.base.model.BaseUserInfo;
import com.olivia.peanut.base.service.BaseUserInfoService;
import com.olivia.sdk.filter.LoginUserContext;
import com.olivia.sdk.service.SetNameService;
import com.olivia.sdk.utils.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户信息(BaseUserInfo)表服务实现类
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
@Slf4j
@Service("baseUserInfoService")
@Transactional
public class BaseUserInfoServiceImpl extends MPJBaseServiceImpl<BaseUserInfoMapper, BaseUserInfo> implements BaseUserInfoService {

  // final static Cache<String, Map<String, String>> cache = CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(30, TimeUnit.MINUTES).build();

  @Resource
  SetNameService setNameService;

  @Resource
//  RedisTemplate<String, BaseUserInfoDto> userInfoDtoRedisTemplate;
  StringRedisTemplate userInfoDtoRedisTemplate;

  private static @Nullable String pwd2Db(String loginPwd) {
    return SecureHashV2Util.hashHex(loginPwd);
  }

  @PostConstruct
  public void init() {
    log.info("init");
  }

  public @Override BaseUserInfoQueryListRes queryList(BaseUserInfoQueryListReq req) {

    MPJLambdaWrapper<BaseUserInfo> q = getWrapper(req.getData());
    List<BaseUserInfo> list = this.list(q);

    List<BaseUserInfoDto> dataList = BaseUserInfoConverter.INSTANCE.queryListRes(list);
    ((BaseUserInfoService) AopContext.currentProxy()).setName(dataList);
    return new BaseUserInfoQueryListRes().setDataList(dataList);
  }

  public @Override DynamicsPage<BaseUserInfoExportQueryPageListInfoRes> queryPageList(BaseUserInfoExportQueryPageListReq req) {

    DynamicsPage<BaseUserInfo> page = new DynamicsPage<>();
    page.setCurrent(req.getPageNum()).setSize(req.getPageSize());
    setQueryListHeader(page);
    MPJLambdaWrapper<BaseUserInfo> q = getWrapper(req.getData());
    List<BaseUserInfoExportQueryPageListInfoRes> records;
    if (Boolean.TRUE.equals(req.getQueryPage())) {
      IPage<BaseUserInfo> list = this.page(page, q);
      IPage<BaseUserInfoExportQueryPageListInfoRes> dataList = list.convert(t -> $.copy(t, BaseUserInfoExportQueryPageListInfoRes.class));
      records = dataList.getRecords();
    } else {
      records = BaseUserInfoConverter.INSTANCE.queryPageListRes(this.list(q));
    }

    // 类型转换，  更换枚举 等操作

    ((BaseUserInfoService) AopContext.currentProxy()).setName(records);
    return DynamicsPage.init(page, records);
  }

  @Override
  public BaseUserInfoDto loginPwd(BaseUserInfoDto req) {
    LoginUserContext.ignoreTenantId();
    String loginPwd = req.getLoginPwd();
    String md5Pwd = pwd2Db(loginPwd);
    BaseUserInfo baseUserInfo = this.getOne(new LambdaQueryWrapper<BaseUserInfo>().eq(BaseUserInfo::getLoginName, req.getLoginName())
        .eq(BaseUserInfo::getLoginPwd, md5Pwd));
    $.requireNonNullCanIgnoreException(baseUserInfo, "用户名密码错误");
    baseUserInfo.setLoginPwd(null);
    BaseUserInfoDto baseUserInfoDto = INSTANCE.entity2Dto(baseUserInfo);
    String uuid = StringUtils.replace(UUID.randomUUID().toString(), "-", "").toUpperCase() + ":" + IdUtils.getIdStr();
    baseUserInfoDto.setUserToken(uuid);
    this.userInfoDtoRedisTemplate.opsForValue().set("user:token:" + uuid, JSONUtils.toJSONString(baseUserInfoDto), Duration.ofDays(3));
    return baseUserInfoDto;
  }

  @Override
  public void save(BaseUserInfoInsertReq req) {
    BaseUserInfo baseUserInfo = INSTANCE.insertReq(req);
    baseUserInfo.setId(null);
    String loginPwd = baseUserInfo.getLoginPwd();
    baseUserInfo.setLoginPwd(pwd2Db(loginPwd));
    this.save(baseUserInfo);
  }

  // 以下为私有对象封装

  public @Override void setName(List<? extends BaseUserInfoDto> list) {

    //   setNameService.setName(list, SetNamePojoUtils.FACTORY, SetNamePojoUtils.OP_USER_NAME);

  }


  // @SuppressWarnings("unchecked")
  private MPJLambdaWrapper<BaseUserInfo> getWrapper(BaseUserInfoDto obj) {
    MPJLambdaWrapper<BaseUserInfo> q = new MPJLambdaWrapper<>();

    LambdaQueryUtil.lambdaQueryWrapper(q, obj, BaseUserInfo.class
        // 查询条件
        , BaseEntity::getId // id
        , BaseUserInfo::getLoginName // 登录名
        , BaseUserInfo::getLoginPwd // 登录密码
        , BaseUserInfo::getRealName // 用户名
        , BaseUserInfo::getPhoneNumber // 手机号
        , BaseUserInfo::getCreateUserName // 创建人姓名
        , BaseUserInfo::getUpdateUserName // 修改人姓名
    );

    q.orderByDesc(BaseUserInfo::getId);
    return q;

  }

  private void setQueryListHeader(DynamicsPage<BaseUserInfo> page) {

  }


}

