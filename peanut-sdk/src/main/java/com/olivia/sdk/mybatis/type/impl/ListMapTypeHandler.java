package com.olivia.sdk.mybatis.type.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.olivia.sdk.mybatis.type.ListMyBaseTypeHandler;
import com.olivia.sdk.mybatis.type.model.MapSub;
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
public class ListMapTypeHandler extends ListMyBaseTypeHandler<List<MapSub>> {


  private List<MapSub> getListFromJSON(String val) {
    if (StringUtils.isBlank(val)) {
      return List.of();
    }
    return JSONUtils.readValue(val, new TypeReference<List<MapSub>>() {
    });
  }


  @Override
  public List<MapSub> parse(String json) {
    return getListFromJSON(json);
  }

}
