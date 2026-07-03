package com.olivia.sdk.mybatis.type.impl;

import com.olivia.sdk.mybatis.type.ListMyBaseTypeHandler;
import com.olivia.sdk.utils.JSONUtils;
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
public class ListStringTypeHandler extends ListMyBaseTypeHandler<List<String>> {

  private List<String> getListStringFromJSON(String val) {
    if (StringUtils.isBlank(val)) {
      return List.of();
    }
    return JSONUtils.readList(val, String.class);
  }


  @Override
  public List<String> parse(String json) {
    return getListStringFromJSON(json);
  }

}
