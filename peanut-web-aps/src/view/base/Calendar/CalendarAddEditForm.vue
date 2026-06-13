<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="工厂" prop="factoryId">
      <el-select
        v-model="addForm.factoryId"
        clearable
        placeholder="请输入工厂ID"
      >
        <el-option
          v-for="f in factoryList"
          :key="f.id"
          :label="f.factoryName"
          :value="f.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="名称" prop="calendarName">
      <el-input
        v-model="addForm.calendarName"
        clearable
        placeholder="请输入名称"
        @change="querySzm"
      />
    </el-form-item>
    <el-form-item label="编码" prop="calendarCode">
      <el-input
        v-model="addForm.calendarCode"
        clearable
        placeholder="请输入编码"
      />
    </el-form-item>
    <el-form-item label="状态" prop="calendarDisabled">
      <el-select v-model="addForm.calendarDisabled" clearable>
        <el-option
          v-for="s in calendarDisabledList"
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
import {type Calendar, calendarDisabledList} from './CalendarType.ts';
import {getById, pinyin4jSzm, postNoResult,} from '@/common/utils/common-js.ts';
import {type FormInstance, FormRules} from 'element-plus';
import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';

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
const dtoUrl = ref<string>('/calendar');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 工厂ID
  factoryId: [
    {required: true, message: '请输入工厂ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  calendarName: [
    {required: true, message: '请输入名称', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  calendarCode: [
    {required: true, message: '请输入编码', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 页面加载事件
onMounted(() => {
  loadById();
  queryFactoryList().then((r) => (factoryList.value = r));
});
// 添加对象
const addForm = ref<Calendar>({
  factoryId: '',
  calendarName: '',
  calendarCode: '',
  calendarType: '',
  calendarDesc: '',
  calendarDisabled: false,
  id: '',
});

const factoryList = ref<Factory[]>([]);

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

function querySzm() {
  pinyin4jSzm(addForm.value.calendarName).then(
    (r) => (addForm.value.calendarCode = r)
  );
}
</script>

<style lang="scss" scoped></style>
