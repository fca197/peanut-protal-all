package com.olivia.peanut.aps.utils.process;


import com.olivia.sdk.utils.model.WeekInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * 周信息工具类
 * 提供基于时间查找周信息的功能
 */
@Slf4j
public class WeekUtils {

  /**
   * 根据使用时间获取对应的周信息
   * 该方法会遍历周信息列表，累加工作秒数，直到找到第一个累积工作秒数大于给定使用时间的周信息
   *
   * @param weekInfoList 周信息列表
   * @param useTime 使用时间（以秒为单位）
   * @return 匹配的周信息，如果没有找到匹配项则返回 null
   */
  public static WeekInfo getWeekInfo(List<WeekInfo> weekInfoList, long useTime) {
    Long workSeconds = 0L;
    //            if (log.isDebugEnabled()) log.debug("getWeekInfo  userTime {}", useTime);
    for (WeekInfo weekInfo : weekInfoList) {
      // 累加当前周的工作秒数
      workSeconds += weekInfo.getWorkSeconds();
      if (useTime < workSeconds) {
        if (log.isDebugEnabled()) {
          log.debug("getWeekInfo  userTime {} {} {}", useTime, weekInfo.getCurrentDate(), workSeconds);
        }
        // 找到匹配的周信息，返回该周信息
        return weekInfo;
      }
    }
    // 没有找到匹配的周信息
    return null;
  }
}