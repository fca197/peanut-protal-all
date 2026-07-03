package com.olivia.sdk.mybatis.type.impl;

import com.olivia.sdk.model.KVEntity;
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
@MappedJdbcTypes(JdbcType.VARCHAR)
public class ListKVTypeHandler extends ListMyBaseTypeHandler<List<KVEntity>> {


  private List<KVEntity> getListFromJSON(String val) {
    if (StringUtils.isBlank(val)) {
      return List.of();
    }
    return JSONUtils.readList(val, KVEntity.class);
  }

  @Override
  public List<KVEntity> parse(String json) {
    return getListFromJSON(json);
  }

}
