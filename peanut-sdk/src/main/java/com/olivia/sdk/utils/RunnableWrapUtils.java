package com.olivia.sdk.utils;

import static com.olivia.sdk.utils.MDCUtils.nextSpanId;
import static org.slf4j.MDC.setContextMap;

import com.olivia.sdk.filter.LoginUser;
import com.olivia.sdk.filter.LoginUserContext;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * 线程任务包装工具类，用于在多线程环境中传递上下文信息
 *
 * <p>核心功能：
 * 1. 解决多线程/线程池环境下MDC日志上下文丢失问题 2. 传递用户登录信息和HTTP请求属性 3. 基于JDK 21特性优化资源管理和线程兼容性
 *
 * <p>JDK 21特性应用：
 * - 虚拟线程(Virtual Threads)支持与信息获取 - try-with-resources增强的自动资源管理 - Thread.threadId()等线程信息API
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 私有构造函数，确保工具类无法实例化
public final class RunnableWrapUtils {

  /**
   * 包装Callable任务，传递当前线程的上下文信息
   *
   * @param key      任务标识，用于日志追踪
   * @param callable 原始任务
   * @param <T>      任务返回值类型
   * @return 包装后的Callable，自动携带上下文
   */
  public static <T> Callable<T> wrap(String key, final Callable<T> callable) {
    // 捕获当前线程的上下文信息（MDC、用户信息、请求属性）
    ContextResult result = getContextResult();

    // 返回包装后的任务，使用try-with-resources自动管理上下文生命周期
    return () -> {
      try (ThreadContextScope ignored = new ThreadContextScope(key, result)) {
        return callable.call(); // 执行原始任务
      }
    };
  }

  /**
   * 包装Runnable任务，传递当前线程的上下文信息
   *
   * @param key      任务标识，用于日志追踪
   * @param runnable 原始任务
   * @return 包装后的Runnable，自动携带上下文
   */
  public static Runnable wrap(String key, final Runnable runnable) {
    ContextResult result = getContextResult();

    return () -> {
      try (ThreadContextScope ignored = new ThreadContextScope(key, result)) {
        runnable.run(); // 执行原始任务
      } catch (Exception e) {
        log.error("Wrapped runnable execution failed, key: {}", key, e);
        throw new RuntimeException("Runnable execution failed for key: " + key, e);
      }
    };
  }

  /**
   * 将Callable转换为Supplier，并传递上下文信息
   *
   * @param key      任务标识，用于日志追踪
   * @param callable 原始Callable任务
   * @param <T>      返回值类型
   * @return 包装后的Supplier，自动携带上下文
   */
  public static <T> Supplier<T> toSupplier(String key, final Callable<T> callable) {
    ContextResult result = getContextResult();

    return () -> {
      try (ThreadContextScope ignored = new ThreadContextScope(key, result)) {
        return callable.call();
      } catch (Exception e) {
        log.error("Wrapped supplier execution failed, key: {}", key, e);
        return null; // 异常场景返回null，可根据业务需求调整
      }
    };
  }

  /**
   * 捕获当前线程的上下文信息，封装为ContextResult对象
   *
   * @return 包含MDC、用户信息和请求属性的上下文结果
   */
  private static ContextResult getContextResult() {
    // 1. 捕获MDC日志上下文（用于分布式追踪）
    Map<String, String> contextMap = MDC.getCopyOfContextMap();

    // 2. 捕获当前登录用户信息
    LoginUser loginUser = LoginUserContext.getLoginUser();

    // 3. 捕获HTTP请求属性（如请求参数、会话信息等）
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

    return new ContextResult(contextMap, loginUser, requestAttributes);
  }

  /**
   * 上下文结果记录类，封装需要传递的所有上下文信息
   *
   * <p>使用JDK 16+的Record特性，自动生成构造函数、getter和equals等方法
   */
  private record ContextResult(Map<String, String> contextMap, // MDC日志上下文
                               LoginUser loginUser, // 当前登录用户信息
                               RequestAttributes requestAttributes // HTTP请求属性
  ) {

  }

  /**
   * 线程上下文作用域管理类，实现AutoCloseable接口
   *
   * <p>核心作用：
   * 1. 在任务执行前设置上下文 2. 任务执行后自动清理/恢复上下文（利用try-with-resources机制） 3. 确保线程复用（尤其是虚拟线程）时的上下文隔离
   */
  private static class ThreadContextScope implements AutoCloseable {

    /**
     * 构造函数，初始化线程上下文
     *
     * @param key    任务标识
     * @param result 需要设置的上下文信息
     */
    public ThreadContextScope(String key, ContextResult result) {
      setupThreadContext(key, result);
    }

    /**
     * 自动关闭方法，在try-with-resources块结束时调用
     *
     * <p>JDK 21优化点：虚拟线程环境下更严格的资源清理，避免上下文泄露
     */
    @Override
    public void close() {
      // 清理HTTP请求属性
      RequestContextHolder.resetRequestAttributes();

      // 清理登录用户信息
      LoginUserContext.clear();

      // 日志调试：确认上下文已清理（仅在DEBUG级别生效）
      if (log.isDebugEnabled()) {
        log.debug("Thread context cleared - threadId: {} name : {}", Thread.currentThread().threadId(),Thread.currentThread().getName());
      }
      // 清理MDC上下文
      MDC.clear();

    }

    /**
     * 设置线程上下文信息
     *
     * @param key    任务标识
     * @param result 上下文数据
     */
    private void setupThreadContext(String key, ContextResult result) {
      // 1. 设置HTTP请求属性
      RequestContextHolder.setRequestAttributes(result.requestAttributes());

      // 2. 设置登录用户信息
      LoginUserContext.setLoginUser(result.loginUser());

      // 3. 设置MDC日志上下文（如分布式追踪ID）
      Map<String, String> contextMap = result.contextMap();
      if (contextMap != null && !contextMap.isEmpty()) {
        setContextMap(contextMap);
      }

      // 4. 生成新的Span ID，用于分布式追踪链路
      nextSpanId();

      // 5. 记录线程信息（利用JDK 21的线程API）
      logThreadInfo(key);
    }

    /**
     * 记录线程详细信息，用于调试和问题追踪
     *
     * <p>JDK 21特性应用：
     * - Thread.threadId()：获取线程ID（long类型，比hashCode更可靠） - Thread.isVirtual()：判断是否为虚拟线程 - 虚拟线程与平台线程的统一信息记录
     */
    private void logThreadInfo(String key) {
      if (!log.isDebugEnabled()) {
        return; // 非DEBUG级别不输出，提升性能
      }

      Thread currentThread = Thread.currentThread();
      log.debug("Thread context setup complete Task key: {} threadId: {} , threadName: {} , Thread group: {} , Priority: {} , Is virtual thread: {}", key,
          currentThread.threadId(), // JDK 19+ API，获取稳定的线程ID
          currentThread.getName(), currentThread.getThreadGroup().getName(), currentThread.getPriority(), currentThread.isVirtual() // JDK 21虚拟线程判断API
      );
    }
  }
}
