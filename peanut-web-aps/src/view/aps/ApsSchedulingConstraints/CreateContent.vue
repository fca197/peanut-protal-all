<template>
  <div>
    <el-col style="margin-bottom: 10px" :span="24" v-if="!isChild">
      <el-button type="primary" icon="plus" @click="addRowConstraint">
        添加
      </el-button>
    </el-col>
    <el-col
      v-for="(item, ooo) in rowConstraintList"
      :key="ooo"
      :span="24"
      style="
        border-bottom: #00afff 1px solid;
        margin-bottom: 20px;
        margin-top: 10px;
      "
    >
      <el-col :span="2" width="60px">
        {{ '约束-' + (ooo + 1) }}
        <el-button
          v-if="ooo !== 0"
          icon="delete"
          type="danger"
          @click="deleteRowConstraint(ooo)"
        >
          删除
        </el-button>
      </el-col>
      <el-col
        :span="22"
        style="padding-left: 10px; border-left: #a15050 1px solid"
      >
        <el-col v-for="(f, index) in item.filterList" :key="index" :span="24">
          <el-col :span="24" style="padding-bottom: 10px">
            <el-select
              v-model="f.fieldName"
              style="width: 200px"
              @change="changeField(f, index)"
            >
              <el-option
                v-for="(item, index3) in constrainedFieldList"
                :key="index3"
                :label="item.showName"
                :value="item.fieldName"
              >
              </el-option>
            </el-select>
            -
            <el-select v-model="f.operator" style="width: 200px">
              <el-option
                v-for="(item, index3) in operatorMap[f.id]"
                :key="index3"
                :label="item.name"
                :value="item.value"
                @change="changeOperator(f)"
              >
              </el-option>
            </el-select>
            <el-button
              v-if="index !== 0"
              icon="delete"
              type="danger"
              @click="deleteFilter(item, ooo)"
            >
              删除
            </el-button>
            <el-button icon="plus" type="primary" @click="addFilter(item, ooo)">
              添加
            </el-button>
          </el-col>
          <el-col :span="2"> 过滤值:</el-col>
          <el-col :span="22" style="padding-bottom: 10px">
            <div v-if="f.filterFieldType === 'DATE'">
              <div
                v-if="f.operator === 'BETWEEN' || f.operator === 'NOT_BETWEEN'"
              >
                <el-date-picker
                  v-model="f.valueList"
                  clearable
                  placeholder="请输入过滤值"
                  type="daterange"
                />
              </div>
              <div v-else-if="f.operator !== 'IS_NULL'">
                <el-date-picker
                  v-model="f.valueList[0]"
                  clearable
                  placeholder="请输入过滤值"
                  type="date"
                />
              </div>
            </div>
            <div v-else-if="f.filterFieldType === 'TEXT'">
              <div
                v-if="f.operator === 'BETWEEN' || f.operator === 'NOT_BETWEEN'"
              >
                <el-input
                  v-model="f.valueList[0]"
                  clearable
                  placeholder="请输入过滤值"
                  style="width: 30%"
                />
                至
                <el-input
                  v-model="f.valueList[1]"
                  clearable
                  placeholder="请输入过滤值"
                  style="width: 30%"
                />
              </div>
              <div v-else-if="f.operator !== 'IS_NULL'">
                <el-input
                  v-model="f.valueList[0]"
                  clearable
                  placeholder="请输入过滤值"
                  style="width: 90%"
                />
              </div>
            </div>
            <div v-else-if="f.filterFieldType === 'SELECT'">
              <div v-if="f.operator === 'IN'">
                <el-select v-model="f.valueList" multiple>
                  <el-option
                    v-for="(item4, index4) in operatorMapValueList[f.id]"
                    :key="index4"
                    :label="item4.valueName"
                    :value="item4.value"
                  >
                  </el-option>
                </el-select>
              </div>
              <div v-else>
                <el-select v-model="f.valueList[0]">
                  <el-option
                    v-for="(item4, index4) in operatorMapValueList[f.id]"
                    :key="index4"
                    :label="item4.valueName"
                    :value="item4.value"
                  >
                  </el-option>
                </el-select>
              </div>
            </div>
          </el-col>
        </el-col>

        <el-col :span="24" style="padding-bottom: 10px">
          <el-col :span="3">
            排序:
            <el-button type="primary" @click="addOrderBy(item)" icon="plus">
              添加
            </el-button>
          </el-col>
          <el-col :span="21">
            <div
              v-for="(o, i) in item.orderBy"
              :key="i"
              style="padding-bottom: 10px"
            >
              <el-select v-model="o.fieldName" style="width: 200px">
                <el-option
                  v-for="(item, index3) in constrainedFieldList"
                  :key="index3"
                  :label="item.showName"
                  :value="item.fieldName"
                >
                </el-option>
              </el-select>
              <el-select v-model="o.orderType" style="width: 200px">
                <el-option label="升序" value="ASC"></el-option>
                <el-option label="降序" value="DSC"></el-option>
              </el-select>
              <el-button
                icon="delete"
                type="danger"
                @click="deleteOrderBy(item, i)"
              >
                删除
              </el-button>
            </div>
          </el-col>
        </el-col>

        <el-col :span="24" style="padding-bottom: 10px">
          <el-col :span="6">
            子约束:
            <el-button type="primary" @click="addChild(item)" icon="plus">
              添加
            </el-button>
          </el-col>
          <CreateContent
            v-if="item.children.length > 0"
            :delete-first="deleteFirst"
            :constrained-field-list="constrainedFieldList"
            :is-child="true"
            :row-constraint-list="item.children"
          />
        </el-col>
      </el-col>
    </el-col>
  </div>
