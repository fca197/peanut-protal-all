package com.olivia.peanut.aps.utils.forecast;

import cn.hutool.core.collection.CollUtil;
import com.google.ortools.sat.*;
import com.olivia.peanut.aps.utils.forecast.model.*;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * ortools 计算占比数量
 */
@Slf4j
public class ApsForecastUtils {

  public static DivisionRes division(int total, List<SkuGroup> groups) {
    return division(total, groups, List.of());
  }

  public static DivisionRes division(int total, List<SkuGroup> groups,
      List<SkuCombinationConstraint> combinationConstraints) {
    DivisionRes res = new DivisionRes().setSkuCombineInfoList(new ArrayList<>())
        .setOnlySkuInfoList(new ArrayList<>());
    if (CollUtil.isEmpty(groups) || total == 0) {
      return res;
    }

    // 尝试求解
    List<Long> solution = solveSkuAllocation(groups, total, combinationConstraints);
    if (solution == null) {
      // 微调比例并重试
      groups = fineTuneRatios(groups);
      solution = solveSkuAllocation(groups, total, combinationConstraints);
      if (solution == null) {
        log.info("即使微调比例后仍未找到可行解，请进一步检查约束条件。");
      }
    }

    if (solution != null) {
      // 生成所有组合
      List<List<String>> combinations = generateCombinations(groups);

      // 输出结果
      for (int i = 0; i < combinations.size(); i++) {
        String sku = combinations.get(i).stream().sorted().collect(Collectors.joining(","));
        Long count = solution.get(i);
        if (log.isDebugEnabled()) {
          log.debug("{}: {}", sku, count);
        }
        res.getSkuCombineInfoList().add(new SkuCombineInfo().setKey(sku).setCount(count));
      }

      // 显示调整后的比例
      for (SkuGroup group : groups) {
        for (Map.Entry<String, Double> entry : group.getRatios().entrySet()) {
          String option = entry.getKey();
          List<Integer> indices = new ArrayList<>();
          for (int i = 0; i < combinations.size(); i++) {
            if (combinations.get(i).get(groups.indexOf(group)).equals(option)) {
              indices.add(i);
            }
          }
          long actual = indices.stream().mapToLong(solution::get).sum();
          res.getOnlySkuInfoList().add(
              new OnlySkuInfo().setGroupName(group.getGroupName()).setSku(option)
                  .setResProportion(actual * 1.0 / total).setOriginalProportion(entry.getValue()));
          if (log.isDebugEnabled()) {
            log.debug("{} {}: {}% (原{}%)", group.getGroupName(), option,
                actual * 100.0 / total, entry.getValue() * 100);
          }
        }
      }
    }
    return res;
  }

