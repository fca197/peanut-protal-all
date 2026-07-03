package com.olivia.peanut.base.api.entity.baseUserInfo;

import com.olivia.peanut.portal.api.entity.BaseEntityDto;
import com.olivia.sdk.ann.InsertCheck;
import com.olivia.sdk.ann.UpdateCheck;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息(BaseUserInfo)查询对象返回
 *
 * @author admin
 * @since 2026-07-04 01:25:45
 */
//@Accessors(chain=true)
@Getter
@Setter
//@SuppressWarnings("serial")
public class BaseUserInfoDto extends BaseEntityDto {

  /***
   *  登录名
   */
  @NotBlank(message = "登录名不能为空", groups = {InsertCheck.class, UpdateCheck.class, LoginCheck.class})
  private String loginName;
  /***
   *  登录密码
   */
  @NotBlank(message = "登录密码不能为空", groups = {InsertCheck.class, UpdateCheck.class, LoginCheck.class})
  private String loginPwd;
  /***
   *  用户名
   */
  @NotBlank(message = "用户名不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String realName;
  /***
   *  手机号
   */
  @NotBlank(message = "手机号不能为空", groups = {InsertCheck.class, UpdateCheck.class})
  private String phoneNumber;

}


