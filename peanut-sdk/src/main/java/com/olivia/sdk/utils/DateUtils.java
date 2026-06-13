package com.olivia.sdk.utils;

import com.olivia.sdk.utils.model.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 日期时间工具类，提供日期格式化、区间计算、工作日处理等功能 基于JDK21的java.time API实现，支持本地化日期时间处理
 */
@Slf4j
public final class DateUtils {

  /**
   * 默认时区偏移量
   */
  public static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.of(Str.OFFSET_ID);

  /**
   * 一天结束时间（23:59:59）
   */
  public static final LocalTime DAY_END_TIME = LocalTime.of(23, 59, 59);

  /**
   * 一天开始时间（00:00:00）
   */
  public static final LocalTime DAY_BEGIN_TIME = LocalTime.of(0, 0, 0);

  /**
   * 完整日期时间格式化器（yyyy-MM-dd HH:mm:ss）
   */
  private static final DateTimeFormatter FORMATTER_ALL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * 日期格式化器（yyyy-MM-dd）
   */
  private static final DateTimeFormatter FORMATTER_DAY = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * 年月格式化器（yyyy-MM）
   */
  private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

  /**
   * 私有构造函数，防止工具类实例化
   */
  private DateUtils() {
    throw new AssertionError("工具类不允许实例化");
  }

  /**
   * 将秒数格式化为友好的时间字符串（如：1小时2分钟3秒）
   *
   * @param seconds 秒数
   * @return 格式化后的时间字符串
   */
  public static String formatSeconds(Long seconds) {
    if (seconds == null || seconds <= 0) {
      return "0秒";
    } else if (seconds < 60) {
      return seconds + "秒";
    } else if (seconds < 3600) {
      long minutes = seconds / 60;
      long remainingSeconds = seconds % 60;
      return remainingSeconds == 0 ? minutes + "分钟" : minutes + "分钟" + remainingSeconds + "秒";
    } else {
      long hours = seconds / 3600;
      long remainingSeconds = seconds % 3600;
      long remainingMinutes = remainingSeconds / 60;
      remainingSeconds %= 60;

      StringBuilder result = new StringBuilder(hours + "小时");
      if (remainingMinutes > 0) {
        result.append(remainingMinutes).append("分钟");
      }
      if (remainingSeconds > 0) {
        result.append(remainingSeconds).append("秒");
      }
      return result.toString();
    }
  }

  /**
   * 格式化LocalDateTime为字符串（yyyy-MM-dd HH:mm:ss）
   *
   * @param localDateTime 本地日期时间
   * @return 格式化后的字符串
   */
  public static String formatDateTime(LocalDateTime localDateTime) {
    return localDateTime == null ? "" : localDateTime.format(FORMATTER_ALL);
  }

  /**
   * 从LocalDateTime中提取日期并格式化（yyyy-MM-dd）
   *
   * @param localDateTime 本地日期时间
   * @return 格式化后的日期字符串
   */
  public static String formatDate(LocalDateTime localDateTime) {
    return localDateTime == null ? "" : localDateTime.format(FORMATTER_DAY);
  }

  /**
   * 格式化LocalDate为字符串（yyyy-MM-dd）
   *
   * @param localDate 本地日期
   * @return 格式化后的日期字符串
   */
  public static String formatDate(LocalDate localDate) {
    return localDate == null ? "" : localDate.format(FORMATTER_DAY);
  }

  /**
   * 将LocalDate和LocalTime组合为LocalDateTime
   *
   * @param localDate 本地日期
   * @param localTime 本地时间
   * @return 组合后的LocalDateTime
   */
  private static LocalDateTime getLocalDateTime(LocalDate localDate, LocalTime localTime) {
    return LocalDateTime.of(localDate, localTime);
  }

