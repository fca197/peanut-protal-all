package com.olivia.sdk.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.olivia.sdk.exception.CanIgnoreException;
import com.olivia.sdk.exception.RunException;
import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 通用工具类，提供断言校验、对象拷贝、集合处理、类型转换等基础功能 类名使用$简化调用，提升开发效率
 */
@Slf4j
public final class $ {

  /**
   * 私有构造函数，防止工具类实例化
   */
  private $() {
    throw new AssertionError("工具类不允许实例化");
  }

  /**
   * 断言不是所有对象都为null，否则抛出异常
   *
   * @param errMsg 异常消息
   * @param objs   待检查的对象数组
   * @throws RunException 如果所有对象都为null
   */
  public static void assertNotAllNull(String errMsg, Object... objs) {
    if (objs == null || objs.length == 0) {
      return;
    }

    boolean allNull = true;
    for (Object obj : objs) {
      if (obj != null) {
        allNull = false;
        break;
      }
    }

    if (allNull) {
      throw new RunException(errMsg);
    }
  }

  /**
   * 获取第一个非null的对象
   *
   * @param objs 待检查的对象数组
   * @param <T>  对象类型
   * @return 第一个非null的对象，全部为null则返回null
   */
  @SuppressWarnings("unchecked")
  public static <T> T firstNotNull(Object... objs) {
    if (objs == null) {
      return null;
    }

    for (Object obj : objs) {
      if (obj != null) {
        return (T) obj;
      }
    }
    return null;
  }

