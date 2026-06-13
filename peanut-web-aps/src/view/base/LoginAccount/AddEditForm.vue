<template>
  <el-form label-width="80px" :model="addForm">
    <el-form-item label="用户名" prop="userName">
      <el-input
        v-model="addForm.userName"
        placeholder="请输入用户名"
      ></el-input>
    </el-form-item>
    <el-form-item label="手机号" prop="loginPhone">
      <el-input
        v-model="addForm.loginPhone"
        placeholder="请输入电话"
      ></el-input>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {postNoResult} from '@/common/utils/common-js.ts';

const props = defineProps({
  saveFun: {
    type: Function,
  },
  editId: {
    type: String,
    required: false,
  },
});

onMounted(() => {
  loadById();
});

function loadById() {
  if (!props.editId) {
    return;
  }
  console.info('props.editId ', props.editId);
  // postResultInfo("/loginAccount/get",{})
}

const addForm = ref({
  id: props.editId,
  userName: undefined,
  loginPhone: undefined,
});

function saveForm() {
  console.info('addForm ', addForm);
  postNoResult(
    '/loginAccount/insert',
    addForm.value,
    '保存成功',
    saveFormAfter
  );
}

function saveFormAfter(data: any) {
  const pwd = data.data.newPwd;
  console.info('data ', data);
  ElMessageBox.alert(
    `密码为： <span style='font-size: 20px;color: red'>${pwd}</span>,仅提示一次，请妥善保存`,
    '密码提示',
    {
      dangerouslyUseHTMLString: true,
    }
  );
  cancelForm();
}

function cancelForm() {
  if (props.saveFun) {
    props.saveFun();
  }
}
</script>

<style lang="scss" scoped></style>
