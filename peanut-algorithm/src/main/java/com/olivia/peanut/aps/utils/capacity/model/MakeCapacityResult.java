package com.olivia.peanut.aps.utils.capacity.model;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 *
 */
@Setter
@Getter
@Accessors(chain = true)
public class MakeCapacityResult {


  /***
   * 返回值列表
   */
  List<Info> data;
  /***
   * 没有匹配成功的map
   */
  List<Map<String, Object>> failMapList;

  @Setter
  @Getter
  @Accessors(chain = true)
  public static class Info {

    private String currentDate;
    private List<Map<String, Object>> mapList;
    private List<Limit> limitList;
  }
}
