package com.olivia.sdk.utils.model;

import com.olivia.sdk.utils.JSONUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)

public class UserInfo {

  /**
   * 雪花id
   */
  private Long id;
  /**
   * 姓名
   */
  private String name;
  /**
   * 年龄
   */
  private Integer age;
  /**
   * 性别 0：女；1：男
   */
  private Integer sex;
  /**
   * 性别
   */
  private String sexStr;
  /**
   * 身高 单位：cm
   */
  private Integer height;
  /**
   * 体重 单位：kg
   */
  private Integer weight;

  /**
   * 手机号
   */
  private String phone;
  /**
   * 电子邮件
   */
  private String email;

  private String address;

  @Override
  public String toString() {
    return JSONUtils.toJSONString(this);
  }
}

