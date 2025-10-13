package com.olivia.peanut.aps.utils.process.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@AllArgsConstructor
public class ProduceAssignedTask implements Comparable<ProduceAssignedTask> {

  Long jobID;
  int taskID;

  int start;
  Long duration;

  Long statusId;

  @Override
  public int compareTo(@NotNull ProduceAssignedTask b) {

    if (this.start != b.start) {
      return this.start - b.start;
    } else {
      return (int) (this.duration - b.duration);
    }
  }
}
