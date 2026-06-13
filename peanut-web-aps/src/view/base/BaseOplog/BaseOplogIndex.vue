<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="操作内容" prop="content">
          <el-input
            v-model="queryForm.content"
            clearable
            placeholder="请输入操作内容"
          />
        </el-form-item>
        <el-form-item label="业务Key" prop="businessKey">
          <el-input
            v-model="queryForm.businessKey"
            clearable
            placeholder="请输入业务Key"
          />
        </el-form-item>
        <el-form-item label="方法名称" prop="methodName">
          <el-input
            v-model="queryForm.methodName"
            clearable
            placeholder="请输入方法名称"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType">
          <el-input
            v-model="queryForm.businessType"
            clearable
            placeholder="请输入业务类型"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType0">
          <el-input
            v-model="queryForm.businessType0"
            clearable
            placeholder="请输入业务类型"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType1">
          <el-input
            v-model="queryForm.businessType1"
            clearable
            placeholder="请输入业务类型"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType2">
          <el-input
            v-model="queryForm.businessType2"
            clearable
            placeholder="请输入业务类型"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType3">
          <el-input
            v-model="queryForm.businessType3"
            clearable
            placeholder="请输入业务类型"
          />
        </el-form-item>
        <el-form-item label="业务类型" prop="businessType4">
          <el-input
            v-model="queryForm.businessType4"
            clearable
            placeholder="请输入业务类型"
          />
        </el-form-item>
        <el-form-item label="请求地址" prop="url">
          <el-input
            v-model="queryForm.url"
            clearable
            placeholder="请输入请求地址"
          />
        </el-form-item>
        <el-form-item label="耗时" prop="useTime">
          <el-input
            v-model="queryForm.useTime"
            clearable
            placeholder="请输入耗时"
          />
        </el-form-item>
        <el-form-item label="参数名 参数1,参数2" prop="paramName">
          <el-input
            v-model="queryForm.paramName"
            clearable
            placeholder="请输入参数名 参数1,参数2"
          />
        </el-form-item>
        <el-form-item label="请求入参" prop="reqBody">
          <el-input
            v-model="queryForm.reqBody"
            clearable
            placeholder="请输入请求入参"
          />
        </el-form-item>
        <el-form-item label="是否成功" prop="isSuccess">
          <el-input
            v-model="queryForm.isSuccess"
            clearable
            placeholder="请输入是否成功"
          />
        </el-form-item>
        <el-form-item label="请求入参" prop="resultStr">
          <el-input
            v-model="queryForm.resultStr"
            clearable
            placeholder="请输入请求入参"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="queryForm.remark"
            clearable
            placeholder="请输入备注"
          />
        </el-form-item>
        <el-form-item label="创建人" prop="createUserName">
          <el-input
            v-model="queryForm.createUserName"
            clearable
            placeholder="请输入创建人"
          />
        </el-form-item>
        <el-form-item label="登录手机号" prop="loginPhone">
          <el-input
            v-model="queryForm.loginPhone"
            clearable
            placeholder="请输入登录手机号"
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
import AddEditFormVue from './BaseOplogAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type BaseOplog} from './BaseOplogType.ts';

const dtoUrl = ref<string>('/baseOplog');
const documentTitle = ref<string>('操作日志');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<BaseOplog>({
  content: undefined,
  businessKey: undefined,
  methodName: undefined,
  businessType: undefined,
  businessType0: undefined,
  businessType1: undefined,
  businessType2: undefined,
  businessType3: undefined,
  businessType4: undefined,
  url: undefined,
  useTime: undefined,
  paramName: undefined,
  reqBody: undefined,
  isSuccess: undefined,
  resultStr: undefined,
  remark: undefined,
  createUserName: undefined,
  loginPhone: undefined,
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
const dataList = ref<BaseOplog[]>([]);
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
function handleSelectionChange(val: BaseOplog[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}

// 页面加载事件
onMounted(() => {
  getDataList();
});
</script>

<style lang="scss" scoped></style>
