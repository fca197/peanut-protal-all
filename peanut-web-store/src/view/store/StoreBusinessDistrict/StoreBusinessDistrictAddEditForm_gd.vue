<script setup lang="ts">
import {onMounted, ref} from "vue"
import {type StoreBusinessDistrict} from "./StoreBusinessDistrictType.ts"
import {getById, pinyin4jSzmV4, postNoResult} from "@/common/utils/common-js.ts"
import {type FormInstance, FormRules} from "element-plus"
import {queryBrandList, TBrand} from "@v/base/TBrand/TBrandType.ts";
import {queryLevelList, StoreBusinessDistrictLevel} from "@v/store/StoreBusinessDistrictLevel/StoreBusinessDistrictLevelType.ts";
import {queryStoreBusinessDistrictTypeList, StoreBusinessDistrictType} from "@v/store/StoreBusinessDistrictType/StoreBusinessDistrictTypeType.ts";

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
    {required: true, message: "请输入商圈级别", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 商圈类别
  businessDistrictTypeId: [
    {required: true, message: "请输入商圈类别", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ]
})


// 添加对象
const addForm = ref<StoreBusinessDistrict>({
  brandId: "",
  businessDistrictCode: "",
  businessDistrictName: "",
  businessDistrictDesc: "",
  businessDistrictAddress: "",
  countryCode: "",
  provinceCode: "",
  cityCode: "",
  areaCode: "",
  countryName: "",
  provinceName: "",
  cityName: "",
  areaName: "",
  businessDistrictRadius: 1000,
  businessDistrictSearchRadius: 0,
  businessDistrictLevelId: "",
  businessDistrictTypeId: "",
  centerLat: "",
  centerLng: "",
  id: ""
})