  /**
   * 获取非工作日时间信息列表
   *
   * @param weekInfoList    星期信息列表
   * @param shiftItemVoList 班次信息列表
   * @return 非工作日时间信息列表
   */
  public static List<NoWorkDayTimeInfo> getNoWorkDayTimeInfo(List<WeekInfo> weekInfoList, List<ShiftItem> shiftItemVoList) {

    if (weekInfoList == null || weekInfoList.isEmpty() || shiftItemVoList == null || shiftItemVoList.isEmpty()) {
      log.info("getNoWorkDayTimeInfo: 周信息或班次信息为空，返回空列表");
      return List.of();
    }

    // 处理跨天班次（如23:00-03:00拆分为23:00-23:59和00:00-03:00）
    List<ShiftItem> shiftItemList = shiftItemVoList.stream().flatMap(shift -> {
      if (shift.getBeginTime().isBefore(shift.getEndTime())) {
        return List.of(shift).stream();
      } else {
        return List.of(new ShiftItem().setBeginTime(shift.getBeginTime()).setEndTime(DAY_END_TIME),
            new ShiftItem().setBeginTime(DAY_BEGIN_TIME).setEndTime(shift.getEndTime())).stream();
      }
    }).sorted(Comparator.comparing(ShiftItem::getBeginTime)).toList();

    if (log.isDebugEnabled()) {
      log.debug("处理后的班次列表: {}", JSON.toJSONString(shiftItemList));
    }

    // 构建日期与是否工作日的映射
    Map<LocalDate, Boolean> weekMap = weekInfoList.stream().collect(Collectors.toMap(WeekInfo::getCurrentDate, WeekInfo::getIsWorkDay));

    // 判断是否存在跨天班次
    boolean hasOverDayShift = shiftItemVoList.stream().anyMatch(shift -> shift.getBeginTime().isAfter(shift.getEndTime()));

    if (log.isDebugEnabled()) {
      log.debug("是否存在跨天班次: {}", hasOverDayShift);
    }

    // 计算非工作时间
    List<NoWorkDayTimeInfo> noWorkDayTimeInfoList = new ArrayList<>();
    weekMap.keySet().stream().sorted().forEach(currentDate -> {
      Boolean isWorkDay = weekMap.getOrDefault(currentDate, false);

      // 非工作日，全天为非工作时间
      if (Boolean.FALSE.equals(isWorkDay)) {
        noWorkDayTimeInfoList.add(
            new NoWorkDayTimeInfo().setBeginTime(getLocalDateTime(currentDate, DAY_BEGIN_TIME)).setEndTime(getLocalDateTime(currentDate, DAY_END_TIME))
                .setCurrentDate(currentDate));
        return;
      }

      // 处理单班次情况
      if (shiftItemList.size() == 1) {
        handleSingleShift(shiftItemList.getFirst(), currentDate, noWorkDayTimeInfoList);
      }
      // 处理跨天班次情况
      else if (hasOverDayShift) {
        setNoWorkDayShiftItem(shiftItemList, noWorkDayTimeInfoList, currentDate);
      }
      // 处理多班次非跨天情况
      else {
        setNoWorkDayShiftItem(shiftItemList, noWorkDayTimeInfoList, currentDate);
        // 处理最后一个班次结束到当天结束的时间
        ShiftItem lastShift = shiftItemList.getLast();
        if (lastShift.getEndTime().isBefore(DAY_END_TIME)) {
          noWorkDayTimeInfoList.add(new NoWorkDayTimeInfo().setBeginTime(getLocalDateTime(currentDate, lastShift.getEndTime()))
              .setEndTime(getLocalDateTime(currentDate, DAY_END_TIME).plusSeconds(1)).setCurrentDate(currentDate));
        }
      }
    });

    // 移除开始时间等于结束时间的无效记录
    noWorkDayTimeInfoList.removeIf(t -> t.getEndTime().equals(t.getBeginTime()));

    // 构建时间信息
    noWorkDayTimeInfoList.forEach(NoWorkDayTimeInfo::buildTime);

    if (log.isDebugEnabled()) {
      log.debug("非工作时间信息列表: {}", JSON.toJSONString(noWorkDayTimeInfoList));
    }

    return noWorkDayTimeInfoList;
  }

