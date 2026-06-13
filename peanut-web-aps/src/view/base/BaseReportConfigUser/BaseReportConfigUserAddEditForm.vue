<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="报表ID" prop="reportConfigId">
      <el-input
        v-model="addForm.reportConfigId"
        clearable
        placeholder="请输入报表ID"
      />
    </el-form-item>
    <el-form-item label="排序" prop="sortIndex">
      <el-input
        v-model="addForm.sortIndex"
        clearable
        placeholder="请输入排序"
      />
    </el-form-item>
    <el-form-item label="宽度 安el-col span 计算" prop="colSpan">
      <el-input
        v-model="addForm.colSpan"
        clearable
        placeholder="请输入宽度 安el-col span 计算"
      />
    </el-form-item>
    <el-form-item label="高度" prop="height">
      <el-input v-model="addForm.height" clearable placeholder="请输入高度"/>
    </el-form-item>
    <el-form-item label="报表名称" prop="reportName">
      <el-input
        v-model="addForm.reportName"
        clearable
        placeholder="请输入报表名称"
      />
    </el-form-item>
    <el-form-item label="报表路径" prop="reportUrl">
      <el-input
        v-model="addForm.reportUrl"
        clearable
        placeholder="请输入报表路径"
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
import {type BaseReportConfigUser} from './BaseReportConfigUserType.ts';
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
const dtoUrl = ref<string>('/baseReportConfigUser');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 报表ID
  reportConfigId: [
    {required: true, message: '请输入报表ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排序
  sortIndex: [
    {required: true, message: '请输入排序', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 宽度 安el-col span 计算
  colSpan: [
    {
      required: true,
      message: '请输入宽度 安el-col span 计算',
      trigger: 'blur',
    },
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 高度
  height: [
    {required: true, message: '请输入高度', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 报表名称
  reportName: [
    {required: true, message: '请输入报表名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 报表路径
  reportUrl: [
    {required: true, message: '请输入报表路径', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<BaseReportConfigUser>({
  reportConfigId: '',
  sortIndex: '',
  colSpan: '',
  height: '',
  reportName: '',
  reportUrl: '',
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

// 页面加载事件
onMounted(() => {
  loadById();
});
</script>

<style lang="scss" scoped></style>
