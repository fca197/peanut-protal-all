<script setup lang="ts">
import {onMounted, ref} from "vue"
import {type StoreBusinessDistrict} from "./StoreBusinessDistrictType.ts"
import {getById, pinyin4jSzmV4, postNoResult} from "@/common/utils/common-js.ts"
import {type FormInstance, FormRules} from "element-plus"
import {queryBrandList, TBrand} from "@v/base/TBrand/TBrandType.ts";
import {queryLevelList, StoreBusinessDistrictLevel} from "@v/store/StoreBusinessDistrictLevel/StoreBusinessDistrictLevelType.ts";
import {queryStoreBusinessDistrictTypeList, StoreBusinessDistrictType} from "@v/store/StoreBusinessDistrictType/StoreBusinessDistrictTypeType.ts";
import DistrictCodeForm from "@v/DistrictCode/DistrictCodeForm.vue";
import GetLngLatTips from "@v/DistrictCode/GetLngLatTips.vue";

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
const dtoUrl = ref<string>("/storeBusinessDistrict")
// 表单引用
const addFormRef = ref<FormInstance>()
// 表单校验规则
const checkRules = ref<FormRules>({
  // 品牌
  brandId: [
    {required: true, message: "请输入品牌", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 编码
  businessDistrictCode: [
    {required: true, message: "请输入编码", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 名称
  businessDistrictName: [
    {required: true, message: "请输入名称", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 地址
  businessDistrictAddress: [
    {required: true, message: "请输入地址", trigger: "blur"},
    {min: 2, max: 200, message: "长度在 2 到 200 个字符", trigger: "blur"}
  ],
  // 半径
  businessDistrictRadius: [
    {required: true, message: "请输入半径", trigger: "blur"},
  ],
  // 商圈级别
  businessDistrictLevelId: [
    {required: true, message: "请选择商圈级别", trigger: "blur"},
  ],
  // 商圈类别
  businessDistrictTypeId: [
    {required: true, message: "请选择商圈类别", trigger: "blur"},
  ],
  // 纬度
  centerLat: [
    {required: true, message: "请输入纬度", trigger: "blur"},
  ],
  // 经度
  centerLng: [
    {required: true, message: "请输入经度", trigger: "blur"},
  ]
})


// 添加对象
const addForm = ref<StoreBusinessDistrict>({
  brandId: undefined,
  businessDistrictCode: undefined,
  businessDistrictName: undefined,
  businessDistrictDesc: undefined,
  businessDistrictAddress: undefined,
  countryCode: undefined,
  provinceCode: undefined,
  cityCode: undefined,
  areaCode: undefined,
  countryName: undefined,
  provinceName: undefined,
  cityName: undefined,
  areaName: undefined,
  businessDistrictRadius: 1000,
  businessDistrictSearchRadius: 0,
  businessDistrictLevelId: undefined,
  businessDistrictTypeId: undefined,
  centerLat: undefined,
  centerLng: undefined,
  id: undefined,
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
      // 存在 ，调用更新
      if (props.editId) {
        postNoResult(`${dtoUrl.value}/updateById`, addForm.value, "修改成功", saveFormAfter)
      } else {
        // 调用保存
        postNoResult(`${dtoUrl.value}/insertAll`, addForm.value, "保存成功", saveFormAfter)
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
const brandList = ref<TBrand[]>([])
const levelList = ref<StoreBusinessDistrictLevel[]>([])
const loadSzm = () => {
  pinyin4jSzmV4(addForm.value.businessDistrictName, addForm, "businessDistrictCode")
}

const typeList = ref<StoreBusinessDistrictType[]>([])
// 页面加载事件
onMounted(() => {
  loadById()
  queryBrandList().then((t) => {
    brandList.value = t
    addForm.value.brandId = brandList.value[0].id
  })
  queryLevelList().then((t) => {
    levelList.value = t
    addForm.value.businessDistrictLevelId = levelList.value[0].id
  })
  //mapDiv
  queryStoreBusinessDistrictTypeList(typeList).then(() => {
    addForm.value.businessDistrictTypeId = typeList.value[0].id
  })
})
</script>

<template>
  <el-form v-loading="loadEntity" label-width="80px" :model="addForm" ref="addFormRef" :rules="checkRules">
    <el-form-item label="品牌" prop="brandId">
      <el-select
        style="width: 100%"
        v-model="addForm.brandId"
        clearable
        placeholder="请选择品牌"
      >
        <el-option
          v-for="b in brandList"
          :key="b.id"
          :value="b.id"
          :label="b.brandName"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="名称" prop="businessDistrictName">
      <el-input
        v-model="addForm.businessDistrictName"
        clearable
        @blur="loadSzm"
        placeholder="请输入名称"
      />
    </el-form-item>
    <el-form-item label="编码" prop="businessDistrictCode">
      <el-input
        v-model="addForm.businessDistrictCode"
        clearable
        placeholder="请输入编码"
      />
    </el-form-item>
    <district-code-form :form-obj="addForm"/>

    <el-form-item label="地址" prop="businessDistrictAddress">
      <el-input
        v-model="addForm.businessDistrictAddress"
        clearable
        type="textarea"
        maxlength="200"
        show-word-limit
        :rows="3"
        placeholder="请输入地址"
      />
    </el-form-item>
    <el-form-item label="描述" prop="businessDistrictDesc">
      <el-input
        v-model="addForm.businessDistrictDesc"
        clearable
        placeholder="请输入描述"
      />
    </el-form-item>
    <el-form-item label="半径" prop="businessDistrictRadius">
      <el-input-number
        v-model="addForm.businessDistrictRadius"
        clearable
        style="width: 100%"
        :pmax="5000"
        :step="100"
        placeholder="请输入半径"
      />
    </el-form-item>
    <el-form-item label="商圈级别" prop="businessDistrictLevelId">
      <el-select
        v-model="addForm.businessDistrictLevelId"
        clearable
        placeholder="请选择商圈级别"
        style="width:100%"
      >
        <el-option
          v-for="l in levelList"
          :label="l.businessDistrictLevelName"
          :key="l.id"
          :value="l.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="商圈类别" prop="businessDistrictTypeId">
      <el-select
        v-model="addForm.businessDistrictTypeId"
        clearable
        placeholder="请选择商圈类别"
      >
        <el-option
          v-for="t in typeList"
          :key="t.id"
          :value="t.id"
          :label="t.businessDistrictTypeName"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <GetLngLatTips/>
    </el-form-item>
    <el-form-item label="纬度" prop="centerLat">

      <el-input
        v-model="addForm.centerLat"
        clearable
        placeholder="请输入纬度"
      />
    </el-form-item>
    <el-form-item label="经度" prop="centerLng">
      <el-input
        v-model="addForm.centerLng"
        clearable
        placeholder="请输入经度"
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
