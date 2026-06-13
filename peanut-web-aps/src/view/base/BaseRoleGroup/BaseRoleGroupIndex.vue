<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="角色组编码" prop="roleGroupCode">
          <el-input
            v-model="queryForm.roleGroupCode"
            clearable
            placeholder="请输入角色组编码"
          />
        </el-form-item>
        <el-form-item label="角色组名称" prop="roleGroupName">
          <el-input
            v-model="queryForm.roleGroupName"
            clearable
            placeholder="请输入角色组名称"
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
              icon="menu"
              @click="editRoleData(scope.row)"
            >
              菜单
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
      v-model="roleMenuSetting"
      :destroy-on-close="true"
      :width="400"
      title="设置菜单"
    >
      <select-resource :close-fun="roleMenuSettingClose" :id="selectRoleId"/>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import AddEditFormVue from './BaseRoleGroupAddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {HeaderInfo, postResultInfo} from '@@/utils/common-js.ts';
import {type BaseRoleGroup} from './BaseRoleGroupType.ts';
import SelectResource from './SelectResource.vue';
import type {BaseRole} from '@v/base/BaseRole/BaseRoleType.ts';

const dtoUrl = ref<string>('/baseRoleGroup');
const documentTitle = ref<string>('角色组');
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`);

const queryForm = ref({
  roleGroupCode: undefined,
  roleGroupName: undefined,
  id: undefined,
});

const multipleSelection = ref<(string | undefined)[]>([]);

function handleSelectionChange(val: BaseRoleGroup[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}

const dataTableRef = ref({});
const dataList = ref([]);
const tableBarRef = ref({});

const currentPageNum = ref(1);
const currentPageSize = ref(10);
const tableTotal = ref(0);
const headerList = ref<HeaderInfo[]>([]);

const roleMenuSetting = ref<boolean>(false);
const selectRoleId = ref<string>('');
const roleMenuSettingOpen = () => {
  roleMenuSetting.value = true;
};
const roleMenuSettingClose = () => {
  roleMenuSetting.value = false;
};

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

onMounted(() => {
  getDataList();
});

function editData(data: any) {
  // console.info("data ", data)
  tableBarRef.value?.showEditDialog(data.id);
}

function handleSizeChange(val: number) {
  currentPageSize.value = val;
  getDataList();
}

function handleCurrentChange(val: number) {
  currentPageNum.value = val;
  getDataList();
}

function editRoleData(data: BaseRole) {
  selectRoleId.value = data.id;
  roleMenuSettingOpen();
}
</script>

<style lang="scss" scoped></style>