</template>

<script setup lang="ts">
import CreateContent from './CreateContent.vue';

const props = defineProps({
  deleteFirst: {
    type: Boolean,
    default: false,
  },
  isChild: {
    default: false,
  },
  constrainedFieldList: {},
  rowConstraintList: {
    type: Array,
  },
});
const constObj = ref({
  filterList: [
    {
      filterFieldType: '',
      fieldName: '',
      operator: '',
      valueList: [],
    },
  ],
  children: [],
  orderBy: [],
});

const operatorMap = ref<any>({});
const operatorMapValueList = ref<any>({});

const initSetOperatorMap = (list: any[]) => {
  if (list) {
    list.forEach((t) => {
      if (t.orderBy === undefined) {
        t.orderBy = [];
      }
      // console.info("t: ", t.filterList)
      for (let i = 0; i < t.filterList.length; i++) {
        const t2 = t.filterList[i];
        console.info('t2: ', t2);
        try {
          const f = props.constrainedFieldList.filter(
            (t) => t.fieldName === t2.fieldName
          )[0];
          // console.info("t2,f: ", t2,f )
          operatorMap.value[t2.id] = f.operator;
          operatorMapValueList.value[t2.id] = f.valueItemList;
        } catch (e) {
          console.info(e);
          try {
            operatorMap.value[t2.id] = props.constrainedFieldList[0].operator;
          } catch (e) {
            console.info(e);
          }
        }
      }
      initSetOperatorMap(t.children);
    });
  }
  console.info('operatorMap', operatorMap.value);
};
const changeOperator = (f: any) => {
  f.valueList = [];
};
const changeField = (it, index) => {
  const t = props.constrainedFieldList.filter(
    (t) => t.fieldName === it.fieldName
  )[0];
  it.fieldName = t.fieldName;
  it.showName = t.showName;
  it.filterFieldType = t.valueType;
  operatorMap.value[it.id] = t.operator;
  operatorMapValueList.value[it.id] = t.valueItemList;
  console.info('changeField: ', it, operatorMapValueList);
  it.valueList = [];
};
const randomNum = (length) => {
  return Math.random() + '' + length;
};
const addRowConstraint = () => {
  try {
    var items = JSON.parse(JSON.stringify(this.constObj));
    items.id = randomNum(38);
    items.filterList.forEach((t) => {
      t.id = randomNum(38);
    });
    props.rowConstraintList.push(items);
  } catch (e) {
    console.info('addRowConstraint: ', e);
  }
};
const addOrderBy = (item) => {
  // debugger
  if (item.orderBy === undefined) {
    item.orderBy = [];
  }
  item.orderBy.push({});
};
const deleteOrderBy = (item: any[], i) => {
  item.orderBy.splice(i, 1);
};
const addChild = (item) => {
  try {
    const items = JSON.parse(JSON.stringify(this.constObj));
    items.id = randomNum(9) + '-';
    items.filterList.forEach((t) => {
      t.id = randomNum(9) + '-';
    });
    item.children.push(items);
  } catch (e) {
    console.info('addChild: ', e);
  }
};

const deleteFilter = (item: any[], i: number) => {
  item.filterList.splice(i, 1);
};
const addFilter = (item) => {
  item.filterList.push({valueList: []});
};
const deleteRowConstraint = (index) => {
  this.rowConstraintList.splice(index, 1);
};
onMounted(() => {
  if (
    props.isChild &&
    props.rowConstraintList &&
    props.rowConstraintList.length === 0
  ) {
    return;
  }
  initSetOperatorMap(props.rowConstraintList);
});
</script>

<style lang="scss" scoped></style>
