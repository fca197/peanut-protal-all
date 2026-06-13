<script setup lang="ts">
import {KVEntity, postNoResult, postResultInfo, postResultInfoList,} from '@@/utils/common-js.ts';
import {useRoute} from 'vue-router';
import {label} from 'happy-dom/lib/PropertySymbol.d.ts.js';
import {ref} from 'vue';
import {ApsOrderGoodsBomKittingTemplate} from '@v/aps/ApsOrderGoodsBomKittingTemplate/ApsOrderGoodsBomKittingTemplateType.ts';

const route = useRoute();
const step = ref<string>(route.params.step);
console.info('step ', step);
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

const total = ref<number>(0);
const apsSchedulingVersionLimitKey = ref<number>(0);
const loading = ref<boolean>(true);
const createKitting = ref<boolean>(false);
const apsSchedulingIssueItemAddDilogShow = ref<boolean>(false);
const tableHeaderList = ref<any[]>([]);
const dayList = ref<any[]>([]);
const brandNameList = ref<any[]>([]);
const apsSchedulingVersionLimitMap = ref({});
const queryParams = ref<any>({
  id: props.id,
  currentDate: [],
  pageNum: 1,
  pageSize: 10,
});
const createKittingModel = ref({
  schedulingVersionTemplateId: null,
  schedulingVersionId: props.id,
  kittingDate: [],
});

const useConstraintsResult = () => {
  queryParams.value.currentDate = queryParams.value.currentDate.sort();
  postResultInfo(
    '/apsSchedulingVersion/useMakeCapacityResult',
    queryParams.value
  ).then((t) => {
    console.log('useMakeCapacityResult', t);
    brandNameList.value = t.data.dataList;
    tableHeaderList.value = t.data.headerList;
    total.value = parseInt(t.data.total);
    loading.value = false;
  });
};

const apsSchedulingVersionLimitFun = () => {
  postResultInfo('/apsSchedulingVersionLimit/queryPageList', {
    queryPage: false,
    data: {versionId: props.id},
  }).then((r) => {
    console.log('apsSchedulingVersionLimit ', r);
    r.data.dataList.forEach((v) => {
      var tt = apsSchedulingVersionLimitMap.value[v.currentDay];
      if (tt === undefined) {
        tt = [v];
      } else {
        tt.push(v);
      }
      tt = tt.sort((a, b) => {
        return a.limitType - b.limitType;
      });
      apsSchedulingVersionLimitMap.value[v.currentDay] = tt;
    });
    apsSchedulingVersionLimitKey.value = Math.random();
  });
};
const loadDayList = () => {
  postResultInfoList('/apsSchedulingVersionDay/queryPageList', {
    queryPage: false,
    data: {versionId: props.id},
  }).then((r) => {
    dayList.value = r.reverse();
  });
};
const updateFinish = () => {
  if ('2' === props.operType) {
    if (props.saveAfterFun) {
      props.saveAfterFun();
      return;
    }
  }
  postNoResult(
    '/apsSchedulingVersion/finish',
    {
      id: props.id,
    },
    '排产结束',
    () => {
      if (props.saveAfterFun) {
        props.saveAfterFun();
      }
    }
  );
};

const autoDayList: KVEntity[] = [1, 3, 7, 10, 15, 20, 30, 60].map((t) => {
  return {
    label: t,
    value: t,
  };
});

const showCreateKitting = () => {
  createKitting.value = true;
};
const selectKittingDate = (c: number) => {
  createKittingModel.value.kittingDate = dayList.value
  .slice(0, c)
  .map((t) => t.currentDay);
  console.info('createKittingModel.value ', createKittingModel.value);
};

const apsSchedulingIssueItemAddDialogForm = ref({
  schedulingVersionId: props.id,
  scheduledDayList: [],
});
const apsSchedulingIssueItemAddDialogDate = (c: number) => {
  apsSchedulingIssueItemAddDialogForm.value.scheduledDayList = dayList.value
  .slice(0, c)
  .map((t) => t.currentDay);
  console.info(
    'apsSchedulingIssueItemAddDialogForm.value ',
    apsSchedulingIssueItemAddDialogForm.value
  );
};

const apsSchedulingIssueItemAdd = () => {
  postNoResult(
    '/apsSchedulingIssueItem/insert',
    apsSchedulingIssueItemAddDialogForm.value,
    '下发完成',
    () => {
      apsSchedulingIssueItemAddDilogShow.value = false;
    }
  );
};
const createKittingVersion = () => {
  postNoResult(
    '/apsOrderGoodsBomKittingVersion/createSchedulingKittingVersion',
    createKittingModel.value,
    '齐套版本创建成功',
    () => {
      createKitting.value = false;
      // router.
      showKitting();
    }
  );
};

const apsOrderGoodsBomKittingTemplateList = ref<
  ApsOrderGoodsBomKittingTemplate[]
>([]);

const kittingTemplate = () => {
  postResultInfoList('/apsOrderGoodsBomKittingTemplate/queryPageList', {
    queryPage: false,
  }).then((r) => {
    apsOrderGoodsBomKittingTemplateList.value = r;
    createKittingModel.value.schedulingVersionTemplateId = r[0].id;
  });
};

const router = useRouter();
const showKitting = () => {
  router.push(`/aps/ApsOrderGoodsBomKittingVersion/${props.id}`);
};

const apsSchedulingIssueItemAddDialogShowFun = () => {
  apsSchedulingIssueItemAddDilogShow.value = true;
};

onMounted(() => {
  loadDayList();
  useConstraintsResult();
  apsSchedulingVersionLimitFun();
  kittingTemplate();
});
</script>

