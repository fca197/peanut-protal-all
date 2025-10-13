package com.olivia.peanut.aps.utils.process;

import static com.olivia.peanut.aps.utils.process.WeekUtils.getWeekInfo;

import cn.hutool.core.collection.CollUtil;
import com.olivia.peanut.aps.utils.bom.model.ApsGoodsBomVo;
import com.olivia.peanut.aps.utils.process.entity.*;
import com.olivia.peanut.aps.utils.process.entity.ApsProcessPathInfo.Info;
import com.olivia.sdk.utils.$;
import com.olivia.sdk.utils.model.WeekInfo;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

/***
 *
 */
@Slf4j
public class ProcessUtils {


  public static Long getDayWorkSecond(List<ShiftItemVo> shiftItemList) {
    AtomicLong dayLong = new AtomicLong();
    shiftItemList.stream().sorted(Comparator.comparing(ShiftItemVo::getBeginTime)).forEach(t -> {
      if (t.getEndTime().isBefore(t.getBeginTime())) {
        LocalTime localTime = LocalTime.of(23, 59, 59);
        long until = t.getBeginTime().until(localTime, ChronoUnit.SECONDS) + 1;
        dayLong.addAndGet(until);
        localTime = LocalTime.of(0, 0, 0);
        until = localTime.until(t.getEndTime(), ChronoUnit.SECONDS);
        dayLong.addAndGet(until);
      } else {
        long until = t.getBeginTime().until(t.getEndTime(), ChronoUnit.SECONDS);
        dayLong.addAndGet(until);
      }
    });
    return dayLong.get();
  }

  public static Long getDayWorkLastSecond(List<ShiftItemVo> shiftItemList, LocalTime nowLocalTime) {

    // 三班: 08-19 . 19-21 ,21-08
    List<ShiftItemVo> spiltItems = new ArrayList<>();
    LocalTime dayEndTime = LocalTime.of(23, 59, 59);
    shiftItemList.forEach(t -> {
      if (t.getBeginTime().isAfter(t.getEndTime())) {
        spiltItems.add(new ShiftItemVo().setBeginTime(t.getBeginTime()).setEndTime(dayEndTime));
        spiltItems.add(
            new ShiftItemVo().setBeginTime(LocalTime.of(0, 0, 0)).setEndTime(t.getEndTime()));
      } else {
        spiltItems.add(t);
      }
    });

    List<ShiftItemVo> shiftItemVoList = spiltItems.stream()
        .sorted(Comparator.comparing(ShiftItemVo::getBeginTime)).toList();

    AtomicLong dayLastSecond = new AtomicLong(0);
    boolean isAdd = false;
    boolean isAddSend = false;
    for (ShiftItemVo shiftItemVo : shiftItemVoList) {
      if (isAdd) {
        if (log.isDebugEnabled()) {
          log.debug("shiftItemVo : {} {}", shiftItemVo.getBeginTime(), shiftItemVo.getEndTime());
        }
        dayLastSecond.addAndGet(
            shiftItemVo.getEndTime().toSecondOfDay() - shiftItemVo.getBeginTime().toSecondOfDay());
        if (shiftItemVo.getEndTime().equals(dayEndTime) && !isAddSend) {
          dayLastSecond.addAndGet(1);
          isAddSend = true;
        }

        continue;
      }
      if ((shiftItemVo.getBeginTime().isBefore(nowLocalTime) || shiftItemVo.getBeginTime()
          .equals(nowLocalTime)) && (nowLocalTime.isBefore(shiftItemVo.getEndTime())
          || nowLocalTime.equals(shiftItemVo.getEndTime()))) {
        dayLastSecond.addAndGet(
            shiftItemVo.getEndTime().toSecondOfDay() - nowLocalTime.toSecondOfDay());
        isAdd = true;
        if (shiftItemVo.getEndTime().equals(dayEndTime)) {
          dayLastSecond.addAndGet(1);
          isAddSend = true;
        }
      }

    }
    return dayLastSecond.get();
  }


