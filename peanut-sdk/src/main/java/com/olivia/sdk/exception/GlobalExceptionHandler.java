package com.olivia.sdk.exception;

import com.google.common.base.Strings;
import com.olivia.sdk.exception.entity.SQLErrorMsg;
import com.olivia.sdk.result.Result;
import com.olivia.sdk.utils.ReqResUtils;
import java.sql.SQLException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理器 统一处理应用中抛出的各类异常，返回标准化的Result响应对象
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * 处理所有未捕获的异常 作为异常处理的最后一道防线，确保所有异常都能被正确捕获并返回友好信息
   *
   * @param e 未捕获的异常对象
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(Exception.class)
  public Result<Object> handleGeneralException(Exception e) {
    log.error("未捕获异常: {}", e.getMessage(), e);
    Throwable throwable = getThrowable(e);
    return Result.fail(throwable.getMessage());
  }

  private Throwable getThrowable(Throwable e) {
    Throwable cause = e.getCause();
    return Objects.isNull(cause) ? e : getThrowable(cause);
  }

  @ExceptionHandler(NotenantException.class)
  public Result<Object> handleGeneralException(NotenantException e) {
    log.error("未捕获异常: {}", e.getMessage(), e);
    return Result.fail("当钱用户租户信息错误");
  }

  /**
   * 处理HTTP请求方法不支持的异常
   *
   * @param e 请求方法不支持异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public Result<Object> handleGeneralException(@NonNull HttpRequestMethodNotSupportedException e) {
    assert e.getSupportedMethods() != null;
    String supportedMethods = String.join(",", e.getSupportedMethods());
    String message = String.format("不支持的请求方式 %s，支持的方式为: %s", e.getMethod(), supportedMethods);
    log.warn("请求方法不支持: {}", message);
    return Result.fail(message);
  }

  /**
   * 处理数据库完整性约束异常
   *
   * @param e 数据库完整性约束异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  public Result<Object> handleGeneralException(DataIntegrityViolationException e) {
    Throwable cause = e.getCause();

    // 如果是SQL异常，直接使用SQL异常处理器
    if (cause instanceof SQLException sqlException) {
      return handleSQLException(sqlException);
    }

    log.error("数据库完整性约束异常: {}", e.getMessage(), e);
    return Result.fail("数据操作失败，违反数据完整性约束");
  }

  /**
   * 处理文件上传大小超限异常
   *
   * @param e 文件上传大小超限异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public Result<Object> handleGeneralException(MaxUploadSizeExceededException e) {
    // 格式化文件大小显示
    String maxSize;
    if (e.getMaxUploadSize() > 1024 * 1024) {
      maxSize = String.format("%.2fMB", e.getMaxUploadSize() / (1024.0 * 1024.0));
    } else if (e.getMaxUploadSize() > 1024) {
      maxSize = String.format("%dKB", e.getMaxUploadSize() / 1024);
    } else {
      maxSize = String.format("%dB", e.getMaxUploadSize());
    }

    String message = "文件过大，请求失败，最大支持: " + maxSize;
    log.warn("文件上传超限: {}", message);
    return Result.fail(message);
  }

  /**
   * 处理请求参数验证失败异常
   *
   * @param e 参数验证失败异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Result<Object> handleGeneralException(MethodArgumentNotValidException e) {
    // 收集所有验证错误信息
    String errorMessage = e.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).filter(msg -> !Strings.isNullOrEmpty(msg))
        .collect(Collectors.joining("，"));

    log.warn("请求参数验证失败: {}", errorMessage);
    return Result.fail("参数验证失败: " + errorMessage);
  }

  /**
   * 处理可忽略异常 这类异常通常是非致命性的，可以被业务逻辑忽略或特殊处理
   *
   * @param e 可忽略异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(CanIgnoreException.class)
  public Result<Object> handleGeneralException(CanIgnoreException e) {
    log.warn("可忽略异常: {}", e.getMessage());
    return Result.fail(e.getMessage());
  }

  /**
   * 处理业务运行时异常
   *
   * @param e 业务运行时异常
   * @return 包含错误信息和错误代码的标准化响应
   */
  @ExceptionHandler(RunException.class)
  public Result<Object> handleGeneralException(RunException e) {
    log.warn("业务异常: {}", e.getMessage());

    // 如果包含错误代码，使用指定的错误代码
    if (Objects.nonNull(e.getErrorCode())) {
      return new Result<>().setCode(e.getErrorCode()).setErrorData(e.getRetObj()).setMsg(e.getMessage());
    }

    return Result.fail(e.getMessage(), e.getRetObj());
  }

  /**
   * 处理HTTP消息不可读异常（通常是JSON解析失败）
   *
   * @param e HTTP消息不可读异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Result<Object> handleGeneralException(HttpMessageNotReadableException e) {
    log.warn("请求参数解析失败: {}", e.getMessage(), e);
    return Result.fail("请求参数格式错误，请检查请求体").setErrorData(e.getMostSpecificCause().getMessage());
  }

  /**
   * 处理资源未找到异常（通常是访问不存在的接口）
   *
   * @param e 资源未找到异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(NoResourceFoundException.class)
  public Result<Object> handleGeneralException(NoResourceFoundException e) {
    String requestURI = ReqResUtils.getRequest().getRequestURI();
    log.warn("资源未找到: {}", requestURI);
    return Result.fail("请求的资源不存在: " + requestURI);
  }

  /**
   * 处理SQL语法错误异常
   *
   * @param e SQL语法错误异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(BadSqlGrammarException.class)
  public Result<Object> handleSQLException(BadSqlGrammarException e) {
    SQLException sqlException = e.getSQLException();
    assert sqlException != null;
    String errorMsg = SQLErrorMsg.getErrMsg(sqlException.getErrorCode());

    log.error("SQL语法错误 - 错误码: {}, 错误信息: {}, SQL: {}", sqlException.getErrorCode(), errorMsg, e.getSql(), e);

    return Result.fail(errorMsg);
  }

  /**
   * 处理SQL异常
   *
   * @param e SQL异常
   * @return 包含错误信息的标准化响应
   */
  @ExceptionHandler(SQLException.class)
  public Result<Object> handleSQLException(SQLException e) {
    String errorMsg = SQLErrorMsg.getErrMsg(e.getErrorCode());

    log.error("数据库异常 - 错误码: {}, 错误信息: {}", e.getErrorCode(), errorMsg, e);

    return Result.fail(errorMsg);
  }
}
