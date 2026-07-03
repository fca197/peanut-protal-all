package com.olivia.sdk.config;

import cn.hutool.core.collection.CollUtil;
import com.olivia.sdk.service.ConsumerService;
import com.olivia.sdk.service.impl.PrintConsoleConsumerServiceImpl;
import com.olivia.sdk.service.pojo.ConsumerReq;
import com.olivia.sdk.utils.RunUtils;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 消费者服务处理器 - 管理 ConsumerService 实现并按业务键路由
 *
 * <h3>基于 JDK 26 + Spring 6 的优化</h3>
 * <ul>
 *   <li>状态迁到实例字段；通过 {@code static volatile} facade 保持原有调用方式</li>
 *   <li>构造器注入替代 {@code SpringUtil.getBean}，消除静态工具调用</li>
 *   <li>消除 DCL（{@code @PostConstruct} 由 Spring 单线程串行执行）</li>
 *   <li>{@link Map#copyOf} 返回不可变快照；{@code Stream.toList()} 产出不可变 List</li>
 *   <li>{@code var} 局部推断 + {@code String.formatted()} + 懒构造的 NPE 消息</li>
 * </ul>
 */
@Slf4j
@Component
public class ConsumerServiceHandler {

  /**
   * 串行初始化由 Spring 保证 happens-before，DCL 不再需要；volatile 仅用于跨线程读 init 后的实例
   */
  private static volatile ConsumerServiceHandler instance;

  private final Map<String, ConsumerService> consumerServices = new ConcurrentHashMap<>();
  private final ApplicationContext applicationContext;
  private final PrintConsoleConsumerServiceImpl defaultConsumerService;

  public ConsumerServiceHandler(
      ApplicationContext applicationContext,
      PrintConsoleConsumerServiceImpl defaultConsumerService) {
    this.applicationContext = applicationContext;
    this.defaultConsumerService = defaultConsumerService;
  }

  public static void consume(ConsumerReq req) {
    Objects.requireNonNull(req, "消费请求对象(req)不能为null");
    if (CollUtil.isEmpty(req.getBizKeyList())) {
      throw new IllegalArgumentException("业务键列表(bizKey)不能为null或空");
    }
    var handler = instance;
    if (handler == null) {
      log.error("ConsumerServiceHandler 尚未初始化，请等待 Spring 容器就绪");
      return;
    }
    var keyList = req.getBizKeyList();
    var tasks = keyList.stream()
        .<Runnable>map(key -> () -> handler.getConsumerService(key).consumer(req))
        .toList();
    RunUtils.run("consumer:" + String.join(",", keyList), tasks);
  }

  /**
   * 调试/测试用：当前已注册服务的不可变快照（防御性拷贝）
   */
  public static Map<String, ConsumerService> getConsumerServices() {
    var handler = instance;
    return handler == null ? Map.of() : Map.copyOf(handler.consumerServices);
  }

  @PostConstruct
  public void init() {
    instance = this;
    var services = applicationContext.getBeansOfType(ConsumerService.class).values();
    if (CollUtil.isEmpty(services)) {
      log.warn("未发现任何ConsumerService实现类");
      return;
    }
    services.stream()
        .filter(s -> s != defaultConsumerService)
        .forEach(this::register);
    log.info("消费者服务初始化完成，共加载 {} 个服务", consumerServices.size());
  }

  private void register(ConsumerService service) {
    var bizKey = service.bizKey();
    // Supplier 让消息仅在真为 null 时拼接，正常路径零开销
    Objects.requireNonNull(bizKey,
        () -> "ConsumerService %s 的bizKey不能为null".formatted(service.getClass().getName()));
    var previous = consumerServices.putIfAbsent(bizKey, service);
    if (previous != null) {
      log.warn("业务键冲突: 键[{}]已被服务[{}]占用，新服务[{}]将覆盖旧服务",
          bizKey, previous.getClass().getSimpleName(), service.getClass().getSimpleName());
      consumerServices.put(bizKey, service);
    }
  }

  private ConsumerService getConsumerService(String bizKey) {
    Objects.requireNonNull(bizKey, "业务键(bizKey)不能为null");
    return consumerServices.getOrDefault(bizKey, defaultConsumerService);
  }
}
