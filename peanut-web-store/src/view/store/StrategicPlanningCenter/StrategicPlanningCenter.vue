<script setup lang="ts">
import {DistrictCode, queryDistrictCode} from "@v/DistrictCode/districtCode.ts";

const loadData = ref<boolean>(true);
const districtExplorerRef = ref(null);
const loadAMap = () => {

}

const districtCodeList = ref<DistrictCode[]>([]);
onMounted(async () => {
  queryDistrictCode(undefined, [0, 1, 2, 3]).then((list) => {
    districtCodeList.value = list;
  })
})
const  chnImg="/assets/store/StrategicPlanningCenter/chn.png"
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

                  >{{ data.name }}</span>

                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-col>
        <el-col :span="20">
          <div id="container" style="height: 800px; width: 100% ">
            <el-image :src="chnImg" />
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style scoped lang="scss">

</style>
