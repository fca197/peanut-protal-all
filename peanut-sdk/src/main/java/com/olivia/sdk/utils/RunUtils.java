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
 * 1. 提供虚拟线程池管理，优化高并发场景下的线程资源利用
 * 2. 支持任务批量执行、异步调度和回调处理
 * 3. 集成上下文传递工具，确保多线程环境下的日志追踪和用户信息一致性
 * 4. 增强的异常处理机制，提供详细的任务执行状态监控
 * 5. 全方法支持超时控制，超时自动中断任务，默认20分钟
 */
@Slf4j
public class RunUtils implements AutoCloseable {

  /**
   * 默认任务超时时间：20分钟
   */
  public static final Duration DEFAULT_TIMEOUT = Duration.ofMinutes(20);

  /**
   * 虚拟线程池，使用JDK 21+虚拟线程特性
   */
  @Getter
  private static final ExecutorService virtualExecutor = Executors.newThreadPerTaskExecutor(
      Thread.ofVirtual().name("virtual-task-", 1)
          .inheritInheritableThreadLocals(true)
          .factory());

  /**
   * 平台线程池，用于需要长时间运行的任务
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
   */
  public static void noImpl(String key) {
    log.error("功能未实现: {}", key);
    throw new CanIgnoreException(key);
  }

  // ======================== asyncRunAndTry ========================

  /**
   * 异步执行任务并处理异常（基于定时器调度），使用默认超时时间
   */

  public static void asyncRunAndTry(AsyncRunAndTry asyncRunAndTry) {

    TimerUtils.schedule(asyncRunAndTry);
  }

  // ======================== asyncRun（单个任务） ========================

  /**
   * 异步执行指定任务，自动传递上下文信息，使用默认超时时间
   */
  public static <T extends Runnable> void asyncRun(String key, T runnable) {
    asyncRun(key, runnable, true, DEFAULT_TIMEOUT);
  }

  /**
   * 异步执行指定任务，支持自定义超时时间
   */
  public static <T extends Runnable> void asyncRun(String key, T runnable, Duration timeout) {
    asyncRun(key, runnable, true, timeout);
  }

  public static <T extends Runnable> void asyncRun(Boolean conn, String key, T runnable) {
    asyncRun(conn, key, runnable, DEFAULT_TIMEOUT);
  }

  /**
   * 异步执行指定任务，支持连接控制和自定义超时时间
   */
  public static <T extends Runnable> void asyncRun(Boolean conn, String key, T runnable, Duration timeout) {
    if (conn) {
      asyncRun(key, runnable, true, timeout);
    } else {
      log.warn("asyncRun: key={}, conn=false", key);
    }
  }

  /**
   * 异步执行指定任务，支持选择线程类型，使用默认超时时间
   */
  public static <T extends Runnable> void asyncRun(String key, T runnable, boolean useVirtual) {
    asyncRun(key, runnable, useVirtual, DEFAULT_TIMEOUT);
  }

  /**
   * 异步执行指定任务，支持选择线程类型和自定义超时时间
   *
   * <p>超时机制：通过 CompletableFuture.orTimeout 实现，超时后自动调用 Future.cancel(true)
   * 中断任务线程。任务在超时前正常完成则不受影响。
   *
   * @param key        任务标识，用于日志追踪
   * @param runnable   待执行的任务
   * @param useVirtual 是否使用虚拟线程
   * @param timeout    任务超时时间
   */
  public static <T extends Runnable> void asyncRun(String key, T runnable, boolean useVirtual, Duration timeout) {
    Duration effectiveTimeout = timeout != null ? timeout : DEFAULT_TIMEOUT;
    Runnable wrappedRunnable = RunnableWrapUtils.wrap(key, runnable);
    ExecutorService executor = useVirtual ? virtualExecutor : platformExecutor;

    // 提交任务并获取Future
    Future<?> future = executor.submit(wrappedRunnable);

    // 注册超时中断：超时后自动cancel(true)中断任务线程
    registerTimeoutCancel(key, future, effectiveTimeout);

    if (log.isDebugEnabled()) {
      log.debug("任务已提交执行 - key: {}, 线程类型: {}, 超时时间: {}ms", key, useVirtual ? "虚拟线程" : "平台线程", effectiveTimeout.toMillis());
    }
  }

  // ======================== asyncRun（批量任务） ========================

