import { ref, onMounted, onUnmounted, type Ref } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useUserStore } from '@/store/user'

// 类型定义
interface ChatWebSocketConfig {
  url: string
  reconnectDelay?: number
  heartbeatIncoming?: number
  heartbeatOutgoing?: number
}

interface ChatMessage {
  senderId: number
  receiverId: number
  content: string
  timestamp: string
}

interface ChatWebSocketHook {
  connect: () => void
  disconnect: () => void
  sendMessage: (message: ChatMessage) => void
  isConnected: Ref<boolean>
  error: Ref<string | null>
  messages: Ref<ChatMessage[]>
}

// 默认配置
const DEFAULT_CONFIG: ChatWebSocketConfig = {
  url: '/ws',
  reconnectDelay: 5000,
  heartbeatIncoming: 10000,
  heartbeatOutgoing: 10000
}

export function useChatWebSocket(config: ChatWebSocketConfig = DEFAULT_CONFIG): ChatWebSocketHook {
  const isConnected = ref(false)
  const error = ref<string | null>(null)
  const stompClient = ref<Client | null>(null)
  const reconnectTimeout = ref<number | null>(null)
  const messages = ref<ChatMessage[]>([])

  const userStore = useUserStore()

  // 连接到WebSocket服务器
  const connect = () => {
    if (!userStore.user?.userId) {
      console.warn('User not logged in, skipping WebSocket connection')
      return
    }

    // 从 localStorage 获取 token
    const token = localStorage.getItem('token')

    try {
      // 创建STOMP客户端
      stompClient.value = new Client({
        brokerURL: config.url,
        connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        debug: function (str) {
          console.log('[Chat WebSocket]', str)
        },
        reconnectDelay: config.reconnectDelay,
        heartbeatIncoming: config.heartbeatIncoming,
        heartbeatOutgoing: config.heartbeatOutgoing,
        webSocketFactory: function () {
          return new SockJS(config.url) as any
        },
        onConnect: onConnected,
        onStompError: onError,
        onWebSocketError: onError
      })

      stompClient.value.activate()
    } catch (err) {
      handleError('Failed to establish WebSocket connection')
    }
  }

  // 断开WebSocket连接
  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.deactivate()
    }

    isConnected.value = false

    // 清除重连定时器
    if (reconnectTimeout.value) {
      clearTimeout(reconnectTimeout.value)
      reconnectTimeout.value = null
    }
  }

  // 连接成功回调
  const onConnected = () => {
    isConnected.value = true
    error.value = null

    // 订阅用户特定的消息频道
    if (userStore.user?.userId && stompClient.value) {
      stompClient.value.subscribe(
        `/user/${userStore.user.userId}/queue/messages`,
        onMessageReceived
      )
    }

    console.log('Connected to Chat WebSocket server')
  }

  // 错误处理回调
  const onError = (err: any) => {
    handleError('Chat WebSocket connection error: ' + (err.message || err))

    // 尝试重新连接
    if (config.reconnectDelay) {
      reconnectTimeout.value = window.setTimeout(connect, config.reconnectDelay)
    }
  }

  // 消息接收回调
  const onMessageReceived = (payload: any) => {
    try {
      const message: ChatMessage = JSON.parse(payload.body)
      messages.value.push(message)
    } catch (err) {
      console.error('Failed to parse chat message:', err)
    }
  }

  // 发送消息
  const sendMessage = (message: ChatMessage) => {
    if (!stompClient.value || !isConnected.value) {
      console.warn('WebSocket not connected, cannot send message')
      return
    }

    stompClient.value.publish({
      destination: '/app/chat',
      body: JSON.stringify(message)
    })
  }

  // 错误处理
  const handleError = (errorMsg: string) => {
    error.value = errorMsg
    isConnected.value = false
    console.error(errorMsg)
  }

  // 组件挂载时连接
  onMounted(() => {
    // 只有在用户登录时才连接
    if (userStore.user?.userId) {
      connect()
    }
  })

  // 组件卸载时断开连接
  onUnmounted(() => {
    disconnect()
  })

  return {
    connect,
    disconnect,
    sendMessage,
    isConnected,
    error,
    messages
  }
}