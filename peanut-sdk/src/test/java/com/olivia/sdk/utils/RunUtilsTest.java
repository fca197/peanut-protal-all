package com.olivia.sdk.utils;

import cn.hutool.core.thread.ThreadUtil;
import com.olivia.sdk.utils.model.CallBackRunnable;
import java.time.Duration;
import java.util.List;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunUtilsTest extends TestCase {


  public void test() {
    RunUtils.run("xxxx", List.of(() -> {
      ThreadUtil.sleep(3000);
    }), true, new CallBackRunnable() {
      @Override
      public void run() {
        log.error("xxxxxxxxxx");
      }
    }, Duration.ofMillis(50));
//    RunUtils.
    log.info("over");
  }
}