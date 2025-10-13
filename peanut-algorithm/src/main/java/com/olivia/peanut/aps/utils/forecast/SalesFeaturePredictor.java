package com.olivia.peanut.aps.utils.forecast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 销售特征比例预测系统
 * <p>
 * 提供多种算法预测未来销售特征比例，增强算法差异性和鲁棒性
 */
public class SalesFeaturePredictor {

  // 精度控制：保留2位小数
  private static final int SCALE = 2;
  // 舍入模式：四舍五入
  private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

  // 历史订单数据存储
  private final List<Order> historicalOrders;

  /**
   * 构造函数：初始化历史订单列表
   */
  public SalesFeaturePredictor() {
    historicalOrders = new ArrayList<>();
  }

  /**
   * 主方法：测试用例入口
   */
  public static void main(String[] args) {
    SalesFeaturePredictor predictor = new SalesFeaturePredictor();

    // 1. 生成具有明显趋势和波动的历史数据
    LocalDate currentDate = LocalDate.now();
    for (int i = 66; i >= 0; i--) {
      LocalDate orderDate = currentDate.minusMonths(i);
      Order order = new Order(orderDate);

      // 颜色特征数据（包含线性趋势、周期性和随机波动）
      Map<String, BigDecimal> colorProportions = generateColorProportions(i);
      order.addFeatureData(new FeatureData("颜色", orderDate, colorProportions));

      // 天窗特征数据（强季节性和随机波动）
      Map<String, BigDecimal> sunroofProportions = generateSunroofProportions(orderDate, i);
      order.addFeatureData(new FeatureData("天窗", orderDate, sunroofProportions));

      predictor.addHistoricalOrder(order);
    }

    // 2. 打印历史数据
    System.out.println("===== 历史销售特征数据 =====");
    printHistoricalData(predictor, "颜色", 6);
    printHistoricalData(predictor, "天窗", 6);

    // 3. 预测未来6个月数据
    int futureMonths = 16;
    System.out.println("\n===== 未来" + futureMonths + "个月预测数据 =====");
    for (PredictionAlgorithm algorithm : PredictionAlgorithm.values()) {
      System.out.println("\n使用算法: " + algorithm);
      System.out.println("--- 颜色特征 ---");
      List<FeatureData> colorPredictions = predictor.predictFeatureProportionsForNextNMonths("颜色",
          futureMonths, algorithm);
      printPredictions(colorPredictions);

      System.out.println("--- 天窗特征 ---");
      List<FeatureData> sunroofPredictions = predictor.predictFeatureProportionsForNextNMonths(
          "天窗", futureMonths, algorithm);
      printPredictions(sunroofPredictions);
    }
  }

  /**
   * 生成颜色特征比例（包含趋势和波动）
   */
  private static Map<String, BigDecimal> generateColorProportions(int monthIndex) {
    Map<String, BigDecimal> proportions = new HashMap<>();
    double month = 23 - monthIndex; // 转换为递增的月份索引

    // 蓝色：线性增长 + 周期性波动 + 随机噪声
    double blue = 20 + month * 0.8 + 8 * Math.sin(month * 0.5) + Math.random() * 6 - 3;
    // 白色：线性衰减 + 随机噪声
    double white = 35 - month * 0.5 + Math.random() * 8 - 4;
    // 黑色：周期性波动 + 随机噪声
    double black = 25 + 10 * Math.sin(month * 0.8) + Math.random() * 7 - 3.5;
    // 红色：剩余比例
    double red = 100 - blue - white - black;

    // 确保所有值在有效范围内
    blue = Math.max(5, Math.min(90, blue));
    white = Math.max(5, Math.min(90, white));
    black = Math.max(5, Math.min(90, black));
    red = Math.max(5, Math.min(90, red));

    // 归一化处理
    double total = blue + white + black + red;
    blue = blue / total * 100;
    white = white / total * 100;
    black = black / total * 100;
    red = red / total * 100;

    return Map.of("蓝色", BigDecimal.valueOf(blue).setScale(SCALE, ROUNDING_MODE), "白色",
        BigDecimal.valueOf(white).setScale(SCALE, ROUNDING_MODE), "黑色",
        BigDecimal.valueOf(black).setScale(SCALE, ROUNDING_MODE), "红色",
        BigDecimal.valueOf(red).setScale(SCALE, ROUNDING_MODE));
  }

