<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingVersion} from './ApsSchedulingVersionType.ts';
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
const dtoUrl = ref<string>('/apsSchedulingVersion');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  schedulingVersionNo: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  schedulingVersionName: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  schedulingConstraintsId: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  headerList: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  capacityHeaderList: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  capacityDateList: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  schedulingDayCount: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  versionStep: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  versionStepError: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // bom汇总结束日期
  bomTotalEndDate: [
    {required: true, message: '请输入bom汇总结束日期', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 开始时间
  startDate: [
    {required: true, message: '请输入开始时间', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 使用工厂产能约束
  useFactoryMakeCapacity: [
    {required: true, message: '请输入使用工厂产能约束', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 使用商品产能约束
  useGoodsMakeCapacity: [
    {required: true, message: '请输入使用商品产能约束', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 使用销售配置产能约束
  useSaleConfigMakeCapacity: [
    {required: true, message: '请输入使用销售配置产能约束', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 使用工程配置产能约束
  useProjectConfigMakeCapacity: [
    {required: true, message: '请输入使用工程配置产能约束', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 工厂ID
  factoryIdList: [
    {required: true, message: '请输入工厂ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 商品ID
  goodsIdList: [
    {required: true, message: '请输入商品ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingVersion>({
  schedulingVersionNo: '',
  schedulingVersionName: '',
  schedulingConstraintsId: '',
  headerList: '',
  capacityHeaderList: '',
  capacityDateList: '',
  schedulingDayCount: '',
  versionStep: '',
  versionStepError: '',
  bomTotalEndDate: '',
  startDate: '',
  useFactoryMakeCapacity: '',
  useGoodsMakeCapacity: '',
  useSaleConfigMakeCapacity: '',
  useProjectConfigMakeCapacity: '',
  factoryIdList: '',
  goodsIdList: '',
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
    <el-form-item label="${column.comment}" prop="schedulingVersionNo">
      <el-input
        v-model="addForm.schedulingVersionNo"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="schedulingVersionName">
      <el-input
        v-model="addForm.schedulingVersionName"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="schedulingConstraintsId">
      <el-input
        v-model="addForm.schedulingConstraintsId"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="headerList">
      <el-input
        v-model="addForm.headerList"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="capacityHeaderList">
      <el-input
        v-model="addForm.capacityHeaderList"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="capacityDateList">
      <el-input
        v-model="addForm.capacityDateList"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="schedulingDayCount">
      <el-input
        v-model="addForm.schedulingDayCount"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="versionStep">
      <el-input
        v-model="addForm.versionStep"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="${column.comment}" prop="versionStepError">
      <el-input
        v-model="addForm.versionStepError"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="bom汇总结束日期" prop="bomTotalEndDate">
      <el-input
        v-model="addForm.bomTotalEndDate"
        clearable
        placeholder="请输入bom汇总结束日期"
      />
    </el-form-item>
    <el-form-item label="开始时间" prop="startDate">
      <el-input
        v-model="addForm.startDate"
        clearable
        placeholder="请输入开始时间"
      />
    </el-form-item>
    <el-form-item label="使用工厂产能约束" prop="useFactoryMakeCapacity">
      <el-input
        v-model="addForm.useFactoryMakeCapacity"
        clearable
        placeholder="请输入使用工厂产能约束"
      />
    </el-form-item>
    <el-form-item label="使用商品产能约束" prop="useGoodsMakeCapacity">
      <el-input
        v-model="addForm.useGoodsMakeCapacity"
        clearable
        placeholder="请输入使用商品产能约束"
      />
    </el-form-item>
    <el-form-item label="使用销售配置产能约束" prop="useSaleConfigMakeCapacity">
      <el-input
        v-model="addForm.useSaleConfigMakeCapacity"
        clearable
        placeholder="请输入使用销售配置产能约束"
      />
    </el-form-item>
    <el-form-item
      label="使用工程配置产能约束"
      prop="useProjectConfigMakeCapacity"
    >
      <el-input
        v-model="addForm.useProjectConfigMakeCapacity"
        clearable
        placeholder="请输入使用工程配置产能约束"
      />
    </el-form-item>
    <el-form-item label="工厂" prop="factoryIdList">
      <el-input
        v-model="addForm.factoryIdList"
        clearable
        placeholder="请输入工厂ID"
      />
    </el-form-item>
    <el-form-item label="商品" prop="goodsIdList">
      <el-input
        v-model="addForm.goodsIdList"
        clearable
        placeholder="请输入商品ID"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
