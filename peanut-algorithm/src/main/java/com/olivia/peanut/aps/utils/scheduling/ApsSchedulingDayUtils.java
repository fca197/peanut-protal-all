package com.olivia.peanut.aps.utils.scheduling;

import static com.olivia.peanut.aps.utils.scheduling.model.ApsSchedulingDayConfigItemConfigBizTypeEnum.def;

import cn.hutool.core.collection.CollUtil;
import com.olivia.peanut.aps.utils.scheduling.model.*;
import com.olivia.sdk.utils.$;
import com.olivia.sdk.utils.RunUtils;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 排程日期处理工具类，负责处理每日排程中的订单与资源匹配逻辑。
 * <p>
 * 主要功能包括订单与房间状态的匹配、排程配置应用及结果聚合。
 */
@Slf4j
public final class ApsSchedulingDayUtils {

  /**
   * 按房间ID和状态ID分组整理排程结果。
   *
   * @param req 排程请求参数
   * @return 以"房间ID-状态ID"为键，排程详情列表为值的映射
   */
  public static Map<String, List<ApsSchedulingDayConfigVersionDetailDto>> orderRoomStatusMap(ApsSchedulingDayOrderRoomReq req) {
    return orderRoomStatus(req).getApsSchedulingDayConfigVersionDetailDtoList().stream()
        .collect(Collectors.groupingBy(detail -> detail.getRoomId() + "-" + detail.getStatusId()));
  }

  /**
   * 处理订单与房间状态的匹配逻辑，生成排程结果。
   *
   * @param req 排程请求参数，包含订单列表和排程配置
   * @return 包含排程详情的响应对象
   */
  public static ApsSchedulingDayOrderRoomRes orderRoomStatus(ApsSchedulingDayOrderRoomReq req) {
    // 校验订单列表非空
    $.requireNonNullCanIgnoreException(req.getIssueItemList(), "排程订单不能为空");
    // 初始化线程安全的结果列表
    List<ApsSchedulingDayConfigVersionDetailDto> resultDetails = Collections.synchronizedList(new ArrayList<>());
    ApsSchedulingDayConfigDto schedulingConfig = req.getSchedulingDayConfigDto();
    if (log.isDebugEnabled()) {
      log.debug("处理排程 - 订单数量: {}, 排程配置ID: {}", req.getIssueItemList().size(), schedulingConfig.getId());
    }
    // 处理空配置情况
    List<ApsSchedulingDayConfigItemDto> configItems = schedulingConfig.getSchedulingDayConfigItemDtoList();
    if (CollUtil.isEmpty(configItems)) {
      configItems = List.of(new ApsSchedulingDayConfigItemDto().setConfigBizType(def.name()));
    }
    // 按房间和状态分组配置项，并行处理每组
    Map<String, List<ApsSchedulingDayConfigItemDto>> configGroupMap = configItems.stream()
        .collect(Collectors.groupingBy(item -> item.getRoomId() + "-" + item.getStatusId()));
    // 构建并执行匹配任务
    List<Runnable> matchingTasks = configGroupMap.values().stream()
        .map(apsSchedulingDayConfigItemDtoList -> createMatchingTask(req.getIssueItemList(), apsSchedulingDayConfigItemDtoList, resultDetails)).toList();
    RunUtils.run("orderRoomStatus-" + req.getSchedulingDayId(), matchingTasks);
    // 设置排程日期ID并返回结果
    resultDetails.forEach(detail -> detail.setSchedulingDayId(req.getSchedulingDayId()));
    return new ApsSchedulingDayOrderRoomRes().setApsSchedulingDayConfigVersionDetailDtoList(resultDetails);
  }

  /**
   * 创建订单与配置项的匹配任务。
   *
   * @param orderItems    订单列表
   * @param configItems   配置项列表
   * @param resultDetails 存储匹配结果的列表
   * @return 可执行的匹配任务
   */
  private static Runnable createMatchingTask(List<ApsSchedulingIssueItemDto> orderItems, List<ApsSchedulingDayConfigItemDto> configItems,
      List<ApsSchedulingDayConfigVersionDetailDto> resultDetails) {
    return () -> matchOrderItems(orderItems, configItems, resultDetails);
  }

