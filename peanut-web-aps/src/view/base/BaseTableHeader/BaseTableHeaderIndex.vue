<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="业务KEY" prop="bizKey">
          <el-input
            v-model="queryForm.bizKey"
            clearable
            placeholder="请输入业务KEY"
          />
        </el-form-item>
        <el-form-item label="字段名称" prop="fieldName">
          <el-input
            v-model="queryForm.fieldName"
            clearable
            placeholder="请输入"
          />
        </el-form-item>
        <el-form-item label="显示名称" prop="showName">
          <el-input
            v-model="queryForm.showName"
            clearable
            placeholder="请输入"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortIndex">
          <el-input-number
            v-model="queryForm.sortIndex"
            :max="100"
            :min="1"
            :step="1"
            clearable
            placeholder="请输入排序"
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
import {ref} from 'vue';
import AddEditFormVue from './BaseTableHeaderAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type BaseTableHeader} from './BaseTableHeaderType.ts';

const dtoUrl = ref<string>('/baseTableHeader');
const documentTitle = ref<string>('表头配置');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<BaseTableHeader>({
  bizKey: undefined,
  fieldName: undefined,
  showName: undefined,
  widthPx: undefined,
  sortIndex: undefined,
  isPicture: undefined,
  planStatus: undefined,
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
const dataList = ref<BaseTableHeader[]>([]);
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

// 页面加载事件
onMounted(() => {
  getDataList();
});

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
function handleSelectionChange(val: BaseTableHeader[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}
</script>

<style lang="scss" scoped></style>
