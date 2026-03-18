package com.olivia.sdk.utils;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import com.olivia.sdk.exception.CanIgnoreException;
import com.olivia.sdk.timer.TimerUtils;
import com.olivia.sdk.utils.model.AsyncRunAndTry;
import com.olivia.sdk.utils.model.CallBackRunnable;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 异步任务执行工具类，基于JDK 21虚拟线程特性优化
 *
 * <p>核心功能：
 * 1. 提供虚拟线程池管理，优化高并发场景下的线程资源利用 2. 支持任务批量执行、异步调度和回调处理 3. 集成上下文传递工具，确保多线程环境下的日志追踪和用户信息一致性 4. 增强的异常处理机制，提供详细的任务执行状态监控
 */
@Slf4j
public class RunUtils implements AutoCloseable {

  /**
   * 虚拟线程池，使用JDK 21+虚拟线程特性
   *
   * <p>JDK 21优化点：
   * - 虚拟线程相比平台线程大幅降低内存占用 - inheritInheritableThreadLocals确保线程本地变量传递 - 命名规则便于问题追踪："virtual-[任务ID]"
   */
  @Getter
  private static final ExecutorService virtualExecutor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name("virtual-task-", 1) // 线程命名前缀+自增ID
      .inheritInheritableThreadLocals(true) // 继承可继承的ThreadLocal
      .factory());

  /**
   * 平台线程池，用于需要长时间运行的任务
   *
   * <p>适用场景：
   * - CPU密集型任务 - 需要线程特定属性（如优先级）的任务
   */
  @Getter
  private static final ExecutorService platformExecutor = Executors.newThreadPerTaskExecutor(
      Thread.ofPlatform().name("platform-task-", 1).priority(Thread.MIN_PRIORITY).factory());

  /**
   * 标记未实现的功能
   */
  public static void noImpl() {
    noImpl("暂不支持");
  }

  /**
   * 标记未实现的功能并指定错误信息
   *
   * @param key 错误标识
   */
  public static void noImpl(String key) {
    log.error("功能未实现: {}", key);
    throw new CanIgnoreException(key);
  }

  /**
   * 异步执行任务并处理异常（基于定时器调度）
   *
   * @param asyncRunAndTry 待执行的异步任务
   */
  public static void asyncRunAndTry(AsyncRunAndTry asyncRunAndTry) {
    TimerUtils.schedule(asyncRunAndTry);
  }

  /**
   * 异步执行指定任务，自动传递上下文信息
   *
   * @param key      任务标识，用于日志追踪
   * @param runnable 待执行的任务
   * @param <T>      任务类型
   */
  public static <T extends Runnable> void asyncRun(String key, T runnable) {
    asyncRun(key, runnable, true);
  }

  public static <T extends Runnable> void asyncRun(Boolean conn, String key, T runnable) {
    if (conn) {
      asyncRun(key, runnable, true);
    } else {
      log.warn("asyncRun: key={}, conn=false", key);
    }
  }

  /**
   * 异步执行指定任务，支持选择线程类型
   *
   * @param key        任务标识，用于日志追踪
   * @param runnable   待执行的任务
   * @param useVirtual 是否使用虚拟线程
   * @param <T>        任务类型
   */
  public static <T extends Runnable> void asyncRun(String key, T runnable, boolean useVirtual) {
    // 使用上下文包装工具包装任务，确保MDC和用户信息传递
    Runnable wrappedRunnable = RunnableWrapUtils.wrap(key, runnable);

    // 根据任务特性选择合适的线程池
    ExecutorService executor = useVirtual ? virtualExecutor : platformExecutor;
    executor.execute(wrappedRunnable);

    if (log.isDebugEnabled()) {
      log.debug("任务已提交执行 - key: {}, 线程类型: {}", key, useVirtual ? "虚拟线程" : "平台线程");
    }
  }

  /**
   * 执行单个任务
   *
   * @param key      任务标识
   * @param runnable 待执行的任务
   * @param <T>      任务类型
   * @return 执行结果是否成功
   */
  public static <T extends Runnable> boolean run(String key, T runnable) {
    return run(key, List.of(runnable));
  }

  /**
   * 执行多个任务
   *
   * @param key          任务标识
   * @param runnableList 待执行的任务列表
   * @return 所有任务是否都执行成功
   */
  public static boolean run(String key, List<? extends Runnable> runnableList) {
    return run(key, runnableList, false, null);
  }

  /**
   * 异步批量执行任务列表
   *
   * @param key          任务标识
   * @param runnableList 待执行的任务列表
   */
  public static void asyncRun(String key, List<? extends Runnable> runnableList) {
    asyncRun(key, runnableList, true);
  }

  /**
   * 异步批量执行任务列表，支持选择线程类型
   *
   * @param key          任务标识
   * @param runnableList 待执行的任务列表
   * @param useVirtual   是否使用虚拟线程
   */
  public static void asyncRun(String key, List<? extends Runnable> runnableList, boolean useVirtual) {
    if (CollUtil.isEmpty(runnableList)) {
      log.info("任务列表为空，无需执行 - key: {}", key);
      return;
    }

    ExecutorService executor = useVirtual ? virtualExecutor : platformExecutor;

    // 包装所有任务并提交执行
    runnableList.stream().map(runnable -> RunnableWrapUtils.wrap(key, runnable)).forEach(executor::execute);

    log.debug("批量任务已提交 - key: {}, 任务数量: {}, 线程类型: {}", key, runnableList.size(), useVirtual ? "虚拟线程" : "平台线程");
  }

  /**
   * 执行多个任务并支持回调
   *
   * @param key              任务标识
   * @param runnableList     待执行的任务列表
   * @param callOnException  是否在发生异常时调用回调
   * @param callBackRunnable 回调任务
   * @return 所有任务是否都执行成功
   */
  public static boolean run(String key, List<? extends Runnable> runnableList, Boolean callOnException, CallBackRunnable callBackRunnable) {
    return run(key, runnableList, callOnException, callBackRunnable, Duration.ofMinutes(5));
  }

  /**
   * 执行多个任务并支持回调和超时控制
   *
   * @param key              任务标识
   * @param runnableList     待执行的任务列表
   * @param callOnException  是否在发生异常时调用回调
   * @param callBackRunnable 回调任务
   * @param timeout          等待超时时间
   * @return 所有任务是否都执行成功
   */
  public static boolean run(String key, List<? extends Runnable> runnableList, Boolean callOnException, CallBackRunnable callBackRunnable, Duration timeout) {
    if (CollUtil.isEmpty(runnableList)) {
      log.info("任务列表为空，直接返回成功 - key: {}", key);
      return true;
    }

    // 初始化同步工具和状态跟踪器
    int taskCount = runnableList.size();
    CountDownLatch countDownLatch = new CountDownLatch(taskCount);
    AtomicInteger failedCount = new AtomicInteger(0);
    List<Exception> exceptionList = Lists.newCopyOnWriteArrayList(); // 线程安全的异常列表

    // 提交所有任务
    for (Runnable runnable : runnableList) {
      Runnable wrappedRunnable = RunnableWrapUtils.wrap(key, runnable);

      virtualExecutor.execute(() -> {
        try {
          wrappedRunnable.run();
        } catch (Exception e) {
          log.error("任务执行失败 - key: {}", key, e);
          failedCount.incrementAndGet();
          exceptionList.add(e);
        } finally {
          countDownLatch.countDown();
        }
      });
    }

    // 等待所有任务完成或超时
    try {
      boolean allCompleted = countDownLatch.await(timeout.toNanos(), TimeUnit.NANOSECONDS);
      if (!allCompleted) {
        log.warn("任务执行超时 - key: {}, 超时时间: {}ms, 未完成任务数: {}", key, timeout.toMillis(), countDownLatch.getCount());
        return false;
      }
    } catch (InterruptedException e) {
      log.error("等待任务完成时被中断 - key: {}", key, e);
      Thread.currentThread().interrupt(); // 恢复中断状态，符合JDK并发规范
      return false;
    }

    // 检查执行结果
    boolean allSuccess = failedCount.get() == 0;
    log.info("任务执行完成 - key: {}, 总任务数: {}, 成功数: {}, 失败数: {}", key, taskCount, taskCount - failedCount.get(), failedCount.get());

    // 执行回调逻辑
    if (Objects.nonNull(callBackRunnable) && (Boolean.TRUE.equals(callOnException) || allSuccess)) {
      callBackRunnable.setExceptionList(exceptionList);
      asyncRun(key + "_callback", callBackRunnable);
    }

    return allSuccess;
  }

  /**
   * 关闭线程池（主要用于应用程序优雅退出）
   *
   * <p>JDK 21优化点：使用新的关闭API，更友好地处理虚拟线程
   */
  private static void shutdown() {
    // 关闭虚拟线程池
    shutdownExecutor(virtualExecutor, "虚拟线程池");

    // 关闭平台线程池
    shutdownExecutor(platformExecutor, "平台线程池");
  }

  /**
   * 关闭指定的线程池
   */
  private static void shutdownExecutor(ExecutorService executor, String name) {
    if (executor == null || executor.isTerminated()) {
      return;
    }

    try {
      log.info("开始关闭 {}...", name);
      executor.shutdown(); // 拒绝新任务
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        log.warn("{} 未在指定时间内关闭，将强制关闭...", name);
        List<Runnable> droppedTasks = executor.shutdownNow(); // 强制关闭
        log.warn("{} 强制关闭，未执行的任务数: {}", name, droppedTasks.size());
      }
      log.info("{} 已成功关闭", name);
    } catch (InterruptedException e) {
      log.error("关闭 {} 时被中断", name, e);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void close() throws Exception {
    shutdown();
  }
}
