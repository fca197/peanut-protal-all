package com.olivia.peanut.aps.utils.constrained.model.sub.constrained;

import com.olivia.peanut.aps.utils.constrained.model.sub.OperatorEnum;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class Operator {

  private String value;
  private String name;

  public static List<Operator> of(OperatorEnum... operatorEnumArr) {
    return Arrays.stream(operatorEnumArr).map(t -> new Operator().setValue(t.getValue()).setName(t.getName())).toList();
  }
}
