package com.olivia.sdk.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpClientUtils {

  @SneakyThrows
  public static <T> T get(String url, Class<T> clazz) {
    return get(url, clazz, 3);
  }

  @SneakyThrows
  public static <T> T get(String url, Class<T> clazz, int timeout) {
    log.info("get , clazz {} , url {}", clazz, url);
    @Cleanup HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(timeout)).build();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).timeout(Duration.ofSeconds(timeout)).GET().build();
    return client.send(request, HttpClientUtils.getBodyHandler(clazz)).body();

  }

  public static <T> BodyHandler<T> getBodyHandler(Class<T> clazz) {
    return responseInfo -> HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8), stringBody -> {
      try {
        if (log.isDebugEnabled()) {
          log.debug("getBodyHandler class : {} , Received a string body of {}", clazz, stringBody);
        }
        // 将 JSON 字符串反序列化为 User 对象
        return JSON.readValue(stringBody, clazz);
      } catch (Exception e) {
        log.error("JSON 反序列化失败 className:{} stringBody:{}", clazz, stringBody, e);
        return null;
      }
    });
  }

}
