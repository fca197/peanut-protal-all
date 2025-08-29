<script setup lang="ts">
import {ref, onMounted} from "vue"
import AddEditFormVue from "./StoreShoppingMallAddEditForm.vue"
import TableBar from "@/layouts/components/TableBar/index.vue"
import {ElTable} from "element-plus";
import {HeaderInfo, postResultInfo} from "@@/utils/common-js.ts"
import {type StoreShoppingMall} from "./StoreShoppingMallType.ts"

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
  {fieldName: "businessTag", showName: "标签List<String>"},
  {fieldName: "businessTel", showName: "联系电话可多个List<String>"},
  {fieldName: "enterLocationLng", showName: "入口经纬度经度"},
  {fieldName: "enterLocationLat", showName: "入口经纬度纬度"},
  {fieldName: "photos", showName: "图片： List<String>"},
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
        <el-form-item label="国家编码" prop="countryCode">
          <el-input
            v-model="queryForm.countryCode"
            clearable
            placeholder="请输入国家编码"
          />
        </el-form-item>
        <el-form-item label="城市编码" prop="provinceCode">
          <el-input
            v-model="queryForm.provinceCode"
            clearable
            placeholder="请输入城市编码"
          />
        </el-form-item>
        <el-form-item label="城市编码" prop="cityCode">
          <el-input
            v-model="queryForm.cityCode"
            clearable
            placeholder="请输入城市编码"
          />
        </el-form-item>
        <el-form-item label="城市编码" prop="areaCode">
          <el-input
            v-model="queryForm.areaCode"
            clearable
            placeholder="请输入城市编码"
          />
        </el-form-item>
        <el-form-item label="国家名称" prop="countryName">
          <el-input
            v-model="queryForm.countryName"
            clearable
            placeholder="请输入国家名称"
          />
        </el-form-item>
        <el-form-item label="省份名称" prop="provinceName">
          <el-input
            v-model="queryForm.provinceName"
            clearable
            placeholder="请输入省份名称"
          />
        </el-form-item>
        <el-form-item label="城市名称" prop="cityName">
          <el-input
            v-model="queryForm.cityName"
            clearable
            placeholder="请输入城市名称"
          />
        </el-form-item>
        <el-form-item label="区县名称" prop="areaName">
          <el-input
            v-model="queryForm.areaName"
            clearable
            placeholder="请输入区县名称"
          />
        </el-form-item>
        <el-form-item label="所属最新商区" prop="belongDistrictId">
          <el-input
            v-model="queryForm.belongDistrictId"
            clearable
            placeholder="请输入所属最新商区"
          />
        </el-form-item>
        <el-form-item label="所属商区 List<Long>" prop="belongDistrictIdList">
          <el-input
            v-model="queryForm.belongDistrictIdList"
            clearable
            placeholder="请输入所属商区 List<Long>"
          />
        </el-form-item>
        <el-form-item label="地址" prop="shoppingMallAddress">
          <el-input
            v-model="queryForm.shoppingMallAddress"
            clearable
            placeholder="请输入地址"
          />
        </el-form-item>
        <el-form-item label="经度" prop="shoppingMallLocationLng">
          <el-input
            v-model="queryForm.shoppingMallLocationLng"
            clearable
            placeholder="请输入经度"
          />
        </el-form-item>
        <el-form-item label="纬度" prop="shoppingMallLocationLat">
          <el-input
            v-model="queryForm.shoppingMallLocationLat"
            clearable
            placeholder="请输入纬度"
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
        <el-form-item label="营业开始时间" prop="businessOpenTimeTodayOpen">
          <el-input
            v-model="queryForm.businessOpenTimeTodayOpen"
            clearable
            placeholder="请输入营业开始时间"
          />
        </el-form-item>
        <el-form-item label="营业结束时间" prop="businessOpenTimeTodayClose">
          <el-input
            v-model="queryForm.businessOpenTimeTodayClose"
            clearable
            placeholder="请输入营业结束时间"
          />
        </el-form-item>
        <el-form-item label="评分" prop="businessRating">
          <el-input
            v-model="queryForm.businessRating"
            clearable
            placeholder="请输入评分"
          />
        </el-form-item>
        <el-form-item label="标签List<String>" prop="businessTag">
          <el-input
            v-model="queryForm.businessTag"
            clearable
            placeholder="请输入标签List<String>"
          />
        </el-form-item>
        <el-form-item label="联系电话可多个List<String>" prop="businessTel">
          <el-input
            v-model="queryForm.businessTel"
            clearable
            placeholder="请输入联系电话可多个List<String>"
          />
        </el-form-item>
        <el-form-item label="入口经纬度经度" prop="enterLocationLng">
          <el-input
            v-model="queryForm.enterLocationLng"
            clearable
            placeholder="请输入入口经纬度经度"
          />
        </el-form-item>
        <el-form-item label="入口经纬度纬度" prop="enterLocationLat">
          <el-input
            v-model="queryForm.enterLocationLat"
            clearable
            placeholder="请输入入口经纬度纬度"
          />
        </el-form-item>
        <el-form-item label="图片： List<String>" prop="photos">
          <el-input
            v-model="queryForm.photos"
            clearable
            placeholder="请输入图片： List<String>"
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
        <ElTableColumn label="ID 自增" prop="id"/>
        <ElTableColumn label="商场名称" prop="shoppingMallName"/>
        <ElTableColumn label="商场别称" prop="businessAlias"/>
        <ElTableColumn label="国家名称" prop="countryName"/>
        <ElTableColumn label="省份名称" prop="provinceName"/>
        <ElTableColumn label="城市名称" prop="cityName"/>
        <ElTableColumn label="区县名称" prop="areaName"/>
        <ElTableColumn label="所属最新商区" prop="belongDistrictName"/>
        <ElTableColumn label="所属商区 List<Long>" prop="belongDistrictIdNameList"/>
        <ElTableColumn label="地址" prop="shoppingMallAddress"/>
        <ElTableColumn label="经度" prop="shoppingMallLocationLng"/>
        <ElTableColumn label="纬度" prop="shoppingMallLocationLat"/>
        <ElTableColumn label="商场别称" prop="businessKeytag"/>
        <ElTableColumn label="营业开始时间" prop="businessOpenTimeTodayOpen"/>
        <ElTableColumn label="营业结束时间" prop="businessOpenTimeTodayClose"/>
        <ElTableColumn label="评分" prop="businessRating"/>
        <ElTableColumn label="标签List<String>" prop="businessTag"/>
        <ElTableColumn label="联系电话可多个List<String>" prop="businessTel"/>
        <ElTableColumn label="入口经纬度经度" prop="enterLocationLng"/>
        <ElTableColumn label="入口经纬度纬度" prop="enterLocationLat"/>
        <ElTableColumn label="图片： List<String>" prop="photos"/>
        <ElTableColumn label="租户ID" prop="tenantId"/>
        <ElTableColumn label="是否删除 0 否,1 是" prop="isDelete"/>
        <ElTableColumn label="创建时间" prop="createTime"/>
        <ElTableColumn label="创建人" prop="createBy"/>
        <ElTableColumn label="修改时间" prop="updateTime"/>
        <ElTableColumn label="修改人" prop="updateBy"/>
        <ElTableColumn label="调用链路" prop="traceId"/>
        <ElTableColumn label="版本号" prop="versionNum"/>
        <ElTableColumn label="创建人姓名" prop="createUserName"/>
        <ElTableColumn label="修改人姓名" prop="updateUserName"/>

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

