<script setup lang="ts">
// 获取当前路由信息
import {useRoute} from 'vue-router';
import CreateSchedulingStepOne from './CreateSchedulingStepOne.vue';
import UseConstraintsResult from './UseConstraintsResult.vue';
import UseMakeCapacityResult from './UseMakeCapacityResult.vue';
import CreateSuccess from './CreateSuccess.vue';

const route = useRoute();

const currentId = ref<string>(route.params.id);
const operType = ref<string>(route.params.operType);
const step = ref<string>(route.params.step);
console.info('params ', currentId, operType, step);

const active = ref<number>(parseInt(step.value));

const toStep1 = () => {
  console.log('toStep ', 1);
  active.value = 1;
};
const toStep2 = (r) => {
  console.log('toStep ', 2, r);
  if (r !== undefined) {
    currentId.value = r.id;
  }
  active.value = 2;
};
const toStep3 = () => {
  console.log('toStep ', 3);
  active.value = 3;
};
const toStep4 = () => {
  console.log('toStep ', 4);
  active.value = 4;
};
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-steps :active="active" finish-status="success" simple>
        <el-step title="基本信息" icon="Edit"></el-step>
        <el-step title="订单排序" icon="Sort"></el-step>
        <el-step title="产能排单" icon="Operation"></el-step>
        <el-step title="结束"></el-step>
      </el-steps>
    </el-card>

    <el-card shadow="never">
      <div v-if="active === 1">
        <CreateSchedulingStepOne
          :id="currentId"
          :oper-type="operType"
          :save-after-fun="toStep2"
        />
      </div>

      <div v-if="active === 2">
        <UseConstraintsResult
          :id="currentId"
          :oper-type="operType"
          :save-after-fun="toStep3"
          :pre-step-fun="toStep1"
        />
      </div>
      <div v-if="active === 3">
        <UseMakeCapacityResult
          :id="currentId"
          :oper-type="operType"
          :save-after-fun="toStep4"
          :pre-step-fun="toStep2"
        />
      </div>
      <div v-if="active === 4">
        <CreateSuccess
          :id="currentId"
          :oper-type="operType"
          :pre-step-fun="toStep2"
        />
      </div>
    </el-card>
  </div>
</template>

<style lang="scss" scoped></style>
