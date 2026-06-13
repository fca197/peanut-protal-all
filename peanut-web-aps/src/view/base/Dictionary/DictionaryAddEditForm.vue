<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="分组" prop="dictionaryGroup">
      <el-input
        v-model="addForm.dictionaryGroup"
        clearable
        placeholder="请输入分组"
      />
    </el-form-item>
    <el-form-item label="值" prop="dictionaryValue">
      <el-input
        v-model="addForm.dictionaryValue"
        clearable
        placeholder="请输入值"
      />
    </el-form-item>
    <el-form-item label="排序" prop="dictionarySort">
      <el-input-number :min="1" :max="100" v-model="addForm.dictionarySort"/>
    </el-form-item>
    <el-form-item label="扩展" prop="dictionaryExt">
      <el-input
        v-model="addForm.dictionaryExt"
        :aria-multiline="5"
        clearable
        placeholder="请输入"
        type="textarea"
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
import {type Dictionary} from './DictionaryType.ts';
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
const dtoUrl = ref<string>('/dictionary');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  dictionaryGroup: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  dictionaryValue: [{required: true, message: '请输入', trigger: 'blur'}],
});

// 添加对象
const addForm = ref<Dictionary>({
  dictionaryGroup: '',
  dictionaryValue: '',
  dictionarySort: 1,
  dictionaryExt: '',
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
