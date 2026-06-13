<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="80px"
  >
    <el-form-item label="工厂" prop="factoryId">
      <el-select v-model="addForm.factoryId">
        <el-option
          v-for="f in factoryList"
          :key="f.id"
          :label="f.factoryName"
          :value="f.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="名称" prop="shiftName">
      <el-input
        v-model="addForm.shiftName"
        clearable
        placeholder="请输入名称"
        @change="loadSzm"
      />
    </el-form-item>
    <el-form-item label="编码" prop="shiftCode">
      <el-input
        v-model="addForm.shiftCode"
        clearable
        placeholder="请输入编码"
      />
    </el-form-item>
    <el-form-item label="班次">
      <el-button icon="plus" type="primary" @click="addItem">添加</el-button>

      <el-col
        v-for="(item, index) in addForm.shiftItemDtoList"
        :key="index"
        :span="24"
      >
        <el-time-picker
          style="width: 200px"
          v-model="item.beginTime"
          end-placeholder="下班时间"
          format="HH:mm"
          start-placeholder="上班时间"
          value-format="HH:mm"
        />
        -
        <el-time-picker
          style="width: 200px"
          v-model="item.endTime"
          end-placeholder="下班时间"
          format="HH:mm"
          start-placeholder="上班时间"
          value-format="HH:mm"
        />

        <el-button
          icon="delete"
          type="danger"
          @click="deleteItem(index)"
        ></el-button>
      </el-col>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type Shift} from './ShiftType.ts';
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
const dtoUrl = ref<string>('/shift');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  shiftCode: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  shiftName: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  //
  factoryId: [
    {required: true, message: '请输入', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<Shift>({
  shiftCode: '',
  shiftName: '',
  factoryId: '',
  id: '',
  shiftItemDtoList: [],
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
  let shiftItemDtoList = [];
  addForm.value.shiftItemList.forEach((t) => {
    shiftItemDtoList.push(t);
  });
  addForm.value.shiftItemDtoList = shiftItemDtoList;
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
  pinyin4jSzm(addForm.value.shiftName).then(
    (r) => (addForm.value.shiftCode = r)
  );
}

function addItem() {
  if (addForm.value.shiftItemList === undefined)
    addForm.value.shiftItemList = [];
  addForm.value.shiftItemList.push({});
}

function deleteItem(index) {
  addForm.value.shiftItemList.splice(index, 1);
}

// 页面加载事件
onMounted(() => {
  loadById();
  queryFactoryList().then((r) => (factoryList.value = r));
});
</script>

<style lang="scss" scoped></style>