<template>
  <el-row v-loading="loading" :key="apsSchedulingVersionLimitKey">
    <el-col :span="3" style="max-height: 90vh; overflow: auto">
      <el-checkbox-group v-model="queryParams.currentDate">
        <el-checkbox
          v-for="(d, index) in dayList"
          :key="index"
          :label="d.currentDay"
          :value="d.currentDay"
          @change="useConstraintsResult"
        >
          <label>{{ d.currentDay }} </label>
          <el-badge v-if="!d.hasEnough" class="item" is-dot>
            {{ d.currentCount }}
          </el-badge
          >
        </el-checkbox>
      </el-checkbox-group>
    </el-col>
    <el-col :span="20">
      <el-col
        v-for="(d, i) in queryParams.currentDate"
        :key="i"
        :span="24"
        style="border-bottom: #f6c1c1 1px solid; margin-bottom: 20px"
      >
        <el-col :span="24"
        >当前日期: <label>{{ d }}</label>
          <el-badge
            v-if="
              dayList.filter((iten) => iten.currentDay === d)[0].hasEnough ===
              false
            "
            class="item"
            is-dot
          ></el-badge>
        </el-col>
        <el-row style="flex-wrap: wrap; width: 100%" type="flex">
          <el-col
            v-for="(li, index) in apsSchedulingVersionLimitMap[d]"
            :key="index"
            :span="6"
            style="margin: 2px 0; height: 25px"
          >
            {{ li.showName }}: {{ li.currentCount }}/{{ li.min }}-{{ li.max }}
            <el-badge
              v-if="li.currentCount < li.min"
              :value="li.min - li.currentCount"
              class="item"
              is-dot
            ></el-badge>
          </el-col>
        </el-row>
      </el-col>
      <el-col :span="24">
        <el-table v-loading="loading" :data="brandNameList" width="100%">
          <!--     <el-table-column label="全选" type="selection" align="center" prop="id" width="50"/>-->
          <el-table-column
            v-for="(item, index) in tableHeaderList"
            :key="index"
            align="center"
            :prop="item.fieldName"
            :width="item.width"
            :label="item.showName"
          />
          <el-table-column label="" type="text" align="center"/>
        </el-table>

        <el-row class="paginationDiv">
          <el-pagination
            background
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            layout="total, sizes, prev, pager, next"
            :total="total"
            @size-change="useConstraintsResult"
            @current-change="useConstraintsResult"
          />
        </el-row>
      </el-col>
    </el-col>
    <el-row>
      <el-button type="primary" @click="updateFinish"> 下一步</el-button>
      <el-button type="primary" v-if="step === '3'" @click="showCreateKitting">
        生成齐套版本
      </el-button>
      <el-button type="primary" v-if="step === '3'" @click="showKitting">
        查看齐套报告
      </el-button>
      <el-button
        v-if="step === '3'"
        type="primary"
        @click="apsSchedulingIssueItemAddDialogShowFun"
      >
        订单下发
      </el-button>
    </el-row>
    <el-dialog
      v-model="createKitting"
      :width="950"
      destroy-on-close
      title="生成齐套版本"
    >
      <el-row style="margin: 10px 20px; text-align: left">
        <el-row style="margin: 10px 0; line-height: 25px">
          模板版本:
          <el-select
            v-model="createKittingModel.schedulingVersionTemplateId"
            style="width: 300px"
          >
            <el-option
              v-for="(t, index) in apsOrderGoodsBomKittingTemplateList"
              :key="t.id"
              :label="t.kittingTemplateName"
              :value="t.id"
            />
          </el-select>
        </el-row>
        <el-row>
          <el-button
            v-for="k in autoDayList"
            type="warning"
            @click="selectKittingDate(k.value)"
            :key="k.value"
          >
            最近 {{ k.label }} 天
          </el-button>
        </el-row>
        <el-row style="margin: 10px 0 20px">
          <el-checkbox-group v-model="createKittingModel.kittingDate">
            <el-checkbox
              v-for="(d, index) in dayList"
              :key="index"
              :value="d.currentDay"
              :label="d.currentDay"
            >
              <label>{{ d.currentDay }} </label>
              <el-badge v-if="!d.hasEnough" class="item" is-dot>
                {{ d.currentCount }}
              </el-badge
              >
            </el-checkbox>
          </el-checkbox-group>
        </el-row>
        <el-row style="width: 900px; display: block">
          <el-button
            style="float: right"
            type="primary"
            @click="createKittingVersion"
          >
            创建齐套版本
          </el-button>
        </el-row>
      </el-row>
    </el-dialog>

    <el-dialog
      v-model="apsSchedulingIssueItemAddDilogShow"
      :width="950"
      destroy-on-close
      title="订单下发"
    >
      <el-row>
        <el-button
          v-for="k in autoDayList"
          type="warning"
          @click="apsSchedulingIssueItemAddDialogDate(k.value)"
          :key="k.value"
        >
          最近 {{ k.label }} 天
        </el-button>
      </el-row>
      <el-row style="margin: 10px 0 20px">
        <el-checkbox-group
          v-model="apsSchedulingIssueItemAddDialogForm.scheduledDayList"
        >
          <el-checkbox
            v-for="(d, index) in dayList"
            :key="index"
            :value="d.currentDay"
            :label="d.currentDay"
          >
            <label>{{ d.currentDay }} </label>
            <el-badge v-if="!d.hasEnough" class="item" is-dot>
              {{ d.currentCount }}
            </el-badge
            >
          </el-checkbox>
        </el-checkbox-group>
      </el-row>
      <el-row style="width: 900px; display: block; margin-bottom: 60px">
        <el-button
          style="float: right"
          type="primary"
          @click="apsSchedulingIssueItemAdd"
        >
          下发
        </el-button>
      </el-row>
    </el-dialog>
  </el-row>
</template>

<style lang="scss" scoped></style>