  /**
   * 处理单班次的非工作时间计算
   */
  private static void handleSingleShift(ShiftItem shiftItem, LocalDate currentDate, List<NoWorkDayTimeInfo> noWorkDayTimeInfoList) {
    // 当天开始到班次开始的时间
    if (!DAY_BEGIN_TIME.equals(shiftItem.getBeginTime())) {
      noWorkDayTimeInfoList.add(
          new NoWorkDayTimeInfo().setBeginTime(getLocalDateTime(currentDate, DAY_BEGIN_TIME)).setEndTime(getLocalDateTime(currentDate, shiftItem.getBeginTime())));
    }

    // 班次结束到当天结束的时间
    if (!DAY_END_TIME.equals(shiftItem.getEndTime())) {
      noWorkDayTimeInfoList.add(new NoWorkDayTimeInfo().setBeginTime(getLocalDateTime(currentDate, shiftItem.getEndTime()))
          .setEndTime(getLocalDateTime(currentDate, DAY_END_TIME).plusSeconds(1)));
    }
  }

  /**
   * 设置非工作日的班次时间信息
   */
  private static void setNoWorkDayShiftItem(List<ShiftItem> shiftItemList, List<NoWorkDayTimeInfo> noWorkDayTimeInfoList, LocalDate currentDate) {
    // 处理当天开始到第一个班次开始的时间
    LocalTime firstShiftBegin = shiftItemList.getFirst().getBeginTime();
    if (DAY_BEGIN_TIME.isBefore(firstShiftBegin)) {
      noWorkDayTimeInfoList.add(
          new NoWorkDayTimeInfo().setBeginTime(getLocalDateTime(currentDate, DAY_BEGIN_TIME)).setEndTime(getLocalDateTime(currentDate, firstShiftBegin))
              .setCurrentDate(currentDate));
    }

    // 处理班次之间的间隔时间
    for (int i = 0; i < shiftItemList.size() - 1; i++) {
      LocalTime currentEnd = shiftItemList.get(i).getEndTime();
      LocalTime nextBegin = shiftItemList.get(i + 1).getBeginTime();

      if (!currentEnd.equals(nextBegin)) {
        noWorkDayTimeInfoList.add(new NoWorkDayTimeInfo().setBeginTime(getLocalDateTime(currentDate, currentEnd)).setEndTime(getLocalDateTime(currentDate, nextBegin))
            .setCurrentDate(currentDate));
      }
    }
  }

