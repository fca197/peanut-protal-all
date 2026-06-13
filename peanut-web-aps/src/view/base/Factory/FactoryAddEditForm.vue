<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type Factory, factoryStatusList} from './FactoryType.ts';
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

//  对象URL
const dtoUrl = ref<string>('/factory');
//  表单引用
const addFormRef = ref<FormInstance>();
//  表单校验规则
const checkRules = ref<FormRules>({
  factoryName: [
    {required: true, message: '请输入工厂名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  factoryCode: [
    {required: true, message: '请输入工厂编码', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  factoryStatus: [
    {required: true, message: '请输入状态', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

//  页面加载事件
onMounted(() => {
  loadById();
});
//  添加对象
const addForm = ref<Factory>({
  factoryName: '',
  factoryCode: '',
  factoryStatus: 'ENABLED',
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
      //  存在ID ，调用更新
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

//  取消方法
function cancelForm() {
  if (props.saveFun) {
    props.saveFun();
  }
}

const factoryNameBlur = () => {
  pinyin4jSzm(addForm.value.factoryName).then((t) => {
    addForm.value.factoryCode = t;
  });
};
</script>

<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="工厂名称" prop="factoryName">
      <el-input
        v-model="addForm.factoryName"
        clearable
        placeholder="请输入工厂名称"
        @blur="factoryNameBlur"
      />
    </el-form-item>
    <el-form-item label="工厂编码" prop="factoryCode">
      <el-input
        v-model="addForm.factoryCode"
        clearable
        placeholder="请输入工厂编码"
      />
    </el-form-item>
    <el-form-item label="工厂状态" prop="factoryStatus">
      <el-select v-model="addForm.factoryStatus">
        <el-option
          v-for="s in factoryStatusList"
          :label="s.label"
          :value="s.value"
          :key="s.value"
        />
      </el-select>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
