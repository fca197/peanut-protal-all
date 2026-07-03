package com.olivia.sdk.mybatis.type.model;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import com.olivia.sdk.utils.JSONUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 增强型Map工具类 提供类型安全的getter方法，支持多种数据类型的自动转换
 */
@Slf4j
@SuppressWarnings("unchecked")
public class MapSub extends HashMap<String, Object> {

  /**
   * 默认日期时间格式化器
   */
  private static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  /**
   * 默认日期格式化器
   */
  private static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  /**
   * 构造一个空的MapSub
   */
  public MapSub() {
    super();
  }

  /**
   * 基于现有Map构造MapSub（默认使用不可变Map）
   *
   * @param map 源Map
   */
  public MapSub(Map<String, Object> map) {
    putAll(map);
  }

  /**
   * 创建一个空的MapSub实例
   *
   * @return 空的MapSub
   */
  public static MapSub of() {
    return new MapSub();
  }

  /**
   * 基于现有Map创建MapSub实例
   *
   * @param map 源Map
   * @return MapSub实例
   */
  public static MapSub of(Map<String, Object> map) {
    return new MapSub(map);
  }

  /**
   * 将对象转换为指定类型
   *
   * @param value      要转换的对象
   * @param targetType 目标类型
   * @param <T>        目标类型泛型
   * @return 转换后的对象，转换失败返回null
   */
  private <T> T convertToType(Object value, Class<T> targetType) {
    if (Objects.isNull(value)) {
      return null;
    }
    // 如果已经是目标类型，直接返回
    if (targetType.isInstance(value)) {
      return targetType.cast(value);
    }
    try {
      // 根据目标类型进行转换
      if (targetType == Long.class) {
        return targetType.cast(convertToLong(value));
      } else if (targetType == Integer.class) {
        return targetType.cast(convertToInteger(value));
      } else if (targetType == Boolean.class) {
        return targetType.cast(convertToBoolean(value));
      } else if (targetType == String.class) {
        return targetType.cast(convertToString(value));
      } else if (targetType == LocalDate.class) {
        return targetType.cast(convertToLocalDate(value));
      } else if (targetType == LocalDateTime.class) {
        return targetType.cast(convertToLocalDateTime(value));
      } else if (targetType == Date.class) {
        return targetType.cast(convertToDate(value));
      } else {
        log.warn("不支持的转换类型: {} value: {}", targetType.getName(), value);
        return null;
      }
    } catch (Exception e) {
      log.error("类型转换失败 - 源值: {}, 目标类型: {}", value, targetType.getName(), e);
      return null;
    }
  }

  /**
   * 将对象转换为指定类型的列表
   *
   * @param value      要转换的对象
   * @param targetType 列表元素的目标类型
   * @param <T>        目标类型泛型
   * @return 转换后的列表，转换失败返回null
   */
  private <T> List<T> convertToList(Object value, Class<T> targetType) {
    if (value == null) {
      return null;
    }
    // 如果已经是列表类型
    if (value instanceof List<?> list) {
      if (CollUtil.isEmpty(list)) {
        return CollUtil.newArrayList();
      }
      // 检查列表元素是否已经是目标类型
      if (targetType.isInstance(list.get(0))) {
        return (List<T>) list;
      }
    }
    // 通过JSON序列化/反序列化进行转换
    try {
      return JSONUtils.readList(JSONUtils.toJSONString(value), targetType);
    } catch (Exception e) {
      log.error("列表类型转换失败 - 源值: {}, 目标元素类型: {}", value, targetType.getName(), e);
      return null;
    }
  }

  /**
   * 获取Long类型的值
   *
   * @param key 键名
   * @return Long值，转换失败返回null
   */
  public Long getLong(String key) {
    return convertToType(get(key), Long.class);
  }

  /**
   * 获取Integer类型的值
   *
   * @param key 键名
   * @return Integer值，转换失败返回null
   */
  public Integer getInteger(String key) {
    return convertToType(get(key), Integer.class);
  }

  /**
   * 获取Boolean类型的值
   *
   * @param key 键名
   * @return Boolean值，转换失败返回null
   */
  public Boolean getBoolean(String key) {
    return convertToType(get(key), Boolean.class);
  }

