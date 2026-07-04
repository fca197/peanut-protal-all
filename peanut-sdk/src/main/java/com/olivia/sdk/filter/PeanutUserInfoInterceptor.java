//package com.olivia.sdk.filter;
//
//import com.olivia.sdk.utils.JSON;
//import com.baomidou.mybatisplus.core.toolkit.IdWorker;
//import com.baomidou.mybatisplus.core.toolkit.StringUtils;
//import com.olivia.com.olivia.config.sdk.PeanutProperties;
//import com.olivia.com.olivia.result.sdk.Result;
//import jakarta.annotation.Resource;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//import lombok.extern.slf4j.Slf4j;
//import org.reactivestreams.Publisher;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
//import org.springframework.core.annotation.Order;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.core.io.buffer.DataBufferFactory;
//import org.springframework.core.io.buffer.DataBufferUtils;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//
//import static com.olivia.com.olivia.utils.sdk.MDCUtils.MDC_KEY_TRACE_ID;
//
/// ***
// *
// */
//@Setter
//@Getter
//@Accessors(chain = true)
//@Component
//@Order(-1)
//@Slf4j
//public class PeanutUserInfoInterceptor implements OrderedWebFilter {
//    @Autowired
//    PeanutProperties peanutProperties;
//
//
//    @Resource
//    StringRedisTemplate stringRedisTemplate;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        MDC.put(MDC_KEY_TRACE_ID, IdWorker.getIdStr());
//        String path = exchange.getRequest().getPath().toString();
//
//        // 响应体
//        ServerHttpResponse response = exchange.getResponse();
//
//        if (peanutProperties.getWhiteList().contains(path)) {
//            log.warn("白名单请求：{}", path);
//            return chain.filter(exchange.mutate().build());
//        }
//        String token = exchange.getRequest().getHeaders().getFirst("j-token");
//        if (StringUtils.isBlank(token)) {
//            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
//            return exchange.getResponse().writeWith(Mono.fromSupplier(() -> {
//                DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();
//                return bufferFactory.wrap(Result.fail("登陆态失效,请登陆").toString().getBytes());
//            }));
//        }
//        String userInfo = stringRedisTemplate.opsForValue().get(peanutProperties.getRedisKey().getUserToken()+token);
/// /        if (StringUtils.isBlank(userInfo)) { /            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE); /            return
/// exchange.getResponse().writeWith(Mono.fromSupplier(() -> { /                DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory(); / return
/// bufferFactory.wrap(Result.fail("登陆态失效,请重新登陆").toString().getBytes()); /            })); /        }
//        LoginUser loginUser = JSON.readValue(userInfo, LoginUser.class);
//        LoginUserContext.setContextThreadLocal(loginUser);
//        return chain.filter(exchange.mutate().build());
//
//    }
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
