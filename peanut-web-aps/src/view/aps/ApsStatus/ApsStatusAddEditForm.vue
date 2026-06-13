<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="名称" prop="statusName">
      <el-input
        v-model="addForm.statusName"
        clearable
        placeholder="请输入名称"
        @change="loadSzm"
      />
    </el-form-item>
    <el-form-item label="编码" prop="statusCode">
      <el-input
        v-model="addForm.statusCode"
        clearable
        placeholder="请输入编码"
      />
    </el-form-item>
    <el-form-item label="排序" prop="sortIndex">
      <el-input-number
        v-model="addForm.sortIndex"
        :max="100"
        :min="1"
        clearable
        placeholder="请输入排序"
        style="width: 100%"
      />
    </el-form-item>
    <el-form-item label="订单状态" prop="orderStatusId">
      <el-select v-model="addForm.orderStatusId" clearable>
        <el-option
          v-for="s in orderStatusList"
          :key="s.value"
          :label="s.label"
          :value="s.value"
        />
      </el-select>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsStatus} from './ApsStatusType.ts';
import {getById, KVEntity, pinyin4jSzm, postNoResult,} from '@/common/utils/common-js.ts';
import {type FormInstance, FormRules} from 'element-plus';
import {queryOrderStatusList,} from '@v/aps/ApsOrder/ApsOrderType.ts';

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
const dtoUrl = ref<string>('/apsStatus');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  statusCode: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  statusName: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

const orderStatusList = ref<KVEntity[]>([]);
// 页面加载事件
onMounted(() => {
  loadById();
  queryOrderStatusList().then((r) => {
    orderStatusList.value = r;
  });
});
// 添加对象
const addForm = ref<ApsStatus>({
  statusCode: '',
  statusName: '',
  sortIndex: 1,
  orderStatusId: '',
  id: undefined,
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
function loadSzm() {
  pinyin4jSzm(addForm.value.statusName).then(
    (r) => (addForm.value.statusCode = r)
  );
}
</script>

<style lang="scss" scoped></style>
