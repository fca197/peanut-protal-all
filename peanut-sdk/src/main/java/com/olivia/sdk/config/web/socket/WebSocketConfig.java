//package com.olivia.sdk.config.web.socket;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
//
////@Configuration
////@EnableWebSocketMessageBroker
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//  @Override
//  public void configureMessageBroker(MessageBrokerRegistry config) {
//    // 启用简单的内存消息代理，用于将消息从一个客户端路由到另一个客户端
//    config.enableSimpleBroker("/topic", "/user");
//    // 以/app开头的消息会路由到@MessageMapping注解的方法
//    config.setApplicationDestinationPrefixes("/ws/msg");
//  }
//
//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    // 注册 STOMP 端点，客户端将使用此 URL 连接到 WebSocket 服务器
//    registry.addEndpoint("/ws")
//        .setAllowedOrigins("https://solveplan.cn", "https://aps.solveplan.cn",
//            "http://localhost:3333")
//        .withSockJS();
//  }
//
//  @Bean
//  public ServletServerContainerFactoryBean createWebSocketContainer() {
//    ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//    container.setMaxTextMessageBufferSize(8192);
//    container.setMaxBinaryMessageBufferSize(8192);
//    return container;
//  }
//}