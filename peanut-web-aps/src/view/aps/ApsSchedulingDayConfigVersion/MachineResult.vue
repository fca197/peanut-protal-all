<script setup lang="ts">
import {gantt} from 'dhtmlx-gantt';
import 'dhtmlx-gantt/codebase/dhtmlxgantt.css';

import {useRoute} from 'vue-router';
import {downloadFilePost, postResultInfo, postResultInfoList,} from '@@/utils/common-js.ts';
import {ApsMachine, queryApsMachineList,} from '@v/aps/ApsMachine/ApsMachineType.ts';
import {initGantt, zoomConfig,} from '@v/aps/ApsSchedulingDayConfigVersion/gatt.ts';

const zoomLevel = ref<string>('day');

const machineList = ref<ApsMachine[]>([]);
const machineUseRate = ref<any>({});
// 获取当前路由信息
const route = useRoute();

const loadingData = ref<boolean>(true);
// 从路由参数中获取id
const id = route.params.id as string;
console.info('id ', id);
const factoryId = route.params.factoryId as string;
console.info('factoryId ', factoryId);

const queryMachineUserRate = () => {
  postResultInfoList(
    '/apsSchedulingDayConfigVersionDetailMachineUseTime/queryList',
    {data: {schedulingDayId: id}}
  ).then((r) => {
    // log(t)
    r.forEach((tt) => {
      machineUseRate.value[tt.machineId] = tt;
    });
  });
};
onMounted(() => {
  queryDetailList();
  queryMachineUserRate();
  loadGantt();
});
const queryDetailList = () => {
  return queryApsMachineList(factoryId).then((r) => {
    r = r
    .sort((a, b) => {
      return parseInt(b.sortIndex) - parseInt(a.sortIndex);
    })
    .reverse();
    machineList.value = r;
    console.info('queryApsMachineList', machineList.value, factoryId);
  });
};

const changeZoomLevel = (value: string) => {
  zoomConfig.levels.filter((t) => t.name === value)[0].name;
  loadGantt();
};

const loadGantt = () => {
  loadingData.value = true;
  initGantt(gantt, zoomConfig, zoomLevel);

  postResultInfo('/apsSchedulingDayConfigVersionDetailMachine/queryList', {
    data: {
      schedulingDayId: id,
    },
  })
  .then((r) => {
    let ll = r.data.dataList.sort((a, b) =>
      a.beginDateTime.localeCompare(b.beginDateTime)
    );
    ll.forEach((tt) => {
      tt.start_date = tt.beginDateTime;
      tt.end_date = tt.endDateTime;
      tt.open = true;
    });

    const groups = {};
    ll.forEach((item) => {
      if (!groups[item.orderId]) {
        groups[item.orderId] = [];
      }
      groups[item.orderId].push(item);
    });

    // 2. 创建父节点数组，并建立父子关系
    const parentNodes = [];
    Object.keys(groups).forEach((orderId) => {
      // 创建父节点
      const parentId = `parent_${orderId}`;
      const lt = groups[orderId];
      const parentNode = {
        id: parentId, // 父节点唯一ID
        orderId: orderId, // 关联的orderId
        machineName: lt[0].orderNo,
        isParent: true, // 标记为父节点
        start_date: lt[0].start_date,
        end_date: lt[lt.length - 1].end_date,
        open: true, // 默认展开
        parentId: null, // 顶级节点父ID为null
      };
      // 为当前分组的所有子节点设置parentId
      lt.forEach((child) => {
        child.parent = parentId;
      });

      parentNodes.push(parentNode);
    });
    // 3. 合并父节点和子节点，形成新的数组
    const result = [...parentNodes, ...ll];
    result.forEach((t) => {
      t.text = t.machineName;
    });
    console.log(' result ', result);
    gantt.parse({
      tasks: result,
    });

    parentNodes.forEach((parentNode) => {
      gantt.close(parentNode.id);
    });
    })
  .then(() => {
    loadingData.value = false;
  });
};
const downloadDataList = () => {
  var timeInterval = zoomConfig.levels.filter(
    (tt) => tt.name === zoomLevel.value
  )[0].timeInterval;
  downloadFilePost(
    '/apsSchedulingDayConfigVersionDetailMachine/exportQueryPageList',
    {
      timeSpan: timeInterval,
      data: {
        schedulingDayId: id,
      },
    },
    '排程结果.xlsx'
  );
};
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form :inline="true">
        <el-form-item label="缩放纬度">
          <el-select
            v-model="zoomLevel"
            style="width: 200px"
            @change="changeZoomLevel"
          >
            <el-option
              v-for="l in zoomConfig.levels"
              :key="l.name"
              :value="l.name"
              :label="l.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="downloadDataList">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="search-wrapper" shadow="never" style="height: 800px">
      <div
        id="gantt_here"
        v-loading="loadingData"
        style="width: 100%; height: 750px"
      />
    </el-card>

    <el-card class="search-wrapper" shadow="never">
      <h2>机器使用率</h2>
      <el-table :data="machineList" v-loading="loadingData">
        <el-table-column label="机器名称" prop="machineName"></el-table-column>
        <el-table-column label="制造数量">
          <template #default="scope">
            {{ machineUseRate[scope.row.id]['makeProduceCount'] }}
          </template>
        </el-table-column>
        <el-table-column label="制造耗时">
          <template #default="scope">
            {{ machineUseRate[scope.row.id]['useTime'] }}
          </template>
        </el-table-column>
        <el-table-column label="使用率">
          <template #default="scope">
            {{ machineUseRate[scope.row.id]['useUsageRate'] }}%
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.headerItem {
  display: inline-flex;
}

.zzljDiv {
  overflow-x: scroll;
  margin: 5px;
}

.zzljDiv > div > div.header {
  border-right: 1px solid #00d3e9;
}

div.orderNoInfo {
  line-height: 25px;
  height: 25px;
  border: 1px solid #dddddd;
}

div.orderNoDiv {
  line-height: 30px;
}

.scrollDiv {
  overflow-x: scroll;
}
</style>
