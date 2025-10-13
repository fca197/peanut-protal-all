package com.olivia.peanut.aps.utils.constrained;

import cn.hutool.core.collection.CollUtil;
import com.olivia.peanut.aps.utils.constrained.model.ConstrainedResult;
import com.olivia.peanut.aps.utils.constrained.model.sub.*;
import com.olivia.sdk.utils.$;
import com.olivia.sdk.utils.DynamicsPage.Header;
import com.olivia.sdk.utils.JSON;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;


/***
 * 约束工具类
 */
@Slf4j
public class ConstrainedContentUtils {

  /****
   *  约束入口
   * @param needStoreList 需要约束的列表
   * @param constrainedList 约束规则列表
   * @return ConstrainedResult 约束结果
   */
  public static ConstrainedResult constrained(List<Map<String, Object>> needStoreList,
      List<ConstrainedContent> constrainedList) {
    if (CollUtil.isEmpty(constrainedList)) {
      return new ConstrainedResult().setHeaderList(Lists.newArrayList())
          .setDataList(Lists.newArrayList());
    }
    List<Header> headerList = new ArrayList<>();
    setHeaderList(headerList, constrainedList);
    headerList = new ArrayList<>(
        headerList.stream().collect(Collectors.toMap(Header::getFieldName, f -> f, (v1, v2) -> v1))
            .values().stream().sorted(Comparator.comparing(Header::getFieldName)).toList());
    if (CollUtil.isEmpty(needStoreList)) {
      return new ConstrainedResult().setHeaderList(headerList);
    }
    List<Map<String, Object>> sortList = new ArrayList<>();
    useConstraints(sortList, needStoreList, constrainedList);
    //
    AtomicLong index = new AtomicLong(1);
    sortList.forEach(t -> t.put("numberIndex", index.getAndIncrement()));
    return new ConstrainedResult().setHeaderList(headerList).setDataList(sortList);
  }

  /****
   * 设置 返回表格头
   * @param headerList  表格头
   * @param constrainedList 约束信息
   */
  private static void setHeaderList(List<Header> headerList,
      List<ConstrainedContent> constrainedList) {
    if (CollUtil.isEmpty(constrainedList)) {
      return;
    }
    constrainedList.forEach(t -> {
      t.getFilterList().forEach(f -> {
        headerList.add(new Header().setFieldName(f.getFieldName()).setFieldName(f.getFieldName()));
      });
      setHeaderList(headerList, t.getChildren());
    });
  }


