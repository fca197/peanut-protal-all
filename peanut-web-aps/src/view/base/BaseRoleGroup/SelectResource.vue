<template>
  <div :key="timeKey">
    <el-tree
      :default-checked-keys="selectResourceList"
      :data="allResourceTree"
      node-key="id"
      :props="treeProps"
      show-checkbox
      ref="treeRef"
    />
    <div class="addFormBtnRow">
      <el-button icon="close" type="info" @click="cancelForm"> 取消</el-button>
      <el-button @click="submitAppForm" type="primary" icon="check">
        确定
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {postNoResult, postResultInfoList} from '@@/utils/common-js.ts';
import {list2tree} from '@@/utils/listUtils.ts';

const props = defineProps({
  closeFun: {
    type: Function,
  },
  id: {
    type: String,
  },
});
const timeKey = ref<string>('');

const timeKeyUpdate = () => {
  timeKey.value = Math.random() + '';
};
const treeProps = {
  label: 'resourceName',
  children: 'children',
};

const selectResourceList = ref<string[]>([]);
const allResourceTree = ref<any[]>([]);
const treeRef = ref(null);

const submitAppForm = () => {
  let checkedKeys = treeRef.value.getCheckedKeys();
  let halfCheckedKeys = treeRef.value.getHalfCheckedKeys();
  checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
  let saveObj = {};
  saveObj['roleGroupId'] = props.id;
  saveObj['resourceIdList'] = checkedKeys;
  console.info('checkedKeys ', saveObj);
  postNoResult('/baseRoleGroupResource/insertList', saveObj, '保存成功', () => {
    cancelForm();
  });
};
const cancelForm = () => {
  if (props.closeFun) {
    props.closeFun();
  }
};
onMounted(() => {
  console.info('id ', props.id);
  const ll = [];
  ll.push(
    postResultInfoList('/baseResource/queryPageList', {queryPage: false})
  );
  ll.push(
    postResultInfoList('/baseRoleGroupResource/queryPageList', {
      queryPage: false,
      data: {roleGroupId: props.id},
    })
  );
  Promise.all(ll).then((vl) => {
    const resourceList = vl[0];
    const baseRoleResourceList = vl[1];
    allResourceTree.value = list2tree(resourceList, '0');
    selectResourceList.value = baseRoleResourceList.map((t) => t.resourceId);
    console.info(
      'selectResourceList ',
      resourceList.map((t) => t.id),
      selectResourceList.value
    );
  });
});
</script>

<style lang="scss" scoped></style>
