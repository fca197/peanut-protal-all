package com.olivia.sdk.utils.model;

import com.olivia.sdk.utils.JSONUtils;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class WeekInfo {

  private String weekNumber;
  private String currentDay;
  private LocalDate currentDate;
  private Boolean isWorkDay;

  private Long workSeconds;

  @Override
  public String toString() {
    return JSONUtils.toJSONString(this);
  }
}
