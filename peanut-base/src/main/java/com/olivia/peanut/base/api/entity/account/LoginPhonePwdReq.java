package com.olivia.peanut.base.api.entity.account;

import com.olivia.sdk.ann.FieldExt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class LoginPhonePwdReq {

  @NotBlank(message = "登陆手机号不能为空")
  @FieldExt
//  @Pattern(regexp = "^1((34[0-8])|(8\\d{2})|(([35][0-35-9]|4[579]|66|7[35678]|9[1389])\\d{1}))\\d{7}$")
  private String loginPhone;
  @NotBlank(message = "密码不能为空")
  @Size(min = 5, max = 60, message = "密码长度必须为5-20位")
  @FieldExt(autoTrim = false)
  private String pwd;
}
