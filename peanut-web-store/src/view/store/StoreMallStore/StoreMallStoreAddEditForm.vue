<script setup lang="ts">
import {onMounted, ref} from "vue"
import {type StoreMallStore} from "./StoreMallStoreType.ts"
import {getById, postNoResult} from "@/common/utils/common-js.ts"
import {type FormInstance, FormRules} from "element-plus"

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
const dtoUrl = ref<string>("/storeMallStore")
// 表单引用
const addFormRef = ref<FormInstance>()
// 表单校验规则
const checkRules = ref<FormRules>({
  // store_shopping_mall ID  商场ID
  shoppingMallId: [
    {required: true, message: "请输入store_shopping_mall ID  商场ID", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 楼层
  mallStoreFloor: [
    {required: true, message: "请输入楼层", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 房间号
  mallStoreRoomNo: [
    {required: true, message: "请输入房间号", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，
  mallStoreStatus: [
    {required: true, message: "请输入状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 状态备注
  mallStoreStatusMark: [
    {required: true, message: "请输入状态备注", trigger: "blur"},
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],

})


// 添加对象
const addForm = ref<StoreMallStore>({
  shoppingMallId: undefined,
  mallStoreFloor: undefined,
  mallStoreRoomNo: undefined,
  mallStoreStatus: undefined,
  mallStoreStatusMark: undefined,
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
  <el-form v-loading="loadEntity" label-width="80px" :model="addForm" ref="addFormRef" :rules="checkRules">
    <el-form-item label="store_shopping_mall ID  商场ID" prop="shoppingMallId">
      <el-input
        v-model="addForm.shoppingMallId"
        clearable
        placeholder="请输入store_shopping_mall ID  商场ID"
      />
    </el-form-item>
    <el-form-item label="楼层" prop="mallStoreFloor">
      <el-input-number
        v-model="addForm.mallStoreFloor"
        clearable
        placeholder="请输入"
      />
    </el-form-item>
    <el-form-item label="房间号" prop="mallStoreRoomNo">
      <el-input-number
        v-model="addForm.mallStoreRoomNo"
        clearable
        placeholder="请输入"
      />
    </el-form-item>
    <el-form-item label="状态 0草稿，1可建店，2被锁定，3评审中，4已确址，5已废弃，" prop="mallStoreStatus">
      <el-input-number
        v-model="addForm.mallStoreStatus"
        clearable
        placeholder="请输入"
      />
    </el-form-item>
    <el-form-item label="状态备注" prop="mallStoreStatusMark">
      <el-input
        v-model="addForm.mallStoreStatusMark"
        clearable
        placeholder="请输入状态备注"
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