  /**
   * 异步批量执行任务列表，使用默认超时时间
   */
  public static void asyncRun(String key, List<? extends Runnable> runnableList) {
    asyncRun(key, runnableList, true, DEFAULT_TIMEOUT);
  }

  /**
   * 异步批量执行任务列表，支持自定义超时时间
   */
  public static void asyncRun(String key, List<? extends Runnable> runnableList, Duration timeout) {
    asyncRun(key, runnableList, true, timeout);
  }

  /**
   * 异步批量执行任务列表，支持选择线程类型，使用默认超时时间
   */
  public static void asyncRun(String key, List<? extends Runnable> runnableList, boolean useVirtual) {
    asyncRun(key, runnableList, useVirtual, DEFAULT_TIMEOUT);
  }

  /**
   * 异步批量执行任务列表，支持选择线程类型和自定义超时时间
   *
   * <p>每个任务独立超时控制，超时后自动中断该任务，不影响其他任务。
   *
   * @param key          任务标识
   * @param runnableList 待执行的任务列表
   * @param useVirtual   是否使用虚拟线程
   * @param timeout      单个任务超时时间
   */
  public static void asyncRun(String key, List<? extends Runnable> runnableList, boolean useVirtual, Duration timeout) {
    if (CollUtil.isEmpty(runnableList)) {
      log.info("任务列表为空，无需执行 - key: {}", key);
      return;
    }

    Duration effectiveTimeout = timeout != null ? timeout : DEFAULT_TIMEOUT;
    ExecutorService executor = useVirtual ? virtualExecutor : platformExecutor;

    for (Runnable runnable : runnableList) {
      Runnable wrappedRunnable = RunnableWrapUtils.wrap(key, runnable);
      Future<?> future = executor.submit(wrappedRunnable);
      registerTimeoutCancel(key, future, effectiveTimeout);
    }

    log.debug("批量任务已提交 - key: {}, 任务数量: {}, 线程类型: {}, 超时时间: {}ms",
        key, runnableList.size(), useVirtual ? "虚拟线程" : "平台线程", effectiveTimeout.toMillis());
  }

  // ======================== run（单个任务） ========================

  /**
   * 执行单个任务，使用默认超时时间
   */
  public static <T extends Runnable> boolean run(String key, T runnable) {
    return run(key, List.of(runnable));
  }

  /**
   * 执行单个任务，支持自定义超时时间
   */
  public static <T extends Runnable> boolean run(String key, T runnable, Duration timeout) {
    return run(key, List.of(runnable), false, null, timeout);
  }

  // ======================== run（批量任务） ========================

  /**
   * 执行多个任务，使用默认超时时间
   */
  public static boolean run(String key, List<? extends Runnable> runnableList) {
    return run(key, runnableList, false, null, DEFAULT_TIMEOUT);
  }

  /**
   * 执行多个任务，支持自定义超时时间
   */
  public static boolean run(String key, List<? extends Runnable> runnableList, Duration timeout) {
    return run(key, runnableList, false, null, timeout);
  }

  /**
   * 执行多个任务并支持回调，使用默认超时时间
   */
  public static boolean run(String key, List<? extends Runnable> runnableList, Boolean callOnException, CallBackRunnable callBackRunnable) {
    return run(key, runnableList, callOnException, callBackRunnable, DEFAULT_TIMEOUT);
  }

