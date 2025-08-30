<script setup lang="ts">
import {onMounted, ref} from "vue"
import {type StoreShoppingMall} from "./StoreShoppingMallType.ts"
import {getById, postNoResult} from "@/common/utils/common-js.ts"
import {type FormInstance, FormRules} from "element-plus"
import DistrictCodeForm from "@v/DistrictCode/DistrictCodeForm.vue";

const props = defineProps({
  saveFun: {
    type: Function
  },
  editId: {
    type: String,
    required: false
  }
})
const loadEntity = ref<boolean>(true)
// 对象URL
const dtoUrl = ref<string>("/storeShoppingMall")
// 表单引用
const addFormRef = ref<FormInstance>()
// 表单校验规则
const checkRules = ref<FormRules>({

  // 地址
  shoppingMallAddress: [
    {required: true, message: "请输入地址", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 经度
  shoppingMallLocationLng: [
    {required: true, message: "请输入经度", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 纬度
  shoppingMallLocationLat: [
    {required: true, message: "请输入纬度", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 商场名称
  shoppingMallName: [
    {required: true, message: "请输入商场名称", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 商场别称
  businessAlias: [
    {required: true, message: "请输入商场别称", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 营业开始时间
  businessOpenTimeTodayOpen: [
    {required: true, message: "请输入营业开始时间", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 营业结束时间
  businessOpenTimeTodayClose: [
    {required: true, message: "请输入营业结束时间", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 评分
  businessRating: [
    {required: true, message: "请输入评分", trigger: "blur"},
  ],
  // 入口经纬度经度
  enterLocationLng: [
    {required: true, message: "请输入入口经纬度经度", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 入口经纬度纬度
  enterLocationLat: [
    {required: true, message: "请输入入口经纬度纬度", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],

})


// 添加对象
const addForm = ref<StoreShoppingMall>({
  countryCode: undefined,
  provinceCode: undefined,
  cityCode: undefined,
  areaCode: undefined,
  countryName: undefined,
  provinceName: undefined,
  cityName: undefined,
  areaName: undefined,
  belongDistrictId: undefined,
  belongDistrictIdList: undefined,
  shoppingMallAddress: undefined,
  shoppingMallLocationLng: undefined,
  shoppingMallLocationLat: undefined,
  shoppingMallName: undefined,
  businessAlias: undefined,
  businessKeytag: undefined,
  businessOpenTimeTodayOpen: undefined,
  businessOpenTimeTodayClose: undefined,
  businessRating: undefined,
  businessTag: undefined,
  businessTel: [],
  enterLocationLng: undefined,
  enterLocationLat: undefined,
  photos: undefined,
  id: undefined
})

const loadById = () => {
  if (!props.editId) {
    loadEntity.value = false
    return
  }
  console.info("props.editId ", props.editId)
  getById(`${dtoUrl.value}/queryByIdList`, props.editId).then((t) => {
    addForm.value = t
    console.info(" addForm.value ", addForm.value)
    loadEntity.value = false
  })
}

// 保存
const saveForm = () => {
  console.info("addForm ", addForm)
  addFormRef.value?.validate((valid) => {
    if (valid) {
      // 存在ID ，调用更新
      if (props.editId) {
        postNoResult(`${dtoUrl.value}/updateById`, addForm.value, "修改成功", saveFormAfter)
      } else {
        // 调用保存
        postNoResult(`${dtoUrl.value}/insert`, addForm.value, "保存成功", saveFormAfter)
      }
    } else {
      ElMessage.error("表单校验失败，请检查必填项")
    }
  })
}

// 保存成功后，方法， 目前关闭弹窗
const saveFormAfter = () => {
  cancelForm()
}

// 取消方法
const cancelForm = () => {
  if (props.saveFun) {
    props.saveFun()
  }
}

// 页面加载事件
onMounted(() => {
  loadById()
})
</script>

<template>
  <el-form v-loading="loadEntity" label-width="110px" :model="addForm" ref="addFormRef" :rules="checkRules">
    <district-code-form :form-obj="addForm"/>

    <el-form-item label="地址" prop="shoppingMallAddress">
      <el-input
        v-model="addForm.shoppingMallAddress"
        clearable
        placeholder="请输入地址"
      />
    </el-form-item>
    <el-form-item label="经度" prop="shoppingMallLocationLng">
      <el-input
        v-model="addForm.shoppingMallLocationLng"
        clearable
        placeholder="请输入经度"
      />
    </el-form-item>
    <el-form-item label="纬度" prop="shoppingMallLocationLat">
      <el-input
        v-model="addForm.shoppingMallLocationLat"
        clearable
        placeholder="请输入纬度"
      />
    </el-form-item>
    <el-form-item label="商场名称" prop="shoppingMallName">
      <el-input
        v-model="addForm.shoppingMallName"
        clearable
        placeholder="请输入商场名称"
      />
    </el-form-item>
    <el-form-item label="商场别称" prop="businessAlias">
      <el-input
        v-model="addForm.businessAlias"
        clearable
        placeholder="请输入商场别称"
      />
    </el-form-item>
    <el-form-item label="营业开始时间" prop="businessOpenTimeTodayOpen">
      <el-time-picker
        value-format="HH:mm:ss"
        v-model="addForm.businessOpenTimeTodayOpen"
        clearable
        placeholder="请输入营业开始时间"
      />
    </el-form-item>
    <el-form-item label="营业结束时间" prop="businessOpenTimeTodayClose">
      <el-time-picker
        value-format="HH:mm:ss"
        v-model="addForm.businessOpenTimeTodayClose"
        clearable
        placeholder="请输入营业结束时间"
      />
    </el-form-item>
    <el-form-item label="评分" prop="businessRating">
      <el-rate
        v-model="addForm.businessRating"
        clearable
        placeholder="请输入评分"
      />
    </el-form-item>

    <el-form-item label="联系电话" prop="businessTel">
      <!-- 循环时获取索引 index，通过索引绑定原数组元素 -->
      <div style="width: 100%">
        <el-button type="primary" size="small" @click="addForm.businessTel.push('')">添加</el-button>
      </div>
      <div style="width: 100%"
           v-for="(t, index) in addForm.businessTel"
           :key="index"
      >
        <el-input v-model="addForm.businessTel[index]" style="width: 70%"></el-input>
        <el-button size="small" type="danger" @click="addForm.businessTel.splice(index, 1)">删除</el-button>
      </div>
    </el-form-item>
    <el-form-item label="入口经度" prop="enterLocationLng">
      <el-input
        v-model="addForm.enterLocationLng"
        clearable
        placeholder="请输入入口经度"
      />
    </el-form-item>
    <el-form-item label="入口纬度" prop="enterLocationLat">
      <el-input
        v-model="addForm.enterLocationLat"
        clearable
        placeholder="请输入入口纬度"
      />
    </el-form-item>
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button @click="cancelForm" type="info" icon="close">
      取消
    </el-button>
    <el-button @click="saveForm" type="primary" icon="check">
      确定
    </el-button>
  </el-row>
</template>

<style scoped lang="scss">
</style>
