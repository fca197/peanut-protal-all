<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="名称" prop="roomName">
          <el-input
            v-model="queryForm.roomName"
            clearable
            placeholder="请输入名称"
          />
        </el-form-item>
        <el-form-item label="编码" prop="roomCode">
          <el-input
            v-model="queryForm.roomCode"
            clearable
            placeholder="请输入编码"
          />
        </el-form-item>
        <el-form-item label="工厂" prop="factoryId">
          <el-select
            v-model="queryForm.factoryId"
            clearable
            placeholder="请选择工厂"
            style="width: 200px"
          >
            <el-option
              v-for="f in factoryList"
              :label="f.factoryName"
              :value="f.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button icon="search" type="primary" @click="getDataList">
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <TableBar
        ref="tableBarRef"
        :add-component="AddEditFormVue"
        :data-batch-delete-url="dataBatchDeleteUrl"
        :data-table-ref="dataTableRef"
        :document-title="documentTitle"
        :multiple-selection="multipleSelection"
        :refresh-list="getDataList"
        :dialog-with="1000"
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
          v-model:current-page="currentPageNum"
          v-model:page-size="currentPageSize"
          :total="tableTotal"
          background
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-row>
    </el-card>
  </div>
</template>

<script lang="ts" setup>
import {ref} from 'vue';
import AddEditFormVue from './ApsRoomAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsRoom} from './ApsRoomType.ts';
import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';

const dtoUrl = ref<string>('/apsRoom');
const documentTitle = ref<string>('车间');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsRoom>({
  roomCode: undefined,
  roomName: undefined,
  factoryId: undefined,
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
const dataList = ref<ApsRoom[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([]);
const factoryList = ref<Factory[]>([]);

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
  queryFactoryList().then((r) => (factoryList.value = r));
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
function handleSelectionChange(val: ApsRoom[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}
</script>

<style lang="scss" scoped></style>
