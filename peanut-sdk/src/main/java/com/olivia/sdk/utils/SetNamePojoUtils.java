package com.olivia.sdk.utils;

import static com.olivia.sdk.utils.Str.FACTORY_NAME;

import com.baomidou.mybatisplus.extension.service.IService;
import com.olivia.sdk.service.pojo.NameConfig;
import com.olivia.sdk.service.pojo.SetNamePojo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetNamePojoUtils {

  static final Class<? extends IService<?>> serviceNameFactoryService = getClass("com.olivia.peanut.base.service.FactoryService");
  //factory
  public static final SetNamePojo FACTORY = new SetNamePojo()//
      .setNameFieldName(FACTORY_NAME).setServiceName(serviceNameFactoryService) //
      .setNameConfigList(List.of(new NameConfig(Str.FACTORY_ID, FACTORY_NAME)));
  static final Class<? extends IService<?>> serviceNameApsGoodsService = getClass("com.olivia.peanut.aps.service.ApsGoodsService");
  //  goods
  public static final SetNamePojo GOODS = new SetNamePojo()//
      .setNameFieldName("goodsName").setServiceName(serviceNameApsGoodsService) //
      .setNameConfigList(List.of(new NameConfig("goodsId", ("goodsName"))));
  static final Class<? extends IService<?>> serviceNameLoginAccountService = getClass("com.olivia.peanut.base.service.LoginAccountService");
  // loginUser
//  public static final SetNamePojo OP_USER_NAME = new SetNamePojo()//
//      .setNameFieldName("userName").setServiceName(serviceNameLoginAccountService)//
//      .setNameConfigList(List.of(new NameConfig("createBy").setNameField("createUserName"),//
//          new NameConfig("updateBy").setNameField("updateUserName")));

  @SuppressWarnings("unchecked")
  private static <T> T getClass(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return (T) clazz;
    } catch (ClassNotFoundException e) {
      log.error("ClassNotFoundException {}", className);
//      throw new RunException(500, "系统错误", "类型未找到:" + className);
    }
    return null;
  }

  /****
   *  获取SetNamePojo 对象
   * @param beanClass  对象查询 service接口
   * @param nameFieldName service接口返回对象中名称对应的字段名称
   * @param idField  返回列表中 service接口 入参接口ID字段名称
   * @param nameField 返回列表中  id需要映射的字段名称
   * @return SetNamePojo 对象
   */
  public static SetNamePojo getSetNamePojo(Class<? extends IService<?>> beanClass, String nameFieldName, String idField, String nameField) {
    return new SetNamePojo().setServiceName(beanClass).setNameFieldName(nameFieldName).setNameConfigList(List.of(new NameConfig(idField).setNameField(nameField)));
  }
}
