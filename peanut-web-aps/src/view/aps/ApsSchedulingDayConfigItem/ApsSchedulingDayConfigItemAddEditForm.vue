<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingDayConfigItem} from './ApsSchedulingDayConfigItemType.ts';
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
const dtoUrl = ref<string>('/apsSchedulingDayConfigItem');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 排程版本ID
  schedulingDayId: [
    {required: true, message: '请输入排程版本ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 工艺路径ID
  processId: [
    {required: true, message: '请输入工艺路径ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 车间ID
  roomId: [
    {required: true, message: '请输入车间ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 状态ID
  statusId: [
    {required: true, message: '请输入状态ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 配置类型 sale,part,bom ,sleep
  configBizType: [
    {
      required: true,
      message: '请输入配置类型 sale,part,bom ,sleep',
      trigger: 'blur',
    },
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 配置业务ID
  configBizId: [
    {required: true, message: '请输入配置业务ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 配置业务名称
  configBizName: [
    {required: true, message: '请输入配置业务名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 配置业务数量
  configBizNum: [
    {required: true, message: '请输入配置业务数量', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 配置业务耗时(秒)
  configBizTime: [
    {required: true, message: '请输入配置业务耗时(秒)', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否默认 0 否,1 是
  isDefault: [
    {required: true, message: '请输入是否默认 0 否,1 是', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingDayConfigItem>({
  schedulingDayId: '',
  processId: '',
  roomId: '',
  statusId: '',
  configBizType: '',
  configBizId: '',
  configBizName: '',
  configBizNum: '',
  configBizTime: '',
  isDefault: '',
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
    <el-form-item label="排程版本ID" prop="schedulingDayId">
      <el-input
        v-model="addForm.schedulingDayId"
        clearable
        placeholder="请输入排程版本ID"
      />
    </el-form-item>
    <el-form-item label="工艺路径ID" prop="processId">
      <el-input
        v-model="addForm.processId"
        clearable
        placeholder="请输入工艺路径ID"
      />
    </el-form-item>
    <el-form-item label="车间ID" prop="roomId">
      <el-input v-model="addForm.roomId" clearable placeholder="请输入车间ID" />
    </el-form-item>
    <el-form-item label="状态ID" prop="statusId">
      <el-input
        v-model="addForm.statusId"
        clearable
        placeholder="请输入状态ID"
      />
    </el-form-item>
    <el-form-item label="配置类型 sale,part,bom ,sleep" prop="configBizType">
      <el-input
        v-model="addForm.configBizType"
        clearable
        placeholder="请输入配置类型 sale,part,bom ,sleep"
      />
    </el-form-item>
    <el-form-item label="配置业务ID" prop="configBizId">
      <el-input
        v-model="addForm.configBizId"
        clearable
        placeholder="请输入配置业务ID"
      />
    </el-form-item>
    <el-form-item label="配置业务名称" prop="configBizName">
      <el-input
        v-model="addForm.configBizName"
        clearable
        placeholder="请输入配置业务名称"
      />
    </el-form-item>
    <el-form-item label="配置业务数量" prop="configBizNum">
      <el-input
        v-model="addForm.configBizNum"
        clearable
        placeholder="请输入配置业务数量"
      />
    </el-form-item>
    <el-form-item label="配置业务耗时(秒)" prop="configBizTime">
      <el-input
        v-model="addForm.configBizTime"
        clearable
        placeholder="请输入配置业务耗时(秒)"
      />
    </el-form-item>
    <el-form-item label="是否默认 0 否,1 是" prop="isDefault">
      <el-input
        v-model="addForm.isDefault"
        clearable
        placeholder="请输入是否默认 0 否,1 是"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
