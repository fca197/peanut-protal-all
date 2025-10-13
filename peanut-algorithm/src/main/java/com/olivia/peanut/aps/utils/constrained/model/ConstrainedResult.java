package com.olivia.peanut.aps.utils.constrained.model;

import com.olivia.sdk.utils.DynamicsPage.Header;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 约束返回值
 */
@Setter
@Getter
@Accessors(chain = true)
public class ConstrainedResult {

  /***
   * table 表头
   */
  private List<Header> headerList;
  /****
   * 返回对象列表
   */
  private List<Map<String, Object>> dataList;
}