  /**
   * 生成天窗特征比例（强季节性）
   */
  private static Map<String, BigDecimal> generateSunroofProportions(LocalDate date,
      int monthIndex) {
    int month = date.getMonthValue();
    double baseValue = 45;

    // 强季节性模式（夏季有天窗比例显著增加）
    if (month >= 5 && month <= 9) {
      baseValue += 25 - monthIndex * 0.5; // 夏季基础值 + 随时间衰减
    } else {
      baseValue -= 15 + monthIndex * 0.3; // 冬季基础值 + 随时间衰减
    }

    // 添加随机波动
    baseValue += Math.random() * 12 - 6;
    baseValue = Math.max(10, Math.min(90, baseValue)); // 确保在有效范围

    return Map.of("有天窗", BigDecimal.valueOf(baseValue).setScale(SCALE, ROUNDING_MODE), "无天窗",
        BigDecimal.valueOf(100 - baseValue).setScale(SCALE, ROUNDING_MODE));
  }

  /**
   * 打印预测结果（格式化对齐）
   */
  private static void printPredictions(List<FeatureData> predictions) {
    if (predictions.isEmpty()) {
      System.out.println("无预测数据");
      return;
    }

    // 计算特征值最大宽度用于对齐
    Set<String> featureValues = predictions.get(0).getProportions().keySet();
    Map<String, Integer> maxWidths = new HashMap<>();

    for (String value : featureValues) {
      int maxLen = value.length() + 6; // 预留": XX%"空间
      for (FeatureData data : predictions) {
        String propStr = data.getProportions().get(value).toPlainString();
        maxLen = Math.max(maxLen, value.length() + propStr.length() + 3);
      }
      maxWidths.put(value, maxLen);
    }

    // 打印预测结果
    for (FeatureData data : predictions) {
      StringBuilder sb = new StringBuilder();
      sb.append("日期: ").append(data.getOrderDate()).append(", 比例: ");

      data.getProportions().forEach((k, v) -> {
        String formatted = String.format("%s:%-3s%%", k, v.toPlainString());
        sb.append(String.format("%-" + maxWidths.get(k) + "s  ", formatted));
      });

      System.out.println(sb);
    }
  }

  /**
   * 打印指定特征的历史数据
   */
  private static void printHistoricalData(SalesFeaturePredictor predictor, String featureName,
      int monthsToPrint) {
    System.out.println("\n" + featureName + "特征最近" + monthsToPrint + "个月历史数据:");
    List<FeatureData> history = predictor.getFeatureHistory(featureName);
    if (history.isEmpty()) {
      System.out.println("无历史数据");
      return;
    }

    int startIndex = Math.max(0, history.size() - monthsToPrint);
    printPredictions(history.subList(startIndex, history.size()));
  }

  /**
   * 添加历史订单数据
   */
  public void addHistoricalOrder(Order order) {
    historicalOrders.add(order);
  }

  /**
   * 获取指定特征的历史数据列表
   */
  public List<FeatureData> getFeatureHistory(String featureName) {
    return historicalOrders.stream().map(order -> order.getFeatureData(featureName))
        .filter(Objects::nonNull).sorted(Comparator.comparing(FeatureData::getOrderDate))
        .collect(Collectors.toList());
  }

  /**
   * 预测未来N个月的特征比例
   */
  public List<FeatureData> predictFeatureProportionsForNextNMonths(String featureName, int nMonths,
      PredictionAlgorithm algorithm) {
    if (historicalOrders.isEmpty()) {
      throw new IllegalStateException("没有历史订单数据可供预测");
    }

    List<FeatureData> featureHistories = getFeatureHistory(featureName);
    if (featureHistories.isEmpty()) {
      throw new IllegalArgumentException("没有找到特征: " + featureName + " 的历史数据");
    }

    LocalDate latestDate = featureHistories.get(featureHistories.size() - 1).getOrderDate();

    return switch (algorithm) {
      case WEIGHTED_MOVING_AVERAGE -> predictWithWeightedMovingAverage(featureHistories, latestDate, nMonths);
      case EXPONENTIAL_SMOOTHING -> predictWithExponentialSmoothing(featureHistories, latestDate, nMonths);
      case ARIMA_SIMPLE -> predictWithEnhancedARIMA(featureHistories, latestDate, nMonths);
      case LINEAR_REGRESSION -> predictWithLinearRegression(featureHistories, latestDate, nMonths);
      case SEASONAL_ADJUSTMENT -> predictWithSeasonalAdjustment(featureHistories, latestDate, nMonths);
      default -> throw new IllegalArgumentException("不支持的预测算法: " + algorithm);
    };
  }

