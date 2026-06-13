<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSaleConfig} from './ApsSaleConfigType.ts';
import {getById, pinyin4jSzm, postNoResult,} from '@/common/utils/common-js.ts';
import {type FormInstance, FormRules} from 'element-plus';

const props = defineProps({
  saveFun: {
    type: Function,
  },
  editId: {
    type: String,
    required: false,
  },
  isValue: {
    type: Number,
    required: false,
    default: -1,
  },
  parentId: {
    type: String,
    required: false,
    default: undefined,
  },
});

// 对象URL
const dtoUrl = ref<string>('/apsSaleConfig');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  saleCode: [
    {required: true, message: '请输入编码', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  saleName: [
    {required: true, message: '请输入名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSaleConfig>({
  saleCode: '',
  saleName: '',
  supplierStatus: '',
  isValue: 0,
  parentId: undefined,
  id: '',
  children: [],
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
  addForm.value.isValue = props.isValue ? props.isValue : addForm.value.isValue;
  addForm.value.parentId = props.parentId
    ? props.parentId
    : addForm.value.parentId;
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

function loadSzm() {
  pinyin4jSzm(addForm.value.saleName).then((t) => (addForm.value.saleCode = t));
}

// 页面加载事件
onMounted(() => {
  loadById();
});
</script>

<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="名称" prop="saleName">
      <el-input
        v-model="addForm.saleName"
        clearable
        placeholder="请输入"
        @change="loadSzm"
      />
    </el-form-item>
    <el-form-item label="编码" prop="saleCode">
      <el-input v-model="addForm.saleCode" clearable placeholder="请输入"/>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
