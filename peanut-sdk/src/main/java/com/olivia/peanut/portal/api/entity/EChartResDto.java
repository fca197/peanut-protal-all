package com.olivia.peanut.portal.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * ECharts 图表响应数据传输对象 用于构建 ECharts 所需的图表数据结构
 */
@Setter
@Getter
@Accessors(chain = true)
public class EChartResDto {

  /**
   * X 轴配置
   */
  @JsonProperty("xAxis")
  public XAxis xAxis;
  /**
   * 图表系列数据
   */
  private List<Series> series;
  /**
   * Y 轴配置
   */
  @JsonProperty("yAxis")
  private YAxis yAxis;

  /**
   * 设置图表系列数据列表
   *
   * @param series 系列数据列表
   * @return 当前对象
   */
  public EChartResDto setSeries(List<Series> series) {
    this.series = series;
    return this;
  }

  /**
   * 设置单个图表系列数据
   *
   * @param series 单个系列数据
   * @return 当前对象
   */
  public EChartResDto setSeries(Series series) {
    this.series = List.of(series);
    return this;
  }

  /**
   * X 轴配置类
   */
  @Setter
  @Getter
  @Accessors(chain = true)
  public static class XAxis {

    /**
     * 轴类型，默认为 'category'（分类轴）
     */
    private String type = "category";

    /**
     * X 轴数据
     */
    private List<String> data;
  }

  /**
   * Y 轴配置类
   */
  @Getter
  public static class YAxis {

    /**
     * 轴类型，默认为 'value'（值轴）
     */
    private String type = "value";
  }

  /**
   * 图表系列配置类
   */
  @Setter
  @Getter
  @Accessors(chain = true)
  public static class Series {

    /**
     * 系列数据
     */
    private List<?> data;

    /**
     * 系列名称
     */
    private String name;

    /**
     * 系列类型，默认为 'bar'（柱状图）
     */
    private String type = "bar";
  }
}
