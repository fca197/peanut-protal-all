<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingDayConfigVersionDetailMachineUseTime} from './ApsSchedulingDayConfigVersionDetailMachineUseTimeType.ts';
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
const dtoUrl = ref<string>(
  '/apsSchedulingDayConfigVersionDetailMachineUseTime'
);
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 排程ID
  schedulingDayId: [
    {required: true, message: '请输入排程ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 机器ID
  machineId: [
    {required: true, message: '请输入机器ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 耗时
  useTime: [
    {required: true, message: '请输入耗时', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 使用率
  useUsageRate: [
    {required: true, message: '请输入使用率', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 商品数
  makeProduceCount: [
    {required: true, message: '请输入商品数', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingDayConfigVersionDetailMachineUseTime>({
  schedulingDayId: '',
  machineId: '',
  useTime: '',
  useUsageRate: '',
  makeProduceCount: '',
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
    <el-form-item label="排程ID" prop="schedulingDayId">
      <el-input
        v-model="addForm.schedulingDayId"
        clearable
        placeholder="请输入排程ID"
      />
    </el-form-item>
    <el-form-item label="机器ID" prop="machineId">
      <el-input
        v-model="addForm.machineId"
        clearable
        placeholder="请输入机器ID"
      />
    </el-form-item>
    <el-form-item label="耗时" prop="useTime">
      <el-input v-model="addForm.useTime" clearable placeholder="请输入耗时"/>
    </el-form-item>
    <el-form-item label="使用率" prop="useUsageRate">
      <el-input
        v-model="addForm.useUsageRate"
        clearable
        placeholder="请输入使用率"
      />
    </el-form-item>
    <el-form-item label="商品数" prop="makeProduceCount">
      <el-input
        v-model="addForm.makeProduceCount"
        clearable
        placeholder="请输入商品数"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