  private static List<Long> solveSkuAllocation(List<SkuGroup> groups, int total,
      List<SkuCombinationConstraint> combinationConstraints) {
    // 生成所有组合
    List<List<String>> combinations = generateCombinations(groups);
    log.info("combinations size {}", combinations.size());
    // 创建模型
    CpModel model = new CpModel();

    // 创建决策变量，至少分配1个（如果组合数<=总数）
    List<IntVar> vars = new ArrayList<>();
    boolean allMustBeAtLeastOne = combinations.size() <= total;
    for (int i = 0; i < combinations.size(); i++) {
      int lowerBound = allMustBeAtLeastOne ? 1 : 0;
      vars.add(model.newIntVar(lowerBound, total, "x" + i));
    }

    // 添加总和约束
    model.addEquality(LinearExpr.sum(vars.toArray(new LinearArgument[]{})), total);

    // 创建比例约束
    double tolerance = 0.05; // 比例容忍度
    for (SkuGroup group : groups) {
      for (Map.Entry<String, Double> entry : group.getRatios().entrySet()) {
        String option = entry.getKey();
        double ratio = entry.getValue();

        // 收集包含此选项的组合索引
        List<Integer> indices = new ArrayList<>();
        int groupIndex = groups.indexOf(group);
        for (int i = 0; i < combinations.size(); i++) {
          if (combinations.get(i).get(groupIndex).equals(option)) {
            indices.add(i);
          }
        }

        // 创建约束：实际数量在期望数量的一定范围内
        int expected = (int) Math.round(total * ratio);
        int expectedMin = Math.max(0, expected - (int) Math.round(total * tolerance));
        int expectedMax = expected + (int) Math.round(total * tolerance);
        LinearArgument actual = LinearExpr.sum(
            indices.stream().map(vars::get).toArray(IntVar[]::new));
        model.addGreaterOrEqual(actual, expectedMin);
        model.addLessOrEqual(actual, expectedMax);
      }
    }

    // 添加特定组合约束
    for (SkuCombinationConstraint constraint : combinationConstraints) {
      String combinationStr = constraint.getCombination();
      double ratio = constraint.getRatio();
      List<Integer> indices = new ArrayList<>();

      // 解析组合约束（假设格式为"颜色=红,轮毂=19"）
      Map<String, String> combinationMap = parseCombination(combinationStr);

      // 找到匹配此组合的所有索引
      for (int i = 0; i < combinations.size(); i++) {
        List<String> combo = combinations.get(i);
        boolean matches = true;

        // 检查该组合是否满足约束条件
        for (Map.Entry<String, String> entry : combinationMap.entrySet()) {
          String groupName = entry.getKey();
          String option = entry.getValue();

          // 找到该组在groups中的索引
          int groupIndex = -1;
          for (int g = 0; g < groups.size(); g++) {
            if (groups.get(g).getGroupName().equals(groupName)) {
              groupIndex = g;
              break;
            }
          }

          // 如果找不到该组或选项不匹配，则不满足约束
          if (groupIndex == -1 || !combo.get(groupIndex).equals(option)) {
            matches = false;
            break;
          }
        }

        if (matches) {
          indices.add(i);
        }
      }

      // 添加约束
      if (!indices.isEmpty()) {
        int expectedCount = (int) Math.round(total * ratio);
        int constraintTolerance = Math.max(1, (int) Math.round(total * 0.02)); // 2%的容差
        LinearArgument actual = LinearExpr.sum(
            indices.stream().map(vars::get).toArray(IntVar[]::new));
        model.addGreaterOrEqual(actual, Math.max(0, expectedCount - constraintTolerance));
        model.addLessOrEqual(actual, expectedCount + constraintTolerance);

        if (log.isDebugEnabled()) {
          log.debug("添加特定组合约束: {} (目标比例: {}%, 目标数量: {})",
              combinationStr, ratio * 100, expectedCount);
        }
      }
    }

    // 创建目标函数：最小化各组合数量的差异
    LinearExprBuilder obj = LinearExpr.newBuilder();
    IntVar maxVar = model.newIntVar(0, total, "max");
    IntVar minVar = model.newIntVar(0, total, "min");

    for (IntVar var : vars) {
      model.addGreaterOrEqual(maxVar, var);
      model.addLessOrEqual(minVar, var);
    }
    obj.addTerm(maxVar, 1);
    obj.addTerm(minVar, -1);

    model.minimize(obj.build());

    // 求解
    CpSolver solver = new CpSolver();
    // 设置求解时间限制（毫秒）
    solver.getParameters().setMaxTimeInSeconds(1);
    CpSolverStatus status = solver.solve(model);

    if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
      List<Long> solution = new ArrayList<>();
      for (IntVar var : vars) {
        solution.add(solver.value(var));
      }
      return solution;
    }
    log.warn("求解状态: {}", status);
    return null;
  }

  // 解析组合约束字符串，格式为"颜色=红,轮毂=19"
  private static Map<String, String> parseCombination(String combinationStr) {
    Map<String, String> result = new HashMap<>();
    if (combinationStr == null || combinationStr.isEmpty()) {
      return result;
    }
    String[] parts = combinationStr.split(",");
    for (String part : parts) {
      String[] keyValue = part.split("=");
      if (keyValue.length == 2) {
        result.put(keyValue[0], keyValue[1]);
      }
    }
    return result;
  }

  // 生成所有组合的笛卡尔积
  public static List<List<String>> generateCombinations(List<SkuGroup> groups) {
    List<List<String>> result = new ArrayList<>();
    generateRecursive(groups, 0, new ArrayList<>(), result);
    return result;
  }

  private static void generateRecursive(List<SkuGroup> groups, int depth, List<String> current,
      List<List<String>> result) {
    if (depth == groups.size()) {
      result.add(new ArrayList<>(current));
      return;
    }

    SkuGroup group = groups.get(depth);
    for (String option : group.getRatios().keySet()) {
      current.add(option);
      generateRecursive(groups, depth + 1, current, result);
      current.removeLast();
    }
  }

  // 微调比例
  public static List<SkuGroup> fineTuneRatios(List<SkuGroup> groups) {
    List<SkuGroup> newGroups = new ArrayList<>();
    double adjustment = 0.01; // 微调幅度
    for (SkuGroup group : groups) {
      Map<String, Double> newRatios = new HashMap<>();
      for (Map.Entry<String, Double> entry : group.getRatios().entrySet()) {
        double newRatio = entry.getValue() + (Math.random() > 0.5 ? adjustment : -adjustment);
        newRatio = Math.max(0.01, Math.min(0.99, newRatio)); // 调整合理范围
        newRatios.put(entry.getKey(), newRatio);
      }
      // 重新归一化比例
      double sum = newRatios.values().stream().mapToDouble(Double::doubleValue).sum();
      newRatios.replaceAll((k, v) -> v / sum);
      newGroups.add(new SkuGroup(group.getGroupName(), newRatios));
    }
    return newGroups;
  }
}