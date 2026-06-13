<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="${column.comment}" prop="fileName">
      <el-input
        v-model="addForm.fileName"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="fileSize">
      <el-input
        v-model="addForm.fileSize"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="localFilePath">
      <el-input
        v-model="addForm.localFilePath"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="cloudFilePath">
      <el-input
        v-model="addForm.cloudFilePath"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="expireTime">
      <el-input
        v-model="addForm.expireTime"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="fileType">
      <el-input
        v-model="addForm.fileType"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="fileSuffix">
      <el-input
        v-model="addForm.fileSuffix"
        clearable
        placeholder="请输入${column.comment}"
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
import {type FileUpload} from './FileUploadType.ts';
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
const dtoUrl = ref<string>('/fileUpload');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  fileName: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  fileSize: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  localFilePath: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  cloudFilePath: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  expireTime: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  fileType: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  fileSuffix: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 页面加载事件
onMounted(() => {
  loadById();
});
// 添加对象
const addForm = ref<FileUpload>({
  fileName: '',
  fileSize: '',
  localFilePath: '',
  cloudFilePath: '',
  expireTime: '',
  fileType: '',
  fileSuffix: '',
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
