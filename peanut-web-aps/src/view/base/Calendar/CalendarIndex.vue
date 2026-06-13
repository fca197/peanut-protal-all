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
        <ElTableColumn label="工厂">
          <template #default="scope">
            {{ factoryMap[scope.row?.factoryId] }}
          </template>
        </ElTableColumn>
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
              @click="openSettingDayFun(scope.row)"
            >
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
    <el-dialog
      v-model="openSettingDay"
      append-to-body
      title="设置工作日"
      width="600px"
    >
      <calendar-day-setting
        :calendar-id="currentCalendarId"
        :cancel-form-fun="() => (openSettingDay = false)"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import CalendarDaySetting from './CalendarDaySetting.vue';
import AddEditFormVue from './CalendarAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {ElTable} from 'element-plus';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type Calendar} from './CalendarType.ts';
import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';

const dtoUrl = ref<string>('/calendar');
const documentTitle = ref<string>('日历');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

// 查询表格
const queryForm = ref<Calendar>({
  factoryId: undefined,
  calendarName: undefined,
  calendarCode: undefined,
  calendarType: undefined,
  calendarDesc: undefined,
  calendarDisabled: undefined,
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
const dataList = ref<Calendar[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([
  {fieldName: 'calendarCode', showName: '编码'},
  {fieldName: 'calendarName', showName: '名称'},
]);

const factoryList = ref<Factory[]>([]);
const factoryMap = ref({});
const openSettingDay = ref<boolean>(false);
const currentCalendarId = ref<string>('');

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
    // headerList.value = t.data.headerList
  });
}

// 页面加载事件
onMounted(() => {
  getDataList();
  queryFactoryList().then((t) => {
    factoryList.value = t;
    t.forEach((f) => {
      factoryMap.value[f.id] = f.factoryName;
    });
  });
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
function handleSelectionChange(val: Calendar[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}
function openSettingDayFun(row) {
  console.info('openSettingDayFun ', row);
  currentCalendarId.value = row.id;
  openSettingDay.value = true;
}
</script>

<style lang="scss" scoped></style>
