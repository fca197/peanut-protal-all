package com.olivia.sdk.utils;

import static com.olivia.sdk.utils.SecureHashV2Util.*;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecureHashV2UtilTest extends TestCase {


  public void test() {

    String v = hashHex("123456");
    log.info("hashHex v {} {}", v.length(), v);
    v = SecureHashV2Util.hashBase64("123456");
    log.info("hashBase64 v {} {}", v.length(), v);
    var cases = new String[]{"",                          // 空字符串
        "a",                         // 单字符
        "hello",                     // 普通英文
        "hello world",               // 含空格
        "你好，世界！",               // 中文
        "🔐secret🔑",                 // emoji
        "password123",               // 密码样例
        "a".repeat(10_000),          // 超长字符串
        "admin",                     // 常见用户名
        "admin"                      // 重复（验证确定性）
    };

    System.out.println("=".repeat(78));
    System.out.println("  OneWayHash48Bytes — 单向哈希测试 (JDK 25)");
    System.out.println("  算法: HMAC-SHA384 → 48 bytes (384-bit), 零截断");
    System.out.println("  Pepper来源: " + (System.getenv().containsKey("HASH_PEPPER") ? "环境变量 HASH_PEPPER" : "默认值"));
    System.out.println("=".repeat(78));
    System.out.println();

    // ── 输出格式对比 ──
    System.out.println("  ┌──────────────────────────────────────────────────────────────────────┐");
    System.out.println("  │ 格式对比（以 \"hello world\" 为例）                                    │");
    System.out.println("  ├──────────┬──────────┬──────────────────────────────────────────────────┤");
    var sample = "hello world";
    var b = hashBytes(sample);
//    System.out.printf("  │ byte[]   │ %d bytes  │ %s...%n", b.length, toShortHex(b, 16));
    System.out.printf("  │ HEX      │ %d chars  │ %s%n", hashHex(sample).length(), hashHex(sample));
    System.out.printf("  │ Base64   │ %d chars  │ %s%n", hashBase64(sample).length(), hashBase64(sample));
    System.out.println("  └──────────┴──────────┴──────────────────────────────────────────────────┘");
    System.out.println();

    // ── 全量测试 ──
    System.out.println("  HEX 输出（96字符 = 48字节）:");
    System.out.println("  " + "-".repeat(74));
    for (var input : cases) {
      var display = input.isEmpty() ? "(空字符串)" : input.length() > 30 ? input.substring(0, 30) + "..." : input;
      var hex = hashHex(input);
      // 验证字节长度
      assert hex.length() == OUTPUT_BYTES * 2 : "长度异常";
      System.out.printf("  %-34s → %s  [%dB/%d字符]%n", display, hex, OUTPUT_BYTES, hex.length());
    }

    System.out.println();
    System.out.println("  Base64 输出（64字符 = 48字节）:");
    System.out.println("  " + "-".repeat(74));
    for (var input : cases) {
      var display = input.isEmpty() ? "(空字符串)" : input.length() > 30 ? input.substring(0, 30) + "..." : input;
      System.out.printf("  %-34s → %s%n", display, hashBase64(input));
    }

    // ── 验证测试 ──
    System.out.println();
    System.out.println("-".repeat(78));
    System.out.println("  验证测试（verify 支持 HEX 和 Base64 两种格式）");
    System.out.println("-".repeat(78));
    var hexHash = hashHex("hello world");
    var b64Hash = hashBase64("hello world");
    System.out.printf("  verify(正确输入, HEX哈希)       = %s%n", verify("hello world", hexHash));
    System.out.printf("  verify(正确输入, Base64哈希)    = %s%n", verify("hello world", b64Hash));
    System.out.printf("  verify(错误输入, HEX哈希)       = %s%n", verify("wrong", hexHash));
    System.out.printf("  verify(null, ...)              = %s%n", verify(null, hexHash));

    // ── 雪崩效应 ──
    System.out.println();
    System.out.println("-".repeat(78));
    System.out.println("  雪崩效应测试（微改输入 → 输出完全不同）");
    System.out.println("-".repeat(78));
    var h1 = hashHex("hello");
    var h2 = hashHex("hellp");
    var diff = 0;
    for (var i = 0; i < h1.length(); i++) {
      if (h1.charAt(i) != h2.charAt(i)) {
        diff++;
      }
    }
    System.out.printf("  hash(\"hello\")  = %s%n", h1);
    System.out.printf("  hash(\"hellp\")  = %s%n", h2);
    System.out.printf("  差异字符数: %d / %d (%.1f%%)%n", diff, h1.length(), diff * 100.0 / h1.length());

    // ── 字节长度断言 ──
    System.out.println();
    System.out.println("-".repeat(78));
    System.out.println("  字节长度断言");
    System.out.println("-".repeat(78));
    for (var input : cases) {
      var bytes = hashBytes(input);
      var ok = bytes.length == OUTPUT_BYTES;
      System.out.printf("  %-34s → %d bytes  %s%n", input.isEmpty() ? "(空)" : input.length() > 30 ? input.substring(0, 30) + "..." : input, bytes.length,
          ok ? "✅" : "❌");
    }
  }
}