<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="名称" prop="factoryName">
          <el-input
            v-model="queryForm.factoryName"
            clearable
            placeholder="请输入名称"
          />
        </el-form-item>
        <el-form-item label="编码" prop="factoryCode">
          <el-input
            v-model="queryForm.factoryCode"
            clearable
            placeholder="请输入编码"
          />
        </el-form-item>
        <el-form-item label="状态" prop="factoryStatus">
          <el-select
            v-model="queryForm.factoryStatus"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="s in factoryStatusList"
              :key="s.value"
              :label="s.label"
              :value="s.value"
            ></el-option>
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
        <ElTableColumn label="状态">
          <template #default="{ row }">
            <!-- 自定义渲染逻辑 -->
            {{ row['factoryStatus'] === 'ENABLED' ? '启用' : '禁用' }}
          </template>
        </ElTableColumn>
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
import AddEditFormVue from './FactoryAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type Factory, factoryStatusList} from './FactoryType.ts';

const dtoUrl = ref<string>('/factory');
const documentTitle = ref<string>('工厂');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);
// 查询表格
const queryForm = ref<Factory>({
  factoryName: '',
  factoryCode: '',
  factoryStatus: '',
  id: '',
});

//  表格选中的id
const multipleSelection = ref<(string | undefined)[]>([]);

//  表格
const dataTableRef = ref({});
//  表格操作头
const tableBarRef = ref<InstanceType<typeof TableBar> | null>(null);
//  表格相关
const dataList = ref<Factory[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {
    showName: '序号',
    fieldName: 'id',
  },
  {
    showName: '工厂名称',
    fieldName: 'factoryName',
  },
  {
    showName: '工厂编码',
    fieldName: 'factoryCode',
  },
]);
//  获取表格内数据
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
    // headerList.value = t.data.headerList
  });
}
//  页面加载事件
onMounted(() => {
  getDataList();
});

//  table点击事件
function editData(data: any) {
  //  console.info("data ", data)
  tableBarRef.value?.showEditDialog(data.id);
}

//  页面条数变更事件
function handleSizeChange(val: number) {
  currentPageSize.value = val;
  getDataList();
}

//  页面变更事件
function handleCurrentChange(val: number) {
  currentPageNum.value = val;
  getDataList();
}

//  表格选中事件
function handleSelectionChange(val: Factory[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}
</script>

<style lang="scss" scoped></style>
