<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item
          label="生产路径 Id aps_produce_process"
          prop="produceProcessId"
        >
          <el-input
            v-model="queryForm.produceProcessId"
            clearable
            placeholder="请输入生产路径 Id aps_produce_process"
          />
        </el-form-item>
        <el-form-item label="机器ID" prop="machineId">
          <el-input
            v-model="queryForm.machineId"
            clearable
            placeholder="请输入机器ID"
          />
        </el-form-item>
        <el-form-item label="状态ID" prop="statusId">
          <el-input
            v-model="queryForm.statusId"
            clearable
            placeholder="请输入状态ID"
          />
        </el-form-item>
        <el-form-item label="耗时（秒）" prop="machineUseTimeSecond">
          <el-input
            v-model="queryForm.machineUseTimeSecond"
            clearable
            placeholder="请输入耗时（秒）"
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

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsProduceProcessItemAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsProduceProcessItem} from './ApsProduceProcessItemType.ts';

const dtoUrl = ref<string>('/apsProduceProcessItem');
const documentTitle = ref<string>('aps 生产机器');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsProduceProcessItem>({
  produceProcessId: undefined,
  machineId: undefined,
  statusId: undefined,
  machineUseTimeSecond: undefined,
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
const dataList = ref<ApsProduceProcessItem[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([]);

// 获取表格内数据
function getDataList() {
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
}

// table点击事件
function editData(data: any) {
  // console.info("data ", data)
  tableBarRef.value?.showEditDialog(data.id);
}
// 页面条数变更事件
function handleSizeChange(val: number) {
  currentPageSize.value = val;
  getDataList();
}
// 页面变更事件
function handleCurrentChange(val: number) {
  currentPageNum.value = val;
  getDataList();
}
// 表格选中事件
function handleSelectionChange(val: ApsProduceProcessItem[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}

// 页面加载事件
onMounted(() => {
  getDataList();
});
</script>

<style lang="scss" scoped></style>