  private static void useConstraints(List<Map<String, Object>> resList,
      List<Map<String, Object>> needStoreList, List<ConstrainedContent> constrainedList) {
    if (CollUtil.isEmpty(needStoreList)) {
      return;
    }
    if (CollUtil.isEmpty(constrainedList)) {
      resList.addAll(needStoreList);
      return;
    }

    List<Map<String, Object>> failList = new ArrayList<>();

    for (ConstrainedContent constrainedContent : constrainedList) {
      List<Map<String, Object>> successList = new ArrayList<>();
      needStoreList.addAll(failList);
      failList.clear();
      while (CollUtil.isNotEmpty(needStoreList)) {
        Map<String, Object> currMap = needStoreList.removeFirst();
        boolean match = isMatch(currMap, constrainedContent.getFilterList());
        if (match) {
          successList.add(currMap);
        } else {
          failList.add(currMap);
        }
      }

      if (CollUtil.isNotEmpty(successList)) {
        List<OrderBy> orderByList = constrainedContent.getOrderBy();
        if (CollUtil.isNotEmpty(orderByList)) {
          orderByList.forEach(orderBy -> {
            Optional<Map<String, Object>> optional = successList.stream()
                .filter(t -> Objects.nonNull(t.get(orderBy.getFieldName()))).findAny();
            optional.ifPresent(v -> {
              Object t = v.get(orderBy.getFieldName());
              if (t instanceof Number) {
                if (Objects.equals(orderBy.getOrderType(), OrderByEnum.ASC)) {
                  successList.sort(Comparator.nullsLast(Comparator.comparingDouble(
                      o -> ((Number) o.get(orderBy.getFieldName())).doubleValue())));
                } else {
                  successList.sort(Comparator.nullsLast(Comparator.comparing(
                      (Map<String, Object> o) -> ((Number) o.get(
                          orderBy.getFieldName())).intValue()).reversed()));
                }
              } else {
                if (Objects.equals(orderBy.getOrderType(), OrderByEnum.ASC)) {
                  successList.sort(Comparator.nullsLast(
                      Comparator.comparing(o -> o.get(orderBy.getFieldName()).toString())));
                } else {
                  successList.sort(Comparator.nullsLast(Comparator.comparing(
                          (Map<String, Object> o) -> o.get(orderBy.getFieldName()).toString())
                      .reversed()));
                }
              }
            });
          });
        }
      }
      List<ConstrainedContent> children = constrainedContent.getChildren();
      if (log.isDebugEnabled()) {
        log.debug("useConstraints,constrainedContent.id {} \n successList:{}", JSON.toJSONString(
                $.copy(constrainedContent, ConstrainedContent.class).setChildren(null)),
            JSON.toJSONString(successList));
      }
      useConstraints(resList, successList, children);

//      resList.addAll(successList);
    }

    resList.addAll(failList);
  }

  /***
   *  信息是否满足 约束
   * @param map  信息
   * @param filterList 约束条件
   * @return bool
   */
  private static boolean isMatch(Map<String, Object> map, List<Filter> filterList) {
    if (CollUtil.isEmpty(filterList)) {
      return true;
    }
//    filterList.removeIf(t -> StringUtils.isBlank(t.getFieldName()));
    if (CollUtil.isEmpty(filterList)) {
      return true;
    }
    for (Filter filter : filterList) {
      Object obj = map.get(filter.getFieldName());
      OperatorEnum operatorEnum = filter.getOperator();
      if (Objects.isNull(operatorEnum)) {
        log.warn("operatorEnum is null, fieldName:{}", filter.getFieldName());
        return true;
      }
      String firstValue = filter.getValueList().getFirst();
      if (obj instanceof Number number) {
        double min = filter.getMin();
        double max = filter.getMax();
        return switch (operatorEnum) {
          case EQ -> number.doubleValue() == min;
          case NE -> number.doubleValue() != min;
          case GT -> number.doubleValue() > min;
          case GE -> number.doubleValue() >= min;
          case LT -> number.doubleValue() < min;
          case LE -> number.doubleValue() <= min;
          case BETWEEN -> number.doubleValue() >= min && number.doubleValue() <= max;
          case NOT_BETWEEN -> number.doubleValue() < min || number.doubleValue() > max;
          default -> false;
        };

      }
      String o = String.valueOf(obj);
      return switch (operatorEnum) {
        case EQ -> o.equals(firstValue);
        case NE -> !o.equals(firstValue);
        case GT -> o.compareTo(firstValue) > 0;
        case GE -> o.compareTo(firstValue) >= 0;
        case LT -> o.compareTo(firstValue) < 0;
        case LE -> o.compareTo(firstValue) <= 0;
        case LIKE -> o.contains(firstValue);
        case IN -> filter.getValueList().contains(o);
        case NOT_IN -> !filter.getValueList().contains(o);
        case BETWEEN -> o.compareTo(firstValue) >= 0
            && o.compareTo(filter.getValueList().get(1)) <= 0;
        case NOT_BETWEEN -> o.compareTo(firstValue) < 0
            || o.compareTo(filter.getValueList().get(1)) > 0;
        case IS_NULL -> StringUtils.containsAny(o, "null", "NULL", "Null", "NULL", "");
      };

    }

    return true;
  }
}
