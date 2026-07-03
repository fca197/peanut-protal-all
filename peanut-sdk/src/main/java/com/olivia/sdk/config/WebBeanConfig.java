package com.olivia.sdk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivia.sdk.filter.WebHandlerInterceptor;
import com.olivia.sdk.utils.JSONUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类 配置拦截器、消息转换器、内容协商等Web相关组件
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebBeanConfig implements WebMvcConfigurer {

  private final WebHandlerInterceptor webHandlerInterceptor;

  /**
   * 配置拦截器 注册WebHandlerInterceptor并设置拦截路径和排除路径
   *
   * @param registry 拦截器注册表
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    // 创建拦截器注册器并配置路径
//    InterceptorRegistration interceptorRegistration = registry.addInterceptor(webHandlerInterceptor).addPathPatterns("/**");  // 拦截所有路径

    // 配置排除路径（白名单）
//    List<String> whiteList = peanutProperties.getUrlWhiteList();
//    if (!whiteList.isEmpty()) {
//      interceptorRegistration.excludePathPatterns(whiteList.toArray(new String[0]));
//      log.info("已配置拦截器排除路径，共 {} 条 whiteList: {}", whiteList.size(), whiteList);
//    }

    log.info("WebHandlerInterceptor 已注册，拦截路径: /**");
  }

  /**
   * 配置内容协商 设置默认响应媒体类型为JSON
   *
   * @param configurer 内容协商配置器
   */
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(MediaType.APPLICATION_JSON).ignoreAcceptHeader(false);  // 不忽略Accept头，支持内容协商

    log.debug("已配置默认响应媒体类型为: {}", MediaType.APPLICATION_JSON);
  }

  /**
   * 配置消息转换器 使用自定义的ObjectMapper替换默认的Jackson消息转换器
   *
   * @param converters 消息转换器列表
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    // 获取自定义的ObjectMapper（已配置好的）
    ObjectMapper customMapper = JSONUtils.getMapper();

    // 创建并配置Jackson消息转换器
    MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter(customMapper);

    // 将自定义转换器添加到列表首位，优先使用
    converters.addFirst(jacksonConverter);

    log.info("已配置自定义Jackson消息转换器");
  }
}
