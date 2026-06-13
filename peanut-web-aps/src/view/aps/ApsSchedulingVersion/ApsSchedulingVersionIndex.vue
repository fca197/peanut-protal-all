<script setup lang="ts">
import {onMounted, ref} from 'vue';
import AddEditFormVue from './ApsSchedulingVersionAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type ApsSchedulingVersion} from './ApsSchedulingVersionType.ts';

const dtoUrl = ref<string>('/apsSchedulingVersion');
const documentTitle = ref<string>('排产版本');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<ApsSchedulingVersion>({
  schedulingVersionNo: undefined,
  schedulingVersionName: undefined,
  schedulingConstraintsId: undefined,
  headerList: undefined,
  capacityHeaderList: undefined,
  capacityDateList: undefined,
  schedulingDayCount: undefined,
  versionStep: undefined,
  versionStepError: undefined,
  bomTotalEndDate: undefined,
  startDate: undefined,
  useFactoryMakeCapacity: undefined,
  useGoodsMakeCapacity: undefined,
  useSaleConfigMakeCapacity: undefined,
  useProjectConfigMakeCapacity: undefined,
  factoryIdList: undefined,
  goodsIdList: undefined,
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
const dataList = ref<ApsSchedulingVersion[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'id', showName: '序号'},
  // { fieldName: "schedulingVersionNo", showName: "" },
  // { fieldName: "schedulingVersionName", showName: "" },
  // { fieldName: "schedulingConstraintsId", showName: "" },
  // { fieldName: "headerList", showName: "" },
  // { fieldName: "capacityHeaderList", showName: "" },
  // { fieldName: "capacityDateList", showName: "" },
  // { fieldName: "schedulingDayCount", showName: "" },
  // { fieldName: "versionStep", showName: "" },
  // { fieldName: "versionStepError", showName: "" },
  // { fieldName: "bomTotalEndDate", showName: "bom汇总结束日期" },
  // { fieldName: "startDate", showName: "开始时间" },
  // { fieldName: "useFactoryMakeCapacity", showName: "使用工厂产能约束" },
  // { fieldName: "useGoodsMakeCapacity", showName: "使用商品产能约束" },
  // { fieldName: "useSaleConfigMakeCapacity", showName: "使用销售配置产能约束" },
  // { fieldName: "useProjectConfigMakeCapacity", showName: "使用工程配置产能约束" },
  // { fieldName: "factoryIdList", showName: "工厂" },
  // { fieldName: "goodsIdList", showName: "商品" },
]);

const router = useRouter();
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
    dataList.value.forEach((tt) => {
      tt.isFinish = tt.versionStep === 100;
      tt.isNotFinish = tt.versionStep !== 100;
    });
    tableTotal.value = Number.parseInt(t.data.total);
    headerList.value = t.data.headerList;
  });
};

// table点击事件
const editData = (data: any, step = 1) => {
  // console.info("data ", data)
  // tableBarRef.value?.showEditDialog(data.id)
  router.push(
    `/aps/CreateScheduling/${data.id}/${data.isNotFinish ? '1' : '2'}/${step}`
  );
};

const settingKitting = (data: ApsSchedulingVersion) => {
  router.push(
    `/aps/CreateScheduling/${data.id}/${data.isNotFinish ? '1' : '2'}/3`
  );
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
const handleSelectionChange = (val: ApsSchedulingVersion[]) => {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
};

const toCreatePage = (isUpdate: boolean) => {
  // const id = new Date().getTime() + "" + Math.floor(Math.random() * 1000000)
  const id = '-1';
  console.info('toCreatePage ', id, isUpdate);
  router.push(`/aps/CreateScheduling/${id}/0/1`);
};

const showKitting = (data: any) => {
  router.push(`/aps/ApsOrderGoodsBomKittingVersion/${data.id}`);
};
// 页面加载事件
onMounted(() => {
  getDataList();
});
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never"/>
    <el-card shadow="never">
      <TableBar
        :show-add-btn="false"
        :document-title="documentTitle"
        :add-component="AddEditFormVue"
        :refresh-list="getDataList"
        :data-table-ref="dataTableRef"
        :multiple-selection="multipleSelection"
        ref="tableBarRef"
        :data-batch-delete-url="dataBatchDeleteUrl"
      >
        <template #otherBtn>
          <el-button type="primary" icon="plus" @click="toCreatePage(true)">
            添加
          </el-button>
        </template>
      </TableBar>
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
          :prop="h.fieldName"
        />
        <ElTableColumn prop="createUserName" label="创建人" width="200"/>
        <ElTableColumn prop="createTime" label="创建时间" width="200"/>
        <ElTableColumn
          fixed="right"
          label="操作"
          style="float: right"
          width="250px"
        >
          <template #default="scope">
            <el-button
              v-if="scope.row.isNotFinish"
              type="warning"
              icon="edit"
              @click="editData(scope.row, 1)"
            >
              编辑
            </el-button>

            <el-dropdown type="primary" split-button v-if="scope.row.isFinish">
              操作
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    icon="Histogram"
                    @click="editData(scope.row, 2)"
                  >
                    查看不加限数据
                  </el-dropdown-item>
                  <el-dropdown-item
                    icon="Histogram"
                    @click="editData(scope.row, 3)"
                  >
                    查看加限数据
                  </el-dropdown-item>
                  <el-dropdown-item
                    icon="Setting"
                    @click="settingKitting(scope.row)"
                  >
                    齐套检查
                  </el-dropdown-item>
                  <el-dropdown-item
                    icon="DataLine"
                    @click="showKitting(scope.row)"
                  >
                    齐套报告
                  </el-dropdown-item>
                  <el-dropdown-item
                    icon="Share"
                    @click="settingKitting(scope.row)"
                  >
                    订单下发
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
