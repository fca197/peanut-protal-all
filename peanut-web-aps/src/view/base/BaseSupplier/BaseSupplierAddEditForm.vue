<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="名称" prop="supplierName">
      <el-input
        v-model="addForm.supplierName"
        clearable
        placeholder="请输入名称"
      />
    </el-form-item>
    <el-form-item label="编码" prop="supplierCode">
      <el-input
        v-model="addForm.supplierCode"
        clearable
        placeholder="请输入编码"
      />
    </el-form-item>
    <el-form-item label="手机号" prop="supplierPhone">
      <el-input
        v-model="addForm.supplierPhone"
        clearable
        placeholder="请输入手机号"
      />
    </el-form-item>
    <el-form-item label="邮箱" prop="supplierEmail">
      <el-input
        v-model="addForm.supplierEmail"
        clearable
        placeholder="请输入邮箱"
      />
    </el-form-item>
    <el-form-item label="地址" prop="supplierAddr">
      <el-input
        v-model="addForm.supplierAddr"
        clearable
        placeholder="请输入地址"
      />
    </el-form-item>
    <el-form-item label="备注" prop="supplierRemark">
      <el-input
        v-model="addForm.supplierRemark"
        clearable
        placeholder="请输入"
        type="textarea"
      />
    </el-form-item>
    <el-form-item label="状态" prop="supplierStatus">
      <el-input
        v-model="addForm.supplierStatus"
        clearable
        placeholder="请输入"
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
import {type BaseSupplier} from './BaseSupplierType.ts';
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
const dtoUrl = ref<string>('/baseSupplier');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  supplierName: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  supplierCode: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  supplierPhone: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  supplierEmail: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<BaseSupplier>({
  supplierName: '',
  supplierCode: '',
  supplierPhone: '',
  supplierEmail: '',
  supplierAddr: '',
  supplierRemark: '',
  supplierStatus: '',
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
