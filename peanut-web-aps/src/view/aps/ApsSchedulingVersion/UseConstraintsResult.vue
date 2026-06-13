<script setup lang="ts">
import {ref} from 'vue';
import TableBar from '@/layouts/components/TableBar/index.vue';
import type {ApsSchedulingVersion} from '@v/aps/ApsSchedulingVersion/ApsSchedulingVersionType.ts';
import {HeaderInfo, postNoResult, postResultInfo,} from '@@/utils/common-js.ts';
import {ApsGoods, queryGoodsList} from '@v/aps/ApsGoods/ApsGoodsType.ts';

const props = defineProps({
  id: {
    type: String,
    required: false,
  },
  operType: {
    type: String,
    default: '0', // 0：创建。 1：修改。 2： 查看
    required: false,
  },
  saveAfterFun: {
    type: Function || undefined,
    required: false,
  },
  preStepFun: {
    type: Function || undefined,
    required: false,
  },
});

const multipleSelection = ref<(string | undefined)[]>([]);

// 表格
// const dataTableRef = ref<InstanceType<typeof ElTable> | null>(null)
const dataTableRef = ref({});
// 表格操作头
const tableBarRef = ref<InstanceType<typeof TableBar> | null>(null);
// 表格相关
const dataList = ref<ApsSchedulingVersion[]>([]);
const currentPageNum = ref<number>(1);
const currentPageSize = ref<number>(10);
const tableTotal = ref<number>(0);
const headerList = ref<HeaderInfo[]>([]);
const loading = ref<boolean>(true);
const brandNameList = ref<any[]>([]);
const queryParams = ref({
  data: {
    goodsId: undefined,
  },
});
const goodsList = ref<ApsGoods[]>([]);
// 页面条数变更事件
const handleSizeChange = (val: number) => {
  currentPageSize.value = val;
  getDataList();
};
// 页面变更事件
const handleCurrentChange = (val: number) => {
  currentPageNum.value = val;
  getDataList();
};

console.info('props ', props);
const getDataList = () => {
  postResultInfo('/apsSchedulingVersion/useConstraintsResult', {
    pageNum: currentPageNum.value,
    pageSize: currentPageSize.value,
    id: props.id,
  }).then((t) => {
    brandNameList.value = t.data.dataList;
    headerList.value = t.data.headerList;
    tableTotal.value = parseInt(t.data.total);
    loading.value = false;
  });
};

const useMakeCapacity = () => {
  console.info('useMakeCapacity ', props.operType);
  if ('2' != props.operType) {
    postNoResult(
      '/apsSchedulingVersion/useMakeCapacity',
      {id: props.id},
      '开始计算产能',
      () => {
        if (props.saveAfterFun) {
          props.saveAfterFun();
        }
      }
    );
  } else {
    if (props.saveAfterFun) {
      props.saveAfterFun();
    }
  }
};

onMounted(() => {
  queryGoodsList().then((r) => (goodsList.value = r));
  getDataList();
});
</script>

<template>
  <div>
    <el-form
      ref="queryForm"
      :inline="true"
      :model="queryParams"
      label-width="88px"
    >
      <el-form-item label="商品" prop="goodsId">
        <el-select
          v-model="queryParams.data.goodsId"
          clearable
          placeholder="请选择商品"
          style="width: 200px"
        >
          <el-option
            v-for="item in goodsList"
            :key="item.id"
            :label="item.goodsName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button
          icon="el-icon-search"
          style="margin: 10px 0"
          type="primary"
          @click="getDataList"
        >
          搜索
        </el-button>
      </el-form-item>
    </el-form>

    <el-table
      ref="dataTableRef"
      v-loading="loading"
      :data="brandNameList"
      width="100%"
    >
      <el-table-column
        align="center"
        label="全选"
        prop="id"
        type="selection"
        width="50"
      />
      <el-table-column
        v-for="(item, index) in headerList"
        :key="index"
        :label="item.showName"
        :prop="item.fieldName"
        align="center"
      />
      <el-table-column label="" align="center"/>
    </el-table>
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

    <el-row style="margin-top: 20px">
      <!--      <el-button type="warning" @click="props.preStepFun">-->
      <!--        上一步-->
      <!--      </el-button>-->
      <el-button type="primary" @click="useMakeCapacity"> 下一步</el-button>
    </el-row>
  </div>
</template>

<style lang="scss" scoped></style>
