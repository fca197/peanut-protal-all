package com.olivia.sdk.ann;

import java.lang.annotation.*;
import lombok.Getter;

/**
 * 方法扩展注解，用于标记方法的特殊行为和属性配置。
 * <p>
 * 支持配置下载功能、数据权限、日志输出等特性，主要用于增强方法的处理能力。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodExt {

  /**
   * 是否为下载方法。
   *
   * @return true表示是下载方法，默认false
   */
  boolean isDownLoad() default false;

  /**
   * 数据库数据权限过滤的列名数组。
   *
   * @return 列名数组，默认空数组
   */
  String[] dbDataAuthColumn() default {};

  /**
   * 下载失败时的错误提示信息。
   *
   * @return 错误提示字符串，默认空字符串
   */
  String downLoadErrorMsg() default "";

  /**
   * 下载文件的后缀类型。
   *
   * @return 文件后缀枚举，默认XLSX
   */
  FileSuffix downLoadFileSuffix() default FileSuffix.XLSX;

  /**
   * 是否打印方法执行结果。
   *
   * @return true表示打印结果，默认true
   */
  boolean printResult() default true;

  /**
   * 支持的文件后缀枚举。
   */
  @Getter
  enum FileSuffix {

    /**
     * Microsoft Excel 电子表格文件
     */
    XLSX("xlsx"),
    /**
     * 逗号分隔值文件
     */
    CSV("csv"),
    /**
     * 文本文件
     */
    TXT("txt"),
    /**
     * 便携式文档格式
     */
    PDF("pdf"),
    /**
     * Microsoft Word 文档
     */
    DOCX("docx");

    /**
     * -- GETTER -- 获取文件后缀的字符串表示。
     * 文件后缀的字符串表示
     */
    private final String code;

    FileSuffix(String code) {
      this.code = code;
    }

  }
}
