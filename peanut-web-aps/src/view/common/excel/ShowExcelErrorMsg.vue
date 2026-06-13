<script setup lang="ts">
const props = defineProps({
  excelErrorMsg: {
    type: Array,
    required: true,
  },
});
const showDialog = ref<boolean>(false);

const showDialogFun = () => {
  console.info('props.excelErrorMsg ', props.excelErrorMsg);
  if (props.excelErrorMsg) {
    if (props.excelErrorMsg.length > 0) {
      showDialog.value = true;
    }
  }
};
onMounted(() => {
  showDialogFun();
});
watch(
  () => props.excelErrorMsg,
  (data) => {
    console.info('excelErrorMsg change ', data);
    showDialogFun();
  }
);
</script>

<template>
  <el-dialog
    v-model="showDialog"
    :width="1000"
    destroy-on-close
    title="文件上传错误信息"
  >
    <el-table :data="props.excelErrorMsg">
      <el-table-column prop="sheetName" label="sheet名称" width="130"/>
      <el-table-column prop="columnIndex" label="列索引" width="80"/>
      <el-table-column prop="columnName" label="列名称" width="80"/>
      <el-table-column prop="columnStr" label="列" width="80"/>
      <el-table-column prop="rowIndex" label="行号" width="80"/>
      <el-table-column prop="errMsg" label="错误信息"/>
    </el-table>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
