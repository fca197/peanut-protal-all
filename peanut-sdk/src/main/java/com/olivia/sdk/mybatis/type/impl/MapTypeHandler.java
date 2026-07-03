package com.olivia.sdk.mybatis.type.impl;

import cn.hutool.core.collection.CollUtil;
import com.olivia.sdk.mybatis.type.model.MapSub;
import com.olivia.sdk.utils.JSONUtils;
import java.sql.*;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.*;

@Slf4j
@MappedTypes(Map.class)
//@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MapTypeHandler extends BaseTypeHandler<MapSub> {

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, MapSub parameter, JdbcType jdbcType)
      throws SQLException {
    if (CollUtil.isNotEmpty(parameter)) {
      ps.setString(i, "{}");
    } else {
      ps.setString(i, JSONUtils.toJSONString(parameter));
    }
  }

  MapSub getMapFromJSON(String val) {
    if (val == null) {
      return new MapSub();
    }
    return JSONUtils.readValue(val, MapSub.class);
  }

  @Override
  public MapSub getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return getMapFromJSON(rs.getString(columnName));
  }

  @Override
  public MapSub getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return getMapFromJSON(rs.getString(columnIndex));
  }

  @Override
  public MapSub getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return getMapFromJSON(cs.getString(columnIndex));
  }
}