  /**
   * 获取工作日时间信息列表
   *
   * @param weekInfoList    星期信息列表
   * @param shiftItemVoList 班次信息列表
   * @return 工作日时间信息列表
   */
  public static List<DayTimeInfo> getWorkDayTimeInfo(List<WeekInfo> weekInfoList, List<ShiftItem> shiftItemVoList) {

    if (weekInfoList == null || weekInfoList.isEmpty() || shiftItemVoList == null || shiftItemVoList.isEmpty()) {
      log.info("getWorkDayTimeInfo: 周信息或班次信息为空，返回空列表");
      return List.of();
    }

    // 排序班次和工作日
    List<ShiftItem> shiftItemList = shiftItemVoList.stream().sorted(Comparator.comparing(ShiftItem::getBeginTime)).toList();

    List<WeekInfo> workDays = weekInfoList.stream().filter(week -> Boolean.TRUE.equals(week.getIsWorkDay())).sorted(Comparator.comparing(WeekInfo::getCurrentDate))
        .toList();

    List<DayTimeInfo> dayTimeInfoList = new ArrayList<>();
    for (WeekInfo workDay : workDays) {
      LocalDate currentDate = workDay.getCurrentDate();

      for (ShiftItem shiftItem : shiftItemList) {
        // 正常班次（开始时间在结束时间之前）
        if (shiftItem.getBeginTime().isBefore(shiftItem.getEndTime())) {
          dayTimeInfoList.add(new DayTimeInfo().setCurrentDate(currentDate).setBeginTime(shiftItem.getBeginTime()).setEndTime(shiftItem.getEndTime()));
        }
        // 跨天班次（如21:00-05:00）
        else if (shiftItem.getBeginTime().isAfter(shiftItem.getEndTime())) {
          // 当天部分
          dayTimeInfoList.add(new DayTimeInfo().setCurrentDate(currentDate).setBeginTime(shiftItem.getBeginTime()).setEndTime(DAY_END_TIME));
          // 第二天部分
          dayTimeInfoList.add(new DayTimeInfo().setCurrentDate(currentDate.plusDays(1)).setBeginTime(DAY_BEGIN_TIME).setEndTime(shiftItem.getEndTime()));
        }
      }
    }

    // 计算工作时长和累计时长
    AtomicLong totalWorkSeconds = new AtomicLong();
    dayTimeInfoList.forEach(dayTimeInfo -> {
      long dayWorkSeconds;
      if (DAY_END_TIME.equals(dayTimeInfo.getEndTime())) {
        dayWorkSeconds = DAY_END_TIME.toSecondOfDay() - dayTimeInfo.getBeginTime().toSecondOfDay() + 1;
        dayTimeInfo.setEndTime(DAY_BEGIN_TIME); // 跨天的结束时间特殊处理
      } else {
        dayWorkSeconds = dayTimeInfo.getEndTime().toSecondOfDay() - dayTimeInfo.getBeginTime().toSecondOfDay();
      }

      dayTimeInfo.setDayWorkSecond(dayWorkSeconds);
      dayTimeInfo.setWorkSum(totalWorkSeconds.addAndGet(dayWorkSeconds));
    });

    if (log.isDebugEnabled()) {
      log.debug("工作日时间信息列表: {}", JSON.toJSONString(dayTimeInfoList));
    }

    return dayTimeInfoList;
  }

  /**
   * 解析年月字符串为整数数组
   *
   * @param dateStr 年月字符串（格式：yyyy-MM）
   * @return 包含年和月的整数数组
   */
  private static int[] getYearMonth(String dateStr) {
    return Arrays.stream(dateStr.split("-")).mapToInt(Integer::parseInt).toArray();
  }

  /**
   * 获取两个日期之间的所有月份
   *
   * @param startDate 开始日期（LocalDate）
   * @param endDate   结束日期（LocalDate）
   * @return 包含所有月份的YearMonth列表
   */
  public static List<YearMonth> getMonthList(LocalDate startDate, LocalDate endDate) {
    Objects.requireNonNull(startDate, () -> "开始日期不能为空");
    Objects.requireNonNull(endDate, () -> "结束日期不能为空");

    return getMonthList(formatDate(startDate), formatDate(endDate));
  }

  /**
   * 获取两个年月字符串之间的所有月份
   *
   * @param startDate 开始年月（格式：yyyy-MM）
   * @param endDate   结束年月（格式：yyyy-MM）
   * @return 包含所有月份的YearMonth列表
   */
  public static List<YearMonth> getMonthList(String startDate, String endDate) {
    Objects.requireNonNull(startDate, () -> "开始日期不能为空");
    Objects.requireNonNull(endDate, () -> "结束日期不能为空");

    int[] beginDate = getYearMonth(startDate);
    LocalDate current = LocalDate.of(beginDate[0], beginDate[1], 1);

    int[] endDateArr = getYearMonth(endDate);
    LocalDate end = LocalDate.of(endDateArr[0], endDateArr[1], 1);

    List<YearMonth> monthList = new ArrayList<>();
    while (!current.isAfter(end)) {
      monthList.add(YearMonth.of(current.getYear(), current.getMonthValue()));
      current = current.plusMonths(1);
    }

    return monthList;
  }

