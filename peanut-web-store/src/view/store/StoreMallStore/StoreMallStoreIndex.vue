<script setup lang="ts">
import {onMounted, ref} from "vue"
import AddEditFormVue from "./StoreMallStoreAddEditForm.vue"
import TableBar from "@/layouts/components/TableBar/index.vue"
import {ElTable} from "element-plus";
import {HeaderInfo, postResultInfo} from "@@/utils/common-js.ts"
import {type StoreMallStore} from "./StoreMallStoreType.ts"

const dtoUrl = ref<string>("/storeMallStore")
const documentTitle = ref<string>("store 商场门店")
const dataBatchDeleteUrl = ref<string>(`${dtoUrl.value}/deleteByIdList`)
const loadEntity = ref<boolean>(true)
// 查询表格
const queryForm = ref<StoreMallStore>({
  shoppingMallId: undefined,
  mallStoreFloor: undefined,
  mallStoreRoomNo: undefined,
  mallStoreStatus: undefined,
  mallStoreStatusMark: undefined,
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
const dataList = ref<StoreMallStore[]>([])
const currentPageNum = ref<number>(1)
const currentPageSize = ref<number>(10)
const tableTotal = ref<number>(0)
const headerList = ref<HeaderInfo[]>([
  {fieldName: "id", showName: "ID 自增"},
  {fieldName: "shoppingMallId", showName: "store_shopping_mall ID  商场ID"},
  {fieldName: "mallStoreFloor", showName: "楼层"},
  {fieldName: "mallStoreRoomNo", showName: "房间号"},
  {fieldName: "mallStoreStatus", showName: "状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，"},
  {fieldName: "mallStoreStatusMark", showName: "状态备注"},
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
const handleSelectionChange = (val: StoreMallStore[]) => {
  multipleSelection.value = val.map(t => t.id)
  console.info("multipleSelection ", multipleSelection)
}
const statusMap = ref<Map<String, String>>({
  "0": "草稿",
  "1": "可建店",
  "2": "被锁定",
  "3": "评审中",
  "4": "已确址",
  "5": "已废弃"
})

// 页面加载事件
onMounted(() => {
  getDataList()
})
</script>

<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-form v-model="queryForm" inline>
        <el-form-item label="store_shopping_mall ID  商场ID" prop="shoppingMallId">
          <el-input
            v-model="queryForm.shoppingMallId"
            clearable
            placeholder="请输入store_shopping_mall ID  商场ID"
          />
        </el-form-item>
        <el-form-item label="楼层" prop="mallStoreFloor">
          <el-input-number
            v-model="queryForm.mallStoreFloor"
            clearable
            placeholder="请输入"
          />
        </el-form-item>
        <el-form-item label="房间号" prop="mallStoreRoomNo">
          <el-input-number
            v-model="queryForm.mallStoreRoomNo"
            clearable
            placeholder="请输入"
          />
        </el-form-item>
        <el-form-item label="状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，" prop="mallStoreStatus">
          <el-input-number
            v-model="queryForm.mallStoreStatus"
            clearable
            placeholder="请输入"
          />
        </el-form-item>
        <el-form-item label="状态备注" prop="mallStoreStatusMark">
          <el-input
            v-model="queryForm.mallStoreStatusMark"
            clearable
            placeholder="请输入状态备注"
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
        <ElTableColumn label="ID" prop="id"/>
        <ElTableColumn label="商场" prop="shoppingMallId"/>
        <ElTableColumn label="楼层" prop="mallStoreFloor"/>
        <ElTableColumn label="房间号" prop="mallStoreRoomNo"/>
        <ElTableColumn label="状态" prop="mallStoreStatus">
          <template #default="scope">
            {{ statusMap[scope.row.mallStoreStatus] }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="状态备注" prop="mallStoreStatusMark" show-overflow-tooltip/>
        <ElTableColumn label="创建时间" prop="createTime"/>
        <ElTableColumn label="修改时间" prop="updateTime"/>
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

