package com.olivia.sdk.config;


import cn.hutool.extra.spring.SpringUtil;
import com.olivia.sdk.utils.RunUtils;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executor;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.AbstractProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class BeanConfig {

  @PostConstruct
  public void init() {
    DataSource bean = SpringUtil.getBean(DataSource.class);
  }

  /**
   * 定义虚拟线程池（供 MyBatis-Plus 异步操作使用）
   */
  @Bean(name = "virtualThreadExecutor")
  public Executor virtualThreadExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    // 核心：使用 JDK 原生虚拟线程工厂
    executor.setThreadFactory(Thread.ofVirtual().factory()); // 等价于 Thread.ofVirtual().factory()
    executor.setCorePoolSize(0); // 虚拟线程池核心线程数设为0（按需创建）
    executor.setMaxPoolSize(Integer.MAX_VALUE); // 虚拟线程无数量上限
    executor.setQueueCapacity(Integer.MAX_VALUE); // 任务队列无限制
    executor.setThreadNamePrefix("mp-virtual-thread-"); // 线程名前缀（便于排查问题）
    executor.initialize();
    return executor;
  }

  /**
   * 自定义 Tomcat 协议处理器，使用虚拟线程
   */
  @Bean
  public TomcatProtocolHandlerCustomizer<AbstractProtocol<?>> protocolHandlerCustomizer() {
    return connector -> {
      // 将 Tomcat 的协议处理器线程池替换为虚拟线程工厂
      connector.setExecutor(RunUtils.getVirtualExecutor());
    };
  }
}
