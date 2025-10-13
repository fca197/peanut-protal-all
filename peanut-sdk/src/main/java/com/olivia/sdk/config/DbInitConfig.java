//package com.olivia.sdk.config;
//
//import static cn.hutool.core.text.CharSequenceUtil.toUnderlineCase;
//import static com.olivia.com.olivia.utils.sdk.Str.H2;
//import static com.olivia.com.olivia.utils.sdk.Str.SQLITE;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.text.CharSequenceUtil;
//import cn.hutool.core.util.ClassUtil;
//import com.olivia.sdk.utils.JSON;
//import com.baomidou.mybatisplus.annotation.TableName;
//import com.google.common.collect.Lists;
//import com.olivia.sdk.ann.BelongDb;
//import com.olivia.com.olivia.ann.sdk.TableFieldExt;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.Resource;
//import java.lang.reflect.Field;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//import javax.sql.DataSource;
//import lombok.Getter;
//import lombok.Setter;
//import lombok.experimental.Accessors;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Component;
//
/// ***
// *
// */
//@Slf4j
//@Component
//@ConditionalOnProperty(prefix = "peanut", value = "dbInit")
//public class DbInitConfig {
//
//  @Resource
//  PeanutProperties peanutProperties;
//
//  @Resource
//  DynamicRoutingDataSource dynamicRoutingDataSource;
//
//  private static String getSqliteColumnType(Field v) {
//    String type = StringUtils.containsAnyIgnoreCase(v.getType().getSimpleName(), "integer", "long") ? "integer" : "text";
//    return type;
//  }
//
//  @PostConstruct
//  public void init() {
//    Set<Class<?>> classSet = ClassUtil.scanPackage(peanutProperties.getEntityPackageName());
//    if (CollUtil.isEmpty(classSet)) {
//              if (log.isDebugEnabled()) log.debug("{} entity is Empty", peanutProperties.getEntityPackageName());
//      return;
//    }
//    classSet.forEach(clazz -> {
/// /              if (log.isDebugEnabled()) log.debug("{} init", clazz);
//      BelongDb belongDb = clazz.getAnnotation(BelongDb.class);
//      if (Objects.isNull(belongDb)) {
////                if (log.isDebugEnabled()) log.debug("{} belongDb not has", clazz);
//        return;
//      }
//      Field[] declaredFields = clazz.getSuperclass().getDeclaredFields();
//      Map<String, Field> fieldMap = Arrays.stream(clazz.getDeclaredFields()).collect(
//          Collectors.toMap(Field::getName, Function.identity(),  BiFunctionImpl.getFist(), LinkedHashMap::new));
//      Map<String, Field> pm = Arrays.stream(declaredFields).collect(Collectors.toMap(Field::getName, Function.identity(),  BiFunctionImpl.getFist(), LinkedHashMap::new));
//
//      Map<String, Field> allFieldMap = new LinkedHashMap<>();
//
//      allFieldMap.putAll(fieldMap);
//      allFieldMap.putAll(pm);
//      allFieldMap.remove("id");
//      ArrayList<String> dbNameList = Lists.newArrayList(belongDb.dataSourceNames());
//      if (dbNameList.contains(H2)) {
//        h2(clazz, allFieldMap);
//      }
//      if (dbNameList.contains(SQLITE)) {
//        sqlite(clazz, allFieldMap);
//      }
//    });
//  }
//
//  private void h2(Class<?> clazz, Map<String, Field> allFieldMap) {
//    DataSource dataSource = dynamicRoutingDataSource.getDataSource(H2);
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//    String tableName = getTableName(clazz);
//
//    List<H2Column> columnList = jdbcTemplate.query("show columns from " + tableName, new RowMapper<H2Column>() {
//      @Override
//      public H2Column mapRow(ResultSet rs, int rowNum) throws SQLException {
//        return new H2Column().setType(rs.getString("TYPE")).setField(rs.getString("FIELD")).setRowNum(rowNum);
//      }
//    });
//            if (log.isDebugEnabled()) log.debug("columnList :{}", JSON.toJSONString(columnList));
//    if (CollUtil.isEmpty(columnList)) {
//      createH2Table(jdbcTemplate, tableName, allFieldMap, clazz);
//    } else {
//      updateH2Table(jdbcTemplate, tableName, allFieldMap, clazz);
//    }
//  }
//
//  private void updateH2Table(JdbcTemplate jdbcTemplate, String tableName, Map<String, Field> allFieldMap, Class<?> clazz) {
//    List<H2Column> h2ColumnList = jdbcTemplate.query("show columns from  " + tableName, (rs, rowNum) -> {
//      return new H2Column().setField(rs.getString("FIELD")).setRowNum(rowNum);
//    });
//    h2ColumnList.forEach(t -> allFieldMap.remove(CharSequenceUtil.toCamelCase(t.getField())));
//    if (CollUtil.isNotEmpty(allFieldMap)) {
//      allFieldMap.forEach((k, v) -> {
//        String sql = "alter table " + tableName + " add  column " + toUnderlineCase(k) + " " + getH2ColumnType(v) + " not null;";
//        jdbcTemplate.execute(sql);
//      });
//    }
//    allFieldMap.values().forEach(t -> {
//      TableFieldExt fieldExt = t.getAnnotation(TableFieldExt.class);
//      if (Objects.nonNull(fieldExt)) {
//        String sql =
//            "select count(1) c from INFORMATION_SCHEMA.INDEXES  where TABLE_NAME   ='" + tableName + "' and  index_name='idx_" + tableName + "_" + toUnderlineCase(t.getName())
//                + "';";
//        int c = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getInt("c"));
//        if (c == 0) {
//          sql = "CREATE INDEX idx_" + tableName + "_" + toUnderlineCase(t.getName()) + " ON " + tableName + " (" + toUnderlineCase(t.getName()) + ");";
//          jdbcTemplate.execute(sql);
//        }
//      }
//    });
//
//  }
//
//  private String getH2ColumnType(Field field) {
//    TableFieldExt fieldExt = field.getAnnotation(TableFieldExt.class);
//    if (Objects.nonNull(fieldExt)) {
//      return switch (field.getType().getSimpleName()) {
//        case "Integer":
//          yield "int";
//        case "Long":
//          yield "bigint";
//        case "Double":
//          yield "double";
//        case "Float":
//          yield "float";
//        case "BigDecimal":
//          yield "decimal(10,2)";
//        case "Date":
//        case "Timestamp":
//        case "LocalDateTime":
//        case "LocalDate":
//          yield "datetime";
//        default:
//          yield "varchar(128)";
//      };
//    }
//    return "varchar(128)";
//  }
//
//  private void createH2Table(JdbcTemplate jdbcTemplate, String tableName, Map<String, Field> allFieldMap, Class<?> clazz) {
//    StringBuilder sb = new StringBuilder();
//    sb.append(" create table ").append(tableName).append("  (id  int PRIMARY KEY, ");
//    List<String> indexSql = new ArrayList<>();
//    allFieldMap.values().forEach(f -> {
//      TableFieldExt fieldExt = f.getAnnotation(TableFieldExt.class);
//      sb.append(toUnderlineCase(f.getName())).append(" ");
//      int length = 128;
//      if (Objects.nonNull(fieldExt)) {
//        if (fieldExt.isKey()) {
//          indexSql.add("CREATE INDEX idx_" + tableName + "_" + f.getName() + " ON " + tableName + " (" + toUnderlineCase(f.getName()) + ")");
//        }
//        sb.append(getH2ColumnType(f)).append(" not null ,");
//      } else {
//        sb.append(" varchar(128)  not null ,");
//      }
//    });
//    sb.deleteCharAt(sb.length() - 1);
//    sb.append(");");
//            if (log.isDebugEnabled()) log.debug("create table sql:{}", sb.toString());
//    jdbcTemplate.execute(sb.toString());
//            if (log.isDebugEnabled()) log.debug("create table index sql:{}", indexSql);
//    indexSql.forEach(jdbcTemplate::execute);
//  }
//
//  private void sqlite(Class<?> clazz, Map<String, Field> allFieldMap) {
//    DataSource dataSource = dynamicRoutingDataSource.getDataSource(SQLITE);
//    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//    String tableName = getTableName(clazz);
//
//    try {
//      List<SqliteColumn> sqliteColumnList = jdbcTemplate
//          .query("PRAGMA table_info('" + tableName + "');", (rs, rowNum) -> new SqliteColumn().setName(CharSequenceUtil.toCamelCase(rs.getString("name")))
//              .setType(rs.getString("type"))
//              .setPk(rs.getInt("pk")).setRowNum(rowNum)
//              .setSql(rs.getString("cid")));
//              if (log.isDebugEnabled()) log.debug("sqliteColumnList:{}", JSON.toJSONString(sqliteColumnList));
//      Set<String> hasColumnSet = sqliteColumnList.stream().map(SqliteColumn::getName).collect(Collectors.toSet());
//      hasColumnSet.forEach(allFieldMap::remove);
//      if (CollUtil.isEmpty(hasColumnSet)) {
//        createSqliteTable(jdbcTemplate, tableName, allFieldMap);
//      } else {
//        updateSqliteTable(jdbcTemplate, tableName, allFieldMap);
//      }
//
//    } catch (Exception e) {
//      log.error("sqlite exec error {}", e.getMessage(), e);
//    }
//  }
//
//  private void updateSqliteTable(JdbcTemplate jdbcTemplate, String tableName, Map<String, Field> allFieldMap) {
//
//    //    alter table t_brand_info add aaa integer;
//    if (CollUtil.isNotEmpty(allFieldMap)) {
//      allFieldMap.values().forEach(t -> {
//        StringBuilder sb = new StringBuilder("alter table ").append(tableName).append(" ");
//        sb.append(" add  ").append(toUnderlineCase(t.getName())).append(" ").append(getSqliteColumnType(t)).append(" ;");
//                if (log.isDebugEnabled()) log.debug("updateSqliteTable:{}", sb.toString());
//        jdbcTemplate.execute(sb.toString());
//      });
//    }
//
//    Map<String, String> indexSql = new LinkedHashMap<>();
//    allFieldMap.values().forEach(t -> {
//      TableFieldExt te = t.getAnnotation(TableFieldExt.class);
//      if (Objects.nonNull(te) && te.isKey()) {
//        //create index t_brand_info_tenant_id_index   on t_brand_info (tenant_id);
//        String key = "idx_" + tableName + "_" + toUnderlineCase(t.getName());
//        indexSql.put(key, " create " + key + " on " + tableName + " ( " + toUnderlineCase(t.getName()) + ");");
//      }
//    });
//
//    List<SqliteIndex> sqliteIndexList = jdbcTemplate.query("select  * from sqlite_master where tbl_name='" + tableName + "' and type='index'",
//        (rs, rowNum) -> new SqliteIndex().setName(rs.getString("name")).setType(rs.getString("type")).setRowNum(rowNum).setTblName(rs.getString("tbl_name")));
//    sqliteIndexList.forEach(t -> indexSql.remove(t.getName()));
//    indexSql.values().forEach(jdbcTemplate::execute);
//  }
//
//  private void createSqliteTable(JdbcTemplate jdbcTemplate, String tableName, Map<String, Field> fieldMap) {
//    StringBuilder sb = new StringBuilder();
//    sb.append("create table ").append(tableName).append(" (     id INTEGER PRIMARY KEY AUTOINCREMENT,");
//    fieldMap.forEach((k, v) -> {
//      String type = getSqliteColumnType(v);
//      sb.append(toUnderlineCase(v.getName())).append("  ")
//          .append(type).append(",");
//    });
//    sb.deleteCharAt(sb.length() - 1);
//    sb.append(");");
//            if (log.isDebugEnabled()) log.debug("createSqliteTable: {}", sb);
//    jdbcTemplate.execute(sb.toString());
//    fieldMap.values().forEach(t -> {
//      TableFieldExt fieldExt = t.getAnnotation(TableFieldExt.class);
//      if (Objects.nonNull(fieldExt) && fieldExt.isKey()) {
//        String sql =
//            "create index idx_" + tableName + "_" + toUnderlineCase(t.getName()) + " on " + tableName + " (" + toUnderlineCase(t.getName())
//                + ");";
//                if (log.isDebugEnabled()) log.debug("createSqliteTable: indexSQL: {}", sql);
//        jdbcTemplate.execute(sql);
//      }
//    });
//  }
//
//  private String getTableName(Class<?> clazz) {
//    TableName tableName = clazz.getAnnotation(TableName.class);
//    return Objects.isNull(tableName) ? toUnderlineCase(clazz.getSimpleName()) : tableName.value();
//  }
//
//  @Setter
//  @Getter
//  @Accessors(chain = true)
//  class SqliteIndex {
//
//    private Integer rowNum;
//    private String type;
//    private String name;
//    private String tblName;
//  }
//
//
//  @Setter
//  @Getter
//  @Accessors(chain = true)
//  class SqliteColumn {
//
//    private String name;
//    private String type;
//    private Integer pk;
//    private String sql;
//    private Integer rowNum;
//  }
//
//  @Setter
//  @Getter
//  @Accessors(chain = true)
//  class H2Column {
//
//    private String field, type;
//    private Integer rowNum;
//  }
//}
