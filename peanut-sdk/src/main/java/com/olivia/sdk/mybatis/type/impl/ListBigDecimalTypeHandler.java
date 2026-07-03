package com.olivia.sdk.mybatis.type.impl;

import com.olivia.sdk.mybatis.type.ListMyBaseTypeHandler;
import com.olivia.sdk.utils.JSONUtils;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

@Slf4j
@MappedTypes(List.class)
//@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ListBigDecimalTypeHandler extends ListMyBaseTypeHandler<List<BigDecimal>> {

  private List<BigDecimal> getListStringFromJSON(String val) {
    if (StringUtils.isBlank(val)) {
      return List.of();
    }
    return JSONUtils.readList(val, BigDecimal.class);
  }


  @Override
  public List<BigDecimal> parse(String json) {
    return getListStringFromJSON(json);
  }

}
