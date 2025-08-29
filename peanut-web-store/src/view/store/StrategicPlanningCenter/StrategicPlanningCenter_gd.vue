<script setup lang="ts">
import {DistrictCode, queryDistrictCode} from "@v/DistrictCode/districtCode.ts";
import {postNoResult} from "@@/utils/common-js.ts";

const loadData = ref<boolean>(true);
const gdMap = ref(null);
const districtExplorerRef = ref(null);
const loadAMap = () => {

  gdMap.value = new AMap.Map("container", {
    zoom: 4,
    center: [108.867694, 30.899997],
  });
}


const districtCodeList = ref<DistrictCode[]>([]);
onMounted(async () => {
  queryDistrictCode(undefined, [0, 1, 2, 3]).then((list) => {
    districtCodeList.value = list;
  })
  loadAMap()
  console.info("loadAMap mounted");
  window.AMapUI.load(['ui/geo/DistrictExplorer', 'lib/$'], function (DistrictExplorer, $) {
    //创建一个实例
    initPage(DistrictExplorer);
  })
})


const downLoadBoundary = (data) => {
  console.log("downLoadBoundary ", data);
  postNoResult("/districtCodeBoundary/insert", {
    districtCode: data.code,
  }, "获取成功", (r) => {
  })
}

function initPage(DistrictExplorer) {
  // console.log(DistrictExplorer);
  //创建一个实例
  var districtExplorer = new DistrictExplorer({
    map: gdMap.value //关联的地图实例
  });

  districtExplorerRef.value = districtExplorer;
  var adcode = 100000; //全国的区划编码
  // var adcode = 370000
  draw(adcode)
}

function draw(adcode) {

  var districtExplorer = districtExplorerRef.value;
  districtExplorer.loadAreaNode(adcode, function (error, areaNode) {
    if (error) {
      console.error(error);
      return;
    }

    //绘制载入的区划节点
    renderAreaNode(districtExplorer, areaNode);
  });
}

function renderAreaNode(districtExplorer, areaNode) {

  //清除已有的绘制内容
  districtExplorer.clearFeaturePolygons();

  //just some colors
  var colors = ["#3366cc", "#dc3912", "#ff9900", "#109618", "#990099", "#0099c6", "#dd4477", "#66aa00"];

  let childrenSize = 0;
  //绘制子级区划
  districtExplorer.renderSubFeatures(areaNode, function (feature, i) {
    childrenSize++

    // console.log(i, feature.properties.adcode, feature);

    var fillColor = colors[i % colors.length];
    var strokeColor = colors[colors.length - 1 - i % colors.length];
    // console.log("areaNode ", areaNode, fillColor, strokeColor);

    return {
      cursor: 'default',
      bubble: true,
      strokeColor: strokeColor, //线颜色
      strokeOpacity: 1, //线透明度
      strokeWeight: 1, //线宽
      fillColor: fillColor, //填充色
      fillOpacity: 0.35, //填充透明度
    };
  });

  const fc = childrenSize !== 0 ? null : "red";
  console.info("childrenSize ", childrenSize, fc)

  //绘制父级区划，仅用黑色描边
  districtExplorer.renderParentFeature(areaNode, {
    cursor: 'default',
    bubble: true,
    strokeColor: 'black', //线颜色
    fillColor: fc,
    strokeWeight: 1, //线宽
  });

  //更新地图视野以适合区划面
  gdMap.value.setFitView(districtExplorer.getAllFeaturePolygons());
}

</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" id="gdMapDivParent" shadow="never" v-model="loadData">
      <el-row :gutter="20">
        <el-col :span="4">

          <el-scrollbar style="height: 800px; overflow-y: scroll">
            <el-tree
                node-click="districtCodeClick"
                node-key="code"
                :default-checked-keys="['100000']"
                :data="districtCodeList"
                :default-expanded-keys="['100000']"
                accordion
            >
              <template #default="{ node, data }">
                <div style="width: 100%;display: flex; justify-content: space-between; align-items: center;">
                  <span
                      style="white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: calc(100% - 100px);"
                      @click="draw(data.code)"
                  >{{ data.name }}</span>
                  <span style="float: right;margin-right: 15px;white-space: nowrap;display: none" @click="downLoadBoundary(data)">
                    <View style="width: 14px;font-size: 14px"/></span>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-col>
        <el-col :span="20">
          <div id="container" style="height: 800px; width: 100%"></div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped lang="scss">

</style>
