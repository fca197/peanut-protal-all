<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="业务KEY" prop="bizKey">
      <el-input
        v-model="addForm.bizKey"
        clearable
        placeholder="请输入业务KEY"
      />
    </el-form-item>
    <el-form-item label="字段名称" prop="fieldName">
      <el-input
        v-model="addForm.fieldName"
        clearable
        placeholder="请输入字段名称"
      />
    </el-form-item>
    <el-form-item label="显示名" prop="showName">
      <el-input
        v-model="addForm.showName"
        clearable
        placeholder="请输入显示名"
      />
    </el-form-item>
    <el-form-item label="宽度" prop="widthPx">
      <el-input v-model="addForm.widthPx" clearable placeholder="请输入宽度"/>
    </el-form-item>
    <el-form-item label="排序" prop="sortIndex">
      <el-input-number
        v-model="addForm.sortIndex"
        :max="100"
        :min="1"
        :step="1"
        clearable
        placeholder="请输入排序"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type BaseTableHeader} from './BaseTableHeaderType.ts';
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
const dtoUrl = ref<string>('/baseTableHeader');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  bizKey: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  fieldName: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  showName: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 页面加载事件
onMounted(() => {
  loadById();
});
// 添加对象
const addForm = ref<BaseTableHeader>({
  bizKey: '',
  fieldName: '',
  showName: '',
  widthPx: '',
  sortIndex: 1,
  isPicture: '',
  planStatus: '',
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
