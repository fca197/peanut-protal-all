package com.olivia.sdk.ann;


import cn.hutool.core.util.DesensitizedUtil.DesensitizedType;
import java.lang.annotation.*;

/**
 * 数据脱敏注解
 * 用于标识需要进行数据脱敏处理的字段，如手机号、身份证号、邮箱等敏感信息
 * 通过指定脱敏类型来实现不同的脱敏策略
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MaskValue {

  /**
   * 脱敏类型
   * 指定数据脱敏的方式，默认为 FIRST_MASK（首字母隐藏）
   *
   * @return 脱敏类型
   */
  DesensitizedType value() default DesensitizedType.FIRST_MASK;

}
