//package com.olivia.sdk.listener;
//
//import static com.olivia.sdk.utils.Str.DEFAULT;
//
//import cn.hutool.core.util.ReflectUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import com.dingtalk.open.app.api.OpenDingTalkClient;
//import com.dingtalk.open.app.api.OpenDingTalkStreamClientBuilder;
//import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
//import com.dingtalk.open.app.api.security.AuthClientCredential;
//import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.olivia.sdk.config.PeanutProperties;
//import com.olivia.sdk.config.ServiceNotice;
//import com.olivia.sdk.config.entity.DingConfig;
//import com.olivia.sdk.service.DingEventBizService;
//import com.olivia.sdk.utils.JSON;
//import com.olivia.sdk.utils.MDCUtils;
//import com.olivia.sdk.utils.RunUtils;
//import jakarta.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.BiFunction;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
/// **
// * 钉钉消息监听器 负责初始化钉钉事件监听客户端，注册事件处理器，并处理接收到的钉钉事件
// */
//@Slf4j
//@Component
//public class DingListener {
//
//  /**
//   * 业务处理函数映射表 key: 事件类型 value: 该事件类型对应的业务处理函数列表
//   */
//  private final Map<String, List<BiFunction<String, GenericOpenDingTalkEvent, EventAckStatus>>> bizFunMap = Maps.newConcurrentMap();
//
//  /**
//   * 初始化钉钉监听器 加载配置，注册事件处理器，启动钉钉客户端
//   */
//  @Lazy
//  @PostConstruct
//  public void initDingListener() {
//    RunUtils.asyncRun("DingListener", () -> {
//      log.info("开始初始化钉钉消息监听器");
//      // 获取Spring上下文的Bean
//      PeanutProperties peanutProperties = SpringUtil.getBean(PeanutProperties.class);
//      Map<String, DingEventBizService> eventServiceMap = SpringUtil.getBeansOfType(DingEventBizService.class);
//      // 注册所有事件处理器
//      registerEventHandlers(eventServiceMap);
//      // 初始化并启动钉钉客户端
//      initAndStartDingClients(peanutProperties.getDingConfigList());
//      log.info("钉钉消息监听器初始化流程完成");
//    });
//  }
//
//  /**
//   * 注册所有事件处理器到映射表
//   *
//   * @param eventServiceMap 事件业务服务映射表
//   */
//  private void registerEventHandlers(Map<String, DingEventBizService> eventServiceMap) {
//    if (CollectionUtils.isEmpty(eventServiceMap)) {
//      log.warn("未发现任何DingEventBizService实现类，无法处理钉钉事件");
//      return;
//    }
//    eventServiceMap.forEach((beanName, service) -> {
//      String eventType = service.bizEventType();
//      List<BiFunction<String, GenericOpenDingTalkEvent, EventAckStatus>> functionList = bizFunMap.computeIfAbsent(eventType, k -> Lists.newArrayList());
//      functionList.add((type, event) -> service.onEvent(event));
//      log.debug("已注册钉钉事件处理器 - 事件类型: {}, 处理器Bean: {}", eventType, beanName);
//    });
//  }
//
//  /**
//   * 初始化并启动所有钉钉客户端
//   *
//   * @param dingConfigList 钉钉配置列表
//   */
//  private void initAndStartDingClients(List<DingConfig> dingConfigList) {
//    if (CollectionUtils.isEmpty(dingConfigList)) {
//      log.info("未配置钉钉客户端信息，跳过钉钉监听器初始化");
//      return;
//    }
//    // 过滤并处理启用了Stream的配置
//    dingConfigList.stream().filter(DingConfig::getUseStream).forEach(this::initAndStartDingClient);
//  }
//
//  /**
//   * 初始化并启动单个钉钉客户端
//   *
//   * @param dingConfig 钉钉配置
//   */
//  private void initAndStartDingClient(DingConfig dingConfig) {
//    String clientId = dingConfig.getClientId();
//    String dingName = dingConfig.getDingName();
//    try {
//      // 构建钉钉客户端
//      OpenDingTalkClient client = OpenDingTalkStreamClientBuilder.custom().credential(new AuthClientCredential(clientId, dingConfig.getClientSecret()))
//          .registerAllEventListener(this::handleDingEvent).build();
//      // 启动客户端
//      client.start();
//      ExecutorService fieldValue = (ExecutorService) ReflectUtil.getFieldValue(client, "executor");
//      fieldValue.shutdown();
//      ReflectUtil.setFieldValue(client, "executor", RunUtils.getVirtualExecutor());
//      log.info("钉钉客户端启动成功 - 名称: {}, ClientId: {}", dingName, clientId);
//      ServiceNotice.sendCustomMessage(dingName + "监听", "钉钉消息监听初始化成功");
//    } catch (Exception e) {
//      log.error("钉钉客户端初始化失败 - 名称: {}, ClientId: {}", dingName, clientId, e);
//      ServiceNotice.sendCustomMessage(dingName + "监听", "钉钉消息监听初始化失败: " + e.getMessage());
//    }
//  }
//
//  /**
//   * 处理接收到的钉钉事件
//   *
//   * @param event 钉钉事件对象
//   * @return 事件处理结果状态
//   */
//  private EventAckStatus handleDingEvent(GenericOpenDingTalkEvent event) {
//    MDCUtils.initMdc(true);
//    try {
//      String eventType = event.getEventType();
//      String eventId = event.getEventId();
//      if (log.isDebugEnabled()) {
//        log.debug("收到钉钉事件 - 类型: {}, ID: {}, 内容: {}", eventType, eventId, JSON.toJSONString(event));
//      }
//      // 获取事件对应的处理器列表，如无则使用默认处理器
//      List<BiFunction<String, GenericOpenDingTalkEvent, EventAckStatus>> handlers = bizFunMap.getOrDefault(eventType,
//          bizFunMap.getOrDefault(DEFAULT, Lists.newArrayList()));
//      if (CollectionUtils.isEmpty(handlers)) {
//        log.warn("未找到钉钉事件处理器 - 类型: {}, ID: {}", eventType, eventId);
//        return EventAckStatus.SUCCESS;
//      }
//      // 并行处理所有相关处理器
//      return processEventWithHandlers(eventType, event, handlers);
//    } catch (Exception e) {
//      log.error("钉钉事件处理异常", e);
//      return EventAckStatus.LATER;
//    } finally {
//      MDCUtils.clear();
//    }
//  }
//
//  /**
//   * 使用处理器列表处理事件
//   *
//   * @param eventType 事件类型
//   * @param event     事件对象
//   * @param handlers  处理器列表
//   * @return 事件处理结果状态
//   */
//  private EventAckStatus processEventWithHandlers(String eventType, GenericOpenDingTalkEvent event,
//      List<BiFunction<String, GenericOpenDingTalkEvent, EventAckStatus>> handlers) {
//    List<Runnable> tasks = new ArrayList<>(handlers.size());
//    AtomicInteger successCount = new AtomicInteger(0);
//    // 构建任务列表
//    handlers.forEach(handler -> tasks.add(() -> {
//      EventAckStatus result = handler.apply(eventType, event);
//      if (EventAckStatus.SUCCESS.equals(result)) {
//        successCount.incrementAndGet();
//      }
//    }));
//    // 执行所有任务
//    String taskName = "钉钉消息 :" + eventType + " " + event.getEventId();
//    RunUtils.run(taskName, tasks);
//    // 所有处理器成功才算成功
//    return successCount.get() == handlers.size() ? EventAckStatus.SUCCESS : EventAckStatus.LATER;
//  }
//
//}
