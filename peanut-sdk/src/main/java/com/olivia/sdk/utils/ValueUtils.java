package com.olivia.sdk.utils;

import cn.hutool.core.date.DateUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/***
 *
 */
public class ValueUtils {


  public static String value2Str(Object v) {
    if (Objects.isNull(v)) {
      return "";
    }
    return switch (v.getClass().getSimpleName()) {
      case "String", "Integer", "Long", "Double", "Float", "Boolean", "BigInteger", "BigDecimal" -> v.toString();
      case "LocalDate" -> ((LocalDate) v).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      case "LocalDateTime" -> ((LocalDateTime) v).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      case "Date" -> DateUtil.format((Date) v, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      case "List" -> JSONUtils.toJSONString(v);
      default -> throw new IllegalStateException("Unexpected value: " + v.getClass().getSimpleName());
    };
  }
}
