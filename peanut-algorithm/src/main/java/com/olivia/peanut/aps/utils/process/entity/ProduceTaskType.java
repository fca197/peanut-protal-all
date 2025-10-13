package com.olivia.peanut.aps.utils.process.entity;

import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.IntervalVar;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ProduceTaskType {

  IntVar start;
  IntVar end;
  IntervalVar interval;
}