const loadById = () => {
  loadMap();
  if (!props.editId) {
    loadEntity.value = false
    return
  }
  console.info("props.editId ", props.editId)
  getById(`${dtoUrl.value}/queryByIdList`, props.editId).then((t) => {
    addForm.value = t
    console.info(" addForm.value ", addForm.value)
    loadEntity.value = false
    mapInstance.value.setCenter([addForm.value.centerLng, addForm.value.centerLat])
    drawCircle()
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
const brandList = ref<TBrand[]>([])
const levelList = ref<StoreBusinessDistrictLevel[]>([])
const loadSzm = () => {
  pinyin4jSzmV4(addForm.value.businessDistrictName, addForm, "businessDistrictCode")
}
const mapCircle = ref(null)
const mapInstance = ref(null)
const nearPoiList = ref([])
watch(() => addForm.value.businessDistrictLevelId, (n) => drawCircle())

const drawCircle = () => {
  const n = addForm.value.businessDistrictRadius
  if (n) {
    if (mapCircle.value != null) {
      mapInstance.value.remove(mapCircle.value)
    }
    let fillColor = "#1791fc";
    if (addForm.value.businessDistrictLevelId) {
      levelList.value.filter(t => t.id == addForm.value.businessDistrictLevelId)
      .forEach((v, i) => {
        fillColor = v.businessDistrictLevelColor
      })
    }
    mapCircle.value = new AMap.Circle({
      center: [addForm.value.centerLng, addForm.value.centerLat],
      radius: n, //半径
      borderWeight: 2,
      strokeColor: "#FF33FF",
      strokeOpacity: 1,
      strokeWeight: 4,
      strokeOpacity: 0.2,
      fillOpacity: 0.4,
      strokeStyle: 'dashed',
      strokeDasharray: [10, 10],
      // 线样式还支持 'dashed'
      fillColor: fillColor,
      zIndex: 50,
    })
    mapInstance.value.add(mapCircle.value)
  }
}

watch(() => addForm.value.businessDistrictRadius, (n) => {
  drawCircle()
})

const loadMap = () => {
  //加载PositionPicker，loadUI的路径参数为模块名中 "ui/" 之后的部分
  AMapUI.loadUI(["misc/PositionPicker"], function (PositionPicker) {
    const map = new AMap.Map("mapDiv", {
      zoom: 10
    })
    map.plugin(["AMap.ToolBar"], function () {
      //加载工具条
      var tool = new AMap.ToolBar();
      map.addControl(tool);
    });
    var positionPicker = new PositionPicker({
      mode: "dragMap",//设定为拖拽地图模式，可选"dragMap"、"dragMarker"，默认为"dragMap"
      map: map//依赖地图对象
    });
    mapInstance.value = map;
    positionPicker.on("success", function (positionResult) {
      console.log("positionResult", positionResult)
      try {

        addForm.value.businessDistrictName = "";
        addForm.value.centerLng = positionResult.position.lng
        addForm.value.centerLat = positionResult.position.lat

        addForm.value.businessDistrictName = positionResult.regeocode?.pois[0].name;
        addForm.value.businessDistrictAddress = positionResult.address
        addForm.value.areaCode = positionResult.regeocode?.addressComponent.adcode
        drawCircle()
        nearPoiList.value = positionResult.regeocode?.pois
        pinyin4jSzmV4(addForm.value.businessDistrictName, addForm, "businessDistrictCode")
      }catch (e){
        ElMessage.error("位置选择错误，请重新选择")
      }
    });
    positionPicker.on("fail", function (positionResult) {
      console.info("fail", positionResult)
      ElMessage.error("位置选择错误，请重新选择")
    });
    map.panBy(0, 1);
    positionPicker.start();

  });

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
const moveMap = (poi) => {
  console.log(poi);
  const location = poi.location;
  mapInstance.value.setCenter([location.lng, location.lat]);
}
</script>

<template>
  <el-form v-loading="loadEntity" label-width="100px" inline :model="addForm" ref="addFormRef" :rules="checkRules">
    <el-form-item label="品牌" prop="brandId">
      <el-select
          v-model="addForm.brandId"
          clearable
          placeholder="请选择品牌"
          style="width:178px"
      >
        <el-option
            v-for="b in brandList"
            :key="b.id"
            :label="b.brandName"
            :value="b.id"
        />
      </el-select>
    </el-form-item>
    <el-divider/>
    <el-form-item>
      <el-form-item>
        <el-text type="warning">拖动地图选择位置</el-text>
        <el-space wrap>
          <el-card id="mapDiv" style="width: 600px; height: 300px;margin-left: 30px">
          </el-card>
          <el-card style="width: 250px;height: 300px; overflow-y: scroll">
            <div v-for="(p,i) in nearPoiList">
              <a style="margin-bottom: 5px; cursor: pointer" @click="moveMap(p)">{{ i + 1 }}) {{ p.name }}</a>
            </div>
          </el-card>
        </el-space>
      </el-form-item>
    </el-form-item>
    <el-form-item label="名称" prop="businessDistrictName" style="width: 880px;">
      <el-input
          v-model="addForm.businessDistrictName"
          clearable
          @blur="loadSzm"
          placeholder="请输入名称"
      />
    </el-form-item>
    <el-form-item label="编码" prop="businessDistrictCode" style="width: 880px;">
      <el-input
          v-model="addForm.businessDistrictCode"
          clearable
          placeholder="请输入编码"
      />
    </el-form-item>
    <el-form-item label="描述" prop="businessDistrictDesc" style="width: 880px;">
      <el-input
          v-model="addForm.businessDistrictDesc"
          clearable
          placeholder="请输入描述"
      />
    </el-form-item>
    <el-form-item label="地址" prop="businessDistrictAddress" style="width: 880px;">
      <el-input
          v-model="addForm.businessDistrictAddress"
          clearable
          placeholder="请输入地址"
      />
    </el-form-item>

    <el-form-item label="商圈半径" prop="businessDistrictRadius">
      <el-input-number
          v-model="addForm.businessDistrictRadius"
          clearable
          :pmax="5000"
          placeholder="请输入半径"
      />
    </el-form-item>
    <el-form-item label="查询半径" prop="businessDistrictRadius">
      <el-input-number
          v-model="addForm.businessDistrictSearchRadius"
          clearable
          placeholder="请输入半径"
      />
    </el-form-item>
    <el-form-item label="商圈级别" prop="businessDistrictLevelId">
      <el-select
          v-model="addForm.businessDistrictLevelId"
          clearable
          placeholder="请输入商圈级别"
          style="width:178px"
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
          clearable style="width:178px"
          placeholder="请输入商圈类别"
      >
        <el-option v-for="t in typeList"
                   :key="t.id"
                   :value="t.id"
                   :label="t.businessDistrictTypeName"
        />
      </el-select>
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
