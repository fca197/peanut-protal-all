package com.olivia.peanut.aps.utils.process;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.google.ortools.sat.*;
import com.olivia.peanut.aps.utils.process.entity.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProduceProcessUtils {

  public static ProduceProcessComputeRes compute(ProduceProcessComputeReq req) {
    ProduceProcessComputeRes res = new ProduceProcessComputeRes().setProcessComputeOrderRes(List.of());
    List<ProduceOrder> produceOrderList = req.getProduceOrderList();
//    List<WeekInfo> weekInfoList = req.getWeekInfoList();
    if (CollUtil.isEmpty(produceOrderList)) {
      if (log.isDebugEnabled()) {
        log.debug("compute return null   order is null :   {}", CollUtil.isEmpty(produceOrderList));
      }
      return res;
    }
    long nowTime = req.getProduceStartTime().toEpochSecond(ZoneOffset.of("+8"));
    Set<Long> machineIdSet = produceOrderList.stream().map(ProduceOrder::getOrderMachineList).flatMap(Collection::stream).map(ProduceOrderMachine::getMachineId)
        .collect(Collectors.toSet());
    if (log.isDebugEnabled()) {
      log.debug("machineIdSet :{}", machineIdSet);
    }
    long horizon = produceOrderList.stream().map(ProduceOrder::getOrderMachineList).flatMap(Collection::stream).mapToLong(ProduceOrderMachine::getUseTime).sum();
    if (log.isDebugEnabled()) {
      log.debug("horizon :{}", horizon);
    }
    CpModel model = new CpModel();
    Map<List<Integer>, ProduceTaskType> allTasks = new HashMap<>();
    Map<Long, List<IntervalVar>> machineToIntervals = new HashMap<>();
    for (int jobID = 0; jobID < produceOrderList.size(); ++jobID) {
      ProduceOrder produceOrder = produceOrderList.get(jobID);
      List<ProduceOrderMachine> job = produceOrder.getOrderMachineList();
      for (int taskID = 0; taskID < job.size(); ++taskID) {
        ProduceOrderMachine task = job.get(taskID);
        String suffix = "_" + jobID + "_" + taskID;
        ProduceTaskType taskType = new ProduceTaskType();
        taskType.setStart(model.newIntVar(0, horizon, "start" + suffix)).setEnd(model.newIntVar(0, horizon, "end" + suffix))
            .setInterval(model.newIntervalVar(taskType.getStart(), LinearExpr.constant(task.getUseTime()), taskType.getEnd(), "interval" + suffix));
        List<Integer> key = Arrays.asList(jobID, taskID);
        allTasks.put(key, taskType);
        machineToIntervals.computeIfAbsent(task.getMachineId(), (Long k) -> new ArrayList<>());
        machineToIntervals.get(task.getMachineId()).add(taskType.getInterval());
      }
    }
    for (Long machine : machineIdSet) {
      List<IntervalVar> list = machineToIntervals.get(machine);
      model.addNoOverlap(list);
    }
    for (int jobID = 0; jobID < produceOrderList.size(); ++jobID) {
      List<ProduceOrderMachine> job = produceOrderList.get(jobID).getOrderMachineList();
      for (int taskID = 0; taskID < job.size() - 1; ++taskID) {
        List<Integer> prevKey = Arrays.asList(jobID, taskID);
        List<Integer> nextKey = Arrays.asList(jobID, taskID + 1);
        model.addGreaterOrEqual(allTasks.get(nextKey).getStart(), allTasks.get(prevKey).getEnd());
      }
    }
    // Makespan objective.
    IntVar objVar = model.newIntVar(0, horizon, "makespan");
    List<IntVar> ends = new ArrayList<>();
    for (int jobID = 0; jobID < produceOrderList.size(); ++jobID) {
      List<ProduceOrderMachine> job = produceOrderList.get(jobID).getOrderMachineList();
      List<Integer> key = Arrays.asList(jobID, job.size() - 1);
      ends.add(allTasks.get(key).getEnd());
    }
    model.addMaxEquality(objVar, ends);
    model.minimize(objVar);
    // Creates a solver and solves the model.
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);
    if (log.isDebugEnabled()) {
      log.debug("{}", System.currentTimeMillis() - nowTime);
    }
    if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
      res.setMaxUseSecond((long) solver.objectiveValue());
      log.info("Solution:");
      // Create one list of assigned tasks per machine.
      Map<Long, List<ProduceAssignedTask>> assignedJobs = new HashMap<>();
      for (int jobID = 0; jobID < produceOrderList.size(); ++jobID) {
        ProduceOrder produceOrder = produceOrderList.get(jobID);
        List<ProduceOrderMachine> job = produceOrder.getOrderMachineList();
        for (int taskID = 0; taskID < job.size(); ++taskID) {
          ProduceOrderMachine task = job.get(taskID);
          List<Integer> key = Arrays.asList(jobID, taskID);
          ProduceAssignedTask assignedTask = new ProduceAssignedTask(produceOrder.getOrderId(), taskID, (int) solver.value(allTasks.get(key).getStart()),
              task.getUseTime(), task.getGoodsStatusId());
          assignedJobs.computeIfAbsent(task.getMachineId(), (Long k) -> new ArrayList<>());
          assignedJobs.get(task.getMachineId()).add(assignedTask);
        }
      }
      List<ProduceProcessComputeOrderRes> processComputeOrderRes = new ArrayList<>();
      assignedJobs.forEach((k, v) -> {
        List<ProduceProcessComputeOrderRes> list = v.stream().map(t -> {
          LocalDateTime beginLocalDateTime = LocalDateTimeUtil.of((nowTime + t.getStart()) * 1000);
          return new ProduceProcessComputeOrderRes() //
              .setBeginLocalDateTime(beginLocalDateTime)  //
              .setEndLocalDateTime(LocalDateTimeUtil.of(LocalDateTimeUtil.toEpochMilli(beginLocalDateTime) + t.getDuration() * 1000)) //
              .setMachineId(k).setOrderId(t.getJobID()).setStartSecond((long) (t.getStart())).setEndSecond(t.getDuration() + t.getStart()).setUseTime(t.getDuration())
              .setStatusId(t.getStatusId());
        }).toList();
        processComputeOrderRes.addAll(list);
      });
      res.setProcessComputeOrderRes(processComputeOrderRes);
      if (log.isDebugEnabled()) {
        // Create per machine output lines.
        StringBuilder output = new StringBuilder();
        for (Long machine : machineIdSet) {
          // Sort by starting time.
          Collections.sort(assignedJobs.get(machine));
          StringBuilder solLineTasks = new StringBuilder("Machine " + machine + ": ");
          StringBuilder solLine = new StringBuilder("           ");
          for (ProduceAssignedTask assignedTask : assignedJobs.get(machine)) {
            String name = "job_" + assignedTask.getJobID() + "_task_" + assignedTask.getTaskID();
            // Add spaces to output to align columns.
            solLineTasks.append(String.format("%-15s", name));
            String solTmp = "[" + assignedTask.getStart() + "," + (assignedTask.getStart() + assignedTask.getDuration()) + "]";
            // Add spaces to output to align columns.
            solLine.append(String.format("%-15s", solTmp));
          }
          output.append(solLineTasks).append("%n");
          output.append(solLine).append("%n");
        }
        log.debug("Optimal Schedule Length: {}", solver.objectiveValue());
        log.debug("output:{}", output);
      }
    } else {
      log.error("No solution found.");
    }
    // Statistics.
    if (log.isDebugEnabled()) {
      log.debug("Statistics");
      log.debug("  conflicts: {}", solver.numConflicts());
      log.debug("  branches : {}", solver.numBranches());
      log.debug("  wall time: {}", solver.wallTime());
    }
    return res;
  }
}
