package com.olivia.peanut.aps.utils.constrained.model.sub.constrained;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class ValueItem {

  private Object value;
  private String valueName;
}