  public static ApsProcessPathInfo apsProcessPathInfo(ApsProcessPathVo apsProcessPathVo,
      List<WeekInfo> weekInfoListAll, LocalDate endDate, Long dayWorkSecond) {
    ApsProcessPathInfo apsProcessPathInfo = new ApsProcessPathInfo();
    List<Info> infoList = new ArrayList<>();
    AtomicLong useTime = new AtomicLong();
    List<WeekInfo> weekInfoList = new ArrayList<>(weekInfoListAll);
    if (log.isDebugEnabled()) {
      log.debug("apsProcessPathInfo :{}  {}", endDate, weekInfoList);
    }
    weekInfoList.removeIf(t -> t.getCurrentDate().isAfter(endDate));
    apsProcessPathVo.getPathRoomList().forEach(r -> {
      List<ApsRoomConfigVo> apsRoomConfigList = r.getApsRoomConfigList();
      apsRoomConfigList.forEach(apsRoomConfig -> {
        long useTmp = useTime.addAndGet(apsRoomConfig.getExecuteTime());
        Info info = $.copy(apsRoomConfig, Info.class);
        int index = (int) (useTmp / dayWorkSecond);
        if (index < weekInfoList.size()) {
          info.setBeginLocalDate(weekInfoList.get(index).getCurrentDate());
        }
        infoList.add(info);
      });
    });

    return apsProcessPathInfo.setDataList(infoList);
  }

  /****
   *  计算日期
   * @param apsProcessPathVo 路径
   * @param weekInfoListAll 工作日历
   * @param dayWorkLastSecond  当天还剩多少秒
   * @param dayWorkSecond 一天上班秒数
   * @param orderGoodsStatusId 开始的状态
   * @param apsGoodsBomListMap  零件集合
   * @param startTime 开始日期
   * @return 当前日历下工作路径及其使用零件配置
   */

  public static ApsProcessPathInfo schedulePathDate(ApsProcessPathVo apsProcessPathVo,
      List<WeekInfo> weekInfoListAll, Long dayWorkLastSecond, Long dayWorkSecond,
      Long orderGoodsStatusId, Map<Long, List<ApsGoodsBomVo>> apsGoodsBomListMap,
      LocalDate startTime) {
    return schedulePathDate(apsProcessPathVo, weekInfoListAll, dayWorkLastSecond, dayWorkSecond,
        orderGoodsStatusId, false, apsGoodsBomListMap, startTime);
  }

