package com.olivia.peanut.ann;

import com.olivia.peanut.enums.CheckEnums;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckObjectFieldValueAnn {
  /**
   * 如果使用 则校验 ImportCheck 类行验证
   *
   * @return bool
   */
  boolean useValid() default false;

  /***
   * 允许为空
   * @return bool
   *
   */
  boolean allowEmpty() default false;

  /***
   * 校验类型
   * @return CheckEnums
   */
  CheckEnums checkEnum() default CheckEnums.Str;

  /***
   * 最小
   * @return 默认 0
   */
  long min() default 0;

  /***
   * 最大
   * @return max
   */
  long max() default Integer.MAX_VALUE;

  /****
   * long 类型枚举
   * @return []
   */
  long[] longValues() default {};

  /****
   * int  枚举
   * @return []
   */
  int[] intValues() default {};

  /***
   * 字符串枚举
   * @return []
   */
  String[] strValues() default {};

  /***
   *错误消息 ，如果存在，使用该值
   * @return str
   */
  String errorMessage() default "";

  /***
   *  字段名称
   * @return str
   */
  String fieldShowName() default "未知";
}
