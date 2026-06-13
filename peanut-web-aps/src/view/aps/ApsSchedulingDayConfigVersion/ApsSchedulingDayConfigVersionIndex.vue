<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsSchedulingDayConfigVersionAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postNoResult, postResultInfo,} from '@@/utils/common-js.ts';
import {type ApsSchedulingDayConfigVersion} from './ApsSchedulingDayConfigVersionType.ts';
import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';

const dtoUrl = ref<string>('/apsSchedulingDayConfigVersion');
const documentTitle = ref<string>('排程版本');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsSchedulingDayConfigVersion>({
  schedulingDayConfigId: undefined,
  factoryId: undefined,
  schedulingDayVersionNo: undefined,
  schedulingDay: undefined,
  searchOld: undefined,
  isIssuedThird: undefined,
  processId: undefined,
  headerList: undefined,
  productType: undefined,
  goodsIdList: undefined,
  saleConfigIdList: undefined,
  stepIndex: undefined,
  orderFieldList: undefined,
  orderUserFieldList: undefined,
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
const dataList = ref<ApsSchedulingDayConfigVersion[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'id', showName: '序号'},
  {fieldName: 'schedulingDayConfigId', showName: '配置'},
  {fieldName: 'factoryId', showName: '工厂'},
  {fieldName: 'schedulingDayVersionNo', showName: '排程版本号'},
  {fieldName: 'schedulingDay', showName: '排程日期'},
  // { fieldName: "searchOld", showName: "是否查询历史订单 0否， 1是" },
  // { fieldName: "isIssuedThird", showName: "是否下发 0 否,1 是" },
  // { fieldName: "processId", showName: "工艺路径id" },
  // { fieldName: "headerList", showName: "排产日配置版本表头" },
  // { fieldName: "productType", showName: "排产生产类型" },
  // { fieldName: "goodsIdList", showName: "商品列" },
  // { fieldName: "saleConfigIdList", showName: "销售配置ID" },
  // { fieldName: "stepIndex", showName: "当前步骤" },
  // { fieldName: "orderFieldList", showName: "订单字段" },
  // { fieldName: "orderUserFieldList", showName: "订单用户字段" },
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
const showData = (data: ApsSchedulingDayConfigVersion) => {
  console.info('data ', data);
  // tableBarRef.value?.showEditDialog(data.id)
  if (data.productType === 'MAKE') {
    router.push(
      `/aps/ApsSchedulingDayConfigVersion/MachineResult/${data.id}/${data.factoryId}`
    );
  } else {
    router.push(
      `/aps/ApsSchedulingDayConfigVersion/ProcessLineResult/${data.id}`
    );
  }
};
// 页面条数变更事件
const handleSizeChange = (val: number) => {
  currentPageSize.value = val;
  getDataList();
};
const router = useRouter();
// 页面变更事件
const handleCurrentChange = (val: number) => {
  currentPageNum.value = val;
  getDataList();
};
// 表格选中事件
const handleSelectionChange = (val: ApsSchedulingDayConfigVersion[]) => {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
};

const factoryList = ref<Factory[]>([]);

const schedulingOrderList = (data: ApsSchedulingDayConfigVersion) => {
  postNoResult(
    '/apsSchedulingDayConfigVersion/schedulingOrderList',
    data,
    '排程成功',
    () => {
      data.stepIndex = 2;
    }
  );
};
// 页面加载事件
onMounted(() => {
  getDataList();
  queryFactoryList().then((r) => (factoryList.value = r));
});
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="工厂" prop="factoryId">
          <el-select
            v-model="queryForm.factoryId"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="f in factoryList"
              :key="f.id"
              :label="f.factoryName"
              :value="f.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="排程日期" prop="schedulingDay">
          <el-date-picker
            v-model="queryForm.schedulingDay"
            clearable
            placeholder="请输入排程日期"
            type="date"
            value-format="YYYY-MM-DD"
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
        :dialog-with="800"
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
          :width="h.width"
          :prop="h.fieldName"
        />
        <ElTableColumn fixed="right" label="操作" width="250px">
          <template #default="scope">
            <el-button
              v-if="scope.row.stepIndex == 2"
              type="primary"
              icon="Histogram"
              @click="showData(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="scope.row.stepIndex == 1"
              type="primary"
              icon="Refresh"
              @click="schedulingOrderList(scope.row)"
            >
              开始排程
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
