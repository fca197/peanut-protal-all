package com.olivia.peanut.base.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.olivia.sdk.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 用户信息(BaseUserInfo)表实体类
 *
 * @author admin
 * @since 2026-07-04 01:25:44
 */
@Accessors(chain = true)
@Getter
@Setter
//@SuppressWarnings("serial")
@TableName(value = "base_user_info")
public class BaseUserInfo extends BaseEntity<BaseUserInfo> {

  /***
   *  登录名
   */
  //@TableField(value= "login_name")
  private String loginName;
  /***
   *  登录密码
   */
  //@TableField(value= "login_pwd")
  private String loginPwd;
  /***
   *  用户名
   */
  //@TableField(value= "real_name")
  private String realName;
  /***
   *  手机号
   */
  //@TableField(value= "phone_number")
  private String phoneNumber;

}