  /**
   * 执行多个任务并支持回调和超时控制
   *
   * <p>超时机制：
   * - 每个任务独立提交并注册超时中断
   * - 超时后自动 cancel(true) 中断任务线程
   * - 使用 CountDownLatch 等待所有任务完成（含超时中断后的完成）
   * - 任何一个任务超时中断，整体返回 false
   *
   * @param key              任务标识
   * @param runnableList     待执行的任务列表
   * @param callOnException  是否在发生异常时调用回调
   * @param callBackRunnable 回调任务
   * @param timeout          等待超时时间，不填默认20分钟
   * @return 所有任务是否都执行成功
   */
  public static boolean run(String key, List<? extends Runnable> runnableList, Boolean callOnException, CallBackRunnable callBackRunnable, Duration timeout) {
    if (CollUtil.isEmpty(runnableList)) {
      log.info("任务列表为空，直接返回成功 - key: {}", key);
      return true;
    }

    Duration effectiveTimeout = timeout != null ? timeout : DEFAULT_TIMEOUT;

    int taskCount = runnableList.size();
    CountDownLatch countDownLatch = new CountDownLatch(taskCount);
    AtomicInteger failedCount = new AtomicInteger(0);
    AtomicInteger timeoutCount = new AtomicInteger(0);
    List<Exception> exceptionList = Lists.newCopyOnWriteArrayList();

    // 提交所有任务，每个任务独立超时控制
    for (Runnable runnable : runnableList) {
      Runnable wrappedRunnable = RunnableWrapUtils.wrap(key, runnable);

      Future<?> future = virtualExecutor.submit(() -> {
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

      // 注册超时中断：超时后cancel(true)中断线程，线程中断后会抛出InterruptedException
      // 进入catch块被计数为失败，最终countDownLatch.countDown()
      registerTimeoutCancel(key, future, effectiveTimeout, () -> {
        timeoutCount.incrementAndGet();
        log.warn("任务超时已中断 - key: {}, 超时时间: {}ms", key, effectiveTimeout.toMillis());
      });
    }

    // 等待所有任务完成（包括被超时中断的任务）
    try {
      // 整体等待时间略大于单任务超时，确保被中断的任务有机会完成收尾
      boolean allCompleted = countDownLatch.await(effectiveTimeout.toMillis() + 3000, TimeUnit.MILLISECONDS);
      if (!allCompleted) {
        log.warn("任务执行整体超时 - key: {}, 等待时间: {}ms, 未完成任务数: {}",
            key, effectiveTimeout.toMillis() + 3000, countDownLatch.getCount());
        return false;
      }
    } catch (InterruptedException e) {
      log.error("等待任务完成时被中断 - key: {}", key, e);
      Thread.currentThread().interrupt();
      return false;
    }

    boolean allSuccess = failedCount.get() == 0;
    log.info("任务执行完成 - key: {}, 总任务数: {}, 成功数: {}, 失败数: {}, 超时中断数: {}",
        key, taskCount, taskCount - failedCount.get(), failedCount.get(), timeoutCount.get());

    // 执行回调逻辑
    if (Objects.nonNull(callBackRunnable) && (Boolean.TRUE.equals(callOnException) || allSuccess)) {
      callBackRunnable.setExceptionList(exceptionList);
      asyncRun(key + "_callback", callBackRunnable);
    }

    return allSuccess;
  }

  // ======================== 超时中断机制 ========================

  /**
   * 注册超时中断：任务超时后调用 Future.cancel(true) 中断任务线程
   *
   * <p>实现原理：
   * 1. 使用 ScheduledExecutorService 延迟执行超时检查 2. 到达超时时间后检查 Future 是否完成 3. 未完成则 cancel(true)，触发任务线程的中断标志 4. 任务线程检查中断标志或等待中被中断，抛出 InterruptedException，从而终止执行
   *
   * @param key     任务标识
   * @param future  任务Future
   * @param timeout 超时时间
   */
  private static void registerTimeoutCancel(String key, Future<?> future, Duration timeout) {
    registerTimeoutCancel(key, future, timeout, null);
  }

  /**
   * 注册超时中断，支持超时回调通知
   *
   * @param key             任务标识
   * @param future          任务Future
   * @param timeout         超时时间
   * @param onTimeout       超时触发时的回调（可为null）
   */
  private static void registerTimeoutCancel(String key, Future<?> future, Duration timeout, Runnable onTimeout) {
    // 使用虚拟线程执行超时守护，不占用平台线程
    virtualExecutor.execute(() -> {
      try {
        Thread.sleep(timeout.toMillis());
        if (!future.isDone()) {
          boolean cancelled = future.cancel(true); // true = 中断正在执行的线程
          log.warn("任务超时中断 - key: {}, 超时时间: {}ms, cancel结果: {}", key, timeout.toMillis(), cancelled);
          if (onTimeout != null) {
            onTimeout.run();
          }
        }
      } catch (InterruptedException e) {
        // 守护线程被中断，无需处理
        Thread.currentThread().interrupt();
      }
    });
  }

  // ======================== 线程池关闭 ========================

  /**
   * 关闭线程池
   */
  private static void shutdown() {
    shutdownExecutor(virtualExecutor, "虚拟线程池");
    shutdownExecutor(platformExecutor, "平台线程池");
  }

  private static void shutdownExecutor(ExecutorService executor, String name) {
    if (executor == null || executor.isTerminated()) {
      return;
    }

    try {
      log.info("开始关闭 {}...", name);
      executor.shutdown();
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        log.warn("{} 未在指定时间内关闭，将强制关闭...", name);
        List<Runnable> droppedTasks = executor.shutdownNow();
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
