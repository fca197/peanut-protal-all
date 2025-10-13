package com.olivia.peanut.aps.utils.process.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ProduceProcessComputeOrderResV2 {

  private Long orderId;
  private Long orderCreateIndex;
  private Long orderWorkId;
  private LocalDateTime beginLocalDateTime;
  private LocalDateTime endLocalDateTime;
}
