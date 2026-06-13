<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingDayConfig, isDefaultList,} from './ApsSchedulingDayConfigType.ts';
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
const dtoUrl = ref<string>('/apsSchedulingDayConfig');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 配置ID
  schedulingDayConfigId: [
    {required: true, message: '请输入配置ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 工厂ID
  factoryId: [
    {required: true, message: '请输入工厂ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排程类型
  schedulingType: [
    {required: true, message: '请输入排程类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 工艺路径ID
  processId: [
    {required: true, message: '请输入工艺路径ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 制造路径
  makeProcessId: [
    {required: true, message: '请输入制造路径', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排程版本号
  schedulingDayNo: [
    {required: true, message: '请输入排程版本号', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排程版本名称
  schedulingDayName: [
    {required: true, message: '请输入排程版本名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否默认 0 否,1 是
  isDefault: [
    {required: true, message: '请输入是否默认 0 否,1 是', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 车间配置
  roomConfig: [
    {required: true, message: '请输入车间配置', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingDayConfig>({
  schedulingDayConfigId: '',
  factoryId: '',
  schedulingType: '',
  processId: '',
  makeProcessId: '',
  schedulingDayNo: '',
  schedulingDayName: '',
  isDefault: false,
  roomConfig: '',
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
    <el-form-item label="配置ID" prop="schedulingDayConfigId">
      <el-input
        v-model="addForm.schedulingDayConfigId"
        clearable
        placeholder="请输入配置ID"
      />
    </el-form-item>
    <el-form-item label="工厂" prop="factoryId">
      <el-input
        v-model="addForm.factoryId"
        clearable
        placeholder="请输入工厂ID"
      />
    </el-form-item>
    <el-form-item label="排程类型" prop="schedulingType">
      <el-input
        v-model="addForm.schedulingType"
        clearable
        placeholder="请输入排程类型"
      />
    </el-form-item>
    <el-form-item label="工艺路径ID" prop="processId">
      <el-input
        v-model="addForm.processId"
        clearable
        placeholder="请输入工艺路径ID"
      />
    </el-form-item>
    <el-form-item label="制造路径" prop="makeProcessId">
      <el-input
        v-model="addForm.makeProcessId"
        clearable
        placeholder="请输入制造路径"
      />
    </el-form-item>
    <el-form-item label="排程版本号" prop="schedulingDayNo">
      <el-input
        v-model="addForm.schedulingDayNo"
        clearable
        placeholder="请输入排程版本号"
      />
    </el-form-item>
    <el-form-item label="排程版本名称" prop="schedulingDayName">
      <el-input
        v-model="addForm.schedulingDayName"
        clearable
        placeholder="请输入排程版本名称"
      />
    </el-form-item>
    <el-form-item label="是否默认" prop="isDefault">
      <el-select v-model="addForm.isDefault">
        <el-option
          v-for="d in isDefaultList"
          :key="d.value"
          :label="d.label"
          :value="d.value"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="车间配置" prop="roomConfig">
      <el-input
        v-model="addForm.roomConfig"
        clearable
        placeholder="请输入车间配置"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
