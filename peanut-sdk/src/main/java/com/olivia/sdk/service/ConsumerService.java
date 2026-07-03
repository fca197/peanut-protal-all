package com.olivia.sdk.service;

import com.olivia.sdk.service.pojo.ConsumerReq;
import jakarta.validation.constraints.NotNull;

/**
 * 消费者通用接口 定义消息/任务消费的标准接口，所有具体消费者实现类需遵循此规范
 */
public interface ConsumerService {

  /**
   * 获取业务标识 用于标识当前消费者可处理的业务类型，对应ConsumerReq中的bizKey值
   *
   * @return 业务标识字符串，不能为null或空字符串
   */
  @NotNull
  String bizKey();

  /**
   * 消费处理方法 执行具体的业务消费逻辑
   *
   * @param req 消费请求参数对象，包含业务处理所需的所有信息
   */
  void consumer(ConsumerReq req);

}
