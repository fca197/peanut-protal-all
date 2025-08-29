package com.olivia.peanut.store.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import com.olivia.peanut.store.service.entity.SelectPoiReq;
import com.olivia.sdk.config.PeanutProperties;
import com.olivia.sdk.config.entity.MapConfig;
import com.olivia.sdk.utils.MDCUtils;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class GdLbsMapServiceImplTest extends TestCase {

  GdLbsMapServiceImpl  gdLbsMapServiceImpl = new GdLbsMapServiceImpl();

  @Before
  public void setUp() throws Exception {
    MDCUtils.initMdc();
    String gaoDeWebKey = "15fd63ae8c86929bce65336807b71778";
    MapConfig mapConfig = new MapConfig().setGaoDeWebKey(gaoDeWebKey).setAroundMaxPageNum(1);
    gdLbsMapServiceImpl.setPeanutProperties(new PeanutProperties().setMapConfig(mapConfig.setAroundMaxPageNum(1)));
  }

  @Test
  public void testGetGdLbsMap() {
    SelectPoiReq req = new SelectPoiReq();
    req.setKeywords("万达广场");
    gdLbsMapServiceImpl.selectAndInsertCityPoi(req);
    ThreadUtil.sleep(10000);
  }
}