<script lang="ts" setup>
import {useSettingsStore} from "@/pinia/stores/settings"
import {useDevice} from "@@/composables/useDevice"
import {useLayoutMode} from "@@/composables/useLayoutMode"
import {useWatermark} from "@@/composables/useWatermark"
import {getCssVar, setCssVar} from "@@/utils/css"
import {RightPanel, Settings} from "./components"
import {useResize} from "./composables/useResize"
import LeftMode from "./modes/LeftMode.vue"
import LeftTopMode from "./modes/LeftTopMode.vue"
import TopMode from "./modes/TopMode.vue"
import {getValue} from "@@/utils/cache/cookies.ts";
// #endregion
import dayjs from "dayjs"
import {useWebSocket} from "@v/websocket/websocket.ts"
import {onBeforeUnmount} from "vue";

// Layout 布局响应式
useResize()

const {setWatermark, clearWatermark} = useWatermark()
const {isMobile} = useDevice()
const {isLeft, isTop, isLeftTop} = useLayoutMode()
const settingsStore = useSettingsStore()
const {showSettings, showTagsView, showWatermark} = storeToRefs(settingsStore)

const {
  webSocketConnect,
  webSocketSubscribe,
  disconnect
} = useWebSocket()
// #region 隐藏标签栏时删除其高度，是为了让 Logo 组件高度和 Header 区域高度始终一致
const cssVarName = "--v3-tagsview-height"
const v3TagsviewHeight = getCssVar(cssVarName)
watchEffect(() => {
  showTagsView.value ? setCssVar(cssVarName, v3TagsviewHeight) : setCssVar(cssVarName, "0px")
})

// 开启或关闭系统水印
watchEffect(() => {
  // showWatermark.value ? setWatermark(import.meta.env.VITE_APP_TITLE) : clearWatermark()
  const loginPhone = getValue("loginPhone")
  const userName = getValue("userName")
  const time = dayjs(new Date()).format("YYYY-MM-DD")
  setWatermark(`${userName}-${loginPhone?.substring(loginPhone?.length - 4)}-${time}`)
})
onMounted(() => {
  // webSocketConnect()
  // setTimeout(() => {
  //   webSocketSubscribe("/notice", (r) => {
  //     console.info("/notice ", r)
  //   },true)
  // })
})

onBeforeUnmount(() => {
  disconnect()
})
</script>

<template>
  <div>
    <!-- 左侧模式 -->
    <LeftMode v-if="isLeft || isMobile"/>
    <!-- 顶部模式 -->
    <TopMode v-else-if="isTop"/>
    <!-- 混合模式 -->
    <LeftTopMode v-else-if="isLeftTop"/>
    <!-- 右侧设置面板 -->
    <RightPanel v-if="showSettings">
      <Settings/>
    </RightPanel>
  </div>
</template>
