package com.olivia.sdk.result;

import com.olivia.sdk.utils.MDCUtils;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * API响应结果封装类 统一API接口的响应格式，包含状态码、消息、数据和附加信息
 *
 * @param <T> 响应数据的类型
 */
@Setter
@Getter
@Accessors(chain = true)
public class Result<T> {

  /**
   * 成功状态码
   */
  public static final int SUCCESS_CODE = 200;
  /**
   * 默认错误状态码
   */
  public static final int DEFAULT_ERROR_CODE = 500;

  /**
   * 状态码
   */
  private Integer code = SUCCESS_CODE;

  /**
   * 响应消息
   */
  private String msg = "操作成功";

  /**
   * 响应数据
   */
  private T data;

  /**
   * 错误详情数据
   */
  private Object errorData;

  /**
   * 追踪ID，用于问题排查
   */
  private String traceId;

  /**
   * 响应时间
   */
  private LocalDateTime retTime;

  /**
   * 构建成功响应
   *
   * @param <T> 数据类型
   * @return 成功响应对象
   */
  public static <T> Result<T> success() {
    return new Result<>();
  }

  /**
   * 构建带数据的成功响应
   *
   * @param data 响应数据
   * @param <T>  数据类型
   * @return 带数据的成功响应对象
   */
  public static <T> Result<T> success(T data) {
    return new Result<T>().setData(data);
  }

  /**
   * 构建错误响应
   *
   * @param message 错误消息
   * @param <T>     数据类型
   * @return 错误响应对象
   */
  public static <T> Result<T> fail(String message) {
    return new Result<T>().setCode(DEFAULT_ERROR_CODE).setMsg(message);
  }

  /**
   * 构建带错误码的错误响应
   *
   * @param code    错误码
   * @param message 错误消息
   * @param <T>     数据类型
   * @return 带错误码的错误响应对象
   */
  public static <T> Result<T> fail(int code, String message) {
    return new Result<T>().setCode(code).setMsg(message);
  }

  /**
   * 构建带错误详情的错误响应
   *
   * @param message   错误消息
   * @param errorData 错误详情数据
   * @param <T>       数据类型
   * @return 带错误详情的错误响应对象
   */
  public static <T> Result<T> fail(String message, Object errorData) {
    return new Result<T>().setCode(DEFAULT_ERROR_CODE).setMsg(message).setErrorData(errorData);
  }

  /**
   * 构建基于结果码枚举的错误响应
   *
   * @param resultCode 结果码枚举
   * @param <T>        数据类型
   * @return 错误响应对象
   */
  public static <T> Result<T> fail(PortalResultCode resultCode) {
    return new Result<T>().setCode(Integer.parseInt(resultCode.getCode())).setMsg(resultCode.getMessage());
  }

  /**
   * 构建基于结果码枚举和错误详情的错误响应
   *
   * @param resultCode 结果码枚举
   * @param errorData  错误详情数据
   * @param <T>        数据类型
   * @return 错误响应对象
   */
  public static <T> Result<T> fail(PortalResultCode resultCode, Object errorData) {
    return new Result<T>().setCode(Integer.parseInt(resultCode.getCode())).setMsg(resultCode.getMessage()).setErrorData(errorData);
  }

  /**
   * 检查响应是否成功
   *
   * @return 如果成功返回true，否则返回false
   */
  public boolean isSuccess() {
    return SUCCESS_CODE == this.code;
  }

  /**
   * 获取追踪ID 从MDC中获取，确保日志追踪
   *
   * @return 追踪ID
   */
  public String getTraceId() {
    // 延迟获取，确保在响应时获取最新的traceId
    if (traceId == null) {
      traceId = MDCUtils.getTraceId();
    }
    return traceId;
  }

  /**
   * 获取响应时间 延迟初始化，确保时间准确性
   *
   * @return 响应时间
   */
  public LocalDateTime getRetTime() {
    if (retTime == null) {
      retTime = LocalDateTime.now();
    }
    return retTime;
  }

  public  String getThreadName(){
    return Thread.currentThread().getName();
  }
}
