<script setup lang="ts">
import {ref} from "vue";
import {DistrictCode, queryDistrictCodeV2} from "@v/DistrictCode/districtCode.ts";

const props = defineProps({
  formObj: {
    type: Object,
    required: true
  }
})

const countryList = ref<DistrictCode[]>([])
const provinceList = ref<DistrictCode[]>([])
const cityList = ref<DistrictCode[]>([])
const areaList = ref<DistrictCode[]>([])

watch(() => props.formObj.countryCode, (n) => {
  if (n !== undefined)
    props.formObj.countryName = countryList.value.filter(t => t.code === n)[0]?.name
  provinceList.value = []
  cityList.value = []
  areaList.value = []
  props.formObj.provinceCode = undefined
  props.formObj.provinceName = undefined
  props.formObj.cityCode = undefined
  props.formObj.cityName = undefined
  props.formObj.areaCode = undefined
  props.formObj.areaName = undefined
  queryDistrictCodeV2(n, provinceList)
})
watch(() => props.formObj.provinceCode, (n) => {

  if (n !== undefined)
    props.formObj.provinceName = provinceList.value.filter(t => t.code === n)[0]?.name
  cityList.value = []
  areaList.value = []
  props.formObj.cityCode = undefined
  props.formObj.cityName = undefined
  props.formObj.areaCode = undefined
  props.formObj.areaName = undefined
  queryDistrictCodeV2(n, cityList)
})
watch(() => props.formObj.cityCode, (n) => {
  if (n !== undefined)
    props.formObj.cityName = cityList.value?.filter(t => t.code === n)[0]?.name

  areaList.value = []
  props.formObj.areaCode = undefined
  props.formObj.areaName = undefined
  queryDistrictCodeV2(n, areaList)
})
watch(() => props.formObj.areaCode, (n) => {
  if (n !== undefined)
    props.formObj.areaName = areaList.value.filter(t => t.code === n)[0]?.name
})
onMounted(() => {
  queryDistrictCodeV2("000000", countryList)
})
</script>

<template>
  <el-form-item label="国家" prop="countryCode">
    <el-select
      v-model="props.formObj.countryCode"
      clearable
      placeholder="请选择国家"
      style="width:200px"
    >
      <el-option
        v-for="dc in countryList"
        :key="dc.code"
        :value="dc.code"
        :label="dc.name"
      />
    </el-select>
  </el-form-item>
  <el-form-item label="省份" prop="provinceCode">

    <el-select
      v-model="props.formObj.provinceCode"
      clearable filterable
      placeholder="请选择省份"
      style="width:200px"
    >
      <el-option
        v-for="dc in provinceList"
        :key="dc.code"
        :value="dc.code"
        :label="dc.name"
      />
    </el-select>
  </el-form-item>
  <el-form-item label="城市" prop="cityCode">

    <el-select
      v-model="props.formObj.cityCode"
      clearable filterable
      placeholder="请选择城市"
      style="width:200px"
    >
      <el-option
        v-for="dc in cityList"
        :key="dc.code"
        :value="dc.code"
        :label="dc.name"
      />
    </el-select>
  </el-form-item>
  <el-form-item label="区县" prop="areaCode">

    <el-select
      v-model="props.formObj.areaCode"
      clearable filterable
      placeholder="请选择区县"
      style="width:200px"
    >
      <el-option
        v-for="dc in areaList"
        :key="dc.code"
        :value="dc.code"
        :label="dc.name"
      />
    </el-select>
  </el-form-item>
</template>

<style scoped lang="scss">

</style>