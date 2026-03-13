package tmp;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/***
 *
 */
@Slf4j
//@EnableCaching
//@EnableAspectJAutoProxy
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@MapperScan(basePackages = {"com.olivia.peanut.*.mapper", "com.olivia.sdk.mapper"})
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = "com.olivia")
@EnableTransactionManagement

public class BaseBootstrapApplication {

  public static void main(String[] args) {
    try {
      log.info(">>>>>>>  BaseBootstrapApplication  start  >>>>>>>");
      SpringApplication.run(BaseBootstrapApplication.class, args);
      log.info(">>>>>>>  BaseBootstrapApplication  start success >>>>>>>");
    } catch (Exception e) {
      log.info(">>>>>>>  BaseBootstrapApplication  start fail >>>>>>> {}", e.getMessage(), e);
    }
  }
}
