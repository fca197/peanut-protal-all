package com.olivia.peanut.aps.utils.capacity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.olivia.peanut.aps.utils.capacity.model.Limit;
import com.olivia.peanut.aps.utils.capacity.model.LimitMatchResult;
import com.olivia.peanut.aps.utils.capacity.model.MakeCapacityResult;
import com.olivia.peanut.aps.utils.capacity.model.MakeCapacityResult.Info;
import com.olivia.sdk.utils.JSON;
import com.olivia.sdk.utils.ValueUtils;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/***
 *  限制接口
 */
@Slf4j
public class MakeCapacityUtils {



  /****
   * 限制实现接口
   * @param needStoreList  待使用限制的对象列表
   * @param limitListAll 所有限制
   * @return MakeCapacityResult  计算后结果
   */
  public static MakeCapacityResult capacity(List<Map<String, Object>> needStoreList, List<Limit> limitListAll) {

    if (CollUtil.isEmpty(needStoreList)) {
      log.warn("needStoreList is null");
      return new MakeCapacityResult().setData(List.of());
    }
    if (CollUtil.isEmpty(limitListAll)) {
      log.warn("limitListAll is null");
      return new MakeCapacityResult().setFailMapList(needStoreList);
    }

    //
    Map<String, List<Limit>> dateLimitMap = limitListAll.stream().collect(Collectors.groupingBy(Limit::getCurrentDate));

    List<Map<String, Object>> errrorList = new ArrayList<>();

    Map<String, Info> infoMap = new HashMap<>();

    // 计算最小值
    dateLimitMap.keySet().stream().sorted().forEach(date -> {

      needStoreList.addAll(errrorList);
      errrorList.clear();
      List<Limit> dayLimitList = dateLimitMap.get(date);
      if (log.isDebugEnabled()) {
        log.debug("date limit {} {}", date, JSON.toJSONString(dayLimitList));
      }
      if (CollUtil.isEmpty(dayLimitList)) {
        if (log.isDebugEnabled()) {
          log.debug("limitListByDate:{} limit is empty", date);
        }
        infoMap.put(date, new Info().setCurrentDate(date).setMapList(List.of()));
        return;
      }
      List<Map<String, Object>> successList = new ArrayList<>();
      // 填充最小值
      while (CollUtil.isNotEmpty(needStoreList)) {
        // 如果所有的最小都满足了, 则跳出循环
        boolean hasMin = dayLimitList.stream().anyMatch(t -> t.getCurrentCount() < t.getMin());
        if (!hasMin) {
          if (log.isDebugEnabled()) {
            log.debug("limitListByDate dayLimitList:{}", JSON.toJSONString(dayLimitList));
          }
          break;
        }
        // 删除已满足最小配置的限制,则跳出循环
        ArrayList<Limit> tmpList = new ArrayList<>(dayLimitList);
        tmpList.removeIf(t -> t.getCurrentCount() >= t.getMin());
        if (CollUtil.isEmpty(tmpList)) {
          break;
        }
        Map<String, Object> curMap = needStoreList.removeFirst();
        List<Limit> matchList = tmpList.stream().filter(t -> isEquals(t, curMap)).toList();
        if (CollUtil.isEmpty(matchList)) {
          errrorList.add(curMap);
          continue;
        }
        List<Limit> limitList = dayLimitList.stream().filter(t -> isEquals(t, curMap)).toList();
        limitList.forEach(Limit::currentCountIncrement);
        successList.add(curMap);

      }

      if (log.isDebugEnabled()) {
        log.debug("LimitListByDate date over:{} successList :{}", date, successList.size());
      }
      //

      needStoreList.addAll(errrorList);
      errrorList.clear();
      while (CollUtil.isNotEmpty(needStoreList)) {
        Map<String, Object> currentMap = needStoreList.removeFirst();
        LimitMatchResult matchResult = isMatchMax(currentMap, dayLimitList);
        if (matchResult.isMatch()) {
          successList.add(currentMap);
        } else {
          errrorList.add(currentMap);
        }
      }
      Info info = new Info();
      if (CollUtil.isNotEmpty(info.getMapList())) {
        info.setLimitList(dayLimitList).getMapList().addAll(successList);
      } else {
        info.setCurrentDate(date).setMapList(successList).setLimitList(dayLimitList);
      }
      successList.sort(Comparator.comparingLong(t -> (Long) t.get("numberIndex")));
      infoMap.put(date, new Info().setCurrentDate(date).setLimitList(dayLimitList).setMapList(successList));
    });
    List<Info> infoList = infoMap.values().stream().filter(t -> CollUtil.isNotEmpty(t.getMapList())).sorted(Comparator.comparing(Info::getCurrentDate))
        .collect(Collectors.toList());
    return new MakeCapacityResult().setData(infoList).setFailMapList(errrorList);
  }

  /***
   *   当前对象是否满足限制条件
   * @param limit 限制规则
   * @param cuMap 当前匹配对象
   * @return bool
   */
  @SneakyThrows
  private static boolean isEquals(Limit limit, Map<String, Object> cuMap) {

    return Objects.equals(ValueUtils.value2Str(cuMap.get(limit.getFieldName())), limit.getFieldValue());
  }


  /***
   * 是否满足最大配皮
   * @param currentMap 当前对象
   * @param limitList 限制条件
   * @return LimitMatchResult 限制结果
   */
  public static LimitMatchResult isMatchMax(Map<String, Object> currentMap, List<Limit> limitList) {
    if (CollUtil.isEmpty(limitList)) {
      return new LimitMatchResult().setMatch(false).setMatcheList(limitList);
    }
    List<Limit> tmpList = limitList.stream().filter(t -> isEquals(t, currentMap)).toList();
    boolean match = tmpList.stream().filter(t -> t.getCurrentCount() < t.getMax()).count() == tmpList.size();
    if (match) {
      tmpList.forEach(Limit::currentCountIncrement);
      return new LimitMatchResult().setMatch(true).setMatcheList(tmpList);
    }
    return new LimitMatchResult().setMatch(false);
  }


  private static boolean isEnoughAllMin(List<Limit> dayLimitList) {
    // 如果都满足最小值 返回false
    long count = dayLimitList.stream().filter(t -> t.getCurrentCount() > t.getCurrentCount()).count();
    if (count == dayLimitList.size()) {
      return false;
    }
    return dayLimitList.stream().filter(Limit::lessMin).count() == dayLimitList.size();
  }
}
