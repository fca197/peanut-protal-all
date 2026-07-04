//package com.olivia.sdk.comment;
//
//import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOHeaders;
//import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTORequest;
//import com.aliyun.teaopenapi.models.Config;
//import com.aliyun.teautil.models.RuntimeOptions;
//import com.olivia.sdk.config.entity.DingConfig;
//import com.olivia.sdk.utils.JSON;
//import jakarta.annotation.Resource;
//import java.util.List;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
/// ***
// *
// */
//@Slf4j
//@Component
//public class DingRobotComment {
//
//  @Resource
//  DingConfigComment dingConfigComment;
//
//  public static com.aliyun.dingtalkrobot_1_0.Client createClient() throws Exception {
//    Config config = new Config();
//    config.protocol = "https";
//    config.regionId = "central";
//    return new com.aliyun.dingtalkrobot_1_0.Client(config);
//  }
//
//  public void sendMessage(DingConfig dingConfig, List<String> dingUserId, String title, String text) {
//    try {
//      // 文档地址: https://open.dingtalk.com/document/orgapp/chatbots-send-one-on-one-chat-messages-in-batches
//      String accessToken = dingConfigComment.getAccessToken(dingConfig);
//      BatchSendOTOHeaders batchSendOTOHeaders = new BatchSendOTOHeaders();
//      batchSendOTOHeaders.xAcsDingtalkAccessToken = accessToken;
//      Map<String, String> map = Map.of("title", title, "text", text);
//      BatchSendOTORequest batchSendOTORequest = new BatchSendOTORequest()
//          .setRobotCode(dingConfig.getRobotCode()).setUserIds(dingUserId).setMsgKey("sampleMarkdown")
//          .setMsgParam(JSON.toJSONString(map));
//      createClient().batchSendOTOWithOptions(batchSendOTORequest, batchSendOTOHeaders, new RuntimeOptions().setAutoretry(Boolean.FALSE));
//    } catch (Exception e) {
//      log.error("sendMessage error {}", e.getMessage(), e);
//    }
//
//  }
//}
