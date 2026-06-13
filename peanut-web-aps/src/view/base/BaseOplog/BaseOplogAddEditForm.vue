<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="操作内容" prop="content">
      <el-input
        v-model="addForm.content"
        clearable
        placeholder="请输入操作内容"
      />
    </el-form-item>
    <el-form-item label="业务Key" prop="businessKey">
      <el-input
        v-model="addForm.businessKey"
        clearable
        placeholder="请输入业务Key"
      />
    </el-form-item>
    <el-form-item label="方法名称" prop="methodName">
      <el-input
        v-model="addForm.methodName"
        clearable
        placeholder="请输入方法名称"
      />
    </el-form-item>
    <el-form-item label="业务类型" prop="businessType">
      <el-input
        v-model="addForm.businessType"
        clearable
        placeholder="请输入业务类型"
      />
    </el-form-item>
    <el-form-item label="业务类型" prop="businessType0">
      <el-input
        v-model="addForm.businessType0"
        clearable
        placeholder="请输入业务类型"
      />
    </el-form-item>
    <el-form-item label="业务类型" prop="businessType1">
      <el-input
        v-model="addForm.businessType1"
        clearable
        placeholder="请输入业务类型"
      />
    </el-form-item>
    <el-form-item label="业务类型" prop="businessType2">
      <el-input
        v-model="addForm.businessType2"
        clearable
        placeholder="请输入业务类型"
      />
    </el-form-item>
    <el-form-item label="业务类型" prop="businessType3">
      <el-input
        v-model="addForm.businessType3"
        clearable
        placeholder="请输入业务类型"
      />
    </el-form-item>
    <el-form-item label="业务类型" prop="businessType4">
      <el-input
        v-model="addForm.businessType4"
        clearable
        placeholder="请输入业务类型"
      />
    </el-form-item>
    <el-form-item label="请求地址" prop="url">
      <el-input v-model="addForm.url" clearable placeholder="请输入请求地址"/>
    </el-form-item>
    <el-form-item label="耗时" prop="useTime">
      <el-input v-model="addForm.useTime" clearable placeholder="请输入耗时"/>
    </el-form-item>
    <el-form-item label="参数名 参数1,参数2" prop="paramName">
      <el-input
        v-model="addForm.paramName"
        clearable
        placeholder="请输入参数名 参数1,参数2"
      />
    </el-form-item>
    <el-form-item label="请求入参" prop="reqBody">
      <el-input
        v-model="addForm.reqBody"
        clearable
        placeholder="请输入请求入参"
      />
    </el-form-item>
    <el-form-item label="是否成功" prop="isSuccess">
      <el-input
        v-model="addForm.isSuccess"
        clearable
        placeholder="请输入是否成功"
      />
    </el-form-item>
    <el-form-item label="请求入参" prop="resultStr">
      <el-input
        v-model="addForm.resultStr"
        clearable
        placeholder="请输入请求入参"
      />
    </el-form-item>
    <el-form-item label="备注" prop="remark">
      <el-input v-model="addForm.remark" clearable placeholder="请输入备注"/>
    </el-form-item>
    <el-form-item label="创建人" prop="createUserName">
      <el-input
        v-model="addForm.createUserName"
        clearable
        placeholder="请输入创建人"
      />
    </el-form-item>
    <el-form-item label="登录手机号" prop="loginPhone">
      <el-input
        v-model="addForm.loginPhone"
        clearable
        placeholder="请输入登录手机号"
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
import {type BaseOplog} from './BaseOplogType.ts';
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
const dtoUrl = ref<string>('/baseOplog');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 操作内容
  content: [
    {required: true, message: '请输入操作内容', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务Key
  businessKey: [
    {required: true, message: '请输入业务Key', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 方法名称
  methodName: [
    {required: true, message: '请输入方法名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务类型
  businessType: [
    {required: true, message: '请输入业务类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务类型
  businessType0: [
    {required: true, message: '请输入业务类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务类型
  businessType1: [
    {required: true, message: '请输入业务类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务类型
  businessType2: [
    {required: true, message: '请输入业务类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务类型
  businessType3: [
    {required: true, message: '请输入业务类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 业务类型
  businessType4: [
    {required: true, message: '请输入业务类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 请求地址
  url: [
    {required: true, message: '请输入请求地址', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 耗时
  useTime: [
    {required: true, message: '请输入耗时', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 参数名 参数1,参数2
  paramName: [
    {required: true, message: '请输入参数名 参数1,参数2', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 请求入参
  reqBody: [
    {required: true, message: '请输入请求入参', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否成功
  isSuccess: [
    {required: true, message: '请输入是否成功', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 请求入参
  resultStr: [
    {required: true, message: '请输入请求入参', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 备注
  remark: [
    {required: true, message: '请输入备注', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 创建人
  createUserName: [
    {required: true, message: '请输入创建人', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 登录手机号
  loginPhone: [
    {required: true, message: '请输入登录手机号', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<BaseOplog>({
  content: '',
  businessKey: '',
  methodName: '',
  businessType: '',
  businessType0: '',
  businessType1: '',
  businessType2: '',
  businessType3: '',
  businessType4: '',
  url: '',
  useTime: '',
  paramName: '',
  reqBody: '',
  isSuccess: '',
  resultStr: '',
  remark: '',
  createUserName: '',
  loginPhone: '',
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
