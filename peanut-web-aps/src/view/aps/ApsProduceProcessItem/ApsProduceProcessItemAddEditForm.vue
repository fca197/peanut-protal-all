<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsProduceProcessItem} from './ApsProduceProcessItemType.ts';
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
const dtoUrl = ref<string>('/apsProduceProcessItem');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 生产路径 Id aps_produce_process
  produceProcessId: [
    {
      required: true,
      message: '请输入生产路径 Id aps_produce_process',
      trigger: 'blur',
    },
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 机器ID
  machineId: [
    {required: true, message: '请输入机器ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 状态ID
  goodsStatusId: [
    {required: true, message: '请输入状态ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 耗时（秒）
  useTime: [
    {required: true, message: '请输入耗时（秒）', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsProduceProcessItem>({
  produceProcessId: '',
  machineId: '',
  goodsStatusId: '',
  useTime: '',
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

<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item
      label="生产路径 Id aps_produce_process"
      prop="produceProcessId"
    >
      <el-input
        v-model="addForm.produceProcessId"
        clearable
        placeholder="请输入生产路径 Id aps_produce_process"
      />
    </el-form-item>
    <el-form-item label="机器ID" prop="machineId">
      <el-input
        v-model="addForm.machineId"
        clearable
        placeholder="请输入机器ID"
      />
    </el-form-item>
    <el-form-item label="状态ID" prop="goodsStatusId">
      <el-input
        v-model="addForm.goodsStatusId"
        clearable
        placeholder="请输入状态ID"
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
