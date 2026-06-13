<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="菜单编码" prop="resourceCode">
      <el-input
        v-model="addForm.resourceCode"
        clearable
        placeholder="请输入菜单编码"
      />
    </el-form-item>
    <el-form-item label="菜单名称" prop="resourceName">
      <el-input
        v-model="addForm.resourceName"
        clearable
        placeholder="请输入菜单名称"
      />
    </el-form-item>
    <el-form-item label="菜单URL" prop="resourceUrl">
      <el-input
        v-model="addForm.resourceUrl"
        clearable
        placeholder="请输入菜单URL"
      />
    </el-form-item>
    <el-form-item label="下个版本" prop="nextLevelResourceUrl">
      <el-input
        v-model="addForm.nextLevelResourceUrl"
        clearable
        placeholder="请输入下个版本"
      />
    </el-form-item>
    <el-form-item label="组建路径" prop="resourceComment">
      <el-input
        v-model="addForm.resourceComment"
        clearable
        placeholder="请输入组建路径"
      />
    </el-form-item>
    <el-form-item label="菜单图标" prop="resourceIcon">
      <el-input
        v-model="addForm.resourceIcon"
        clearable
        placeholder="请输入菜单图标"
      />
    </el-form-item>
    <el-form-item label="菜单类型" prop="resourceType">
      <el-input
        v-model="addForm.resourceType"
        clearable
        placeholder="请输入菜单类型"
      />
    </el-form-item>
    <el-form-item label="是否按钮 0 否,1 是" prop="isButton">
      <el-input
        v-model="addForm.isButton"
        clearable
        placeholder="请输入是否按钮 0 否,1 是"
      />
    </el-form-item>
    <el-form-item label="是否因此 0否,1是" prop="isHidden">
      <el-input
        v-model="addForm.isHidden"
        clearable
        placeholder="请输入是否因此 0否,1是"
      />
    </el-form-item>
    <el-form-item label="父菜单ID" prop="parentId">
      <el-input
        v-model="addForm.parentId"
        clearable
        placeholder="请输入父菜单ID"
      />
    </el-form-item>
    <el-form-item label="菜单路径" prop="path">
      <el-input v-model="addForm.path" clearable placeholder="请输入菜单路径"/>
    </el-form-item>
    <el-form-item label="排序" prop="sortIndex">
      <el-input
        v-model="addForm.sortIndex"
        clearable
        placeholder="请输入排序"
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
import {type BaseResource} from './BaseResourceType.ts';
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
const dtoUrl = ref<string>('/baseResource');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 菜单编码
  resourceCode: [
    {required: true, message: '请输入菜单编码', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 菜单名称
  resourceName: [
    {required: true, message: '请输入菜单名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 菜单URL
  resourceUrl: [
    {required: true, message: '请输入菜单URL', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 下个版本
  nextLevelResourceUrl: [
    {required: true, message: '请输入下个版本', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 组建路径
  resourceComment: [
    {required: true, message: '请输入组建路径', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 菜单图标
  resourceIcon: [
    {required: true, message: '请输入菜单图标', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 菜单类型
  resourceType: [
    {required: true, message: '请输入菜单类型', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否按钮 0 否,1 是
  isButton: [
    {required: true, message: '请输入是否按钮 0 否,1 是', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 是否因此 0否,1是
  isHidden: [
    {required: true, message: '请输入是否因此 0否,1是', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 父菜单ID
  parentId: [
    {required: true, message: '请输入父菜单ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 菜单路径
  path: [
    {required: true, message: '请输入菜单路径', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排序
  sortIndex: [
    {required: true, message: '请输入排序', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<BaseResource>({
  resourceCode: '',
  resourceName: '',
  resourceUrl: '',
  nextLevelResourceUrl: '',
  resourceComment: '',
  resourceIcon: '',
  resourceType: '',
  isButton: '',
  isHidden: '',
  parentId: '',
  path: '',
  sortIndex: '',
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
