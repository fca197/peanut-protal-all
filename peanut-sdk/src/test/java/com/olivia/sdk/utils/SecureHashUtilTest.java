package com.olivia.sdk.utils;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecureHashUtilTest extends TestCase {


  public void testHashTo48Bytes() {
    String va = SecureHashUtil.hashTo48Bytes("123456");
    log.info("va:{} {}", va, va.length());
  }
}