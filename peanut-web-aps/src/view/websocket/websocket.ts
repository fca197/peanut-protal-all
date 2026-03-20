import {computed, onBeforeUnmount, onMounted, ref} from "vue"
import {Client, Message, StompHeaders} from "@stomp/stompjs"
import SockJS from "sockjs-client"
import {ActivationState} from "@stomp/stompjs/src/types.ts";
import {getToken} from "@@/utils/cache/cookies.ts";

const baseUrl = import.meta.env.VITE_BASE_URL;

// 定义消息接口
export interface WebSocketMessage {
  messageType: string
  messageTypeDesc: string
  messageTitle: string
  messageContent: string
  messageDetailUrl: string
  messageCallBackFunName: string
  messageDateTime: string
  requestId: string
  tmp01: string
  tmp02: string
  tmp03: string
  tmp04: string
  tmp05: string
  tmp06: string
}

// 定义连接状态类型
export type ConnectionState = "DISCONNECTED" | "CONNECTING" | "CONNECTED" | "ERROR"

// WebSocket 服务
export const useWebSocket = () => {
  const stompClient = ref<Client | null>(null)
  const connectionState = ref<ConnectionState>("DISCONNECTED")
  const error = ref<string | null>(null)

  // 连接到 WebSocket 服务器
  const webSocketConnect = (url = `${baseUrl}/ws`) => {
    connectionState.value = "CONNECTING"
    error.value = null

    // 创建 SockJS 连接
    const socket =  new SockJS(url)

    // 初始化 STOMP 客户端
    stompClient.value = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000, // 初始重连延迟（毫秒）
      // maxReconnectDelay: 30000, // 最大重连延迟
      heartbeatIncoming: 10000, // 期望接收心跳的间隔（毫秒）
      heartbeatOutgoing: 10000, // 发送心跳的间隔（毫秒）
      connectionTimeout: 10000, // 连接超时时间


      // 连接成功回调
      onConnect: () => {
        connectionState.value = "CONNECTED"
        console.log("WebSocket 连接成功")
      },

      // 连接错误回调
      onStompError: (frame) => {
        connectionState.value = "ERROR"
        error.value = frame.headers.message || "Stomp 连接错误"
        console.error("Stomp 连接错误:", frame)
      },

      // 通用错误回调
      onWebSocketError: (error) => {
        connectionState.value = "ERROR"
        console.error("WebSocket 错误:", error)
      },
      onChangeState: (state: ActivationState) => {
        console.log("onChangeState state ", state)
      }
    })

    // 激活客户端
    // stompClient.value.activate()
  }

  // 断开连接
  const disconnect = () => {
    if (stompClient.value && stompClient.value.connected) {
      stompClient.value.deactivate()
      connectionState.value = "DISCONNECTED"
      console.log("WebSocket 已断开连接")
    }
  }

  // 订阅主题
  const webSocketSubscribe = (
    destination: string,
    callback: (message: WebSocketMessage) => void,
    isUserMsg: boolean = false,
    sleepTime: number = 2000
  ): void => {
    setTimeout(() => {
      if (!stompClient.value || !stompClient.value.connected) {
        console.warn("无法订阅，连接未建立")
        return
      }
      if (isUserMsg) {
        destination = `/user/${getToken()}${destination}`
      }
      console.log("开始订阅 ", destination)
      stompClient.value.subscribe(destination, (stompMessage: Message) => {
        try {
          console.info("destination ", destination, stompMessage)
          if (stompMessage.body) {
            const message: WebSocketMessage = JSON.parse(stompMessage.body)
            callback(message)
          } else {
            console.log("消息为空")
          }
        } catch (e) {
          console.error("解析消息失败:", e)
        }
      })
    }, sleepTime)
  }

  // 发送消息
  const webSocketSend = (
    destination: string,
    body: any,
    headers: StompHeaders = {}
  ) => {
    if (!stompClient.value || !stompClient.value.connected) {
      console.warn("无法发送消息，连接未建立")
      return false
    }

    const payload = typeof body === "string" ? body : JSON.stringify(body)
    stompClient.value.publish({destination, body: payload, headers})
    return true
  }

  // 生命周期管理
  onMounted(() => {
    console.log("onMounted used")
    // webSocketConnect()
  })

  onBeforeUnmount(() => {
    disconnect()
  })

  // 计算属性：连接状态文本
  const connectionText = computed(() => {
    switch (connectionState.value) {
      case "CONNECTED":
        return "已连接"
      case "CONNECTING":
        return "连接中..."
      case "DISCONNECTED":
        return "已断开"
      case "ERROR":
        return "连接错误"
      default:
        return "未知状态"
    }
  })

  return {
    connectionState,
    connectionText,
    error,
    webSocketConnect,
    disconnect,
    webSocketSubscribe,
    webSocketSend
  }
}
