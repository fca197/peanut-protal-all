<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {type ApsSchedulingDayConfigVersion, boolArr,} from './ApsSchedulingDayConfigVersionType.ts';
import {getById, KVEntity, postNoResult} from '@/common/utils/common-js.ts';
import {type FormInstance, FormRules} from 'element-plus';
import {ApsSchedulingDayConfig, queryApsSchedulingDayConfigList,} from '@v/aps/ApsSchedulingDayConfig/ApsSchedulingDayConfigType.ts';
import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';
import {ApsGoods, queryGoodsList} from '@v/aps/ApsGoods/ApsGoodsType.ts';
import {queryOrderFieldList} from '@v/aps/ApsOrder/ApsOrderType.ts';
import {queryOrderUserFieldList} from '@v/aps/ApsOrderUser/ApsOrderUserType.ts';
import {querySaleConfigList,} from '@v/aps/ApsSaleConfig/ApsSaleConfigType.ts';
import dayjs from 'dayjs';

const props = defineProps({
  saveFun: {
    type: Function,
  },
  editId: {
    type: String,
    required: false,
  },
});

// 对象URL
const dtoUrl = ref<string>('/apsSchedulingDayConfigVersion');
// 表单引用
const addFormRef = ref<FormInstance>();
// 表单校验规则
const checkRules = ref<FormRules>({
  // 配置ID
  schedulingDayConfigId: [
    {required: true, message: '请输入配置ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 工厂ID
  factoryId: [
    {required: true, message: '请输入工厂ID', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
  // 排程版本号
  schedulingDayVersionNo: [
    {required: true, message: '请输入排程版本号', trigger: 'blur'},
    {min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur'},
  ],
  // 排程日期
  schedulingDay: [
    {required: true, message: '请输入排程日期', trigger: 'blur'},
    {min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur'},
  ],
});

// 添加对象
const addForm = ref<ApsSchedulingDayConfigVersion>({
  schedulingDayConfigId: undefined,
  factoryId: undefined,
  schedulingDayVersionNo:
    'PC-' +
    dayjs().format('YYYYMMDDHHmmss') +
    '-' +
    Math.floor(Math.random() * 10000 + 1),
  schedulingDay: undefined,
  searchOld: true,
  isIssuedThird: undefined,
  processId: undefined,
  headerList: undefined,
  productType: undefined,
  goodsIdList: undefined,
  saleConfigIdList: undefined,
  stepIndex: undefined,
  orderFieldList: undefined,
  orderUserFieldList: undefined,
  id: undefined,
});

const loadById = () => {
  if (!props.editId) {
    return;
  }
  console.info('props.editId ', props.editId);
  getById(`${dtoUrl.value}/queryByIdList`, props.editId).then((t) => {
    addForm.value = t;
    console.info(' addForm.value ', addForm.value);
  });
};

// 保存
const saveForm = () => {
  console.info('addForm ', addForm);
  addFormRef.value?.validate((valid) => {
    if (valid) {
      // 存在ID ，调用更新
      if (props.editId) {
        postNoResult(
          `${dtoUrl.value}/updateById`,
          addForm.value,
          '修改成功',
          saveFormAfter
        );
      } else {
        // 调用保存
        postNoResult(
          `${dtoUrl.value}/insert`,
          addForm.value,
          '保存成功',
          saveFormAfter
        );
      }
    } else {
      ElMessage.error('表单校验失败，请检查必填项');
    }
  });
};

// 保存成功后，方法， 目前关闭弹窗
const saveFormAfter = () => {
  cancelForm();
};

// 取消方法
const cancelForm = () => {
  if (props.saveFun) {
    props.saveFun();
  }
};

const schedulingDayConfigChange = (id) => {
  const ll = apsSchedulingDayConfigList.value.filter((t) => t.id === id);
  if (ll.length === 0) {
    addForm.value.productType = undefined;
    return;
  }
  const t = ll[0];
  addForm.value.processId = t.processId;
  if (t.schedulingType === 'make') {
    addForm.value.productType = 'MAKE';
  } else {
    addForm.value.productType = 'PROCESS';
  }
};

const apsSchedulingDayConfigList = ref<ApsSchedulingDayConfig[]>([]);

const factoryList = ref<Factory[]>([]);
const apsGoodsList = ref<ApsGoods[]>([]);
const orderFieldList = ref<KVEntity[]>([]);
const orderUserFieldList = ref<KVEntity[]>([]);
const apsSaleConfigList = ref<KVEntity[]>([]);

watch(
  () => addForm.value.factoryId,
  (n) => {
    console.info('addForm.factoryId', n);
    addForm.value.schedulingDayConfigId = undefined;
    addForm.value.goodsIdList = undefined;
  }
);

// 页面加载事件
onMounted(() => {
  loadById();
  queryApsSchedulingDayConfigList().then(
    (r) => (apsSchedulingDayConfigList.value = r)
  );
  queryFactoryList().then((r) => {
    factoryList.value = r;
    console.info('props.editId', props.editId);
    if (props.editId === '') {
      addForm.value.factoryId = r[0].id;
    }
  });
  queryGoodsList().then((t) => (apsGoodsList.value = t));
  queryOrderFieldList().then((r) => (orderFieldList.value = r));
  queryOrderUserFieldList().then((r) => (orderUserFieldList.value = r));
  querySaleConfigList().then(
    (r) =>
      (apsSaleConfigList.value = r
      .filter((t) => t.isValue === 0)
      .map((t) => {
        return {
          label: t.saleName,
          value: t.id,
        };
      }))
  );
});
</script>

<template>
  <el-form
    ref="addFormRef"
    :model="addForm"
    :rules="checkRules"
    label-width="120px"
  >
    <el-form-item label="工厂" prop="factoryId">
      <el-select v-model="addForm.factoryId">
        <el-option
          v-for="f in factoryList"
          :key="f.id"
          :label="f.factoryName"
          :value="f.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="排程配置" prop="schedulingDayConfigId">
      <el-select
        v-model="addForm.schedulingDayConfigId"
        @change="schedulingDayConfigChange"
      >
        <el-option
          v-for="c in apsSchedulingDayConfigList.filter(
            (t) => t.factoryId === addForm.factoryId
          )"
          :key="c.id"
          :value="c.id as string"
          :label="c.schedulingDayName"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="排程版本号" prop="schedulingDayVersionNo">
      <el-input
        v-model="addForm.schedulingDayVersionNo"
        clearable
        placeholder="请输入排程版本号"
      />
    </el-form-item>
    <el-form-item label="排程日期" prop="schedulingDay">
      <el-date-picker
        v-model="addForm.schedulingDay"
        placeholder="请输入排程日期"
        style="width: 100%"
        value-format="YYYY-MM-DD"
      />
    </el-form-item>
    <el-form-item label="查询历史订单" prop="searchOld">
      <el-select v-model="addForm.searchOld" style="width: 100%">
        <el-option
          v-for="b in boolArr"
          :key="b.value"
          :label="b.label"
          :value="b.value"
        ></el-option>
      </el-select>
    </el-form-item>
    <!--    <el-form-item label="商品列" prop="goodsIdList">-->
    <!--      <el-select v-model="addForm.goodsIdList" clearable placeholder="请选择商品列" multiple>-->
    <!--        <el-option-->
    <!--          v-for="g in apsGoodsList.filter(t => t.factoryId === addForm.factoryId)" :key="g.id"-->
    <!--          :label="g.goodsName" :value="g.id"-->
    <!--        />-->
    <!--      </el-select>-->
    <!--    </el-form-item>-->

    <!--    <el-form-item label="销售配置" prop="saleConfigIdList">-->
    <!--      <el-select v-model="addForm.saleConfigIdList" clearable placeholder="请选择销售配置" multiple>-->
    <!--        <el-option v-for="s in apsSaleConfigList" :value="s" :label="s.label" :key="s.label"/>-->
    <!--      </el-select>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="订单字段" prop="orderFieldList">-->
    <!--      <el-select v-model="addForm.orderFieldList" clearable placeholder="请选择订单字段" multiple>-->
    <!--        <el-option v-for="k in orderFieldList" :value="k" :key="k.value" :label="k.label"/>-->
    <!--      </el-select>-->
    <!--    </el-form-item>-->
    <!--    <el-form-item label="订单用户字段" prop="orderUserFieldList">-->
    <!--      <el-select v-model="addForm.orderUserFieldList" clearable placeholder="请选择订单用户字段" multiple>-->
    <!--        <el-option v-for="k in orderUserFieldList" :value="k" :key="k.value" :label="k.label"/>-->
    <!--      </el-select>-->
    <!--    </el-form-item>-->
  </el-form>
  <el-row class="addFormBtnRow">
    <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
    <el-button icon="check" type="primary" @click="saveForm"> 确定</el-button>
  </el-row>
</template>

<style lang="scss" scoped></style>
