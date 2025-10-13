package com.olivia.peanut.aps.utils.process;

import cn.hutool.core.collection.CollUtil;
import com.google.ortools.sat.*;
import com.olivia.peanut.aps.utils.process.entity.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class ProduceProcessUtilsV2 {

  private static final int SCALING_FACTOR = 1000; // 功率放大因子

  public static List<ProduceProcessComputeOrderResV2> schedule(ProduceProcessComputeReq input) {
    // 解析基准时间
    LocalDateTime baseTime = input.getProduceStartTime();
    // 预处理电力时间段：转换为秒数偏移，过滤无效时间段
    List<PowerPeriod> powerPeriods = new ArrayList<>();
    if (CollUtil.isNotEmpty(input.getDayPowerList())) {
      for (DayPower dp : input.getDayPowerList()) {
        LocalDateTime start = dp.getStartDateTime();
        LocalDateTime end = dp.getEndDateTime();
        // 忽略在排产开始时间前结束的时间段
        if (end.isBefore(baseTime)) {
          continue;
        }
        // 调整时间段：起始时间取max(baseTime, start)
        LocalDateTime adjustedStart = start.isBefore(baseTime) ? baseTime : start;
        long startSec = Duration.between(baseTime, adjustedStart).getSeconds();
        long endSec = Duration.between(baseTime, end).getSeconds();
        if (endSec > startSec) { // 有效时间段
          powerPeriods.add(new PowerPeriod(startSec, endSec, (int) (dp.getMaxPower() * SCALING_FACTOR)));
        }
      }
    }
    // 收集所有任务并建立索引
    List<Task> allTasks = new ArrayList<>();
    Map<Long, List<Integer>> machineToTasks = new HashMap<>(); // 机器ID -> 任务索引
    Map<Long, Integer> taskIdToIndex = new HashMap<>(); // orderWorkId -> 任务索引
    int taskIndex = 0;
    // 按紧急程度降序排序订单（urgentLevel越高越优先）
    List<ProduceOrder> sortedOrders = input.getProduceOrderList().stream().peek(t -> t.setUrgencyLevel(Objects.isNull(t.getUrgencyLevel()) ? 0 : t.getUrgencyLevel()))
        .sorted(Comparator.comparingInt(ProduceOrder::getUrgencyLevel).reversed()).toList();
    for (ProduceOrder order : sortedOrders) {
      List<ProduceOrderMachine> orderTasks = order.getOrderMachineList();
      for (int i = 0; i < orderTasks.size(); i++) {
        ProduceOrderMachine om = orderTasks.get(i);
        Task task = new Task(taskIndex++, order.getOrderId(), om.getOrderWorkId(), order.getUrgencyLevel(), om.getMachineId(), om.getUseTime(),
            (int) (om.getMaxPower() * SCALING_FACTOR), // 缩放功率
            i // 任务在订单中的顺序
        );
        allTasks.add(task);
        taskIdToIndex.put(om.getOrderWorkId(), task.index());
        // 添加到机器映射
        machineToTasks.computeIfAbsent(om.getMachineId(), k -> new ArrayList<>()).add(task.index());
      }
    }
    // 创建CP-SAT模型
    CpModel model = new CpModel();
    int numTasks = allTasks.size();
    int horizon = 10 * 365 * 24 * 3600; // 10年（秒），作为时间上限
    // 创建任务变量：开始时间、结束时间、区间
    IntVar[] starts = new IntVar[numTasks];
    IntVar[] ends = new IntVar[numTasks];
    IntervalVar[] intervals = new IntervalVar[numTasks];
    for (Task task : allTasks) {
      int idx = task.index();
      starts[idx] = model.newIntVar(0, horizon, "start_" + idx);
      ends[idx] = model.newIntVar(0, horizon, "end_" + idx);
      intervals[idx] = model.newIntervalVar(starts[idx], model.newConstant(task.duration()), ends[idx], "interval_" + idx);
    }
    // 添加顺序约束（同一订单的任务顺序）
    for (ProduceOrder order : sortedOrders) {
      List<ProduceOrderMachine> orderTasks = order.getOrderMachineList();
      for (int i = 1; i < orderTasks.size(); i++) {
        int prevIdx = taskIdToIndex.get(orderTasks.get(i - 1).getOrderWorkId());
        int currIdx = taskIdToIndex.get(orderTasks.get(i).getOrderWorkId());
        model.addGreaterOrEqual(starts[currIdx], ends[prevIdx]);
      }
    }
    // 添加机器约束（同一机器任务不重叠）
    for (List<Integer> taskIndices : machineToTasks.values()) {
      IntervalVar[] machineIntervals = taskIndices.stream().map(idx -> intervals[idx]).toArray(IntervalVar[]::new);
      model.addNoOverlap(machineIntervals);
    }
    // 添加电力约束
    if (!powerPeriods.isEmpty()) {
      for (PowerPeriod period : powerPeriods) {
        List<Literal> overlaps = new ArrayList<>();
        List<Integer> powers = new ArrayList<>();
        for (Task task : allTasks) {
          int idx = task.index();
          Literal overlapsPeriod = model.newBoolVar("overlap_" + idx + "_" + period.startSec);
          // 创建条件变量：start <= period.endSec 且 end >= period.startSec
          BoolVar cond1 = model.newBoolVar("");
          model.addLessOrEqual(starts[idx], period.endSec).onlyEnforceIf(cond1);
          model.addGreaterThan(starts[idx], period.endSec).onlyEnforceIf(cond1.not());
          BoolVar cond2 = model.newBoolVar("");
          model.addGreaterOrEqual(ends[idx], period.startSec).onlyEnforceIf(cond2);
          model.addLessThan(ends[idx], period.startSec).onlyEnforceIf(cond2.not());
          // overlapsPeriod = cond1 AND cond2
          model.addBoolAnd(new Literal[]{cond1, cond2}).onlyEnforceIf(overlapsPeriod);
          model.addBoolOr(new Literal[]{cond1.not(), cond2.not()}).onlyEnforceIf(overlapsPeriod.not());
          overlaps.add(overlapsPeriod);
          powers.add(task.power());
        }
        // 添加功率约束：Σ(overlap * power) <= maxPower
        Literal[] overlapsArray = overlaps.toArray(new Literal[0]);
        long[] powersArray = powers.stream().mapToLong(Integer::intValue).toArray();
        model.addLessOrEqual(LinearExpr.weightedSum(overlapsArray, powersArray), period.maxPower);
      }
    }
    // 设置目标：最小化最大结束时间（makespan）
    IntVar makespan = model.newIntVar(0, horizon, "makespan");
    model.addMaxEquality(makespan, ends);
    model.minimize(makespan);
    // 创建求解器并求解
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);
    if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
      throw new RuntimeException("No solution found");
    }
    // 收集结果
    List<ProduceProcessComputeOrderResV2> results = new ArrayList<>();

    // 步骤1: 收集每个订单的第一个任务开始时间
    Map<Long, Long> orderFirstStartMap = new HashMap<>();
    for (Task task : allTasks) {
      if (task.seqInOrder() == 0) { // 仅处理订单的第一个任务
        long startSec = solver.value(starts[task.index()]);
        orderFirstStartMap.put(task.orderId(), startSec);
      }
    }

    // 步骤2: 按开始时间排序订单ID
    List<Long> sortedOrderIds = orderFirstStartMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).toList();

    // 步骤3: 创建订单顺序索引映射 (orderId -> orderCreateIndex)
    Map<Long, Long> orderIndexMap = new HashMap<>();
    for (int i = 0; i < sortedOrderIds.size(); i++) {
      orderIndexMap.put(sortedOrderIds.get(i), (long) (i + 1));
    }

    // 步骤4: 构建结果
    for (Task task : allTasks) {
      int idx = task.index();
      long startSec = solver.value(starts[idx]);
      LocalDateTime startTime = baseTime.plusSeconds(startSec);

      ProduceProcessComputeOrderResV2 res = new ProduceProcessComputeOrderResV2().setOrderWorkId(task.orderWorkId()).setBeginLocalDateTime(startTime)
          .setEndLocalDateTime(startTime.plusSeconds(task.duration())).setOrderId(task.orderId())  // 设置订单ID
          .setOrderCreateIndex(orderIndexMap.get(task.orderId()));  // 设置订单制造顺序索引

      results.add(res);
    }

    return results;
  }

  // 辅助类：电力时间段
  private record PowerPeriod(long startSec, long endSec, int maxPower) {

  }

  // 辅助类：任务信息
  private record Task(int index, Long orderId, long orderWorkId, int urgentLevel, Long machineId, Long duration, Integer power, int seqInOrder) {

  }
}
//一种常见的计划问题是作业工作室，在这种情况下，需要处理多个作业 并在多台机器上处理
//
//每个作业都由一系列任务组成，这些任务必须在指定的 而且每项任务都必须在特定的机器上处理 例如，职位可以是单个消费品的制造商，如 汽车。 问题在于如何在计算机上调度任务， 时间表的 length（完成所有作业所需的时间）。
//
//对于作业车间问题，有以下几个限制：
//作业的上一个任务无法启动前，无法启动该作业的上一个任务 已完成。
//一台机器一次只能处理一项任务。
//任务一旦开始，就必须运行至完成。
//
//下面是一个任务的简单配置：
//{
//  "dayPowerList": [
//    {
//      "startDateTime": "2025-08-07 11:12:55",
//      "endDateTime": "2025-08-07 11:12:55",
//      "maxPower": 1000
//    }
//  ],
//  "produceStartTime": "2025-08-07 11:12:55",
//  "produceOrderList": [
//    {
//      "orderId": 10,
//      "urgentLevel": 10,
//      "orderMachineList": [
//        {
//          "orderWorkId":111111,
//          "machineId": 0,
//          "useTime": 110,
//          "maxPower": 1.00
//        }
//      ]
//    }
//  ]
//}
//
//其中 dayPowerList 指 每个时间短内可以使用的最大功率，startDateTime指功率限制开始时间， endDateTime指功率限制结束时间 ，maxPower指时间段内最大功率。 如果制造时间不在该时间段内， 默认最大功率无限大
//
//produceStartTime  指 订单开始排产时间， 订单后续制造以当前时间开始
//
//produceOrderList指订单列表， orderId 指订单ID，标定订单唯一性， urgentLevel指订单紧急程度， 当订单含有多个时，urgentLevel 越高优先制造，
//	orderMachineList 指订单制造所属工艺， 可以多个，也可以单个，orderWorkId 当前订单制造节点标识，该id 全局唯一，   machineId 指订单制造所属机器ID， 一台机器一次只能处理一项任务。
//	useTime： 指订单制造耗时时间， 单位秒
//	maxPower：指订单当前生产节点所需要最大功耗。
//
//求解作业车间问题的目标是最大限度地缩短 makespan： 从作业的最早开始时间到最晚结束时间的时长。满足订单所需要的功耗满足最大限制，
//
//返回格式为：
//
//[
//  {
// 		 "orderId": 10,
//          "orderWorkId":111111,
//          "beginLocalDateTime": "2025-08-07 11:12:55",
//          "endLocalDateTime": "2025-08-07 11:22:55",
//    }
//]
//其中  orderId 指订单ID， orderWorkId 指订单制造节点标识， beginLocalDateTime指订单制造开始时间，endLocalDateTime 订单制造结束时间
//
//实现语言为 jdk21+ lombok+ ortools-java-9.14.6206.jar， 深度优化代码， 并提供测试用例，测试数据，测试结果 , 代码合并成一个java 文件