<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsRollingForecastFactoryCapacityAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsRollingForecastFactoryCapacity} from './ApsRollingForecastFactoryCapacityType.ts';
import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';

const dtoUrl = ref<string>('/apsRollingForecastFactoryCapacity');
const documentTitle = ref<string>('滚动预测');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsRollingForecastFactoryCapacity>({
  factoryId: undefined,
  year: undefined,
  month: undefined,
  day01: undefined,
  day02: undefined,
  day03: undefined,
  day04: undefined,
  day05: undefined,
  day06: undefined,
  day07: undefined,
  day08: undefined,
  day09: undefined,
  day10: undefined,
  day11: undefined,
  day12: undefined,
  day13: undefined,
  day14: undefined,
  day15: undefined,
  day16: undefined,
  day17: undefined,
  day18: undefined,
  day19: undefined,
  day20: undefined,
  day21: undefined,
  day22: undefined,
  day23: undefined,
  day24: undefined,
  day25: undefined,
  day26: undefined,
  day27: undefined,
  day28: undefined,
  day29: undefined,
  day30: undefined,
  day31: undefined,
  capacity: undefined,
  id: undefined,
});

// 表格选中的id
const multipleSelection = ref<(string | undefined)[]>([]);

// 表格
// const dataTableRef = ref<InstanceType<typeof ElTable> | null>(null)
const dataTableRef = ref({});
// 表格操作头
const tableBarRef = ref<InstanceType<typeof TableBar> | null>(null);
// 表格相关
const dataList = ref<ApsRollingForecastFactoryCapacity[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([]);

// 获取表格内数据
const getDataList = () => {
  const req = {
    pageSize: currentPageSize.value,
    pageNum: currentPageNum.value,
    data: queryForm.value,
  };
  console.info('getDataList {}', req);
  postResultInfo(`${dtoUrl.value}/queryPageList`, req).then((t) => {
    dataList.value = t.data.dataList;
    tableTotal.value = Number.parseInt(t.data.total);
    headerList.value = t.data.headerList;
  });
};

// table点击事件
const editData = (data: any) => {
  // console.info("data ", data)
  tableBarRef.value?.showEditDialog(data.id);
};
// 页面条数变更事件
const handleSizeChange = (val: number) => {
  currentPageSize.value = val;
  getDataList();
};
// 页面变更事件
const handleCurrentChange = (val: number) => {
  currentPageNum.value = val;
  getDataList();
};
// 表格选中事件
const handleSelectionChange = (val: ApsRollingForecastFactoryCapacity[]) => {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
};

const factoryList = ref<Factory[]>([]);
const yearList = ref<number[]>([]);
const monthList = ref<number[]>(Array.from({length: 12}, (_, i) => i + 1));
// 页面加载事件
onMounted(() => {
  getDataList();
  queryFactoryList().then((r) => (factoryList.value = r));
  const fullYear = new Date().getFullYear();
  for (let i = -2; i < 2; i++) {
    yearList.value.push(fullYear + i);
  }
});
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="工厂" prop="factoryId">
          <el-select
            v-model="queryForm.factoryId"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="f in factoryList"
              :key="f.id"
              :label="f.factoryName"
              :value="f.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="年份" prop="year">
          <el-select v-model="queryForm.year" clearable style="width: 200px">
            <el-option v-for="y in yearList" :label="y" :value="y" :key="y"/>
          </el-select>
        </el-form-item>
        <el-form-item label="月份" prop="month">
          <el-select v-model="queryForm.month" clearable style="width: 200px">
            <el-option v-for="y in monthList" :label="y" :value="y" :key="y"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="search" @click="getDataList">
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <TableBar
        :document-title="documentTitle"
        :add-component="AddEditFormVue"
        :refresh-list="getDataList"
        :data-table-ref="dataTableRef"
        :multiple-selection="multipleSelection"
        ref="tableBarRef"
        :data-batch-delete-url="dataBatchDeleteUrl"
        :dialog-with="700"
      />
      <ElTable
        ref="dataTableRef"
        :data="dataList"
        stripe
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection"/>
        <ElTableColumn
          v-for="h in headerList"
          :key="h.fieldName"
          :label="h.showName"
          :min-width="h.width"
          :prop="h.fieldName"
        />
      </ElTable>
      <el-row class="paginationDiv">
        <el-pagination
          background
          v-model:current-page="currentPageNum"
          v-model:page-size="currentPageSize"
          layout="total, sizes, prev, pager, next"
          :total="tableTotal"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-row>
    </el-card>
  </div>
</template>

<style lang="scss" scoped></style>
