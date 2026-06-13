<template>
  <div class="app-container">
    <el-card class="search-wrapper" shadow="never">
      <el-divider> 工艺路径生产顺序</el-divider>
      <el-row
        v-for="(item, index) in data.headerList"
        :key="index"
        class="orderDivMain"
      >
        {{ void (orderList = data.versionDetailMap[item.fieldName]) }}
        <div class="orderDivTitle">
          <div class="value">{{ item.showName }}</div>
          <div class="operation hidden">
            <el-button
              type="primary"
              @click="confirmSortIndex(item.fieldName + index)"
            >确认序列
            </el-button>
          </div>
        </div>
        <div class="orderDivSingleton">
          <div
            v-for="(order, index) in orderList"
            v-bind:id="order.id"
            :key="index"
            :ref="item.fieldName"
            class="orderDiv"
          >
            <table>
              <tr>
                <td class="title">制造序号:</td>
                <td class="value">
                  {{ order.sortIndex }}
                </td>
              </tr>
              <tr>
                <td class="title">排程制造ID:</td>
                <td class="value">
                  {{ order.id }}
                </td>
              </tr>
              <tr>
                <td class="title">单号:</td>
                <td class="value">
                  {{ order.orderNo }}
                </td>
              </tr>
              <tr>
                <td class="title">匹配类型:</td>
                <td class="value">
                  {{ order.configBizType }}
                </td>
              </tr>

              <tr>
                <td class="title">匹配名称:</td>
                <td class="value">
                  {{ order.configBizName }}
                </td>
              </tr>
              <tr>
                <td class="title">匹配:</td>
                <td class="value">
                  {{ order.isMatch }}
                </td>
              </tr>
              <tr>
                <td class="title">满足:</td>
                <td class="value">
                  {{ order.loopEnough }}
                </td>
              </tr>
              <tr>
                <td class="title">循环:</td>
                <td class="value">
                  {{ order.loopIndex }}
                </td>
              </tr>
            </table>
          </div>
        </div>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {postResultInfo} from '@@/utils/common-js.ts';
import {useRoute} from 'vue-router';

// 获取当前路由信息
const route = useRoute();

// 从路由参数中获取id
const id = route.params.id as string;
console.info('id ', id);

interface DataInfo {
  versionDetailMap: undefined;
  headerList: [];
}

const data = ref<DataInfo>({});

const confirmSortIndex = () => {
  console.info('confirmSortIndex');
};
onMounted(() => {
  postResultInfo('/apsSchedulingDayConfigVersion/detailList', {id: id}).then(
    (r) => {
      console.info('r ', r);
      data.value = r.data;
    }
  );
});
</script>

<style scoped lang="scss">
.orderDivMain {
  width: 100%;
  margin: 5px 0;
}

.orderDivSingleton {
  display: inline-flex;
  overflow-x: scroll;
}

.orderDivSingleton > div {
  overflow-x: scroll;
  display: inline-table;
  margin-bottom: 20px;
  width: 350px;
}

.orderDivTitle {
  font-size: 20px;
  width: 100%;
  padding: 0 0 10px 0;
  display: -webkit-box;
}

.operation {
  padding-left: 30px;
  float: right;
}

.orderDivTitle > div.value {
  font-weight: bold;
  color: #d23131;
}

.orderDiv {
  margin: 0 10px 10px 0;
  padding: 10px;
  border: 1px solid #ccc;
  width: 350px;
  scroll-behavior: smooth;
  cursor: move;
}

.orderDiv > div {
  line-height: 20px;
  display: -webkit-box;
}

.orderDiv > table {
  width: 350px;
}

.orderDiv > table > tr > td.title::after {
  width: 10px;
}

.orderDiv > table > tr > td {
  margin-left: 30px;
}

.orderDiv > table > tr > td.title {
  text-align: right;
  padding-right: 10px;
}
</style>
