<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingConstraints} from './ApsSchedulingConstraintsType.ts';
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
});

// 对象URL
const dtoUrl = ref<string>('/apsSchedulingConstraints');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  constraintsNo: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  constraintsName: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  constraintsContext: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  constraintsRemark: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingConstraints>({
  constraintsNo: '',
  constraintsName: '',
  constraintsContext: '',
  constraintsRemark: '',
  id: '',
});

const loadById = () => {
  if (!props.editId) {
    return;
  }
  console.info('props.editId ', props.editId);
  getById(`${dtoUrl.value}/queryByIdList`, props.editId).then((t) => {
    addForm.value = t;
    console.info(' addForm.value ', addForm.value);
  });
};

// 保存
const saveForm = () => {
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
};

// 保存成功后，方法， 目前关闭弹窗
const saveFormAfter = () => {
  cancelForm();
};

// 取消方法
const cancelForm = () => {
  if (props.saveFun) {
    props.saveFun();
  }
};

const loadSzm = () => {
  pinyin4jSzm(addForm.value.constraintsName).then(
    (r) => (addForm.value.constraintsNo = r)
  );
};

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
    <el-form-item label="名称" prop="constraintsName">
      <el-input
        v-model="addForm.constraintsName"
        clearable
        placeholder="请输入"
        @click="loadSzm"
      />
    </el-form-item>
    <el-form-item label="编码" prop="constraintsNo">
      <el-input
        v-model="addForm.constraintsNo"
        clearable
        placeholder="请输入"
      />
    </el-form-item>
    <el-form-item label="备注" prop="constraintsRemark">
      <el-input
        v-model="addForm.constraintsRemark"
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

<style lang="scss" scoped></style>