  /**
   * 获取指定年月范围内的所有日期及对应的周数
   *
   * @param startDate 开始年月（格式：yyyy-MM）
   * @param endDate   结束年月（格式：yyyy-MM）
   * @return 包含日期和周数的WeekInfo列表
   */
  public static List<WeekInfo> getWeekList(String startDate, String endDate) {
    Objects.requireNonNull(startDate, () -> "开始日期不能为空");
    Objects.requireNonNull(endDate, () -> "结束日期不能为空");

    int[] beginDate = getYearMonth(startDate);
    LocalDate currentDate = LocalDate.of(beginDate[0], beginDate[1], 1);

    int[] endDateArr = getYearMonth(endDate);
    LocalDate endDateObj = LocalDate.of(endDateArr[0], endDateArr[1], 1).with(TemporalAdjusters.lastDayOfMonth());

    List<WeekInfo> weekList = new ArrayList<>();
    while (!currentDate.isAfter(endDateObj)) {
      // 获取周数（ISO周历，周一为一周的第一天）
      int weekNumber = currentDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
      String weekNumberStr = String.format("%02d", weekNumber);

      weekList.add(new WeekInfo().setWeekNumber(weekNumberStr).setCurrentDate(currentDate).setCurrentDay(formatDate(currentDate)));

      currentDate = currentDate.plusDays(1);
    }

    return weekList;
  }

  /**
   * 获取两个日期之间的所有日期
   *
   * @param startDate 开始日期
   * @param endDate   结束日期
   * @return 日期列表
   */
  public static List<LocalDate> getLocalDateBetween(LocalDate startDate, LocalDate endDate) {
    Objects.requireNonNull(startDate, () -> "开始日期不能为空");
    Objects.requireNonNull(endDate, () -> "结束日期不能为空");

    List<LocalDate> localDateList = new ArrayList<>();
    LocalDate current = startDate;
    while (!current.isAfter(endDate)) {
      localDateList.add(current);
      current = current.plusDays(1);
    }

    return localDateList;
  }

  /**
   * 从多个日期区间中获取最小开始日期和最大结束日期
   *
   * @param localDateList 日期区间列表（每个区间包含两个日期）
   * @return 包含最小开始日期和最大结束日期的列表
   */
  public static List<LocalDate> getMinMaxDate(List<List<LocalDate>> localDateList) {
    if (localDateList == null || localDateList.isEmpty()) {
      return List.of();
    }

    AtomicReference<LocalDate> minStartDate = new AtomicReference<>();
    AtomicReference<LocalDate> maxEndDate = new AtomicReference<>();

    localDateList.forEach(range -> {
      if (range == null || range.size() < 2) {
        return;
      }

      LocalDate start = range.get(0);
      LocalDate end = range.get(1);

      // 更新最小开始日期
      if (minStartDate.get() == null || start.isBefore(minStartDate.get())) {
        minStartDate.set(start);
      }

      // 更新最大结束日期
      if (maxEndDate.get() == null || end.isAfter(maxEndDate.get())) {
        maxEndDate.set(end);
      }
    });

    return List.of(minStartDate.get(), maxEndDate.get());
  }

  /**
   * 将LocalDateTime格式化为年月字符串（yyyy-MM）
   *
   * @param dateTime 本地日期时间
   * @return 年月字符串
   */
  public static String localDateTime2Month(LocalDateTime dateTime) {
    return dateTime == null ? "" : dateTime.format(YEAR_MONTH_FORMATTER);
  }

  /**
   * 将LocalDate格式化为年月字符串（yyyy-MM）
   *
   * @param date 本地日期
   * @return 年月字符串
   */
  public static String localDate2Month(LocalDate date) {
    return date == null ? "" : date.format(YEAR_MONTH_FORMATTER);
  }
}
