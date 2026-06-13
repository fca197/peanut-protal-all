<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="角色编码" prop="roleCode">
      <el-input
        v-model="addForm.roleCode"
        clearable
        placeholder="请输入角色编码"
      />
    </el-form-item>
    <el-form-item label="角色名称" prop="roleName">
      <el-input
        v-model="addForm.roleName"
        clearable
        placeholder="请输入角色名称"
      />
    </el-form-item>
    <el-form-item label="角色组" prop="roleGroupId">
      <el-select v-model="addForm.roleGroupId">
        <el-option
          v-for="f in baseRoleGroupList"
          :key="f.id"
          :label="f.roleGroupName"
          :value="f.id"
        />
      </el-select>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {getById, postNoResult} from '@/common/utils/common-js.ts';
import {type FormInstance, FormRules} from 'element-plus';
import {BaseRoleGroup, getRoleGroupList,} from '@v/base/BaseRoleGroup/BaseRoleGroupType.ts';

const props = defineProps({
  saveFun: {
    type: Function,
  },
  editId: {
    type: String,
    required: false,
  },
});

const dtoUrl = ref<string>('/baseRole');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  roleCode: [
    {required: true, message: '请输入角色编码', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  roleName: [
    {required: true, message: '请输入角色名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  roleGroupId: [
    {required: true, message: '请输入角色组', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

const baseRoleGroupList = ref<BaseRoleGroup[]>([]);

onMounted(() => {
  loadById();
  getRoleGroupList().then((t) => (baseRoleGroupList.value = t));
});

const addForm = ref({
  roleCode: undefined,
  roleName: undefined,
  roleGroupId: undefined,
  id: undefined,
});

function loadById() {
  if (!props.editId) {
    return;
  }
  console.info('props.editId ', props.editId);
  getById(`${dtoUrl.value}/queryByIdList`, props.editId).then((t) => {
    addForm.value = t;
    console.info(' addForm.value ', addForm.value);
  });
}

function saveForm() {
  console.info('addForm ', addForm);
  addFormRef.value?.validate((valid) => {
    if (valid) {
      if (props.editId) {
        postNoResult(
          `${dtoUrl.value}/updateById`,
          addForm.value,
          '修改成功',
          saveFormAfter
        );
      } else {
        postNoResult(
          `${dtoUrl.value}/insert`,
          addForm.value,
          '保存成功',
          saveFormAfter
        );
      }
    } else {
      ElMessage.error('表单校验失败，请检查必填项');
    }
  });
}

function saveFormAfter() {
  cancelForm();
}

function cancelForm() {
  if (props.saveFun) {
    props.saveFun();
  }
}
</script>

<style lang="scss" scoped></style>
