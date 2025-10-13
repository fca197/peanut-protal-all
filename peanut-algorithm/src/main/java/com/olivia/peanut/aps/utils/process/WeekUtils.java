package com.olivia.peanut.aps.utils.process;


import com.olivia.sdk.utils.model.WeekInfo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WeekUtils {

  public static WeekInfo getWeekInfo(List<WeekInfo> weekInfoList, long useTime) {
    Long workSeconds = 0L;
//            if (log.isDebugEnabled()) log.debug("getWeekInfo  userTime {}", useTime);
    for (WeekInfo weekInfo : weekInfoList) {
      workSeconds += weekInfo.getWorkSeconds();
      if (useTime < workSeconds) {
        if (log.isDebugEnabled()) {
          log.debug("getWeekInfo  userTime {} {} {}", useTime, weekInfo.getCurrentDate(), workSeconds);
        }
        return weekInfo;
      }
    }
    return null;
  }
}
