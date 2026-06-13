<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingDayConfigVersionDetailMachine} from './ApsSchedulingDayConfigVersionDetailMachineType.ts';
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
const dtoUrl = ref<string>('/apsSchedulingDayConfigVersionDetailMachine');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 版本ID
  schedulingDayId: [
    {required: true, message: '请输入版本ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 订单ID
  orderId: [
    {required: true, message: '请输入订单ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 机器ID
  machineId: [
    {required: true, message: '请输入机器ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 状态ID
  statusId: [
    {required: true, message: '请输入状态ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 开始时间
  beginDateTime: [
    {required: true, message: '请输入开始时间', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 结束时间
  endDateTime: [
    {required: true, message: '请输入结束时间', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 开始秒
  startSecond: [
    {required: true, message: '请输入开始秒', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 结束秒
  endSecond: [
    {required: true, message: '请输入结束秒', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 耗时（秒）
  useTime: [
    {required: true, message: '请输入耗时（秒）', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingDayConfigVersionDetailMachine>({
  schedulingDayId: '',
  orderId: '',
  machineId: '',
  statusId: '',
  beginDateTime: '',
  endDateTime: '',
  startSecond: '',
  endSecond: '',
  useTime: '',
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
    <el-form-item label="版本ID" prop="schedulingDayId">
      <el-input
        v-model="addForm.schedulingDayId"
        clearable
        placeholder="请输入版本ID"
      />
    </el-form-item>
    <el-form-item label="订单ID" prop="orderId">
      <el-input
        v-model="addForm.orderId"
        clearable
        placeholder="请输入订单ID"
      />
    </el-form-item>
    <el-form-item label="机器ID" prop="machineId">
      <el-input
        v-model="addForm.machineId"
        clearable
        placeholder="请输入机器ID"
      />
    </el-form-item>
    <el-form-item label="状态ID" prop="statusId">
      <el-input
        v-model="addForm.statusId"
        clearable
        placeholder="请输入状态ID"
      />
    </el-form-item>
    <el-form-item label="开始时间" prop="beginDateTime">
      <el-input
        v-model="addForm.beginDateTime"
        clearable
        placeholder="请输入开始时间"
      />
    </el-form-item>
    <el-form-item label="结束时间" prop="endDateTime">
      <el-input
        v-model="addForm.endDateTime"
        clearable
        placeholder="请输入结束时间"
      />
    </el-form-item>
    <el-form-item label="开始秒" prop="startSecond">
      <el-input
        v-model="addForm.startSecond"
        clearable
        placeholder="请输入开始秒"
      />
    </el-form-item>
    <el-form-item label="结束秒" prop="endSecond">
      <el-input
        v-model="addForm.endSecond"
        clearable
        placeholder="请输入结束秒"
      />
    </el-form-item>
    <el-form-item label="耗时（秒）" prop="useTime">
      <el-input
        v-model="addForm.useTime"
        clearable
        placeholder="请输入耗时（秒）"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
