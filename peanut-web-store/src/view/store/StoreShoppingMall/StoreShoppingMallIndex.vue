<script setup lang="ts">
import {onMounted, ref} from "vue"
import AddEditFormVue from "./StoreShoppingMallAddEditForm.vue"
import TableBar from "@/layouts/components/TableBar/index.vue"
import {ElTable} from "element-plus";
import {HeaderInfo, postResultInfo} from "@@/utils/common-js.ts"
import {type StoreShoppingMall} from "./StoreShoppingMallType.ts"
import DistrictCodeForm from "@v/DistrictCode/DistrictCodeForm.vue";

const dtoUrl = ref<string>("/storeShoppingMall")
const documentTitle = ref<string>("门店 商场")
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`)
const loadEntity = ref<boolean>(true)
// 查询表格
const queryForm = ref<StoreShoppingMall>({
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
  businessTel: undefined,
  enterLocationLng: undefined,
  enterLocationLat: undefined,
  photos: undefined,
  id: undefined
})

// 表格
// 表格选中的id
const multipleSelection = ref<(string | undefined)[]>([])
// const dataTableRef = ref<InstanceType<typeof ElTable> | null>(null)
const dataTableRef = ref({})
// 表格操作头
const tableBarRef = ref<InstanceType<typeof TableBar> | null>(null)
// 表格相关
const dataList = ref<StoreShoppingMall[]>([])
const currentPageNum = ref<number>(1)
const currentPageSize = ref<number>(10)
const tableTotal = ref<number>(0)
const headerList = ref<HeaderInfo[]>([
  {fieldName: "id", showName: "ID 自增"},
  {fieldName: "countryCode", showName: "国家编码"},
  {fieldName: "provinceCode", showName: "城市编码"},
  {fieldName: "cityCode", showName: "城市编码"},
  {fieldName: "areaCode", showName: "城市编码"},
  {fieldName: "countryName", showName: "国家名称"},
  {fieldName: "provinceName", showName: "省份名称"},
  {fieldName: "cityName", showName: "城市名称"},
  {fieldName: "areaName", showName: "区县名称"},
  {fieldName: "belongDistrictId", showName: "所属最新商区"},
  {fieldName: "belongDistrictIdList", showName: "所属商区 List<Long>"},
  {fieldName: "shoppingMallAddress", showName: "地址"},
  {fieldName: "shoppingMallLocationLng", showName: "经度"},
  {fieldName: "shoppingMallLocationLat", showName: "纬度"},
  {fieldName: "shoppingMallName", showName: "商场名称"},
  {fieldName: "businessAlias", showName: "商场别称"},
  {fieldName: "businessKeytag", showName: "商场别称"},
  {fieldName: "businessOpenTimeTodayOpen", showName: "营业开始时间"},
  {fieldName: "businessOpenTimeTodayClose", showName: "营业结束时间"},
  {fieldName: "businessRating", showName: "评分"},
  {fieldName: "businessTag", showName: "标签"},
  {fieldName: "businessTel", showName: "联系电话可多个"},
  {fieldName: "enterLocationLng", showName: "入口经纬度经度"},
  {fieldName: "enterLocationLat", showName: "入口经纬度纬度"},
  {fieldName: "photos", showName: "图片： "},
  {fieldName: "tenantId", showName: "租户ID"},
  {fieldName: "isDelete", showName: "是否删除 0 否,1 是"},
  {fieldName: "createTime", showName: "创建时间"},
  {fieldName: "createBy", showName: "创建人"},
  {fieldName: "updateTime", showName: "修改时间"},
  {fieldName: "updateBy", showName: "修改人"},
  {fieldName: "traceId", showName: "调用链路"},
  {fieldName: "versionNum", showName: "版本号"},
  {fieldName: "createUserName", showName: "创建人姓名"},
  {fieldName: "updateUserName", showName: "修改人姓名"},
])

// 获取表格内数据
const getDataList = () => {
  const req = {
    pageSize: currentPageSize.value,
    pageNum: currentPageNum.value,
    data: queryForm.value
  }
  console.info("getDataList {}", req)
  loadEntity.value = true
  postResultInfo(`${dtoUrl.value}/queryPageList`, req)
  .then((t) => {
    dataList.value = t.data.dataList
    tableTotal.value = Number.parseInt(t.data.total)
    headerList.value = t.data.headerList
    loadEntity.value = false
  })
}

// table点击事件
const editData = (data: any) => {
  // console.info("data ", data)
  tableBarRef.value?.showEditDialog(data.id)
}
// 页面条数变更事件
const handleSizeChange = (val: number) => {
  currentPageSize.value = val
  getDataList()
}
// 页面变更事件
const handleCurrentChange = (val: number) => {
  currentPageNum.value = val
  getDataList()
}
// 表格选中事件
const handleSelectionChange = (val: StoreShoppingMall[]) => {
  multipleSelection.value = val.map(t => t.id)
  console.info("multipleSelection ", multipleSelection)
}

// 页面加载事件
onMounted(() => {
  getDataList()
})
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>

        <DistrictCodeForm :form-obj="queryForm"/>
        <el-form-item label="所属最新商区" prop="belongDistrictId">
          <el-input
            v-model="queryForm.belongDistrictId"
            clearable
            placeholder="请输入所属最新商区"
          />
        </el-form-item>
        <el-form-item label="商场名称" prop="shoppingMallName">
          <el-input
            v-model="queryForm.shoppingMallName"
            clearable
            placeholder="请输入商场名称"
          />
        </el-form-item>
        <el-form-item label="商场别称" prop="businessAlias">
          <el-input
            v-model="queryForm.businessAlias"
            clearable
            placeholder="请输入商场别称"
          />
        </el-form-item>
        <el-form-item label="商场别称" prop="businessKeytag">
          <el-input
            v-model="queryForm.businessKeytag"
            clearable
            placeholder="请输入商场别称"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="search" @click="getDataList">
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <TableBar
        :document-title="documentTitle"
        :add-component="AddEditFormVue"
        :refresh-list="getDataList"
        :data-table-ref="dataTableRef"
        :multiple-selection="multipleSelection"
        ref="tableBarRef"
        :data-batch-delete-url="dataBatchDeleteUrl"
      />
      <ElTable v-loading="loadEntity" ref="dataTableRef" :data="dataList" stripe @selection-change="handleSelectionChange">
        <ElTableColumn type="selection"/>
        <ElTableColumn label="ID 自增" prop="id" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="商场名称" prop="shoppingMallName" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="商场别称" prop="businessAlias" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="国家名称" prop="countryName" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="省份名称" prop="provinceName" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="城市名称" prop="cityName" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="区县名称" prop="areaName" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="所属最新商区" prop="belongDistrictName" show-overflow-tooltip :min-width="120"/>
        <ElTableColumn label="所属商区" prop="belongDistrictIdNameList" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="地址" prop="shoppingMallAddress" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="经度" prop="shoppingMallLocationLng" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="纬度" prop="shoppingMallLocationLat" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="商场别称" prop="businessKeytag" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="营业时间" prop="businessOpenTimeTodayOpen" :min-width="200">
          <template #default="scope">
            {{ scope.row.businessOpenTimeTodayOpen }}-{{ scope.row.businessOpenTimeTodayClose }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="评分" prop="businessRating" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="标签" prop="businessTag" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="联系电话" prop="businessTel" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="入口经度" prop="enterLocationLng" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="入口纬度" prop="enterLocationLat" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="图片 " prop="photos" show-overflow-tooltip :min-width="100"/>
        <ElTableColumn label="修改时间" prop="updateTime" show-overflow-tooltip :min-width="200"/>
        <ElTableColumn label="创建人姓名" prop="createUserName" show-overflow-tooltip :min-width="100"/>

        <ElTableColumn fixed="right" label="操作" width="150px">
          <template #default="scope">
            <el-button
              type="warning"
              icon="edit"
              @click="editData(scope.row)"
            >
              编辑
            </el-button>
          </template>
        </ElTableColumn>
      </ElTable>
      <el-row class="paginationDiv">
        <el-pagination
          background
          v-model:current-page="currentPageNum"
          v-model:page-size="currentPageSize"
          layout="total, sizes, prev, pager, next"
          :total="tableTotal"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </el-row>
    </el-card>
  </div>
</template>

<style scoped lang="scss">

</style>