  /**
   * 判断源对象是否与目标对象数组中的任何一个相等 支持null值比较：源为null时，目标数组包含null则返回true
   *
   * @param source 源对象
   * @param target 目标对象数组
   * @return 是否匹配
   */
  public static boolean equalsAny(Object source, Object... target) {
    if (target == null || target.length == 0) {
      return false;
    }

    if (source == null) {
      for (Object obj : target) {
        if (obj == null) {
          return true;
        }
      }
      return false;
    }

    for (Object obj : target) {
      if (source.equals(obj)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 抛出可忽略异常
   *
   * @param msg 异常消息
   * @throws CanIgnoreException 可忽略的异常
   */
  public static void throwCanIgnoreException(String msg) {
    throw new CanIgnoreException(msg);
  }

  /**
   * 断言表达式为true，否则抛出可忽略异常
   *
   * @param expression 布尔表达式
   * @param msg        异常消息
   * @throws CanIgnoreException 如果表达式为false
   */
  public static void assertTrueCanIgnoreException(boolean expression, String msg) {
    if (!expression) {
      throw new CanIgnoreException(msg);
    }
  }

  public static void assertTrueCanIgnoreException(boolean expression, String... errMsg) {
    if (!expression) {
      throw new CanIgnoreException(String.join("", errMsg));
    }
  }

  /**
   * 断言表达式为true，否则抛出指定异常
   *
   * @param expression        布尔表达式
   * @param exceptionSupplier 异常提供者
   * @throws RuntimeException 如果表达式为false
   */
  public static void assertTrueException(boolean expression, Supplier<RuntimeException> exceptionSupplier) {
    if (!expression) {
      throw exceptionSupplier.get();
    }
  }

  /**
   * 校验对象不为null，否则抛出可忽略异常
   *
   * @param obj 待校验对象
   * @param msg 异常消息
   * @throws CanIgnoreException 如果对象为null
   */
  public static void requireNonNullCanIgnoreException(Object obj, String msg) {
    if (obj == null) {
      log.warn("参数校验失败: {}", msg);
      throw new CanIgnoreException(msg);
    }
  }

  /**
   * 校验集合不为空，否则抛出可忽略异常
   *
   * @param list 待校验集合
   * @param msg  异常消息
   * @throws CanIgnoreException 如果集合为空
   */
  public static void requireNonNullCanIgnoreException(List<?> list, String msg) {
    if (CollUtil.isEmpty(list)) {
      log.warn("集合校验失败: {}", msg);
      throw new CanIgnoreException(msg);
    }
  }

  /**
   * 校验字符串不为空，否则抛出可忽略异常
   *
   * @param str 待校验字符串
   * @param msg 异常消息
   * @throws CanIgnoreException 如果字符串为空
   */
  public static void requireNonNullCanIgnoreException(String str, String msg) {
    if (StringUtils.isBlank(str)) {
      log.warn("字符串校验失败: {}", msg);
      throw new CanIgnoreException(msg);
    }
  }

  /**
   * 对象拷贝（源不为null时）
   *
   * @param source 源对象
   * @param clazz  目标类
   * @param <T>    目标类型
   * @return 拷贝后的对象
   */
  public static <T> T copy(Object source, Class<T> clazz) {
    return copy(source, clazz, true);
  }

  /**
   * 对象拷贝，支持源为null时创建空对象
   *
   * @param source                  源对象
   * @param clazz                   目标类
   * @param createObjWhenSourceNull 源为null时是否创建空对象
   * @param <T>                     目标类型
   * @return 拷贝后的对象
   */

  public static <T> T copy(Object source, Class<T> clazz, boolean createObjWhenSourceNull) {
    requireNonNullCanIgnoreException(clazz, "目标类不能为空");

    if (source == null) {
      if (createObjWhenSourceNull) {
        try {
          return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
          log.error("创建空对象失败 [类: {}]", clazz.getName(), e);
        }
      }
      return null;
    }

    return BeanUtil.copyProperties(source, clazz);
  }

  /**
   * 集合对象拷贝
   *
   * @param list  源集合
   * @param clazz 目标类
   * @param <T>   目标类型
   * @param <S>   源类型
   * @return 拷贝后的集合
   */
  public static <T, S> List<T> copyList(List<S> list, Class<T> clazz) {
    return copyList(list, clazz, null);
  }

  /**
   * 带后置处理的集合对象拷贝
   *
   * @param list     源集合
   * @param clazz    目标类
   * @param consumer 目标对象后置处理器
   * @param <T>      目标类型
   * @param <S>      源类型
   * @return 拷贝后的集合
   */
  public static <T, S> List<T> copyList(List<S> list, Class<T> clazz, Consumer<T> consumer) {
    if (CollUtil.isEmpty(list) || clazz == null) {
      return List.of();
    }

    List<T> resultList = BeanUtil.copyToList(list, clazz);

    if (consumer != null) {
      resultList.forEach(consumer);
    }

    return resultList;
  }

  /**
   * 带双向处理的集合对象拷贝
   *
   * @param list     源集合
   * @param clazz    目标类
   * @param consumer 源对象与目标对象的处理器
   * @param <T>      目标类型
   * @param <S>      源类型
   * @return 拷贝后的集合
   */
  public static <T, S> List<T> copyListByBiConsumer(List<S> list, Class<T> clazz, BiConsumer<S, T> consumer) {
    if (CollUtil.isEmpty(list) || clazz == null) {
      return List.of();
    }

    if (consumer == null) {
      return copyList(list, clazz);
    }

    return list.stream().map(source -> {
      T target = copy(source, clazz);
      consumer.accept(source, target);
      return target;
    }).collect(Collectors.toList());
  }

  /**
   * 拷贝对象属性到目标对象
   *
   * @param source 源对象
   * @param target 目标对象
   */
  public static void copy(Object source, Object target) {
    if (source != null && target != null) {
      BeanUtil.copyProperties(source, target);
    }
  }

  /**
   * 解析字符串为Duration对象（支持s/m/h/d/w单位）
   *
   * @param duration 时间字符串，如"5s"、"3h"、"2d"
   * @return Duration对象
   * @throws CanIgnoreException 如果格式错误
   */
  public static Duration getDuration(String duration) {
    requireNonNullCanIgnoreException(duration, "时间字符串不能为空");

    String timeStr = ReUtil.getGroup0("\\d+", duration);
    String unitStr = ReUtil.getGroup0("[a-zA-Z]+", duration).toUpperCase();

    requireNonNullCanIgnoreException(timeStr, "时间格式错误，无法提取数值: " + duration);
    requireNonNullCanIgnoreException(unitStr, "时间格式错误，无法提取单位: " + duration);

    long time;
    try {
      time = Long.parseLong(timeStr);
    } catch (NumberFormatException e) {
      throw new CanIgnoreException("时间数值格式错误: " + timeStr);
    }

    return switch (unitStr) {
      case "S" -> Duration.ofSeconds(time);
      case "M" -> Duration.ofMinutes(time);
      case "H" -> Duration.ofHours(time);
      case "D" -> Duration.ofDays(time);
      case "W" -> Duration.ofDays(time * 7);
      default -> throw new CanIgnoreException("不支持的时间单位: " + unitStr);
    };
  }

  /**
   * 当值不为null时执行消费操作
   *
   * @param value    待检查的值
   * @param consumer 消费函数
   * @param <T>      值类型
   */
  public static <T> void consumerNotNull(T value, Consumer<T> consumer) {
    if (value != null && consumer != null) {
      consumer.accept(value);
    }
  }

  /**
   * 获取非空集合（如果输入为null或空则返回空集合）
   *
   * @param list 输入集合
   * @param <T>  元素类型
   * @return 非空集合
   */
  public static <T> List<T> getNoNullList(List<T> list) {
    return CollUtil.isEmpty(list) ? List.of() : list;
  }

  /****
   * 字符串转list
   * @param strValue  自从
   * @param separator 分隔符
   * @return list 非空 ，非可变list
   */
  public static List<String> getStringList(String strValue, String separator) {
    if (StrUtil.isBlank(strValue)) {
      return List.of();
    }
    return StrUtil.split(strValue, separator, true, true).stream().distinct().sorted().toList();
  }

}
