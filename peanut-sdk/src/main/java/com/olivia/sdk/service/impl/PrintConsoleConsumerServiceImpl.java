package com.olivia.sdk.service.impl;

import com.olivia.sdk.service.ConsumerService;
import com.olivia.sdk.service.pojo.ConsumerReq;
import com.olivia.sdk.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 控制台打印消费者服务实现类 用于将消费的消息内容打印到控制台，主要用于调试和日志记录
 */
@Slf4j
@Service
public class PrintConsoleConsumerServiceImpl implements ConsumerService {

  /**
   * 业务标识：控制台打印
   */
  private static final String BIZ_KEY = "printConsole";

  @Override
  public String bizKey() {
    return BIZ_KEY;
  }

  @Override
  public void consumer(ConsumerReq req) {

    // 根据日志级别输出不同详细程度的信息
    if (log.isDebugEnabled()) {
      log.debug("控制台打印消费者 - 消费消息: {}", JSONUtils.toJSONString(req));
    }

  }
}
