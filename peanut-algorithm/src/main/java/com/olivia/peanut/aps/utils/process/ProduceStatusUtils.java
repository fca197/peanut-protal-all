package com.olivia.peanut.aps.utils.process;

import static com.olivia.peanut.aps.utils.process.WeekUtils.getWeekInfo;

import cn.hutool.core.collection.CollUtil;
import com.olivia.peanut.aps.utils.process.entity.*;
import com.olivia.sdk.utils.JSON;
import com.olivia.sdk.utils.model.WeekInfo;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 生产状态计算工具类，用于计算生产流程项的状态时间信息
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE) // 私有构造函数，防止实例化
public final class ProduceStatusUtils {

  /**
   * 计算生产状态时间信息
   *
   * @param req 计算状态请求参数
   * @return 包含计算结果的响应对象
   */
  public static ComputeStatusRes computeStatusTime(ComputeStatusReq req) {
    // 使用JDK 21的var关键字简化局部变量声明
    var res = new ComputeStatusRes();
    List<ApsProduceProcessItemPojo> processItems = req.getApsProduceProcessItemPojoList();

    // 空集合快速返回
    if (CollUtil.isEmpty(processItems)) {
      return res;
    }

    // 处理周信息列表
    List<WeekInfo> weekInfos = req.getWeekInfoList();
    LocalDate beginDate = req.getBeginLocalDateTime().toLocalDate();

    // 使用JDK 8+的removeIf简化集合过滤
    weekInfos.removeIf(weekInfo -> weekInfo.getCurrentDate().isBefore(beginDate));

    // 设置工作日秒数，使用方法引用简化代码
    weekInfos.forEach(weekInfo -> weekInfo.setWorkSeconds(req.getDayWorkSecond()));

    // 处理流程项列表
    if (!req.getIsBegin()) {
      adjustProcessItems(processItems, req.getCurrentGoodsStatusId());
    }

    log.info("处理后的生产流程项列表: {}", JSON.toJSONString(processItems));

    // 计算执行时间并设置状态日期
    AtomicLong totalExecutionTime = new AtomicLong();
    processItems.forEach(item -> {
      long currentTime = totalExecutionTime.get();
      WeekInfo weekInfo = getWeekInfo(weekInfos, currentTime);

      // 使用Objects.requireNonNullElse简化空判断
      if (Objects.nonNull(weekInfo)) {
        item.setStatusLocalDate(weekInfo.getCurrentDate());
      }

      totalExecutionTime.addAndGet(item.getMachineUseTimeSecond());
    });

    return res.setApsProduceProcessItemPojoList(processItems);
  }

  /**
   * 调整流程项列表，将当前状态项移至首位
   *
   * @param processItems   流程项列表
   * @param targetStatusId 目标状态ID
   */
  private static void adjustProcessItems(List<ApsProduceProcessItemPojo> processItems, Long targetStatusId) {
    // 使用循环替代while循环，提高可读性
    for (int i = 0; i < processItems.size(); i++) {
      ApsProduceProcessItemPojo item = processItems.get(i);
      if (Objects.equals(item.getStatusId(), targetStatusId)) {
        // 将找到的元素移至首位
        processItems.add(0, processItems.remove(i));
        break;
      }
    }
  }
}