  /**
   * 匹配订单与配置项，生成排程详情。
   *
   * @param orderItems    订单列表
   * @param configItems   配置项列表
   * @param resultDetails 存储匹配结果的列表
   */
  private static void matchOrderItems(List<ApsSchedulingIssueItemDto> orderItems, List<ApsSchedulingDayConfigItemDto> configItems,
      List<ApsSchedulingDayConfigVersionDetailDto> resultDetails) {
    int loopIndex = 0;
    int sortIndex = 1;
    List<ApsSchedulingIssueItemDto> failedMatches = new ArrayList<>();
    boolean shouldContinue;
    // 复制订单列表以避免并发修改问题
    List<ApsSchedulingIssueItemDto> remainingOrders = new ArrayList<>(orderItems);
    do {
      loopIndex++;
      shouldContinue = false;
      List<ApsSchedulingDayConfigVersionDetailDto> currentLoopDetails = new ArrayList<>();
      List<ApsSchedulingIssueItemDto> successfulMatches = new ArrayList<>();
      for (ApsSchedulingDayConfigItemDto configItem : configItems) {
        ApsSchedulingDayConfigItemConfigBizTypeEnum bizType = ApsSchedulingDayConfigItemConfigBizTypeEnum.of(configItem.getConfigBizType());
        // 处理默认类型：匹配所有订单
        if (bizType.equals(def)) {
          successfulMatches.addAll(remainingOrders);
          remainingOrders.clear();
        } else {
          // 处理特定业务类型的匹配逻辑
          successfulMatches = processSpecificBizType(bizType, configItem, remainingOrders, failedMatches);
        }
        // 生成匹配成功的详情记录
        sortIndex = generateSuccessDetails(configItem, successfulMatches, loopIndex, sortIndex, currentLoopDetails, resultDetails, shouldContinue);
        successfulMatches.clear();
        // 更新当前循环是否匹配充足的状态
        updateLoopEnoughStatus(currentLoopDetails, configItems, bizType);
      }
      // 检查是否需要继续下一轮匹配
      shouldContinue = !currentLoopDetails.isEmpty();
    } while (shouldContinue);
    // 处理最终未匹配成功的订单
    processUnmatchedOrders(configItems.getFirst(), remainingOrders, failedMatches, loopIndex, sortIndex, resultDetails);
  }

