<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsSchedulingConstraintsAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {getResult, HeaderInfo, postNoResult, postResultInfo,} from '@@/utils/common-js.ts';
import {type ApsSchedulingConstraints} from './ApsSchedulingConstraintsType.ts';
import CreateContent from './CreateContent.vue';

const dtoUrl = ref<string>('/apsSchedulingConstraints');
const documentTitle = ref<string>('排产约束');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsSchedulingConstraints>({
  constraintsNo: undefined,
  constraintsName: undefined,
  constraintsContext: undefined,
  constraintsRemark: undefined,
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
const dataList = ref<ApsSchedulingConstraints[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'id', showName: '序号'},
  {fieldName: 'constraintsNo', showName: ''},
  {fieldName: 'constraintsName', showName: ''},
  {fieldName: 'constraintsContext', showName: ''},
  {fieldName: 'constraintsRemark', showName: ''},
]);

const constraintList = ref<any[]>([]);
const constrainedFieldList = ref<any[]>([]);
const indexCreate = ref<number>(0);
const openContent = ref<boolean>(false);
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
const handleSelectionChange = (val: ApsSchedulingConstraints[]) => {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
};
const editDtoId = ref<string>('0');
const editContentData = (data: ApsSchedulingConstraints) => {
  console.info('val ', data);
  editDtoId.value = data.id;
  if (data.constraintsContext === null || data.constraintsContext === '') {
    let constObj = {
      filterList: [
        {
          filterFieldType: '',
          fieldName: '',
          operator: '',
          valueList: [],
        },
      ],
      children: [],
      orderBy: [],
    };
    constraintList.value = [constObj];
  } else {
    constraintList.value = JSON.parse(data.constraintsContext);
  }
  openContent.value = true;
};
const constrainedFieldListFun = () => {
  getResult('/apsSchedulingConstraints/getUseField', undefined, (r) => {
    constrainedFieldList.value = r.data.values;
  });
};
const handleCreateOrUpdate = () => {
  const req = {
    id: editDtoId.value,
    constraintsContext: JSON.stringify(constraintList.value),
  };
  console.log('handleCreateOrUpdate', req);
  postNoResult(`${dtoUrl.value}/updateById`, req, '保存成功', () => {
    openContent.value = false;
  });
};
// 页面加载事件
onMounted(() => {
  getDataList();
  constrainedFieldListFun();
});
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="名称" prop="constraintsName">
          <el-input
            v-model="queryForm.constraintsName"
            clearable
            placeholder="请输入名称"
          />
        </el-form-item>
        <el-form-item label="编码" prop="constraintsNo">
          <el-input
            v-model="queryForm.constraintsNo"
            clearable
            placeholder="请输入编码"
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
        <ElTableColumn fixed="right" label="操作" width="250px">
          <template #default="scope">
            <el-button icon="edit" type="warning" @click="editData(scope.row)">
              编辑
            </el-button>
            <el-button
              type="primary"
              icon="setting"
              @click="editContentData(scope.row)"
            >
              约束
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
    <el-dialog v-model="openContent" title="编辑约束">
      <CreateContent
        :key="indexCreate"
        :constrained-field-list="constrainedFieldList"
        :is-child="false"
        :row-constraint-list="constraintList"
      />
      <template #footer>
        <el-button @click="openContent = false"> 取消</el-button>
        <el-button type="primary" @click="handleCreateOrUpdate">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped></style>
