<script setup lang="ts">
// 获取当前路由信息

import {Factory, queryFactoryList} from '@v/base/Factory/FactoryType.ts';
import {ApsGoods, queryGoodsList} from '@v/aps/ApsGoods/ApsGoodsType.ts';
import {FormRules} from 'element-plus';
import {pinyin4jSzm, postNoResult, postResultInfoList,} from '@@/utils/common-js.ts';

const props = defineProps({
  id: {
    type: String,
    required: false,
  },
  operType: {
    type: String,
    default: '2', // 0：创建。 1：修改。 2： 查看
    required: false,
  },
  saveAfterFun: {
    type: Function,
    required: false,
  },
  preStepFun: {
    type: Function,
    required: false,
  },
});

interface AddFormDto {
  schedulingVersionNo: string;
  schedulingVersionName: string;
  schedulingConstraintsId: string;
  startDate: string;
  useSaleConfigMakeCapacity: boolean;
  useGoodsMakeCapacity: boolean;
  useFactoryMakeCapacity: boolean;
  schedulingDayCount: number;
  factoryIdList: string[];
  goodsIdList: string[];
}

const addForm = ref<AddFormDto>({
  schedulingVersionNo: '',
  schedulingVersionName: '',
  schedulingConstraintsId: '',
  startDate: '',
  useSaleConfigMakeCapacity: false,
  useGoodsMakeCapacity: false,
  useFactoryMakeCapacity: false,
  schedulingDayCount: 94,
  factoryIdList: [],
  goodsIdList: [],
  id: props.id,
});

const formRef = ref(null);
const saveOrUpdate = () => {
  console.info('saveOrUpdate addForm ', addForm, props.operType);
  if (props.operType === '2') {
    props.saveAfterFun();
    return;
  }

  formRef.value.validate((res) => {
    if (res) {
      if ('0' === props.operType) {
        postNoResult(
          '/apsSchedulingVersion/insert',
          addForm.value,
          '保存成功',
          (r) => {
            postNoResult(
              '/apsSchedulingVersion/useConstraints',
              {
                id: r.id,
              },
              '开始筛选并排序订单',
              () => {
                props.saveAfterFun(r);
              }
            );
          }
        );
      } else if ('1' === props.operType) {
        postNoResult(
          '/apsSchedulingVersion/updateById',
          addForm.value,
          '修改成功',
          (r) => {
            postNoResult(
              '/apsSchedulingVersion/useConstraints',
              {
                id: props.id,
              },
              '开始筛选并排序订单',
              () => {
                props.saveAfterFun(undefined);
              }
            );
          }
        );
      }
    } else {
      ElMessage.error('表单校验失败，请检查必填项');
    }
  });
};

const factoryList = ref<Factory[]>([]);
const goodsList = ref<ApsGoods[]>([]);

const schedulingConstraintsList = ref<any[]>([]);

const rules = ref<FormRules>({
  schedulingDayCount: [
    {required: true, message: '不能为空'},
    {type: 'number', message: '必须为数字值'},
  ],
  schedulingVersionNo: [
    {required: true, message: '不能为空', trigger: 'blur'},
    {
      min: 1,
      max: 20,
      message: '长度在 1 到 20 个字符',
      trigger: 'blur',
    },
  ],
  schedulingVersionName: [
    {required: true, message: '不能为空', trigger: 'blur'},
    {
      min: 1,
      max: 20,
      message: '长度在 1 到 20 个字符',
      trigger: 'blur',
    },
  ],
  schedulingConstraintsId: [
    {required: true, message: '不能为空', trigger: 'blur'},
  ],
  startDate: [{required: true, message: '不能为空', trigger: 'blur'}],
});
const loadSzm = () => {
  pinyin4jSzm(addForm.value.schedulingVersionName).then(
    (r) => (addForm.value.schedulingVersionNo = r)
  );
};

onMounted(() => {
  queryFactoryList().then((r) => (factoryList.value = r));
  queryGoodsList().then((r) => (goodsList.value = r));
  postResultInfoList('/apsSchedulingConstraints/queryPageList', {
    queryPage: false,
  }).then((r) => (schedulingConstraintsList.value = r));

  postResultInfoList('/apsSchedulingVersion/queryByIdList', {
    idList: [props.id],
  }).then((t) => {
    console.log('apsSchedulingVersion ', t);
    if (t.length > 0) {
      addForm.value = t[0];
    }
  });
});
</script>

<template>
  <el-form ref="formRef" :model="addForm" label-width="130px" :rules="rules">
    <el-form-item label="版本名称" prop="schedulingVersionName">
      <el-input
        v-model="addForm.schedulingVersionName"
        placeholder="请输入版本名称"
        @change="loadSzm"
      />
    </el-form-item>
    <el-form-item label="版本号" prop="schedulingVersionNo">
      <el-input
        v-model="addForm.schedulingVersionNo"
        placeholder="请输入版本号"
      />
    </el-form-item>

    <el-form-item label="工厂" prop="factoryIdList">
      <el-select
        v-model="addForm.factoryIdList"
        multiple
        placeholder="请输入版本名称"
        @change="addForm.goodsIdList = []"
      >
        <el-option
          v-for="f in factoryList"
          :key="f.id"
          :label="f.factoryName"
          :value="f.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="商品" prop="goodsIdList">
      <el-select
        v-model="addForm.goodsIdList"
        multiple
        placeholder="请输入版本名称"
      >
        <el-option
          v-for="g in goodsList.filter(
            (t) =>
              addForm.factoryIdList.filter((tt) => tt === t.factoryId).length >
              0
          )"
          :key="g.id"
          :label="g.goodsName"
          :value="g.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="约束条件" prop="schedulingConstraintsId">
      <el-select
        v-model="addForm.schedulingConstraintsId"
        placeholder="请选择约束条件"
      >
        <el-option
          v-for="item in schedulingConstraintsList"
          :key="item.id"
          :label="item.constraintsName"
          :value="item.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="开始日期" prop="startDate">
      <el-date-picker
        v-model="addForm.startDate"
        type="date"
        value-format="YYYY-MM-DD"
        placeholder="开始日期"
      />
    </el-form-item>
    <el-form-item label="排产限制约束">
      <el-checkbox
        v-model="addForm.useFactoryMakeCapacity"
        :value="true"
        label="工厂产能"
      />
      <el-checkbox
        v-model="addForm.useGoodsMakeCapacity"
        :value="true"
        label="商品产能"
      />
      <el-checkbox
        v-model="addForm.useSaleConfigMakeCapacity"
        :value="true"
        label="销售配置产能"
      />
    </el-form-item>
    <el-form-item label="排产天数" prop="schedulingDayCount">
      <el-input-number
        v-model.number="addForm.schedulingDayCount"
        :max="100"
        :min="1"
        placeholder="请输入排产天数"
      />
    </el-form-item>
    <el-row>
      <el-button type="primary" @click="saveOrUpdate"> 下一步</el-button>
    </el-row>
  </el-form>
</template>

<style scoped lang="scss">
.el-checkbox-group {
  margin: 0 20px 0 0px;
}
</style>
