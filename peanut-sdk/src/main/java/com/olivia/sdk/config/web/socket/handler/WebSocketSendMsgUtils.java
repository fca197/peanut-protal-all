//package com.olivia.sdk.config.web.socket.handler;
//
//import com.olivia.sdk.config.web.socket.entity.WebSocketMessage;
//import com.olivia.sdk.filter.LoginUserContext;
//import jakarta.annotation.Resource;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
/// /@Component
//public class WebSocketSendMsgUtils {
//
//  private static SimpMessagingTemplate simpMessagingTemplate;
//
//  /**
//   * 发送点对点消息
//   *
//   * @param destination   消息订阅节点名称 /user/{token}/名称
//   * @param socketSendMsg 消息
//   */
//  public static void sendToUserByUserId(String destination, WebSocketMessage socketSendMsg) {
//    Long id = LoginUserContext.getLoginUser().getId();
//    destination = checkDestination(destination);
//    log.info("sendToUserByUserId message to user: {}  {} {} ", id, destination, socketSendMsg);
//    simpMessagingTemplate.convertAndSendToUser(String.valueOf(id), destination, socketSendMsg);
//  }
//
//  /**
//   * 发送点对点消息
//   *
//   * @param destination   消息订阅节点名称 /user/{token}/名称
//   * @param socketSendMsg 消息
//   */
//  public static void sendToUserByUserId(Long targetUserId, String destination,
//      WebSocketMessage socketSendMsg) {
//    destination = checkDestination(destination);
//    log.info("sendToUserByUserId message to user targetUserId: {}  {} {} ", targetUserId,
//        destination, socketSendMsg);
//    simpMessagingTemplate.convertAndSendToUser(String.valueOf(targetUserId), destination,
//        socketSendMsg);
//  }
//
//  /**
//   * 发送点对点消息
//   *
//   * @param destination   消息订阅节点名称 /user/当前用户token/名称
//   * @param socketSendMsg 消息
//   */
//  public static void sendToUserByUserToken(String destination, WebSocketMessage socketSendMsg) {
//    destination = checkDestination(destination);
//    String token = LoginUserContext.getLoginUser().getToken();
//    log.info("sendToUserByUserToken message to user: {}  {} {} ", token, destination,
//        socketSendMsg);
//    simpMessagingTemplate.convertAndSendToUser(token, destination, socketSendMsg);
//  }
//
//  /**
//   * 发送点对点消息
//   *
//   * @param targetUserToken 雨后登录token
//   * @param destination     消息订阅节点名称 /user/当前用户token/名称
//   * @param socketSendMsg   消息
//   */
//  public static void sendToUserByUserToken(String targetUserToken, String destination,
//      WebSocketMessage socketSendMsg) {
//    destination = checkDestination(destination);
//    log.info("sendToUserByUserToken message to user: {}  {} {} ", targetUserToken, destination,
//        socketSendMsg);
//    simpMessagingTemplate.convertAndSendToUser(targetUserToken, destination, socketSendMsg);
//  }
//
//  /***
//   * 发送一对多消息
//   * @param destination 消息节点名称
//   * @param socketSendMsg 消息
//   */
//  public static void sendToTopic(@NonNull String destination, WebSocketMessage socketSendMsg) {
//    destination = checkDestination(destination);
//    log.info("sendToTopic message to topic: {} {} ", destination, socketSendMsg);
//    simpMessagingTemplate.convertAndSend("/topic/" + destination, socketSendMsg);
//  }
//
//  @NonNull
//  private static String checkDestination(@NonNull String destination) {
//    destination = destination.startsWith("/") ? destination.substring(1) : destination;
//    return destination;
//  }
//
//  @Resource
//  public void setSimpMessagingTemplate(SimpMessagingTemplate simpMessagingTemplate) {
//    WebSocketSendMsgUtils.simpMessagingTemplate = simpMessagingTemplate;
//  }
//}
