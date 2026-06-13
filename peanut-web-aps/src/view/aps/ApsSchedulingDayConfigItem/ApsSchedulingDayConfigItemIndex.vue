<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsSchedulingDayConfigItemAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsSchedulingDayConfigItem} from './ApsSchedulingDayConfigItemType.ts';

const dtoUrl = ref<string>('/apsSchedulingDayConfigItem');
const documentTitle = ref<string>('排程版本配置');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsSchedulingDayConfigItem>({
  schedulingDayId: undefined,
  processId: undefined,
  roomId: undefined,
  statusId: undefined,
  configBizType: undefined,
  configBizId: undefined,
  configBizName: undefined,
  configBizNum: undefined,
  configBizTime: undefined,
  isDefault: undefined,
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
const dataList = ref<ApsSchedulingDayConfigItem[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'id', showName: '序号'},
  {fieldName: 'schedulingDayId', showName: '排程版本ID'},
  {fieldName: 'processId', showName: '工艺路径ID'},
  {fieldName: 'roomId', showName: '车间ID'},
  {fieldName: 'statusId', showName: '状态ID'},
  {fieldName: 'configBizType', showName: '配置类型 sale,part,bom ,sleep'},
  {fieldName: 'configBizId', showName: '配置业务ID'},
  {fieldName: 'configBizName', showName: '配置业务名称'},
  {fieldName: 'configBizNum', showName: '配置业务数量'},
  {fieldName: 'configBizTime', showName: '配置业务耗时(秒)'},
  {fieldName: 'isDefault', showName: '是否默认 0 否,1 是'},
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
const handleSelectionChange = (val: ApsSchedulingDayConfigItem[]) => {
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
        <el-form-item label="排程版本ID" prop="schedulingDayId">
          <el-input
            v-model="queryForm.schedulingDayId"
            clearable
            placeholder="请输入排程版本ID"
          />
        </el-form-item>
        <el-form-item label="工艺路径ID" prop="processId">
          <el-input
            v-model="queryForm.processId"
            clearable
            placeholder="请输入工艺路径ID"
          />
        </el-form-item>
        <el-form-item label="车间ID" prop="roomId">
          <el-input
            v-model="queryForm.roomId"
            clearable
            placeholder="请输入车间ID"
          />
        </el-form-item>
        <el-form-item label="状态ID" prop="statusId">
          <el-input
            v-model="queryForm.statusId"
            clearable
            placeholder="请输入状态ID"
          />
        </el-form-item>
        <el-form-item
          label="配置类型 sale,part,bom ,sleep"
          prop="configBizType"
        >
          <el-input
            v-model="queryForm.configBizType"
            clearable
            placeholder="请输入配置类型 sale,part,bom ,sleep"
          />
        </el-form-item>
        <el-form-item label="配置业务ID" prop="configBizId">
          <el-input
            v-model="queryForm.configBizId"
            clearable
            placeholder="请输入配置业务ID"
          />
        </el-form-item>
        <el-form-item label="配置业务名称" prop="configBizName">
          <el-input
            v-model="queryForm.configBizName"
            clearable
            placeholder="请输入配置业务名称"
          />
        </el-form-item>
        <el-form-item label="配置业务数量" prop="configBizNum">
          <el-input
            v-model="queryForm.configBizNum"
            clearable
            placeholder="请输入配置业务数量"
          />
        </el-form-item>
        <el-form-item label="配置业务耗时(秒)" prop="configBizTime">
          <el-input
            v-model="queryForm.configBizTime"
            clearable
            placeholder="请输入配置业务耗时(秒)"
          />
        </el-form-item>
        <el-form-item label="是否默认 0 否,1 是" prop="isDefault">
          <el-input
            v-model="queryForm.isDefault"
            clearable
            placeholder="请输入是否默认 0 否,1 是"
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
        ref="tableBarRef"
        :add-component="AddEditFormVue"
        :data-batch-delete-url="dataBatchDeleteUrl"
        :data-table-ref="dataTableRef"
        :document-title="documentTitle"
        :multiple-selection="multipleSelection"
        :refresh-list="getDataList"
      />
      <ElTable
        ref="dataTableRef"
        :data="dataList"
        stripe
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection" />
        <ElTableColumn
          v-for="h in headerList"
          :key="h.fieldName"
          :label="h.showName"
          :min-width="h.width"
          :prop="h.fieldName"
        />
        <ElTableColumn fixed="right" label="操作" width="150px">
          <template #default="scope">
            <el-button type="warning" icon="edit" @click="editData(scope.row)">
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

<style lang="scss" scoped></style>
