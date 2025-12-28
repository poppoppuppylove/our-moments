<template>
  <div class="message-indicator" v-if="unreadCount > 0" @click="goToMessages">
    <div class="indicator-badge">{{ unreadCount }}</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { messageApi } from '@/api'

const router = useRouter()
const userStore = useUserStore()
const unreadCount = ref(0)

// 获取未读消息数量
async function fetchUnreadCount() {
  if (!userStore.user) return

  try {
    const messages = await messageApi.getUnreadMessages()
    unreadCount.value = messages.length
  } catch (error) {
    console.error('Failed to fetch unread messages:', error)
  }
}

// 跳转到消息页面
function goToMessages() {
  router.push('/chat')
}

// 定期检查未读消息
let intervalId: number | null = null

onMounted(() => {
  if (userStore.isLoggedIn) {
    fetchUnreadCount()
    // 每30秒检查一次未读消息
    intervalId = window.setInterval(fetchUnreadCount, 30000)
  }
})

// 在组件卸载时清理定时器
onUnmounted(() => {
  if (intervalId) {
    clearInterval(intervalId)
  }
})
</script>

<style scoped lang="scss">
.message-indicator {
  position: fixed;
  bottom: 100px;
  right: 20px;
  z-index: 1000;
  cursor: pointer;
}

.indicator-badge {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: var(--color-soft-purple);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.1rem;
  box-shadow: 0 4px 12px rgba(168, 140, 190, 0.3);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(168, 140, 190, 0.3);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 6px 16px rgba(168, 140, 190, 0.4);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 4px 12px rgba(168, 140, 190, 0.3);
  }
}
</style>