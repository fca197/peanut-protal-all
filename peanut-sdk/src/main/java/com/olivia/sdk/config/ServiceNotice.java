//package com.olivia.sdk.config;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.date.DateUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import cn.hutool.system.SystemUtil;
//import com.olivia.sdk.comment.DingRobotComment;
//import com.olivia.sdk.config.entity.DingConfig;
//import jakarta.annotation.PreDestroy;
//import java.util.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.stereotype.Component;
//
/// **
// * 服务通知管理器 负责处理服务生命周期事件（启动、停止）的通知，以及发送自定义消息到钉钉
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ServiceNotice implements InitializingBean {
//
//  /**
//   * 通用消息类型
//   */
//  public static final String MSG_TYPE_COMMON = "common";
//
//  /**
//   * 服务结束消息类型
//   */
//  public static final String MSG_TYPE_END = "end";
//
//  /**
//   * 服务启动消息类型
//   */
//  public static final String MSG_TYPE_START = "start";
//
//  /**
//   * 单例实例，用于静态方法中访问Spring Bean
//   */
//  private static ServiceNotice instance;
//
//  private final DingRobotComment dingRobotComment;
//  private final PeanutProperties peanutProperties;
//
//  /**
//   * 发送服务启动通知
//   */
//  public static void notifyStart() {
//    sendDingMessage("服务启动", "服务启动成功，启动时间: " + DateUtil.now(), MSG_TYPE_START);
//  }
//
//  /**
//   * 发送服务正常停止通知
//   */
//  public static void notifyStop() {
//    sendDingMessage("服务关闭", "服务正常关闭，关闭时间: " + DateUtil.now(), MSG_TYPE_END);
//  }
//
//  /**
//   * 发送服务异常停止通知
//   *
//   * @param e 导致服务停止的异常
//   */
//  public static void notifyErrorStop(Exception e) {
//    log.error("服务异常关闭", e);
//    String message = String.format("服务异常关闭，关闭时间: %s，异常原因: %s", DateUtil.now(), e.getMessage());
//    sendDingMessage("服务异常关闭", message, MSG_TYPE_END);
//  }
//
//  /**
//   * 发送自定义消息（指定消息类型）
//   *
//   * @param title    消息标题
//   * @param message  消息内容
//   * @param userType 接收消息的用户类型
//   */
//  public static void sendCustomMessage(String title, String message, String userType) {
//    sendDingMessage(title, message, userType);
//  }
//
//  /**
//   * 发送自定义通用消息
//   *
//   * @param title   消息标题
//   * @param message 消息内容
//   */
//  public static void sendCustomMessage(String title, String message) {
//    sendDingMessage(title, message, MSG_TYPE_COMMON);
//  }
//
//  /**
//   * 发送消息到钉钉
//   *
//   * @param title    消息标题
//   * @param message  消息内容
//   * @param userType 接收消息的用户类型
//   */
//  private static void sendDingMessage(String title, String message, String userType) {
//    try {
//      // 检查实例是否初始化完成
//      if (Objects.isNull(instance)) {
//        log.debug("服务通知实例未初始化完成，暂不发送消息: {} - {}", title, message);
//        return;
//      }
//
//      // 获取钉钉配置
//      List<DingConfig> dingConfigs = instance.peanutProperties.getDingConfigList();
//
//      // 本地环境或无配置时不发送消息
//      if (isLocalEnvironment() || CollUtil.isEmpty(dingConfigs)) {
//        log.debug("本地环境或无钉钉配置，不发送消息: {} - {}", title, message);
//        return;
//      }
//
//      // 丰富消息内容，添加服务器信息
//      String enrichedMessage = enrichMessageWithServerInfo(message);
//
//      // 获取接收消息的用户ID列表
//      Map<String, List<String>> noticeUserMap = instance.peanutProperties.getServerNoticeUserIdMap();
//      List<String> userIds = Optional.ofNullable(noticeUserMap).map(map -> map.get(userType)).orElse(List.of());
//
//      // 发送消息到第一个钉钉配置
//      DingConfig primaryDingConfig = dingConfigs.getFirst();
//      instance.dingRobotComment.sendMessage(primaryDingConfig, userIds, title, enrichedMessage);
//
//      log.trace("钉钉消息发送成功: {} - {}", title, enrichedMessage);
//    } catch (Exception e) {
//      log.error("发送钉钉消息失败 - 标题: {}, 内容: {}, 类型: {}", title, message, userType, e);
//    }
//  }
//
//  /**
//   * 检查是否为本地环境
//   *
//   * @return 如果是本地环境返回true，否则返回false
//   */
//  private static boolean isLocalEnvironment() {
//    return "local".equals(SpringUtil.getActiveProfile());
//  }
//
//  /**
//   * 丰富消息内容，添加服务器信息
//   *
//   * @param originalMessage 原始消息内容
//   * @return 包含服务器信息的消息内容
//   */
//  private static String enrichMessageWithServerInfo(String originalMessage) {
//    String serverUser = SystemUtil.getUserInfo().getName();
//    String hostName = SystemUtil.getHostInfo().getName();
//    return String.format("[%s@%s] %s", serverUser, hostName, originalMessage);
//  }
//
//  /**
//   * 服务销毁前执行，发送服务停止通知
//   */
//  @PreDestroy
//  public void onDestroy() {
//    notifyStop();
//  }
//
//  /**
//   * 初始化完成后执行，设置单例实例
//   */
//  @Override
//  public void afterPropertiesSet() {
//    instance = this;
//    log.info("服务通知管理器初始化完成");
//  }
//}
