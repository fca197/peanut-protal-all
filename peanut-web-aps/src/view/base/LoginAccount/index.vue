<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="手机号" prop="loginPhone">
          <el-input v-model="queryForm.loginPhone" clearable></el-input>
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
        :data-table-ref="dataTableRef"
        :multiple-selection="multipleSelection"
        :refresh-list="getDataList"
        document-title="用户"
        data-batch-delete-url="/loginAccount/deleteByIdList"
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
        <ElTableColumn fixed="right" label="操作" width="550px">
          <template #default="scope">
            <el-button icon="edit" type="warning" @click="editData(scope.row)">
              编辑
            </el-button>
            <el-button
              type="danger"
              icon="Refresh"
              @click="resetPwd(scope.row)"
            >
              重置密码
            </el-button>
            <el-button
              type="primary"
              icon="edit"
              @click="editRoleFun(scope.row)"
            >
              角色信息
            </el-button>
            <el-button icon="edit" type="success" @click="todo(scope.row)">
              部门
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
      v-model="editConfigShow"
      :title="editConfigShowTitle"
      destroy-on-close
      width="700px"
    >
      <component
        :is="EditRole"
        v-if="editConfigShowType"
        :close-dialog-fun="closeDialog"
        :user-info="editTableDto"
      ></component>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import AddEditFormVue from './AddEditForm.vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import {postNoResult, postResultInfo, todo} from '@@/utils/common-js.ts';
import {type LoginAccount} from '@v/base/LoginAccount/Type.ts';
import EditRole from './EditRole.vue';

const queryForm = ref({
  loginPhone: undefined,
});

const multipleSelection = ref<(string | undefined)[]>([]);

function handleSelectionChange(val: LoginAccount[]) {
  multipleSelection.value = val.map((t) => t.id);
  console.info('multipleSelection ', multipleSelection);
}

const dataTableRef = ref({});
const editConfigShow = ref<boolean>(false);
const editConfigShowTitle = ref<string>('');
const editConfigShowType = ref<string>('');
const dataList = ref([]);
const tableBarRef = ref({});
const headerList = ref([
  {
    showName: '序号',
    fieldName: 'id',
  },
  {
    showName: '用户名',
    fieldName: 'userName',
  },
  {
    showName: '手机号',
    fieldName: 'loginPhone',
  },
  {
    showName: '角色',
    fieldName: 'baseRoleName',
  },
  {
    showName: '角色组',
    fieldName: 'baseRoleGroupName',
  },
  {
    showName: '部门',
    fieldName: 'deptName',
  },
]);

const currentPageNum = ref(1);
const currentPageSize = ref(10);
const tableTotal = ref(0);
const editTableDto = ref<any>(null);

function getDataList() {
  const req = {
    pageSize: currentPageSize.value,
    pageNum: currentPageNum.value,
    data: queryForm.value,
  };
  console.info('getDataList {}', req);
  postResultInfo('/loginAccount/queryPageList', req).then((t) => {
    dataList.value = t.data.records;
    tableTotal.value = Number.parseInt(t.data.total);
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

function resetPwd(row: LoginAccount) {
  postNoResult(
    '/loginAccount/resetPwd',
    {
      id: row.id,
    },
    '修改成功',
    (t) => {
      const newPwd = t.data.newPwd;
      ElMessageBox.alert(
        `密码为： <span style='font-size: 20px;color: red'>${newPwd}</span>,仅提示一次，请妥善保存`,
        '密码提示',
        {
          dangerouslyUseHTMLString: true,
        }
      );
    }
  );
}

function editRoleFun(row: LoginAccount) {
  editConfigShowTitle.value = '修改角色信息';
  editConfigShowType.value = 'role';
  editConfigShow.value = true;
  editTableDto.value = row;
}

function closeDialog() {
  editConfigShow.value = false;
  getDataList();
}
</script>

<style lang="scss" scoped></style>
