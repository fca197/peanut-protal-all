package com.olivia.sdk.mybatis.type.impl;

import java.sql.*;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKBReader;
import org.locationtech.jts.io.WKBWriter;

public class GeoTypeHandler extends BaseTypeHandler<Geometry> {

  private static final WKBReader reader = new WKBReader();
  private static final WKBWriter writer = new WKBWriter();

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType) throws SQLException {

    ps.setBytes(i, writer.write(parameter));
  }

  @Override
  public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
    byte[] wkt = rs.getBytes(columnName);
    return parseWkt(wkt);
  }

  @Override
  public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    byte[] wkt = rs.getBytes(columnIndex);

    return parseWkt(wkt);
  }

  @Override
  public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    byte[] wkt = cs.getBytes(columnIndex);
    return parseWkt(wkt);
  }

  private Geometry parseWkt(byte[] wkt) {
    if (wkt == null) {
      return null;
    }
    try {
      return reader.read(wkt);
    } catch (Exception e) {
      throw new RuntimeException("解析WKT失败: " + wkt, e);
    }
  }
}