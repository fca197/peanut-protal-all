<template>
  <el-form label-width="80px" :model="addForm" ref="addFormRef" :rules="checkRules">
    <el-form-item label="名称" prop="roomName">
      <el-input v-model="addForm.roomName" clearable placeholder="请输入名称" @change="loadSzm"/>
    </el-form-item>
    <el-form-item label="编码" prop="roomCode">
      <el-input v-model="addForm.roomCode" clearable placeholder="请输入编码"/>
    </el-form-item>
    <el-form-item label="工厂" prop="factoryId">
      <el-select v-model="addForm.factoryId">
        <el-option v-for="f in factoryList" :key="f.id" :value="f.id" :label="f.factoryName"/>
      </el-select>
    </el-form-item>
    <el-form-item label="车间配置">

      <el-col :span="24">
        <el-button icon="plus" type="primary" @click="addConfig"></el-button>
      </el-col>
      <el-col :span="5">
        工段
      </el-col>
      <el-col :span="5">
        工位
      </el-col>
      <el-col :span="5">
        状态
      </el-col>
      <el-col :span="5">
        耗时(秒)
      </el-col>
      <el-row v-for="(item,index) in addForm.configList">
        <el-col :span="5">
          <el-select v-model="item.sectionId" clearable placeholder="请选择工段" filterable>
            <el-option v-for="item in sectionList" :key="item.id" :label="item.sectionName"
                       :value="item.id"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="item.stationId" clearable placeholder="请选择工位" filterable>
            <el-option v-for="item in stationList" :key="item.id" :label="item.stationName"
                       :value="item.id"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="item.statusId" clearable placeholder="请选择状态" filterable>
            <el-option v-for="item in statusList" :key="item.id" :label="item.statusName" :value="item.id"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-input v-model="item.executeTime" placeholder="请输入"/>
        </el-col>
        <el-col :span="4">
          <el-button icon="delete" type="danger"
                     @click="deleteConfig(index)"></el-button>
        </el-col>
      </el-row>
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

<script setup lang="ts">
import {onMounted, ref} from "vue"
import {type ApsRoom} from "./ApsRoomType.ts"
import {getById, pinyin4jSzm, postNoResult} from "@/common/utils/common-js.ts"
import {type FormInstance, FormRules} from "element-plus"
import {Factory, queryFactoryList} from "@v/base/Factory/FactoryType.ts";
import {ApsWorkshopSection, querySectionList} from "@v/aps/ApsWorkshopSection/ApsWorkshopSectionType.ts";
import {ApsWorkshopStation, queryStationList} from "@v/aps/ApsWorkshopStation/ApsWorkshopStationType.ts";
import {ApsStatus, queryApsStatusList} from "@v/aps/ApsStatus/ApsStatusType.ts";

const props = defineProps({
  saveFun: {
    type: Function
  },
  editId: {
    type: String,
    required: false
  }
})

// 对象URL
const dtoUrl = ref<string>("/apsRoom")
// 表单引用
const addFormRef = ref<FormInstance>()
// 表单校验规则
const checkRules = ref<FormRules>({
  //
  roomCode: [
    {required: true, message: "请输入编码", trigger: "blur" },
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  //
  roomName: [
    {required: true, message: "请输入名称", trigger: "blur" },
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],
  // 工厂ID
  factoryId: [
    {required: true, message: "请输入工厂ID", trigger: "blur" },
    {min: 2, max: 20, message: "长度在 2 到 20 个字符", trigger: "blur"}
  ],

})
const factoryList = ref<Factory[]>([])
const sectionList = ref<ApsWorkshopSection[]>([])
const stationList = ref<ApsWorkshopStation[]>([])
const statusList = ref<ApsStatus[]>([])
// 页面加载事件
onMounted(() => {
  loadById()
  queryFactoryList().then(r => factoryList.value = r)
  querySectionList().then(r => sectionList.value = r)
  queryStationList().then(r => stationList.value = r)
  queryApsStatusList().then(r => statusList.value = r)
})
// 添加对象
const addForm = ref<ApsRoom>({
  roomCode: "",
  roomName: "",
  factoryId: "",
  id: "",
  configList: []
})

function loadById() {
  if (!props.editId) {
    return
  }
  console.info("props.editId ", props.editId)
  getById(`${dtoUrl.value}/queryByIdList`, props.editId).then((t) => {
    addForm.value = t
    console.info(" addForm.value ", addForm.value)
  })
}

// 保存
function saveForm() {
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
function saveFormAfter() {
  cancelForm()
}

// 取消方法
function cancelForm() {
  if (props.saveFun) {
    props.saveFun()
  }
}

function loadSzm() {
  pinyin4jSzm(addForm.value.roomName).then(t => addForm.value.roomCode = t)
}

function addConfig() {
  addForm.value.configList.push({
    roomId: "",
    sectionId: "",
    stationId: "",
    executeTime: "",
    factoryId: "",
    statusId: "",
    id: ""
  })
}

function deleteConfig(index: number) {
  addForm.value.configList.splice(index, 1)
}
</script>

<style scoped lang="scss">

</style>

