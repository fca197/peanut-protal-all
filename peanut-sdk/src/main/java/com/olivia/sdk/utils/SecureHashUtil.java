package com.olivia.sdk.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import lombok.NonNull;

public class SecureHashUtil {

  // JDK 25 推荐使用 HexFormat 替代传统的字节转Hex工具
  static MessageDigest digest;

  static {
    try {
      digest = MessageDigest.getInstance("SHA-384");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 将任意字符串进行不可逆哈希，返回固定 48 字节的十六进制字符串 (96位Hex)
   *
   * @param input 原始字符串
   * @return 96位十六进制字符串 (代表48字节)
   */
  public static String hashTo48Bytes(@NonNull String input) {

    try {

      byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

      // 验证长度确实为 48 字节
      assert hashBytes.length == 48 : "SHA-384 output must be 48 bytes";

      return HexFormat.of().formatHex(hashBytes).toUpperCase();
    } catch (Exception e) {
      // SHA-384 是所有标准 JDK 必须支持的算法，理论上不会抛出此异常
      throw new RuntimeException("SHA-384 algorithm not found", e);
    }
  }
}
