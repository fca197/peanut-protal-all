package com.olivia.peanut.aps.utils.capacity.model;

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
public class LimitMatchResult {

  /***
   * 是否满足
   */
  private boolean isMatch;
  /***
   * 限制列表
   */
  private List<Limit> matcheList;
}
