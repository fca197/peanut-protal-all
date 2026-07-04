package com.olivia.sdk.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import java.util.Objects;
import org.slf4j.MDC;

/**
 * MDC上下文工具类，用于管理日志追踪的traceId和spanId，支持分布式系统日志追踪 基于JDK21特性优化，提供更安全的上下文操作和空值处理
 */
public final class MDCUtils {

  /**
   * MDC中traceId的键名
   */
  public static final String MDC_KEY_TRACE_ID = "traceId";

  /**
   * MDC中spanId的键名
   */
  public static final String MDC_KEY_SPAN_ID = "spanId";

  /**
   * 私有构造函数，防止工具类实例化
   */
  private MDCUtils() {
    throw new AssertionError("工具类不允许实例化");
  }

  /**
   * 初始化MDC上下文，自动生成traceId和spanId 默认清理已存在的MDC上下文
   */
  public static void initMdc() {
    initMdc(true);
  }

  /**
   * 生成并设置新的spanId JDK21优化：使用IdWorker生成唯一ID，适用于分布式系统
   */
  public static void nextSpanId() {
    MDC.put(MDC_KEY_SPAN_ID, IdWorker.getIdStr());
  }

  /**
   * 初始化MDC上下文，可指定是否清理现有上下文
   *
   * @param clear 是否清理已存在的MDC上下文
   */
  public static void initMdc(Boolean clear) {
    // JDK21优化：使用Objects.requireNonNullElse避免null值
    if (Objects.requireNonNullElse(clear, true)) {
      MDC.clear();
    }
    String traceId = getTraceId();
    if (StringUtils.isEmpty(traceId)) {
      MDC.put(MDC_KEY_TRACE_ID, IdWorker.getIdStr());
    }
    nextSpanId();
  }

  /**
   * 清理MDC上下文
   */
  public static void clear() {
    MDC.clear();
  }

  /**
   * 获取当前traceId
   *
   * @return 当前traceId，如果不存在则返回null
   */
  public static String getTraceId() {
    return MDC.get(MDC_KEY_TRACE_ID);
  }

  /**
   * 获取当前spanId
   *
   * @return 当前spanId，如果不存在则返回null
   */
  public static String getSpanId() {
    String spanId = MDC.get(MDC_KEY_SPAN_ID);
    if (StringUtils.isEmpty(spanId)) {
      spanId = IdWorker.getIdStr();
      MDC.put(MDC_KEY_SPAN_ID, spanId);
    }
    return spanId;
  }

  /**
   * 设置自定义spanId
   *
   * @param spanId 自定义spanId
   * @throws IllegalArgumentException 如果spanId为null或空字符串
   */
  public static void setSpanId(String spanId) {
    // JDK21优化：增强参数校验，避免设置空的spanId
    if (spanId == null || spanId.isBlank()) {
      throw new IllegalArgumentException("spanId不能为null或空字符串");
    }
    MDC.put(MDC_KEY_SPAN_ID, spanId);
  }
}
