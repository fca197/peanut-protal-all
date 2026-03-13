package com.olivia.sdk.ann;


import java.lang.annotation.*;

/**
 * 字段扩展注解 用于标识和配置实体类中的扩展字段 通常用于标记需要特殊处理的字段，比如映射到不同的数据库字段名
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldExt {

  /**
   * 字段名称 指定实际的字段名，当实体字段名与数据库字段名不一致时使用
   *
   * @return 字段名称，默认为空字符串
   */
  String fieldName() default "";

  /***
   * 是否删除前后空格
   * @return true 表示删除前后空格，false 表示不删除
   */
  boolean autoTrim() default true;
}
