package com.olivia.peanut.aps.utils.scheduler.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SchedulerModel {


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Product {

    private String id;
    private String type; // M种产品类型
    private Map<String, Object> attributes; // N种属性
    private int requiredPower; // 生产所需功率(A)
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TimeSlot {

    private LocalTime startTime;
    private LocalTime endTime;
    private int maxPower; // 该时间段最大功率限制(B)
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DailyLimit {

    private String productType;
    private String attributeName;
    private Object attributeValue;
    private int minQuantity;
    private int maxQuantity;
    private List<TimeSlot> timeSlots; // 每天的时间段功率限制
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ProductionAssignment {

    private String orderId;
    private String productId;
    private int quantity;
    private TimeSlot timeSlot; // 分配的时间段
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DailyPlan {

    private LocalDate date;
    private List<ProductionAssignment> assignments;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ProductionPlan {

    private List<DailyPlan> dailyPlans;
    private Map<String, Integer> unassignedProducts;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Order {

    private String orderId;
    private Product product;
    private int quantity;
    private LocalDate specificProductionDate;
  }
}
