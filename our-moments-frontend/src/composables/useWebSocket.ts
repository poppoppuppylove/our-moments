import { ref, onMounted, onUnmounted } from 'vue'
import { Stomp } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import type { Notification } from '@/types'
import { useNotificationStore } from '@/store/notification'
import { useUserStore } from '@/store/user'

// 类型定义
interface WebSocketConfig {
  url: string
  reconnectDelay?: number
  heartbeatIncoming?: number
  heartbeatOutgoing?: number
}

interface WebSocketHook {
  connect: () => void
  disconnect: () => void
  isConnected: boolean
  error: string | null
}

// 默认配置
const DEFAULT_CONFIG: WebSocketConfig = {
  url: '/api/ws',
  reconnectDelay: 5000,
  heartbeatIncoming: 10000,
  heartbeatOutgoing: 10000
}

export function useWebSocket(config: WebSocketConfig = DEFAULT_CONFIG): WebSocketHook {
  const isConnected = ref(false)
  const error = ref<string | null>(null)
  const stompClient = ref<any>(null)
  const reconnectTimeout = ref<number | null>(null)

  const notificationStore = useNotificationStore()
  const userStore = useUserStore()

  // 连接到WebSocket服务器
  const connect = () => {
    if (!userStore.user?.userId) {
      console.warn('User not logged in, skipping WebSocket connection')
      return
    }

    try {
      // 创建SockJS连接
      const socket = new SockJS(config.url)

      // 创建STOMP客户端
      stompClient.value = Stomp.over(socket)
      stompClient.value.connect({}, onConnected, onError)
    } catch (err) {
      handleError('Failed to establish WebSocket connection')
    }
  }

  // 断开WebSocket连接
  const disconnect = () => {
    if (stompClient.value) {
      stompClient.value.disconnect()
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

    // 订阅用户特定的通知频道
    if (userStore.user?.userId) {
      stompClient.value.subscribe(
        `/user/${userStore.user.userId}/queue/notifications`,
        onNotificationReceived
      )
    }

    console.log('Connected to WebSocket server')
  }

  // 错误处理回调
  const onError = (err: any) => {
    handleError('WebSocket connection error: ' + (err.message || err))

    // 尝试重新连接
    if (config.reconnectDelay) {
      reconnectTimeout.value = window.setTimeout(connect, config.reconnectDelay)
    }
  }

  // 通知接收回调
  const onNotificationReceived = (payload: any) => {
    try {
      const notification: Notification = JSON.parse(payload.body)
      notificationStore.addNotification(notification)
    } catch (err) {
      console.error('Failed to parse notification:', err)
    }
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
    isConnected: isConnected.value,
    error: error.value
  }
}