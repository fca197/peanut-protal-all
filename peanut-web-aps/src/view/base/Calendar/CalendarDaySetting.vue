<template>
  <el-form
    ref="form"
    :model="openSettingDayForm"
    :rules="openSettingDayFormRules"
    label-width="100px"
  >
    <el-form-item label="默认工作日" prop="workYear">
      <el-select
        v-model="openSettingDayForm.workYear"
        filterable
        multiple
        placeholder="请选择"
        style="width: 90%"
      >
        <el-option
          v-for="(item, index) in workYearList"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="默认工作日" prop="defaultWorkDay">
      <el-checkbox-group v-model.trim="openSettingDayForm.defaultWorkDay">
        <el-checkbox
          v-for="(item, index) in weekDays"
          :key="index + 1"
          :label="index + 1"
          :value="index"
        >{{ item }}
        </el-checkbox>
      </el-checkbox-group>
    </el-form-item>
    <el-form-item label="增加工作日" prop="workDayList">
      <el-button icon="plus" @click="addWorkDay" type="primary">
        添加
      </el-button>
      <el-form-item v-for="(item, index) in openSettingDayForm.workDayList">
        <el-date-picker
          style="width: 45%"
          v-model="item.date"
          end-placeholder="结束日期"
          range-separator="-"
          start-placeholder="开始日期"
          type="daterange"
          value-format="yyyy-MM-DD"
        />

        <el-button type="danger" icon="Minus" @click="deleteWorkDay(index)">
          删除
        </el-button>
      </el-form-item>
    </el-form-item>
    <el-form-item label="增加休息日" prop="noWorkDayList">
      <el-button icon="plus" @click="addNoWorkDay" type="primary">
        添加
      </el-button>
      <el-form-item v-for="(item, index) in openSettingDayForm.noWorkDayList">
        <el-date-picker
          style="width: 30%"
          v-model="item.date"
          end-placeholder="结束日期"
          range-separator="-"
          start-placeholder="开始日期"
          type="daterange"
          value-format="yyyy-MM-DD"
        />
        <el-button type="danger" icon="Minus" @click="deleteNoWorkDay(index)">
          <delete/>
          删除
        </el-button>
      </el-form-item>
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<script setup lang="ts">
import {Delete} from '@element-plus/icons-vue';
import {postNoResult} from '@@/utils/common-js.ts';

const props = defineProps({
  calendarId: {
    type: String,
    required: true,
  },
  cancelFormFun: {
    type: Function,
    required: true,
  },
});

const workYearList = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map(
  (val) => new Date().getFullYear() + val
);

const weekDays = ['一', '二', '三', '四', '五', '六', '天'].map(
  (val) => '星期' + val
);

const openSettingDayFormRules = [];

onMounted(() => {
  console.info('props.calendarId ', props.calendarId);
});
const openSettingDayForm = ref({
  id: undefined,
  workYear: undefined,
  defaultWorkDay: [0, 1, 2, 3, 4],
  workDayList: [],
  noWorkDayList: [],
});

function addWorkDay() {
  openSettingDayForm.value.workDayList.push({date: undefined});
}

function deleteWorkDay(index) {
  openSettingDayForm.value.workDayList.splice(index, 1);
}

function addNoWorkDay() {
  openSettingDayForm.value.noWorkDayList.push({date: undefined});
}

function deleteNoWorkDay(index) {
  openSettingDayForm.value.noWorkDayList.splice(index, 1);
}

function calendarChange(v) {
  console.info('calendarChange ', v);
}

function cancelForm() {
  props.cancelFormFun();
}

function saveForm() {
  const req = Object.assign({}, openSettingDayForm.value);
  req.id = openSettingDayForm.id;
  req.workDayList = openSettingDayForm.workDayList?.map((t) => t.date);
  req.noWorkDayList = openSettingDayForm.noWorkDayList?.map((t) => t.date);
  req.defaultWorkDayList = openSettingDayForm.defaultWorkDay;
  console.log('saveForm req ', req);
  postNoResult('/calendar/day/update', req, '保存成功', () => cancelForm());
}
</script>

<style lang="scss" scoped></style>
