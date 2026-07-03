package com.olivia.peanut.aps.utils.capacity.model;

import com.olivia.sdk.utils.JSONUtils;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/***
 * 限制配置
 */
@Setter
@Getter
@Accessors(chain = true)
public class Limit {

  /***
   * 限制类型
   */
  private LimitTypeEnum limitTypeEnum;

  /****
   * 当前天
   */
  private String currentDate;
  /***
   * 显示名称。 表字段名称
   */
  private String showName;
  /***
   * 字段名称
   */
  private String fieldName;
  /***
   * 字段值
   */
  private String fieldValue;
  /***
   * 当前添加数量
   */
  private int currentCount;
  /***
   * 最低数量
   */
  private int min;
  /****
   * 最高数量
   */
  private int max;

  public void currentCountIncrement() {
    this.currentCount++;
  }

  public Limit setMin(Integer min) {
    this.min = Objects.nonNull(min) ? min : 0;
    return this;
  }

  /***
   * 最大为空时， 设置为 Integer.MAX_VALUE
   * @param max
   * @return
   */
  public Limit setMax(Integer max) {
    this.max = Objects.nonNull(max) ? max : 0;
    return this;
  }

  /**
   * 是否满足最新
   *
   * @return bool
   */
  public boolean lessMin() {
    return this.currentCount < this.min && this.currentCount < this.max;
  }

  /***
   * 是否满足最大
   * @return bool
   */
  public boolean lessMax() {
    return this.currentCount <= this.max;
  }


  @Override
  public String toString() {
    return JSONUtils.toJSONString(this);
  }

  @Getter
  public enum LimitTypeEnum {
    FACTORY_LIMIT("工厂限制"),//
    GOODS_LIMIT("商品限制"),//
    SALE_CONFIG_LIMIT("销售限制"),//
    PRODUCT_CONFIG_LIMIT("产品限制"),
    ;

    private final String name;

    LimitTypeEnum(String name) {
      this.name = name;
    }


  }
}
