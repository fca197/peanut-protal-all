<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsRollingForecastFactoryCapacity} from './ApsRollingForecastFactoryCapacityType.ts';
import {postNoResult} from '@/common/utils/common-js.ts';
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
const dtoUrl = ref<string>('/apsRollingForecastFactoryCapacity');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({});

// 添加对象
const addForm = ref<ApsRollingForecastFactoryCapacity>({
  capacityList: [{}],
});

// 保存
const saveForm = () => {
  console.info('addForm ', addForm);
  let list = addForm.value.capacityList;
  for (let i = 0; i < list.length; i++) {
    let t = list[i];
    if (t.timeRange === undefined || t.timeRange.length !== 2) {
      ElMessage.error('请选择时间范围');
      return;
    }
    if (t.capacity === undefined || t.capacity === '') {
      ElMessage.error('请选择时间范围');
      return;
    }
    t.beginTime = t.timeRange[0];
    t.endTime = t.timeRange[1];
  }
  postNoResult(
    '/apsRollingForecastFactoryCapacity/insert',
    {...addForm.value},
    '添加成功',
    saveFormAfter
  );
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

const addCapacity = () => {
  addForm.value.capacityList.push({});
};
const deleteCapacity = (index) => {
  console.info(
    'addForm.value.capacityList?.length',
    addForm.value.capacityList?.length,
    index
  );
  if (addForm.value.capacityList.length > 1) {
    addForm.value.capacityList.splice(index, 1);
    return;
  }
  ElMessage.error('请保留一条');
};

const factoryList = ref<Factory[]>([]);
// 页面加载事件
onMounted(() => {
  addForm.value.capacityList = [{}];
  // loadById()
  queryFactoryList().then((r) => {
    factoryList.value = r;
    addForm.value.factoryId = r[0].id;
  });
});
</script>

<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="100px"
  >
    <el-form-item label="工厂" prop="factoryId">
      <el-select v-model="addForm.factoryId" clearable style="width: 100%">
        <el-option
          v-for="f in factoryList"
          :key="f.id"
          :label="f.factoryName"
          :value="f.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item
      v-for="(c, i) in addForm.capacityList"
      :key="i"
      label="产能"
      prop="capacityList"
    >
      <el-date-picker
        v-model="c.timeRange"
        placeholder="选择日期"
        style="width: 300px"
        type="daterange"
        value-format="YYYY-MM-DD"
      />
      <el-input
        v-model="c.capacity"
        clearable
        placeholder="请输入产能"
        style="width: 150px; margin: 0 5px 0"
      />
      <el-button type="primary" icon="plus" @click="addCapacity()"></el-button>
      <el-button
        icon="minus"
        type="danger"
        @click="deleteCapacity(i)"
      ></el-button>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
