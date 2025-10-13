package com.olivia.peanut.aps.utils.process.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DayPower {

  private LocalDateTime startDateTime;
  private LocalDateTime endDateTime;
  private Long maxPower;
}
