<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsSchedulingDayConfigVersionDetailMachineUseTimeAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsSchedulingDayConfigVersionDetailMachineUseTime} from './ApsSchedulingDayConfigVersionDetailMachineUseTimeType.ts';

const dtoUrl = ref<string>(
  '/apsSchedulingDayConfigVersionDetailMachineUseTime'
);
const documentTitle = ref<string>('排程结果机器使用率');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsSchedulingDayConfigVersionDetailMachineUseTime>({
  schedulingDayId: undefined,
  machineId: undefined,
  useTime: undefined,
  useUsageRate: undefined,
  makeProduceCount: undefined,
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
const dataList = ref<ApsSchedulingDayConfigVersionDetailMachineUseTime[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'id', showName: '序号'},
  {fieldName: 'schedulingDayId', showName: '排程ID'},
  {fieldName: 'machineId', showName: '机器ID'},
  {fieldName: 'useTime', showName: '耗时'},
  {fieldName: 'useUsageRate', showName: '使用率'},
  {fieldName: 'makeProduceCount', showName: '商品数'},
]);

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
const handleSelectionChange = (
  val: ApsSchedulingDayConfigVersionDetailMachineUseTime[]
) => {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
};

// 页面加载事件
onMounted(() => {
  getDataList();
});
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="排程ID" prop="schedulingDayId">
          <el-input
            v-model="queryForm.schedulingDayId"
            clearable
            placeholder="请输入排程ID"
          />
        </el-form-item>
        <el-form-item label="机器ID" prop="machineId">
          <el-input
            v-model="queryForm.machineId"
            clearable
            placeholder="请输入机器ID"
          />
        </el-form-item>
        <el-form-item label="耗时" prop="useTime">
          <el-input
            v-model="queryForm.useTime"
            clearable
            placeholder="请输入耗时"
          />
        </el-form-item>
        <el-form-item label="使用率" prop="useUsageRate">
          <el-input
            v-model="queryForm.useUsageRate"
            clearable
            placeholder="请输入使用率"
          />
        </el-form-item>
        <el-form-item label="商品数" prop="makeProduceCount">
          <el-input
            v-model="queryForm.makeProduceCount"
            clearable
            placeholder="请输入商品数"
          />
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
        <ElTableColumn fixed="right" label="操作" width="150px">
          <template #default="scope">
            <el-button icon="edit" type="warning" @click="editData(scope.row)">
              编辑
            </el-button>
          </template>
        </ElTableColumn>
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