  /**
   * 加权移动平均法（强权重衰减）
   */
  private List<FeatureData> predictWithWeightedMovingAverage(List<FeatureData> featureHistories,
      LocalDate latestDate, int nMonths) {
    List<FeatureData> predictions = new ArrayList<>();
    Set<String> featureValues = featureHistories.get(0).getProportions().keySet();
    int historySize = featureHistories.size();

    for (int i = 1; i <= nMonths; i++) {
      LocalDate predictionDate = latestDate.plusMonths(i);
      Map<String, BigDecimal> predictedProportions = new HashMap<>();

      for (String featureValue : featureValues) {
        BigDecimal weightedSum = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        // 强权重衰减：最近3个月权重显著高于更早数据
        for (int j = 0; j < historySize; j++) {
          double weightFactor = j >= historySize - 3 ? 0.8 : 0.2; // 最近3个月权重0.8，其他0.2
          BigDecimal weight = BigDecimal.valueOf(weightFactor);

          BigDecimal value = featureHistories.get(j).getProportions()
              .getOrDefault(featureValue, BigDecimal.ZERO);

          weightedSum = weightedSum.add(value.multiply(weight));
          totalWeight = totalWeight.add(weight);
        }

        // 处理除零情况
        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
          predictedProportions.put(featureValue, BigDecimal.ZERO);
        } else {
          BigDecimal predictedValue = weightedSum.divide(totalWeight, SCALE, ROUNDING_MODE);
          predictedProportions.put(featureValue, predictedValue);
        }
      }

      predictedProportions = normalizeProportions(predictedProportions);
      predictions.add(new FeatureData(featureHistories.get(0).getFeatureName(), predictionDate,
          predictedProportions));
    }

