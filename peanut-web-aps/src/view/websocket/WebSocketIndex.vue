<script setup lang="ts">
import {ref} from 'vue';
import {useWebSocket} from './websocket.ts';
import {useUserStore} from '@/pinia/stores/user.ts';

// 使用 WebSocket 服务
const {
  connectionState,
  connectionText,
  error,
  webSocketConnect,
  disconnect,
  webSocketSubscribe,
  webSocketSend,
} = useWebSocket();

// 消息内容
const messageText = ref('');
// 用户名
const username = ref('用户' + Math.floor(Math.random() * 1000));

// 发送消息
const sendMessage = () => {
  if (messageText.value.trim()) {
    const message = {
      sender: username.value,
      content: messageText.value,
      timestamp: new Date().toISOString(),
    };

    if (webSocketSend('/app/message', message)) {
      // messageText.value = ''
    }
  }
};

onMounted(() => {
  webSocketSubscribe('/user/' + useUserStore().token + '/userMessage', (r) => {
    // console.log("webSocketSubscribe r ", r)
  });
  webSocketSubscribe('/topic/allMessage', (r) => {
    // console.log("allMessage ", r)
  });
});
</script>

<template>
  <div class="bg-white rounded-lg shadow-md p-6 max-w-md mx-auto mt-8">
    <!-- 连接状态显示 -->
    <div class="mb-4 flex items-center">
      <span
        class="inline-block w-3 h-3 rounded-full mr-2"
        :class="{
          'bg-green-500': connectionState === 'CONNECTED',
          'bg-yellow-500': connectionState === 'CONNECTING',
          'bg-red-500':
            connectionState === 'DISCONNECTED' || connectionState === 'ERROR',
        }"
      ></span>
      <span>
        {{ connectionText }}
      </span>
      <span v-if="error" class="ml-2 text-red-500 text-sm">
        {{ error }}
      </span>
    </div>

    <!-- 输入区域 -->
    <div class="flex">
      <input
        v-model="messageText"
        class="flex-1 border rounded-l px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        placeholder="输入消息..."
      />
      <button
        @click="sendMessage"
        class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-r transition-colors"
        :disabled="connectionState !== 'CONNECTED'"
      >
        发送
      </button>
    </div>

    <!-- 连接控制 -->
    <div class="mt-4 flex justify-between">
      <el-button
        @click="webSocketConnect"
        class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded transition-colors"
        :disabled="
          connectionState === 'CONNECTING' || connectionState === 'CONNECTED'
        "
      >
        连接
      </el-button>
      <el-button
        @click="disconnect"
        class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded transition-colors"
        :disabled="connectionState === 'DISCONNECTED'"
      >
        断开
      </el-button>
    </div>
  </div>
</template>

<style scoped>
/* 自定义样式 */
</style>
