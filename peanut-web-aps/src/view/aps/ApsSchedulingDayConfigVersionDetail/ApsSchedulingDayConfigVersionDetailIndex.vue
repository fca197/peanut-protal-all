<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsSchedulingDayConfigVersionDetailAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsSchedulingDayConfigVersionDetail} from './ApsSchedulingDayConfigVersionDetailType.ts';

const dtoUrl = ref<string>('/apsSchedulingDayConfigVersionDetail');
const documentTitle = ref<string>('排程版本配置明细');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsSchedulingDayConfigVersionDetail>({
  schedulingDayId: undefined,
  configBizType: undefined,
  configBizId: undefined,
  configBizName: undefined,
  configBizNum: undefined,
  orderId: undefined,
  orderNo: undefined,
  roomId: undefined,
  statusId: undefined,
  sortIndex: undefined,
  isMatch: undefined,
  loopIndex: undefined,
  loopEnough: undefined,
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
const dataList = ref<ApsSchedulingDayConfigVersionDetail[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'id', showName: '序号'},
  {fieldName: 'schedulingDayId', showName: ''},
  {fieldName: 'configBizType', showName: '配置类型 sale,part,bom'},
  {fieldName: 'configBizId', showName: '配置业务ID'},
  {fieldName: 'configBizName', showName: '配置业务名称'},
  {fieldName: 'configBizNum', showName: '配置业务数量'},
  {fieldName: 'orderId', showName: '订单ID'},
  {fieldName: 'orderNo', showName: '订单编号'},
  {fieldName: 'roomId', showName: '房间id'},
  {fieldName: 'statusId', showName: '状态 Id'},
  {fieldName: 'sortIndex', showName: '排序'},
  {fieldName: 'isMatch', showName: '是否匹配 0 否,1 是'},
  {fieldName: 'loopIndex', showName: '循环次数'},
  {fieldName: 'loopEnough', showName: '是否满足 0 否,1 是'},
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
const handleSelectionChange = (val: ApsSchedulingDayConfigVersionDetail[]) => {
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
        <el-form-item label="${column.comment}" prop="schedulingDayId">
          <el-input
            v-model="queryForm.schedulingDayId"
            clearable
            placeholder="请输入${column.comment}"
          />
        </el-form-item>
        <el-form-item label="配置类型 sale,part,bom" prop="configBizType">
          <el-input
            v-model="queryForm.configBizType"
            clearable
            placeholder="请输入配置类型 sale,part,bom"
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
        <el-form-item label="订单ID" prop="orderId">
          <el-input
            v-model="queryForm.orderId"
            clearable
            placeholder="请输入订单ID"
          />
        </el-form-item>
        <el-form-item label="订单编号" prop="orderNo">
          <el-input
            v-model="queryForm.orderNo"
            clearable
            placeholder="请输入订单编号"
          />
        </el-form-item>
        <el-form-item label="房间id" prop="roomId">
          <el-input
            v-model="queryForm.roomId"
            clearable
            placeholder="请输入房间id"
          />
        </el-form-item>
        <el-form-item label="状态 Id" prop="statusId">
          <el-input
            v-model="queryForm.statusId"
            clearable
            placeholder="请输入状态 Id"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortIndex">
          <el-input
            v-model="queryForm.sortIndex"
            clearable
            placeholder="请输入排序"
          />
        </el-form-item>
        <el-form-item label="是否匹配 0 否,1 是" prop="isMatch">
          <el-input
            v-model="queryForm.isMatch"
            clearable
            placeholder="请输入是否匹配 0 否,1 是"
          />
        </el-form-item>
        <el-form-item label="循环次数" prop="loopIndex">
          <el-input
            v-model="queryForm.loopIndex"
            clearable
            placeholder="请输入循环次数"
          />
        </el-form-item>
        <el-form-item label="是否满足 0 否,1 是" prop="loopEnough">
          <el-input
            v-model="queryForm.loopEnough"
            clearable
            placeholder="请输入是否满足 0 否,1 是"
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