  /**
   * 处理特定业务类型的订单匹配。
   *
   * @param bizType         业务类型枚举
   * @param configItem      配置项
   * @param remainingOrders 剩余未匹配订单
   * @param failedMatches   匹配失败的订单
   * @return 更新后的匹配成功列表
   */
  private static List<ApsSchedulingIssueItemDto> processSpecificBizType(ApsSchedulingDayConfigItemConfigBizTypeEnum bizType, ApsSchedulingDayConfigItemDto configItem,
      List<ApsSchedulingIssueItemDto> remainingOrders, List<ApsSchedulingIssueItemDto> failedMatches) {
    List<ApsSchedulingIssueItemDto> successfulMatches = new ArrayList<>();
    Long bizId = configItem.getConfigBizId();
    long maxMatches = configItem.getConfigBizNum();
    // 将上一轮失败的订单重新加入匹配池
    remainingOrders.addAll(failedMatches);
    failedMatches.clear();
    // 匹配订单直到达到最大数量或无更多订单
    while (CollUtil.isNotEmpty(remainingOrders) && successfulMatches.size() < maxMatches) {
      ApsSchedulingIssueItemDto order = remainingOrders.removeFirst();
      if (isOrderMatchBizType(order, bizType, bizId)) {
        successfulMatches.add(order);
      } else {
        failedMatches.add(order);
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("业务ID: {} 匹配成功: {} 个, 匹配失败: {} 个", bizId, successfulMatches.size(), failedMatches.size());
    }
    return successfulMatches;
  }

  /**
   * 检查订单是否匹配特定业务类型。
   *
   * @param order   订单对象
   * @param bizType 业务类型
   * @param bizId   业务ID
   * @return 如果匹配返回true，否则返回false
   */
  private static boolean isOrderMatchBizType(ApsSchedulingIssueItemDto order, ApsSchedulingDayConfigItemConfigBizTypeEnum bizType, Long bizId) {
    return switch (bizType) {
      case sale -> order.getSaleConfigIdList().contains(bizId);
      case project -> order.getProjectConfigIdList().contains(bizId);
      case bom -> order.getBomIdList().contains(bizId);
      default -> false;
    };
  }

  /**
   * 生成匹配成功的详情记录。
   *
   * @param configItem         配置项
   * @param successfulMatches  匹配成功的订单
   * @param loopIndex          当前循环索引
   * @param sortIndex          排序索引
   * @param currentLoopDetails 当前循环的详情列表
   * @param resultDetails      总结果列表
   * @param shouldContinue     是否继续循环的标志
   * @return 更新后的排序索引
   */
  private static int generateSuccessDetails(ApsSchedulingDayConfigItemDto configItem, List<ApsSchedulingIssueItemDto> successfulMatches, int loopIndex, int sortIndex,
      List<ApsSchedulingDayConfigVersionDetailDto> currentLoopDetails, List<ApsSchedulingDayConfigVersionDetailDto> resultDetails, boolean shouldContinue) {
    for (ApsSchedulingIssueItemDto order : successfulMatches) {
      shouldContinue = true;
      ApsSchedulingDayConfigVersionDetailDto detail = new ApsSchedulingDayConfigVersionDetailDto();
      detail.setRoomId(configItem.getRoomId()).setStatusId(configItem.getStatusId()).setLoopIndex(loopIndex).setIsMatch(true).setOrderId(order.getOrderId())
          .setOrderNo(order.getOrderNo()).setConfigBizId(configItem.getConfigBizId()).setConfigBizType(configItem.getConfigBizType())
          .setConfigBizName(configItem.getConfigBizName()).setLoopEnough(true).setSortIndex(sortIndex++);
      currentLoopDetails.add(detail);
    }
    resultDetails.addAll(currentLoopDetails);
    return sortIndex;
  }

  /**
   * 更新当前循环的匹配充足状态。
   *
   * @param currentLoopDetails 当前循环的详情列表
   * @param configItems        配置项列表
   * @param bizType            业务类型
   */
  private static void updateLoopEnoughStatus(List<ApsSchedulingDayConfigVersionDetailDto> currentLoopDetails, List<ApsSchedulingDayConfigItemDto> configItems,
      ApsSchedulingDayConfigItemConfigBizTypeEnum bizType) {
    boolean isLoopEnough;
    if (def.equals(bizType)) {
      isLoopEnough = true;
    } else {
      long totalRequired = configItems.stream().mapToLong(ApsSchedulingDayConfigItemDto::getConfigBizNum).sum();
      isLoopEnough = currentLoopDetails.size() == totalRequired;
    }
    currentLoopDetails.forEach(detail -> detail.setLoopEnough(isLoopEnough));
  }

  /**
   * 处理最终未匹配成功的订单。
   *
   * @param configItem      配置项
   * @param remainingOrders 剩余订单
   * @param failedMatches   匹配失败的订单
   * @param loopIndex       循环索引
   * @param sortIndex       排序索引
   * @param resultDetails   结果列表
   */
  private static void processUnmatchedOrders(ApsSchedulingDayConfigItemDto configItem, List<ApsSchedulingIssueItemDto> remainingOrders,
      List<ApsSchedulingIssueItemDto> failedMatches, int loopIndex, int sortIndex, List<ApsSchedulingDayConfigVersionDetailDto> resultDetails) {
    // 合并所有未匹配的订单
    remainingOrders.addAll(failedMatches);
    for (ApsSchedulingIssueItemDto order : remainingOrders) {
      ApsSchedulingDayConfigVersionDetailDto detail = new ApsSchedulingDayConfigVersionDetailDto();
      detail.setRoomId(configItem.getRoomId()).setStatusId(configItem.getStatusId()).setLoopIndex(loopIndex).setIsMatch(false).setOrderId(order.getOrderId())
          .setOrderNo(order.getOrderNo()).setConfigBizId(configItem.getConfigBizId()).setConfigBizType(configItem.getConfigBizType())
          .setConfigBizName(configItem.getConfigBizName()).setLoopEnough(false).setSortIndex(sortIndex++);
      resultDetails.add(detail);
    }
  }
}
