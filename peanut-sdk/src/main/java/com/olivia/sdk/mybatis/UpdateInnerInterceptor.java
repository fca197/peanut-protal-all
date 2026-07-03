package com.olivia.sdk.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;

@Slf4j
public class UpdateInnerInterceptor implements InnerInterceptor {

  @Override
  public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
    log.info("");
    InnerInterceptor.super.beforeUpdate(executor, ms, parameter);
  }

  @Override
  public boolean willDoUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
    return InnerInterceptor.super.willDoUpdate(executor, ms, parameter);
  }
}
