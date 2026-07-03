package com.olivia.sdk.filter;

import com.google.common.base.Strings;
import com.olivia.sdk.config.PeanutProperties;
import com.olivia.sdk.result.Result;
import com.olivia.sdk.utils.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Web请求拦截器 处理登录验证、白名单检查、请求上下文设置等前置逻辑
 */
@Slf4j
@Component
public class WebHandlerInterceptor implements HandlerInterceptor {

  @Resource
  private StringRedisTemplate stringRedisTemplate;

  @Resource
  private PeanutProperties peanutProperties;

  /**
   * 向响应写入JSON格式的错误信息
   *
   * @param response 响应对象
   * @param code     错误代码
   * @param msg      错误信息
   */
  private static void writeErrorResponse(HttpServletResponse response, Integer code, String msg) {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json;charset=UTF-8");

    // 使用try-with-resources确保PrintWriter自动关闭
    try (PrintWriter writer = response.getWriter()) {
      Result<Object> errorResult = Result.fail(msg).setCode(code);
      writer.write(JSONUtils.toJSONString(errorResult));
      writer.flush();
    } catch (Exception e) {
      log.error("写入错误响应失败: {}", e.getMessage(), e);
    }
  }

  /**
   * 预处理请求，进行登录验证和白名单检查
   *
   * @param request  请求对象
   * @param response 响应对象
   * @param handler  处理器
   * @return 如果验证通过返回true，否则返回false
   * @throws Exception 处理过程中发生的异常
   */
  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
    MDCUtils.initMdc(true);
    String requestUri = request.getRequestURI();
    log.debug("处理请求前置拦截: {}", requestUri);

    // 白名单检查，如果是白名单路径则直接放行
    if (isWhiteListPath(request)) {
      log.debug("请求路径在白名单中，直接放行: {}", requestUri);
      return true;
    }

    // 登录验证
    if (!isValidLogin(request, response)) {
      writeErrorResponse(response, 401, "登录信息失效，请重新登录");
      return false;
    }

    return true;
  }

  /**
   * 检查请求路径是否在白名单中
   *
   * @param request 请求对象
   * @return 如果在白名单中返回true，否则返回false
   */
  private boolean isWhiteListPath(HttpServletRequest request) {
    String requestUri = request.getRequestURI();
    String contextPath = request.getContextPath();

    // 移除上下文路径，获取相对路径
    String relativeUri = Strings.isNullOrEmpty(contextPath) ? requestUri : requestUri.substring(contextPath.length());

    return peanutProperties.isUrlWhite(relativeUri);
  }

  /**
   * 验证用户登录状态
   *
   * @param request  请求对象
   * @param response 响应对象
   * @return 如果登录有效返回true，否则返回false
   */
  private boolean isValidLogin(HttpServletRequest request, HttpServletResponse response) {
    String requestUri = request.getRequestURI();

    // 检查是否是禁用的操作
    if (isDisabledOperation(requestUri)) {
      writeErrorResponse(response, 500, "演示环境，禁止此操作");

      return false;
    }

    // 获取并验证Token
    String token = request.getHeader(Str.ReqHeader.J_TOKEN);
    if (Strings.isNullOrEmpty(token)) {
      log.warn("请求头中未包含有效Token: {}", requestUri);

      return false;
    }

    // 从Redis获取用户信息
    String redisKey = peanutProperties.getRedisKey().getUserToken() + token;
    String userInfoJson = stringRedisTemplate.opsForValue().get(redisKey);

    // 记录访问日志
    String clientIp = IpUtils.getClientIp(request);
    String deviceId = request.getHeader("x-device-id");
    log.info("Token验证 - key: {}, URL: {}, IP: {}, DeviceId: {}", redisKey, requestUri, clientIp, deviceId);

    // 验证用户信息
    if (Strings.isNullOrEmpty(userInfoJson)) {
      log.warn("Token无效或已过期 - key: {}, URL: {}", redisKey, requestUri);
      return false;
    }

    // 解析用户信息并设置上下文
    try {
      LoginUser loginUser = JSONUtils.readValue(userInfoJson, LoginUser.class);
      loginUser.setIpAddress(clientIp).setDeviceId(deviceId).setToken(token);

      // 将用户信息存入线程上下文
      LoginUserContext.setLoginUser(loginUser);
      LoginUserContext.setIgnoreTenantId(false);

      return true;
    } catch (Exception e) {
      log.error("解析用户信息失败: {}", e.getMessage(), e);

      return false;
    }
  }

  /**
   * 检查是否是禁用的操作
   *
   * @param requestUri 请求路径
   * @return 如果是禁用的操作返回true，否则返回false
   */
  private boolean isDisabledOperation(String requestUri) {
    String[] disabledOperations = peanutProperties.getDisableOperation();
    if (Objects.isNull(disabledOperations)) {
      return false;
    }

    for (String disabledOp : disabledOperations) {
      if (requestUri.contains(disabledOp)) {
        return true;
      }
    }
    return false;
  }


  /**
   * 请求处理完成后清理资源
   *
   * @param request  请求对象
   * @param response 响应对象
   * @param handler  处理器
   * @param ex       异常对象
   */
  @Override
  public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
    // 清理线程上下文和MDC，防止内存泄漏
    log.debug("请求处理完成，清理上下文资源: {}", request.getRequestURI());
    LoginUserContext.clear();
    MDCUtils.clear();
  }
}