  /**
   * 获取String类型的值
   *
   * @param key 键名
   * @return String值，转换失败返回null
   */
  public String getString(String key) {
    return convertToType(get(key), String.class);
  }

  /**
   * 获取LocalDate类型的值
   *
   * @param key 键名
   * @return LocalDate值，转换失败返回null
   */
  public LocalDate getLocalDate(String key) {
    return convertToType(get(key), LocalDate.class);
  }

  /**
   * 获取LocalDateTime类型的值
   *
   * @param key 键名
   * @return LocalDateTime值，转换失败返回null
   */
  public LocalDateTime getLocalDateTime(String key) {
    return convertToType(get(key), LocalDateTime.class);
  }

  /**
   * 获取Date类型的值
   *
   * @param key 键名
   * @return Date值，转换失败返回null
   */
  public Date getDate(String key) {
    return convertToType(get(key), Date.class);
  }

  /**
   * 获取Long类型的列表
   *
   * @param key 键名
   * @return Long列表，转换失败返回空列表
   */
  public List<Long> getLongList(String key) {
    List<Long> result = convertToList(get(key), Long.class);
    return Objects.nonNull(result) ? result : CollUtil.newArrayList();
  }

  /**
   * 获取String类型的列表
   *
   * @param key 键名
   * @return String列表，转换失败返回空列表
   */
  public List<String> getStringList(String key) {
    List<String> result = convertToList(get(key), String.class);
    return Objects.nonNull(result) ? result : CollUtil.newArrayList();
  }

  /**
   * 将对象转换为Long
   *
   * @param value 要转换的对象
   * @return 转换后的Long
   */
  private Long convertToLong(Object value) {
    if (value instanceof Number number) {
      return number.longValue();
    }
    return Long.parseLong(value.toString().trim());
  }

  /**
   * 将对象转换为Integer
   *
   * @param value 要转换的对象
   * @return 转换后的Integer
   */
  private Integer convertToInteger(Object value) {
    if (value instanceof Number number) {
      return number.intValue();
    }
    return Integer.parseInt(value.toString().trim());
  }

  /**
   * 将对象转换为Boolean
   *
   * @param value 要转换的对象
   * @return 转换后的Boolean
   */
  private Boolean convertToBoolean(Object value) {
    if (value instanceof Number number) {
      return number.intValue() != 0;
    }
    return BooleanUtil.toBoolean(value.toString());
  }

  /**
   * 将对象转换为String
   *
   * @param value 要转换的对象
   * @return 转换后的String
   */
  private String convertToString(Object value) {
    return value.toString();
  }

  /**
   * 将对象转换为LocalDate
   *
   * @param value 要转换的对象
   * @return 转换后的LocalDate
   */
  private LocalDate convertToLocalDate(Object value) {
    if (value instanceof LocalDateTime dateTime) {
      return dateTime.toLocalDate();
    }
    if (value instanceof Date date) {
      return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }
    String str = value.toString().trim();
    try {
      return LocalDate.parse(str, DEFAULT_DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      // 尝试其他常见格式
      return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
  }

  /**
   * 将对象转换为LocalDateTime
   *
   * @param value 要转换的对象
   * @return 转换后的LocalDateTime
   */
  private LocalDateTime convertToLocalDateTime(Object value) {
    if (value instanceof LocalDate date) {
      return date.atStartOfDay();
    }
    if (value instanceof Date date) {
      return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }
    String str = value.toString().trim();
    try {
      return LocalDateTime.parse(str, DEFAULT_DATETIME_FORMATTER);
    } catch (DateTimeParseException e) {
      // 尝试其他常见格式
      return LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
  }

  /**
   * 将对象转换为Date
   *
   * @param value 要转换的对象
   * @return 转换后的Date
   */
  private Date convertToDate(Object value) {
    if (value instanceof LocalDateTime dateTime) {
      return Date.from(dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
    if (value instanceof LocalDate date) {
      return Date.from(date.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
    return DateUtil.parse(value.toString().trim());
  }
}
