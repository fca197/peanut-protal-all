package com.olivia.peanut.aps.utils.process.entity;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ComputeStatusRes {

  private List<ApsProduceProcessItemPojo> apsProduceProcessItemPojoList;
}
