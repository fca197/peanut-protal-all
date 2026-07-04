package com.olivia.sdk.utils;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.NonNull;

/**
 * 单向哈希工具 — 固定 48 字节输出（384 位安全强度）
 *
 * <h2>算法选型</h2>
 * <p>
 * SHA-384 原生输出恰好 48 字节（384 位），无需任何截断或折叠， 是"48字节定长"需求的天然匹配。在此之上用 HMAC 包裹加入 pepper， 兼顾抗彩虹表与抗长度扩展。
 * </p>
 *
 * <h2>算法流程</h2>
 * <pre>
 *   输入字符串（任意长度）
 *       │
 *       ▼
 *   HMAC-SHA384(input, pepper)  ──→  48 字节（384位）  ← 原生定长，零截断
 *       │                           ├── 抗彩虹表（pepper）
 *       │                           └── 抗长度扩展（HMAC）
 *       ▼
 *   按需编码输出：
 *     · byte[]   → 48 字节原始二进制
 *     · hex      → 96 位大写十六进制字符串
 *     · Base64   → 64 位 ASCII 字符串
 * </pre>
 *
 * <h2>安全特性</h2>
 * <ul>
 *   <li><b>不可逆</b>：SHA-384 是 NIST 标准单向哈希，数学上无法从输出推导输入</li>
 *   <li><b>定长输出</b>：任意输入 → 固定 48 字节，无截断误差</li>
 *   <li><b>抗彩虹表</b>：内置 pepper，攻击者无法使用预计算表</li>
 *   <li><b>抗长度扩展</b>：HMAC 结构天然免疫</li>
 *   <li><b>零信息丢失</b>：SHA-384 原生 48 字节输出，无需折叠/截断</li>
 *   <li><b>线程安全</b>：无共享可变状态</li>
 * </ul>
 *
 * <h2>三种输出格式对比</h2>
 * <pre>
 *   格式      长度       适用场景
 *   ──────── ────────── ──────────────────────
 *   byte[]   48 字节    内存中传递、二进制协议
 *   HEX      96 字符    数据库存储、日志、URL
 *   Base64   64 字符    JSON/API 传输、Token
 * </pre>
 *
 * @author olivia
 * @since JDK 25
 */

public final class SecureHashV2Util {
  // ======================== 常量配置 ========================

  /**
   * 输出字节数：48 字节 = 384 位（SHA-384 原生输出长度）
   */
  public static final int OUTPUT_BYTES = 48;

  /**
   * HMAC 算法名称
   */
  private static final String ALGORITHM = "HmacSHA384";

  /**
   * Pepper 密钥 — 防彩虹表攻击。
   * <p>
   * 生产环境务必通过环境变量 {@code HASH_PEPPER} 注入随机值（至少 48 字节）。 注意：更换 pepper 后，所有历史哈希值将失效，无法再验证。
   * </p>
   */
  private static final byte[] PEPPER = System.getenv().getOrDefault("HASH_PEPPER", "0l1v1a-s3cur1ty-p3pp3r-2026-384bit").getBytes(StandardCharsets.UTF_8);

  /**
   * 十六进制格式化器（JDK 17+，不可变，线程安全，大写输出）
   */
  private static final HexFormat HEX = HexFormat.of().withUpperCase();

  /**
   * Base64 编码器（无换行，线程安全）
   */
  private static final Base64.Encoder B64 = Base64.getEncoder().withoutPadding();

  // ======================== 核心 API ========================

  /**
   * 单向哈希（原始字节） — 任意字符串 → 48 字节
   *
   * <p>算法：HMAC-SHA384，原生输出 48 字节，零截断</p>
   *
   * @param input 任意输入字符串
   * @return 48 字节数组；input 为 null 时返回 null
   * @throws IllegalStateException 若 HMAC 初始化失败（JDK 环境异常）
   */
  public static byte[] hashBytes(String input) {
    if (input == null) {
      return null;
    }
    return hmacSha384(input.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 单向哈希（十六进制） — 任意字符串 → 96 位大写 HEX
   *
   * @param input 任意输入字符串
   * @return 96 字符大写十六进制字符串；input 为 null 时返回 null
   */
  public static String hashHex(@NonNull String input) {
    var bytes = hashBytes(input);
    return bytes == null ? null : HEX.formatHex(bytes);
  }

  /**
   * 单向哈希（Base64） — 任意字符串 → 64 位 Base64
   *
   * @param input 任意输入字符串
   * @return 64 字符 Base64 字符串；input 为 null 时返回 null
   */
  public static String hashBase64(@NonNull String input) {
    var bytes = hashBytes(input);
    return bytes == null ? null : B64.encodeToString(bytes);
  }

  /**
   * 验证输入是否匹配已存储的哈希值（恒定时间比较，防时序攻击）
   *
   * @param input        原始输入
   * @param expectedHash 已存储的哈希值（byte[] / hex / base64 均可比对）
   * @return true = 匹配
   */
  public static boolean verify(String input, String expectedHash) {
    if (input == null || expectedHash == null) {
      return false;
    }
    return constantTimeEquals(requireNonNull(hashHex(input)), expectedHash) || constantTimeEquals(requireNonNull(hashBase64(input)), expectedHash);
  }

  // ======================== 内部方法 ========================

  /**
   * HMAC-SHA384 计算 — 原生输出 48 字节
   */
  private static byte[] hmacSha384(byte[] data) {
    try {
      var mac = Mac.getInstance(ALGORITHM);
      mac.init(new SecretKeySpec(PEPPER, ALGORITHM));
      return mac.doFinal(data);
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("HMAC-SHA384 初始化失败，请检查 JDK 环境", e);
    }
  }

  /**
   * 恒定时间字符串比较 — 防止时序侧信道攻击
   */
  private static boolean constantTimeEquals(@NonNull String a, @NonNull String b) {
    if (a.length() != b.length()) {
      return false;
    }
    var result = 0;
    for (var i = 0; i < a.length(); i++) {
      result |= a.charAt(i) ^ b.charAt(i);
    }
    return result == 0;
  }

}
