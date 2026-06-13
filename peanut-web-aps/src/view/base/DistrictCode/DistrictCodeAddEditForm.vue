<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="编码" prop="code">
      <el-input v-model="addForm.code" clearable placeholder="请输入编码"/>
    </el-form-item>
    <el-form-item label="名称" prop="name">
      <el-input v-model="addForm.name" clearable placeholder="请输入名称"/>
    </el-form-item>
    <el-form-item label="上级编码" prop="parentCode">
      <el-input
        v-model="addForm.parentCode"
        clearable
        placeholder="请输入上级编码"
      />
    </el-form-item>
    <el-form-item label="层级" prop="level">
      <el-select
        v-model="addForm.level"
        clearable
        placeholder="请输入层级"
        style="width: 200px"
      >
        <el-option
          v-for="kv in districtCodeLevel"
          :key="kv.value"
          :label="kv.label"
          :value="kv.value"
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
import {type DistrictCode, districtCodeLevel} from './DistrictCodeType.ts';
import {getById, postNoResult} from '@/common/utils/common-js.ts';
import {type FormInstance, FormRules} from 'element-plus';

const props = defineProps({
  saveFun: {
    type: Function,
  },
  editId: {
    type: String,
    required: false,
  },
});

// 对象URL
const dtoUrl = ref<string>('/districtCode');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  code: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  name: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  parentCode: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 页面加载事件
onMounted(() => {
  loadById();
});
// 添加对象
const addForm = ref<DistrictCode>({
  code: '',
  name: '',
  parentCode: '',
  path: '',
  level: 1,
  id: '',
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

// 保存
function saveForm() {
  console.info('addForm ', addForm);
  addFormRef.value?.validate((valid) => {
    if (valid) {
      // 存在ID ，调用更新
      if (props.editId) {
        postNoResult(
          `${dtoUrl.value}/updateById`,
          addForm.value,
          '修改成功',
          saveFormAfter
        );
      } else {
        // 调用保存
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

// 保存成功后，方法， 目前关闭弹窗
function saveFormAfter() {
  cancelForm();
}

// 取消方法
function cancelForm() {
  if (props.saveFun) {
    props.saveFun();
  }
}
</script>

<style lang="scss" scoped></style>
