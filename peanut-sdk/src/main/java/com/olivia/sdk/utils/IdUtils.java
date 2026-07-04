package com.olivia.sdk.utils;

import cn.hutool.core.util.RadixUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * ID生成工具类，提供分布式唯一ID、基于时间的ID及进制转换功能 基于JDK21特性优化，增强ID生成的安全性和可读性
 */
public final class IdUtils {

  /**
   * 纯数字日期时间格式（不含分隔符）
   */
  private static final DateTimeFormatter PURE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  /**
   * 私有构造函数，防止工具类实例化
   */
  private IdUtils() {
    throw new AssertionError("工具类不允许实例化");
  }

  /**
   * 生成分布式唯一Long类型ID 使用MyBatis-Plus的IdWorker，适用于分布式系统
   *
   * @return 唯一Long类型ID
   */
  public static Long getId() {
    return IdWorker.getId();
  }

  public static String getIdStr() {
    return IdWorker.getIdStr();
  }

  /**
   * 生成基于59进制的唯一字符串ID 由分布式ID转换而来，长度更短，适合URL等场景
   *
   * @return 59进制唯一字符串ID
   */
  public static String getUniqueId() {
    return RadixUtil.encode(RadixUtil.RADIXS_59, IdWorker.getId());
  }

  /**
   * 将Long类型ID转换为59进制字符串
   *
   * @param id 原始Long类型ID
   * @return 59进制字符串表示
   * @throws IllegalArgumentException 如果id为null
   */
  public static String getUniqueId(Long id) {
    Objects.requireNonNull(id, () -> "原始ID不能为空");
    return RadixUtil.encode(RadixUtil.RADIXS_59, id);
  }

  /**
   * 生成基于当前时间的59进制唯一ID 包含时间信息，便于追溯生成时间
   *
   * @return 基于时间的59进制唯一ID
   */
  public static String getSecondUniqueId() {
    // JDK21优化：使用java.time API替代旧版Date，线程安全且更高效
    LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
    String timeStr = PURE_DATETIME_FORMATTER.format(now).substring(1);
    return RadixUtil.encode(RadixUtil.RADIXS_59, Long.parseLong(timeStr));
  }

  /**
   * 将59进制字符串ID转换为原始Long类型
   *
   * @param uniqueId 59进制字符串ID
   * @return 原始Long类型ID
   * @throws IllegalArgumentException 如果uniqueId为null或空字符串
   */
  public static Long getLongId(String uniqueId) {
    Objects.requireNonNull(uniqueId, () -> "字符串ID不能为空");
    if (uniqueId.isBlank()) {
      throw new IllegalArgumentException("字符串ID不能为空白");
    }
    return RadixUtil.decode(RadixUtil.RADIXS_59, uniqueId);
  }
}
