<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingDayConfigVersionDetail} from './ApsSchedulingDayConfigVersionDetailType.ts';
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
const dtoUrl = ref<string>('/apsSchedulingDayConfigVersionDetail');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  schedulingDayId: [
    {required: true, message: '请输入${column.comment}', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 配置类型 sale,part,bom
  configBizType: [
    {
      required: true,
      message: '请输入配置类型 sale,part,bom',
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
  // 订单ID
  orderId: [
    {required: true, message: '请输入订单ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 订单编号
  orderNo: [
    {required: true, message: '请输入订单编号', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 房间id
  roomId: [
    {required: true, message: '请输入房间id', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 状态 Id
  statusId: [
    {required: true, message: '请输入状态 Id', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排序
  sortIndex: [
    {required: true, message: '请输入排序', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否匹配 0 否,1 是
  isMatch: [
    {required: true, message: '请输入是否匹配 0 否,1 是', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 循环次数
  loopIndex: [
    {required: true, message: '请输入循环次数', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否满足 0 否,1 是
  loopEnough: [
    {required: true, message: '请输入是否满足 0 否,1 是', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingDayConfigVersionDetail>({
  schedulingDayId: '',
  configBizType: '',
  configBizId: '',
  configBizName: '',
  configBizNum: '',
  orderId: '',
  orderNo: '',
  roomId: '',
  statusId: '',
  sortIndex: '',
  isMatch: '',
  loopIndex: '',
  loopEnough: '',
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
    <el-form-item label="${column.comment}" prop="schedulingDayId">
      <el-input
        v-model="addForm.schedulingDayId"
        clearable
        placeholder="请输入${column.comment}"
      />
    </el-form-item>
    <el-form-item label="配置类型 sale,part,bom" prop="configBizType">
      <el-input
        v-model="addForm.configBizType"
        clearable
        placeholder="请输入配置类型 sale,part,bom"
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
    <el-form-item label="订单ID" prop="orderId">
      <el-input
        v-model="addForm.orderId"
        clearable
        placeholder="请输入订单ID"
      />
    </el-form-item>
    <el-form-item label="订单编号" prop="orderNo">
      <el-input
        v-model="addForm.orderNo"
        clearable
        placeholder="请输入订单编号"
      />
    </el-form-item>
    <el-form-item label="房间id" prop="roomId">
      <el-input v-model="addForm.roomId" clearable placeholder="请输入房间id"/>
    </el-form-item>
    <el-form-item label="状态 Id" prop="statusId">
      <el-input
        v-model="addForm.statusId"
        clearable
        placeholder="请输入状态 Id"
      />
    </el-form-item>
    <el-form-item label="排序" prop="sortIndex">
      <el-input
        v-model="addForm.sortIndex"
        clearable
        placeholder="请输入排序"
      />
    </el-form-item>
    <el-form-item label="是否匹配 0 否,1 是" prop="isMatch">
      <el-input
        v-model="addForm.isMatch"
        clearable
        placeholder="请输入是否匹配 0 否,1 是"
      />
    </el-form-item>
    <el-form-item label="循环次数" prop="loopIndex">
      <el-input
        v-model="addForm.loopIndex"
        clearable
        placeholder="请输入循环次数"
      />
    </el-form-item>
    <el-form-item label="是否满足 0 否,1 是" prop="loopEnough">
      <el-input
        v-model="addForm.loopEnough"
        clearable
        placeholder="请输入是否满足 0 否,1 是"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
