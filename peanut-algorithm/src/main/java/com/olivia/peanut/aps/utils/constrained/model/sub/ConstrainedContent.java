package com.olivia.peanut.aps.utils.constrained.model.sub;

import com.olivia.sdk.utils.JSON;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 约束
 */
@Setter
@Getter
@Accessors(chain = true)
public class ConstrainedContent {

  private String id;

  private List<Filter> filterList;
  private List<OrderBy> orderBy;
  private List<ConstrainedContent> children;

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
