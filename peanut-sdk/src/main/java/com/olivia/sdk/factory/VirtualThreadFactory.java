package com.olivia.sdk.factory;
import java.util.concurrent.ThreadFactory;
import org.jspecify.annotations.NonNull;

/**
 * 自定义虚拟线程工厂，供 HikariCP 配置文件引用
 */
public class VirtualThreadFactory implements ThreadFactory {
  // 线程名称前缀，便于日志排查
  private final String threadNamePrefix;
  private int threadCount = 1;

  // 无参构造器（配置文件中引用时需要）
  public VirtualThreadFactory() {
    this("hikari-virtual-");
  }

  // 带名称前缀的构造器（可选）
  public VirtualThreadFactory(String threadNamePrefix) {
    this.threadNamePrefix = threadNamePrefix;
  }

  @Override
  public Thread newThread( @NonNull Runnable r) {
    // 创建虚拟线程，指定名称前缀和自增序号
    return Thread.ofVirtual()
        .name(threadNamePrefix, threadCount++)
        .unstarted(r);
  }
}