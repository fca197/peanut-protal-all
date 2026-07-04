//package com.olivia.sdk.comment;
//
//import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
//import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponseBody;
//import com.olivia.sdk.config.entity.DingConfig;
//import com.olivia.sdk.utils.$;
//import com.olivia.sdk.utils.JSON;
//import jakarta.annotation.Resource;
//import java.util.concurrent.TimeUnit;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
/// ***
// *
// */
//@Component
//@Slf4j
//public class DingConfigComment {
//
//  static final String dingTokenKey = "dingToken:";
//  @Resource
//  StringRedisTemplate stringRedisTemplate;
//
//  public com.aliyun.dingtalkoauth2_1_0.Client createClient() {
//    try {
//      com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
//      config.protocol = "https";
//      config.regionId = "central";
//      return new com.aliyun.dingtalkoauth2_1_0.Client(config);
//    } catch (Exception e) {
//      log.error("ding client create Error {}", e.getMessage(), e);
//      throw new RuntimeException("钉钉客户端创建异常");
//    }
//  }
//
//  public String getAccessToken(DingConfig dingConfig) {
//    try {
//      $.requireNonNullCanIgnoreException(dingConfig, "钉钉配置不能为空");
//      String key = dingTokenKey + dingConfig.getClientId();
//      String token = this.stringRedisTemplate.opsForValue().get(key);
//      if (StringUtils.isNotBlank(token)) {
//        return token;
//      }
//      com.aliyun.dingtalkoauth2_1_0.Client client = createClient();
//      com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
//          .setAppKey(dingConfig.getClientId()).setAppSecret(dingConfig.getClientSecret());
//      GetAccessTokenResponse accessToken = client.getAccessToken(getAccessTokenRequest);
//      GetAccessTokenResponseBody responseBody = accessToken.getBody();
//      if (log.isDebugEnabled()) {
//        log.debug("Access token response  {} {}", dingConfig.getDingCode(), JSON.toJSONString(responseBody));
//      }
//      this.stringRedisTemplate.opsForValue().set(key, responseBody.getAccessToken(), responseBody.getExpireIn(), TimeUnit.SECONDS);
//      return responseBody.getAccessToken();
//    } catch (Exception e) {
//      log.error("getAccessToken Error {} {}", dingConfig.getDingCode(), e.getMessage(), e);
//      throw new RuntimeException("获取钉钉token异常");
//    }
//  }
//
//}
