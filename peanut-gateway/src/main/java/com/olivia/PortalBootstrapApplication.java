package com.olivia;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Indexed;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/***
 *
 */
@Indexed
@Slf4j
//@EnableCaching
//@EnableAspectJAutoProxy
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@MapperScan(basePackages = {"com.olivia.peanut.*.mapper", "com.olivia.sdk.mapper"})
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableTransactionManagement
public class PortalBootstrapApplication {

  static void main(String[] args) {
    try {
      SpringApplication app = new SpringApplication(PortalBootstrapApplication.class);
      app.setApplicationStartup(new BufferingApplicationStartup(2048));
      app.run(args);
//      SpringApplication.run(PortalBootstrapApplication.class, args);
      log.info(">>>>>>>  PortalBootstrapApplication  start success >>>>>>>");
    } catch (Exception e) {
      log.info(">>>>>>>  PortalBootstrapApplication  start fail >>>>>>> {}", e.getMessage(), e);
    }
  }
}
