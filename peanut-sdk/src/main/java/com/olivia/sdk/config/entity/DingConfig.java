package com.olivia.sdk.config.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 钉钉配置实体类
 * 用于存储钉钉集成相关的配置信息，包括客户端凭证、机器人配置等
 */
@Setter
@Getter
@Accessors(chain = true)
public class DingConfig {

  /**
   * 钉钉应用客户端ID
   */
  private String clientId;

  /**
   * 钉钉应用客户端密钥
   */
  private String clientSecret;

  /**
   * 钉钉应用Agent ID
   */
  private Long agentId;

  /**
   * 钉钉企业Corp ID
   */
  private String corpId;

  /**
   * 钉钉机器人编码
   */
  private String robotCode;

  /**
   * 钉钉用户编码
   */
  private String dingCode;

  /**
   * 钉钉用户名
   */
  private String dingName;

  /**
   * 是否使用流式处理
   */
  private Boolean useStream;

  /**
   * 用户ID列名
   */
  private String userIdColumnName;
}
