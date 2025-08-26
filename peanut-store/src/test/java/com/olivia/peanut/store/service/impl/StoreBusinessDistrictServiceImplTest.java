package com.olivia.peanut.store.service.impl;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class StoreBusinessDistrictServiceImplTest extends TestCase {

  @Test
  public void testInsert() {
    String str = "370000.370100.370126";
    String[] split = str.split("[.]");
    for (String s : split) {
      log.info(s);
    }

  }
}