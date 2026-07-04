//package com.olivia.sdk.service.impl;
//
//import static com.olivia.sdk.utils.Str.DEFAULT;
//
//import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
//import com.dingtalk.open.app.stream.protocol.event.EventAckStatus;
//import com.olivia.sdk.service.DingEventBizService;
//import com.olivia.sdk.utils.JSON;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
/// ***
// *
// */
//@Slf4j
//@Service
//public class DefaultDingEventBizService implements DingEventBizService {
//
//  @Override
//  public String bizEventType() {
//    return DEFAULT;
//  }
//
//  @Override
//  public EventAckStatus onEvent(GenericOpenDingTalkEvent event) {
//    if (log.isDebugEnabled()) {
//      log.debug("default bizEventType:{} event: {} ", bizEventType(), JSON.toJSONString(event));
//    }
//    return EventAckStatus.SUCCESS;
//  }
//}
