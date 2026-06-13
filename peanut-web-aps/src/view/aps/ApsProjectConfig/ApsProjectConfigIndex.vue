<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsProjectConfigAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsProjectConfig} from './ApsProjectConfigType.ts';

const dtoUrl = ref<string>('/apsProjectConfig');
const documentTitle = ref<string>('项目配置');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsProjectConfig>({
  saleCode: undefined,
  saleName: undefined,
  supplierStatus: undefined,
  isValue: undefined,
  parentId: undefined,
  id: undefined,
  children: [],
});

// 表格选中的id
const multipleSelection = ref<(string | undefined)[]>([]);

// 表格
// const dataTableRef = ref<InstanceType<typeof ElTable> | null>(null)
const dataTableRef = ref({});
// 表格操作头
const tableBarRef = ref<InstanceType<typeof TableBar> | null>(null);
// 表格相关
const dataList = ref<ApsProjectConfig[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'parentSaleCode', showName: '制造组编码'},
  {fieldName: 'parentSaleName', showName: '制造组名称'},
  {fieldName: 'saleCode', showName: '制造编码'},
  {fieldName: 'saleName', showName: '制造名称'},
]);

// 获取表格内数据
function getDataList() {
  const req = {
    pageSize: currentPageSize.value,
    pageNum: currentPageNum.value,
    data: queryForm.value,
  };
  console.info('getDataList {}', req);
  postResultInfo(`${dtoUrl.value}/queryPageList`, req).then((t) => {
    tableTotal.value = Number.parseInt(t.data.total);
    // headerList.value = t.data.headerList
    t.data.dataList.forEach((tt) => {
      tt['parentSaleCode'] = tt.saleCode;
      tt['parentSaleName'] = tt.saleName;
      tt['saleCode'] = '';
      tt['saleName'] = '';
      dataList.value.push(tt);
      tt.children.forEach((ttt) => {
        dataList.value.push(ttt);
      });
    });
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
function handleSelectionChange(val: ApsProjectConfig[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}

const addSaleConfigDialog = ref<boolean>(false);
const isValue = ref<number>(0);
const parentId = ref<string | undefined>(undefined);

function addSaleConfigFun(type: number, parentIdValue: string | undefined) {
  isValue.value = type;
  parentId.value = parentIdValue;
  addSaleConfigDialog.value = true;
}

function closeAddSaleConfigFun() {
  getDataList();
  addSaleConfigDialog.value = false;
}

// 页面加载事件
onMounted(() => {
  getDataList();
});
</script>

<template>
  <div class="app-container">
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
        <ElTableColumn prop="id" label="ID"/>
        <ElTableColumn
          v-for="h in headerList"
          :key="h.fieldName"
          :label="h.showName"
          :min-width="h.width"
          :prop="h.fieldName"
        />
        <ElTableColumn fixed="right" label="操作" width="250px">
          <template #default="scope">
            <el-button
              v-if="scope.row.isValue === 0"
              icon="Plus"
              type="primary"
              @click="addSaleConfigFun(1, scope.row.parentId)"
            >
              添加
            </el-button>
            <el-button icon="edit" type="warning" @click="editData(scope.row)">
              编辑
            </el-button>
          </template>
        </ElTableColumn>
      </ElTable>
    </el-card>
    <el-dialog v-model="addSaleConfigDialog" title="制造配置" destroy-on-close>
      <add-edit-form-vue
        :is-value="1"
        :parent-id="parentId"
        :save-fun="closeAddSaleConfigFun"
      />
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped></style>