  public static ApsProcessPathInfo schedulePathDate(ApsProcessPathVo apsProcessPathVo,
      List<WeekInfo> weekInfoListAll, Long dayWorkLastSecond, Long dayWorkSecond,
      Long orderGoodsStatusId, boolean isBegin, Map<Long, List<ApsGoodsBomVo>> apsGoodsBomListMap,
      LocalDate startTime) {
    if (dayWorkLastSecond < 0) {
      log.warn("dayWorkLastSecond  {}< 0", dayWorkLastSecond);
      dayWorkLastSecond = 0L;
    }
    if (CollUtil.isEmpty(weekInfoListAll)) {
      return new ApsProcessPathInfo().setDataList(List.of());
    }
    ApsProcessPathVo processPathVo = $.copy(apsProcessPathVo, ApsProcessPathVo.class);
    List<WeekInfo> weekInfoList = new ArrayList<>(weekInfoListAll);
    weekInfoList.removeIf(t -> t.getCurrentDate().isBefore(startTime));
    weekInfoList.forEach(t -> t.setWorkSeconds(dayWorkSecond));
    if (CollUtil.isNotEmpty(weekInfoList)) {
      weekInfoList.getFirst().setWorkSeconds(dayWorkLastSecond);
    }
    List<Info> infoList = new ArrayList<>();

    AtomicInteger index = new AtomicInteger();

    List<ApsProcessPathRoomVo> pathRoomList = processPathVo.getPathRoomList();
    pathRoomList = CollUtil.isEmpty(pathRoomList) ? List.of() : pathRoomList;
    pathRoomList.forEach(
        t -> t.getApsRoomConfigList().forEach(r -> r.setSortIndex(index.getAndIncrement())));

    pathRoomList.forEach(t -> {
      t.getApsRoomConfigList().forEach(ts -> {
        infoList.add($.copy(ts, Info.class));
      });
    });
//    for (int i = 0; i < infoList.size(); ) {
//      Info info = infoList.get(i);
//      if (!Objects.equals(info.getStatusId(), orderGoodsStatusId)) {
//        infoList.remove(0);
//        break;
//      }
//    }

    if (!isBegin) {
      if (Objects.nonNull(orderGoodsStatusId)) {
        while (CollUtil.isNotEmpty(infoList)) {
          Info info = infoList.removeFirst();
          if (Objects.equals(info.getStatusId(), orderGoodsStatusId)) {
            infoList.addFirst(info);
            break;
          }
        }
      }
    }

//            if (log.isDebugEnabled()) log.debug("ApsProcessPathVo {} infoList:{} ", orderGoodsStatusId, JSON.toJSONString(infoList));

    if (CollUtil.isEmpty(infoList)) {
      if (log.isDebugEnabled()) {
        log.debug("ApsProcessPathVo is empty");
      }
      return new ApsProcessPathInfo().setDataList(List.of());
    }
    // 执行总耗时  当天还剩 3600秒
    AtomicLong useExecuteTime = new AtomicLong();
    infoList.forEach(info -> {
      long useTime = useExecuteTime.getAndAdd(info.getExecuteTime());
      WeekInfo weekInfoTmp = getWeekInfo(weekInfoList, useTime);
      if (Objects.nonNull(weekInfoTmp)) {
        info.setBeginLocalDate(weekInfoTmp.getCurrentDate());
        weekInfoTmp = getWeekInfo(weekInfoList, useTime + info.getExecuteTime());
        if (Objects.nonNull(weekInfoTmp)) {
          info.setEndLocalDate(weekInfoTmp.getCurrentDate());
        }
      }
      if (CollUtil.isEmpty(apsGoodsBomListMap)) {
        return;
      }
      info.setApsGoodsBomList(apsGoodsBomListMap.get(info.getStationId()));
    });

    return new ApsProcessPathInfo().setDataList(infoList);
  }


  public static List<Long> getStatusBetween(ApsProcessPathVo apsProcessPathVo, Long beginStatus,
      Long endStatusId) {
    List<Long> retList = new ArrayList<>();
    boolean isAdd = false;
    for (ApsProcessPathRoomVo apsProcessPathRoomVo : apsProcessPathVo.getPathRoomList()) {
      for (ApsRoomConfigVo apsRoomConfig : apsProcessPathRoomVo.getApsRoomConfigList()) {
        if (apsRoomConfig.getStatusId().equals(beginStatus)) {
          retList.add(apsRoomConfig.getStatusId());
          isAdd = true;
          continue;
        }
        if (apsRoomConfig.getStatusId().equals(endStatusId)) {
          retList.add(apsRoomConfig.getStatusId());
          return retList;
        }
        if (isAdd) {
          retList.add(apsRoomConfig.getStatusId());
        }
      }
    }
    return retList;
  }

  public static void getPathBetween(ApsProcessPathVo apsProcessPathVo, Long beginStatus,
      Long endStatusId) {
    List<Long> statusBetween = getStatusBetween(apsProcessPathVo, beginStatus, endStatusId);
    if (CollUtil.isEmpty(statusBetween)) {
      apsProcessPathVo.setPathRoomList(List.of());
    }
    apsProcessPathVo.getPathRoomList().forEach(t -> {
      ArrayList<ApsRoomConfigVo> apsRoomConfigVos = new ArrayList<>(t.getApsRoomConfigList());
      apsRoomConfigVos.removeIf(rc -> !statusBetween.contains(rc.getStatusId()));
      t.setApsRoomConfigList(apsRoomConfigVos);
    });
    apsProcessPathVo.getPathRoomList().removeIf(t -> CollUtil.isEmpty(t.getApsRoomConfigList()));

  }

}
