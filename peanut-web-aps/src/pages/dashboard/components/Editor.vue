<script lang="ts" setup>
import SvgDashboard from '../images/dashboard.svg?component'; // vite-svg-loader 插件的功能
</script>

<template>
  <div class="app-container center">
    <SvgDashboard class="svg"/>
    <p>欢迎使用<span class="domainSpanClass">APS</span>高级排产排程系统</p>
  </div>
</template>

<style lang="scss" scoped>
.center {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  .svg {
    width: 600px;
    max-width: 100%;
  }
}

.domainSpanClass {
  margin: 0 10px;
  line-height: 20px;
  font-size: 20px;
  color: red;
}
</style>