    return predictions;
  }

  /**
   * 指数平滑法（高灵敏度）
   */
  private List<FeatureData> predictWithExponentialSmoothing(List<FeatureData> featureHistories,
      LocalDate latestDate, int nMonths) {
    List<FeatureData> predictions = new ArrayList<>();
    double alpha = 0.8; // 高平滑系数，快速响应变化
    Set<String> featureValues = featureHistories.get(0).getProportions().keySet();

    for (int i = 1; i <= nMonths; i++) {
      LocalDate predictionDate = latestDate.plusMonths(i);
      Map<String, BigDecimal> predictedProportions = new HashMap<>();

      for (String featureValue : featureValues) {
        // 确保有历史数据
        if (featureHistories.isEmpty()) {
          predictedProportions.put(featureValue, BigDecimal.ZERO);
          continue;
        }

        BigDecimal smoothValue = featureHistories.get(featureHistories.size() - 1).getProportions()
            .get(featureValue);

        // 应用高灵敏度指数平滑
        for (int j = featureHistories.size() - 2; j >= 0; j--) {
          BigDecimal currentValue = featureHistories.get(j).getProportions().get(featureValue);
          smoothValue = currentValue.multiply(BigDecimal.valueOf(alpha))
              .add(smoothValue.multiply(BigDecimal.valueOf(1 - alpha)))
              .setScale(SCALE, ROUNDING_MODE);
        }

        predictedProportions.put(featureValue, smoothValue);
      }

      predictedProportions = normalizeProportions(predictedProportions);
      predictions.add(new FeatureData(featureHistories.get(0).getFeatureName(), predictionDate,
          predictedProportions));
    }

    return predictions;
  }

  /**
   * 增强型ARIMA模型（捕捉趋势和波动）
   */
  private List<FeatureData> predictWithEnhancedARIMA(List<FeatureData> featureHistories,
      LocalDate latestDate, int nMonths) {
    List<FeatureData> predictions = new ArrayList<>();
    Set<String> featureValues = featureHistories.get(0).getProportions().keySet();

    for (int i = 1; i <= nMonths; i++) {
      LocalDate predictionDate = latestDate.plusMonths(i);
      Map<String, BigDecimal> predictedProportions = new HashMap<>();

      for (String featureValue : featureValues) {
        List<BigDecimal> series = featureHistories.stream()
            .map(data -> data.getProportions().get(featureValue)).collect(Collectors.toList());

        // 确保有足够的数据点
        if (series.size() < 3) {
          // 数据不足时回退到简单预测
          predictedProportions.put(featureValue,
              series.isEmpty() ? BigDecimal.ZERO : series.get(series.size() - 1));
          continue;
        }

        // ARIMA(1,1,1)模型实现
        BigDecimal predictedValue = arimaModel(series);
        predictedProportions.put(featureValue, predictedValue);
      }

      predictedProportions = normalizeProportions(predictedProportions);
      predictions.add(new FeatureData(featureHistories.get(0).getFeatureName(), predictionDate,
          predictedProportions));
    }

    return predictions;
  }

  /**
   * ARIMA(1,1,1)模型实现
   */
  private BigDecimal arimaModel(List<BigDecimal> series) {
    int n = series.size();
    if (n < 3) {
      return n == 0 ? BigDecimal.ZERO : series.get(n - 1);
    }

    // 1. 一阶差分
    List<BigDecimal> diff = new ArrayList<>();
    for (int i = 1; i < n; i++) {
      diff.add(series.get(i).subtract(series.get(i - 1)));
    }

    // 2. AR(1)部分：使用前一个差分预测下一个差分
    BigDecimal ar = diff.get(diff.size() - 1).multiply(BigDecimal.valueOf(0.6));

    // 3. MA(1)部分：使用前一个预测误差
    List<BigDecimal> errors = new ArrayList<>();
    for (int i = 1; i < diff.size(); i++) {
      errors.add(diff.get(i).subtract(diff.get(i - 1).multiply(BigDecimal.valueOf(0.6))));
    }
    BigDecimal ma = errors.isEmpty() ? BigDecimal.ZERO
        : errors.get(errors.size() - 1).multiply(BigDecimal.valueOf(0.3));

    // 4. 预测下一个差分
    BigDecimal predictedDiff = ar.add(ma).setScale(SCALE, ROUNDING_MODE);

    // 5. 转换回原始尺度
    return series.get(n - 1).add(predictedDiff).setScale(SCALE, ROUNDING_MODE);
  }

  /**
   * 线性回归法
   */
  private List<FeatureData> predictWithLinearRegression(List<FeatureData> featureHistories,
      LocalDate latestDate, int nMonths) {
    List<FeatureData> predictions = new ArrayList<>();
    Set<String> featureValues = featureHistories.get(0).getProportions().keySet();

    for (int i = 1; i <= nMonths; i++) {
      LocalDate predictionDate = latestDate.plusMonths(i);
      Map<String, BigDecimal> predictedProportions = new HashMap<>();

      for (String featureValue : featureValues) {
        List<BigDecimal> xValues = IntStream.range(0, featureHistories.size())
            .mapToObj(BigDecimal::valueOf).collect(Collectors.toList());
        List<BigDecimal> yValues = featureHistories.stream()
            .map(data -> data.getProportions().get(featureValue)).collect(Collectors.toList());

        // 确保有足够的数据点进行回归
        if (xValues.size() < 2) {
          predictedProportions.put(featureValue,
              yValues.isEmpty() ? BigDecimal.ZERO : yValues.get(0));
          continue;
        }

        LinearRegressionResult result = calculateLinearRegression(xValues, yValues);
        BigDecimal xFuture = BigDecimal.valueOf(featureHistories.size() + i - 1);
        BigDecimal predictedValue = result.slope.multiply(xFuture).add(result.intercept)
            .setScale(SCALE, ROUNDING_MODE);

        // 确保预测值在有效范围内
        predictedValue = predictedValue.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
        predictedProportions.put(featureValue, predictedValue);
      }

      predictedProportions = normalizeProportions(predictedProportions);
      predictions.add(new FeatureData(featureHistories.get(0).getFeatureName(), predictionDate,
          predictedProportions));
    }

    return predictions;
  }

  /**
   * 季节性调整法
   */
  private List<FeatureData> predictWithSeasonalAdjustment(List<FeatureData> featureHistories,
      LocalDate latestDate, int nMonths) {
    List<FeatureData> predictions = new ArrayList<>();
    int seasonality = 12;
    Set<String> featureValues = featureHistories.get(0).getProportions().keySet();

    for (int i = 1; i <= nMonths; i++) {
      LocalDate predictionDate = latestDate.plusMonths(i);
      Map<String, BigDecimal> predictedProportions = new HashMap<>();

      for (String featureValue : featureValues) {
        List<BigDecimal> series = featureHistories.stream()
            .map(data -> data.getProportions().get(featureValue)).collect(Collectors.toList());

        // 确保有足够的数据进行季节性分析
        if (series.size() < seasonality) {
          // 数据不足时回退到简单平均
          BigDecimal sum = series.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
          predictedProportions.put(featureValue, series.isEmpty() ? BigDecimal.ZERO
              : sum.divide(BigDecimal.valueOf(series.size()), SCALE, ROUNDING_MODE));
          continue;
        }

        BigDecimal predictedValue = seasonalAdjustment(series, seasonality, i);
        predictedProportions.put(featureValue, predictedValue);
      }

      predictedProportions = normalizeProportions(predictedProportions);
      predictions.add(new FeatureData(featureHistories.get(0).getFeatureName(), predictionDate,
          predictedProportions));
    }

    return predictions;
  }

  /**
   * 计算线性回归参数
   */
  private LinearRegressionResult calculateLinearRegression(List<BigDecimal> xValues,
      List<BigDecimal> yValues) {
    int n = xValues.size();
    if (n < 2) {
      return new LinearRegressionResult(BigDecimal.ZERO, n == 0 ? BigDecimal.ZERO : yValues.get(0));
    }

    BigDecimal sumX = xValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal sumY = yValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    BigDecimal meanX = sumX.divide(BigDecimal.valueOf(n), SCALE, ROUNDING_MODE);
    BigDecimal meanY = sumY.divide(BigDecimal.valueOf(n), SCALE, ROUNDING_MODE);

    BigDecimal numerator = BigDecimal.ZERO, denominator = BigDecimal.ZERO;
    for (int i = 0; i < n; i++) {
      BigDecimal xDiff = xValues.get(i).subtract(meanX);
      BigDecimal yDiff = yValues.get(i).subtract(meanY);
      numerator = numerator.add(xDiff.multiply(yDiff));
      denominator = denominator.add(xDiff.multiply(xDiff));
    }

    // 处理除零情况
    if (denominator.compareTo(BigDecimal.ZERO) == 0) {
      return new LinearRegressionResult(BigDecimal.ZERO, meanY);
    }

    BigDecimal slope = numerator.divide(denominator, SCALE, ROUNDING_MODE);
    BigDecimal intercept = meanY.subtract(slope.multiply(meanX)).setScale(SCALE, ROUNDING_MODE);
    return new LinearRegressionResult(slope, intercept);
  }

  /**
   * 季节性调整
   */
  private BigDecimal seasonalAdjustment(List<BigDecimal> series, int seasonality,
      int forecastStep) {
    if (series.size() < seasonality) {
      // 数据不足时回退到简单平均
      BigDecimal sum = series.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      return series.isEmpty() ? BigDecimal.ZERO
          : sum.divide(BigDecimal.valueOf(series.size()), SCALE, ROUNDING_MODE);
    }

    Map<Integer, BigDecimal> seasonalFactors = new HashMap<>();
    for (int i = 0; i < seasonality; i++) {
      List<BigDecimal> seasonalValues = new ArrayList<>();
      for (int j = i; j < series.size(); j += seasonality) {
        seasonalValues.add(series.get(j));
      }

      // 确保有足够的季节性数据
      if (seasonalValues.isEmpty()) {
        seasonalFactors.put(i, BigDecimal.ZERO);
        continue;
      }

      BigDecimal sum = seasonalValues.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      seasonalFactors.put(i,
          sum.divide(BigDecimal.valueOf(seasonalValues.size()), SCALE, ROUNDING_MODE));
    }

    LinearRegressionResult trend = calculateLinearRegression(
        IntStream.range(0, series.size()).mapToObj(BigDecimal::valueOf)
            .collect(Collectors.toList()), series);

    int lastIndex = series.size() - 1;
    int seasonalIndex = (lastIndex + forecastStep) % seasonality;
    BigDecimal trendValue = trend.slope.multiply(BigDecimal.valueOf(lastIndex + forecastStep))
        .add(trend.intercept).setScale(SCALE, ROUNDING_MODE);
    BigDecimal seasonalFactor = seasonalFactors.get(seasonalIndex);

    return trendValue.add(seasonalFactor).setScale(SCALE, ROUNDING_MODE);
  }

  /**
   * 归一化处理
   */
  private Map<String, BigDecimal> normalizeProportions(Map<String, BigDecimal> proportions) {
    // 处理空比例
    if (proportions.isEmpty()) {
      return new HashMap<>();
    }

    // 计算原始总和
    BigDecimal total = proportions.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

    // 如果总和已经是100%，直接返回
    if (total.compareTo(BigDecimal.valueOf(100)) == 0) {
      return new HashMap<>(proportions);
    }

    // 处理总和为零的情况
    if (total.compareTo(BigDecimal.ZERO) == 0) {
      // 平均分配
      Map<String, BigDecimal> normalized = new HashMap<>();
      BigDecimal equalShare = BigDecimal.valueOf(100)
          .divide(BigDecimal.valueOf(proportions.size()), SCALE, ROUNDING_MODE);
      proportions.forEach((k, v) -> normalized.put(k, equalShare));
      return normalized;
    }

    // 比例缩放
    Map<String, BigDecimal> normalized = new HashMap<>();
    for (Map.Entry<String, BigDecimal> entry : proportions.entrySet()) {
      BigDecimal norm = entry.getValue().multiply(BigDecimal.valueOf(100))
          .divide(total, SCALE, ROUNDING_MODE);
      normalized.put(entry.getKey(), norm);
    }

    // 处理舍入误差
    BigDecimal totalTmp = normalized.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    if (totalTmp.compareTo(BigDecimal.valueOf(100)) != 0) {
      // 找出最大的元素调整差值，减少视觉差异
      String keyToAdjust = normalized.entrySet().stream().max(Map.Entry.comparingByValue())
          .map(Map.Entry::getKey).orElse(normalized.keySet().iterator().next());

      BigDecimal difference = BigDecimal.valueOf(100).subtract(totalTmp);
      BigDecimal adjustedValue = normalized.get(keyToAdjust).add(difference)
          .setScale(SCALE, ROUNDING_MODE);
      normalized.put(keyToAdjust, adjustedValue);
    }

    return normalized;
  }

  /**
   * 预测算法枚举
   */
  public enum PredictionAlgorithm {
    WEIGHTED_MOVING_AVERAGE("加权移动平均法"), EXPONENTIAL_SMOOTHING("指数平滑法"), ARIMA_SIMPLE(
        "增强型ARIMA模型"), LINEAR_REGRESSION("线性回归法"), SEASONAL_ADJUSTMENT("季节性调整法");

    PredictionAlgorithm(String string) {

    }
  }

  /**
   * 线性回归结果类
   */
  private static class LinearRegressionResult {

    BigDecimal slope, intercept;

    public LinearRegressionResult(BigDecimal slope, BigDecimal intercept) {
      this.slope = slope;
      this.intercept = intercept;
    }
  }

  /**
   * 订单类
   */
  public static class Order {

    private final LocalDate orderDate;
    private final Map<String, FeatureData> featureDataMap;

    public Order(LocalDate orderDate) {
      this.orderDate = orderDate;
      this.featureDataMap = new HashMap<>();
    }

    public void addFeatureData(FeatureData featureData) {
      featureDataMap.put(featureData.getFeatureName(), featureData);
    }

    public FeatureData getFeatureData(String featureName) {
      return featureDataMap.get(featureName);
    }

    public LocalDate getOrderDate() {
      return orderDate;
    }
  }

  /**
   * 特征数据类
   */
  public static class FeatureData {

    private final String featureName;
    private final LocalDate orderDate;
    private Map<String, BigDecimal> proportions;

    public FeatureData(String featureName, LocalDate orderDate,
        Map<String, BigDecimal> proportions) {
      this.featureName = featureName;
      this.orderDate = orderDate;
      this.proportions = proportions;

      // 验证比例和为100%
      BigDecimal total = proportions.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
        // 尝试自动归一化
        Map<String, BigDecimal> normalized = new HashMap<>();
        for (Map.Entry<String, BigDecimal> entry : proportions.entrySet()) {
          BigDecimal norm = entry.getValue().multiply(BigDecimal.valueOf(100))
              .divide(total, SCALE, ROUNDING_MODE);
          normalized.put(entry.getKey(), norm);
        }

        // 再次验证
        total = normalized.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.valueOf(100)) != 0) {
          // 调整最大比例值处理舍入误差
          String keyToAdjust = normalized.entrySet().stream().max(Map.Entry.comparingByValue())
              .map(Map.Entry::getKey).orElse(normalized.keySet().iterator().next());

          BigDecimal difference = BigDecimal.valueOf(100).subtract(total);
          normalized.put(keyToAdjust,
              normalized.get(keyToAdjust).add(difference).setScale(SCALE, ROUNDING_MODE));
        }

        this.proportions = normalized;
      }
    }

    public String getFeatureName() {
      return featureName;
    }

    public LocalDate getOrderDate() {
      return orderDate;
    }

    public Map<String, BigDecimal> getProportions() {
      return new HashMap<>(proportions);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("日期: ").append(orderDate).append(", 特征: ").append(featureName)
          .append(", 比例: ");
      proportions.forEach((k, v) -> sb.append(k).append(": ").append(v).append("%  "));
      return sb.toString();
    }
  }
}