<template>
  <el-tabs v-model="activeName">
    <el-tab-pane name="role" label="角色配置">
      <el-transfer
        :titles="['可选', '已选']"
        v-model="userRoleList"
        :data="baseRoleList"
      />
    </el-tab-pane>
    <el-tab-pane name="roleGroup" label="角色组配置">
      <el-transfer
        :titles="['可选', '已选']"
        v-model="userRoleGroupList"
        :data="baseRoleGroupList"
      />
    </el-tab-pane>
  </el-tabs>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="closeDialog"> 取消</el-button>
    <el-button type="primary" icon="check" @click="saveUserRole">
      确定
    </el-button>
  </el-row>
</template>

<script setup lang="ts">
import {onMounted} from 'vue';
import {getRoleList} from '@v/base/BaseRole/BaseRoleType.ts';
import {getRoleGroupList} from '@v/base/BaseRoleGroup/BaseRoleGroupType.ts';
import {postNoResult, postResultInfo} from '@@/utils/common-js.ts';

interface Option {
  key: number;
  label: string;
  disabled: boolean;
}

const props = defineProps({
  closeDialogFun: {
    type: Function,
    required: false,
  },
  userInfo: {
    type: Object,
    required: true,
  },
});

const activeName = ref<string>('role');
const userRoleList = ref<any[]>([]);
const userRoleGroupList = ref<any[]>([]);

const baseRoleList = ref<Option[]>([]);
const baseRoleGroupList = ref<Option[]>([]);

onMounted(() => {
  getRoleList().then((t) => {
    baseRoleList.value = t.map((tt) => {
      return {key: tt.id, label: tt.roleName};
    });
    console.info('baseRoleList ', baseRoleList.value);
  });
  getRoleGroupList().then(
    (t) =>
      (baseRoleGroupList.value = t.map((tt) => {
        return {key: tt.id, label: tt.roleGroupName};
      }))
  );
  postResultInfo('/baseUserRole/queryPageList', {
    queryPage: false,
    data: {
      userId: props.userInfo.id,
    },
  }).then((t) => {
    userRoleList.value = t.data.dataList.map((tt) => tt.roleId);
    console.info('userRoleList.value  ', userRoleList.value);
  });
  postResultInfo('/baseUserRoleGroup/queryPageList', {
    queryPage: false,
    data: {
      userId: props.userInfo.id,
    },
  }).then((t) => {
    userRoleGroupList.value = t.data.dataList.map((tt) => tt.roleGroupId);
    console.info('userRoleGroupList.value  ', userRoleList.value);
  });
});

function closeDialog() {
  if (props.closeDialogFun) {
    props.closeDialogFun();
  }
}

function saveUserRole() {
  const req = {
    roleGroupIds: userRoleGroupList.value,
    roleIds: userRoleList.value,
    userId: props.userInfo.id,
  };
  postNoResult('/loginAccount/updateRole', req, '保存成功', closeDialog);
}
</script>
<style lang="scss" scoped></style>
