package com.olivia.peanut.aps.utils.scheduler;

import com.google.ortools.sat.*;
import com.olivia.peanut.aps.utils.scheduler.model.SchedulerModel.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class AdvancedProductionPlanner {


  public static ProductionPlan planProduction(List<Order> orders, LocalDate startDate, LocalDate endDate, Map<LocalDate, List<DailyLimit>> dailyLimits) {

    // 1. 预处理数据
    long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
    List<LocalDate> allDates = startDate.datesUntil(endDate.plusDays(1)).toList();

    // 分离特定日期订单和普通订单
    Map<LocalDate, List<Order>> specificDateOrders = orders.stream().filter(o -> o.getSpecificProductionDate() != null)
        .collect(Collectors.groupingBy(Order::getSpecificProductionDate));

    List<Order> flexibleOrders = orders.stream().filter(o -> o.getSpecificProductionDate() == null).toList();

    // 2. 创建模型
    CpModel model = new CpModel();

    // 3. 创建变量
    // productId -> date -> timeSlot -> IntVar (分配数量)
    Map<String, Map<LocalDate, Map<TimeSlot, IntVar>>> assignmentVars = new HashMap<>();

    // 为每个产品、日期和时间段创建变量
    for (Order order : flexibleOrders) {
      String productId = order.getProduct().getId();
      Map<LocalDate, Map<TimeSlot, IntVar>> dateVars = new HashMap<>();

      for (LocalDate date : allDates) {
        List<DailyLimit> limits = dailyLimits.getOrDefault(date, Collections.emptyList());
        Map<TimeSlot, IntVar> timeSlotVars = new HashMap<>();

        // 找到适用于该产品的限制
        for (DailyLimit limit : limits) {
          if (matchesLimit(order.getProduct(), limit) && limit.getTimeSlots() != null) {
            for (TimeSlot slot : limit.getTimeSlots()) {
              IntVar var = model.newIntVar(0, order.getQuantity(), String.format("%s_%s_%s-%s", productId, date, slot.getStartTime(), slot.getEndTime()));
              timeSlotVars.put(slot, var);
            }
          }
        }

        if (!timeSlotVars.isEmpty()) {
          dateVars.put(date, timeSlotVars);
        }
      }

      if (!dateVars.isEmpty()) {
        assignmentVars.put(productId, dateVars);
      }
    }

    // 4. 添加约束

    // 4.1 每个订单的总分配量不超过其数量
    for (Order order : flexibleOrders) {
      String productId = order.getProduct().getId();
      if (assignmentVars.containsKey(productId)) {
        List<IntVar> allVars = new ArrayList<>();
        for (Map<TimeSlot, IntVar> timeSlotVars : assignmentVars.get(productId).values()) {
          allVars.addAll(timeSlotVars.values());
        }
        model.addLessOrEqual(LinearExpr.sum(allVars.toArray(new IntVar[0])), order.getQuantity());
      }
    }

    // 4.2 每日产品数量限制约束
    for (LocalDate date : allDates) {
      List<DailyLimit> limits = dailyLimits.getOrDefault(date, Collections.emptyList());

      for (DailyLimit limit : limits) {
        // 找到符合限制条件的产品
        List<Order> matchingOrders = flexibleOrders.stream().filter(o -> matchesLimit(o.getProduct(), limit)).toList();

        if (!matchingOrders.isEmpty()) {
          // 计算当天这类产品的总生产量
          List<IntVar> dailyProduction = new ArrayList<>();
          for (Order order : matchingOrders) {
            String productId = order.getProduct().getId();
            if (assignmentVars.containsKey(productId) && assignmentVars.get(productId).containsKey(date)) {
              dailyProduction.addAll(assignmentVars.get(productId).get(date).values());
            }
          }

          if (!dailyProduction.isEmpty()) {
            // 添加数量限制
            model.addLinearConstraint(LinearExpr.sum(dailyProduction.toArray(new IntVar[0])), limit.getMinQuantity(), limit.getMaxQuantity());
          }
        }
      }
    }

    // 4.3 每日时间段功率限制约束
    for (LocalDate date : allDates) {
      List<DailyLimit> limits = dailyLimits.getOrDefault(date, Collections.emptyList());

      for (DailyLimit limit : limits) {
        if (limit.getTimeSlots() != null) {
          for (TimeSlot slot : limit.getTimeSlots()) {
            // 计算该时间段所有产品的总功率
            List<IntVar> powerVars = new ArrayList<>();
            List<Integer> powerCoefficients = new ArrayList<>();

            for (Order order : flexibleOrders) {
              if (matchesLimit(order.getProduct(), limit)) {
                String productId = order.getProduct().getId();
                if (assignmentVars.containsKey(productId) && assignmentVars.get(productId).containsKey(date) && assignmentVars.get(productId).get(date)
                    .containsKey(slot)) {

                  powerVars.add(assignmentVars.get(productId).get(date).get(slot));
                  powerCoefficients.add(order.getProduct().getRequiredPower());
                }
              }
            }

            if (!powerVars.isEmpty()) {
              // 添加功率限制: sum(quantity * power) <= maxPower
              model.addLessOrEqual(LinearExpr.weightedSum(powerVars.toArray(new IntVar[0]), powerCoefficients.stream().mapToLong(Long::valueOf).toArray()),
                  slot.getMaxPower());
            }
          }
        }
      }
    }

    // 5. 设置目标：最大化分配量
    List<IntVar> allVars = new ArrayList<>();
    for (Map<LocalDate, Map<TimeSlot, IntVar>> dateVars : assignmentVars.values()) {
      for (Map<TimeSlot, IntVar> timeSlotVars : dateVars.values()) {
        allVars.addAll(timeSlotVars.values());
      }
    }
    model.maximize(LinearExpr.sum(allVars.toArray(new IntVar[0])));

    // 6. 求解
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);

    // 7. 处理结果
    ProductionPlan plan = new ProductionPlan();
    List<DailyPlan> dailyPlans = new ArrayList<>();
    Map<String, Integer> unassigned = new HashMap<>();

    if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
      // 7.1 处理特定日期订单
      for (Map.Entry<LocalDate, List<Order>> entry : specificDateOrders.entrySet()) {
        LocalDate date = entry.getKey();
        List<ProductionAssignment> assignments = new ArrayList<>();

        for (Order order : entry.getValue()) {
          // 为特定日期订单分配默认时间段（第一个可用时间段）
          List<DailyLimit> limits = dailyLimits.getOrDefault(date, Collections.emptyList());
          TimeSlot defaultSlot = findFirstMatchingTimeSlot(order.getProduct(), limits);

          assignments.add(new ProductionAssignment(order.getOrderId(), order.getProduct().getId(), order.getQuantity(), defaultSlot));
        }

        dailyPlans.add(new DailyPlan(date, assignments));
      }

      // 7.2 处理普通订单
      for (LocalDate date : allDates) {
        List<ProductionAssignment> assignments = new ArrayList<>();

        for (Order order : flexibleOrders) {
          String productId = order.getProduct().getId();
          if (assignmentVars.containsKey(productId) && assignmentVars.get(productId).containsKey(date)) {
            for (Map.Entry<TimeSlot, IntVar> entry : assignmentVars.get(productId).get(date).entrySet()) {
              int assigned = (int) solver.value(entry.getValue());
              if (assigned > 0) {
                assignments.add(new ProductionAssignment(order.getOrderId(), productId, assigned, entry.getKey()));
              }
            }
          }
        }

        if (!assignments.isEmpty()) {
          dailyPlans.add(new DailyPlan(date, assignments));
        }
      }

      // 7.3 计算未分配的产品
      for (Order order : flexibleOrders) {
        String productId = order.getProduct().getId();
        int totalAssigned = 0;

        if (assignmentVars.containsKey(productId)) {
          for (Map<TimeSlot, IntVar> timeSlotVars : assignmentVars.get(productId).values()) {
            for (IntVar var : timeSlotVars.values()) {
              totalAssigned += (int) solver.value(var);
            }
          }
        }

        int remaining = order.getQuantity() - totalAssigned;
        if (remaining > 0) {
          unassigned.merge(productId, remaining, Integer::sum);
        }
      }

      plan.setDailyPlans(dailyPlans);
      plan.setUnassignedProducts(unassigned);
    } else {
      // 处理无解情况

      plan.setDailyPlans(Collections.emptyList());
      plan.setUnassignedProducts(orders.stream().collect(Collectors.toMap(o -> o.getProduct().getId(), Order::getQuantity, Integer::sum)));
    }

    return plan;
  }

  private static boolean matchesLimit(Product product, DailyLimit limit) {
    // 检查产品类型
    if (!product.getType().equals(limit.getProductType())) {
      return false;
    }

    // 检查属性值
    Object productValue = product.getAttributes().get(limit.getAttributeName());
    return productValue != null && productValue.equals(limit.getAttributeValue());
  }

  private static TimeSlot findFirstMatchingTimeSlot(Product product, List<DailyLimit> limits) {
    for (DailyLimit limit : limits) {
      if (matchesLimit(product, limit) && limit.getTimeSlots() != null && !limit.getTimeSlots().isEmpty()) {
        return limit.getTimeSlots().getFirst();
      }
    }
    return new TimeSlot(LocalTime.of(8, 0), LocalTime.of(17, 0), Integer.MAX_VALUE);
  }
}