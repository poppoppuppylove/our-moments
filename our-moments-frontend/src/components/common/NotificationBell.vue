<template>
  <div class="notification-bell" @click="toggleDropdown">
    <button class="bell-button" :class="{ 'bell-button--has-unread': hasUnread }">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        class="bell-icon"
        :class="{ 'bell-icon--ring': isDropdownOpen }"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
      </svg>
      <transition name="badge">
        <span v-if="hasUnread" class="unread-badge">{{ unreadCount }}</span>
      </transition>
    </button>

    <!-- 通知下拉面板 -->
    <transition name="dropdown">
      <div v-if="isDropdownOpen" class="notification-dropdown">
        <div class="dropdown-header">
          <h3 class="dropdown-title">通知</h3>
          <button
            v-if="notificationStore.unreadCount > 0"
            class="mark-all-read"
            @click.stop="markAllAsRead"
          >
            全部标为已读
          </button>
        </div>

        <div class="dropdown-content">
          <HandLoading v-if="notificationStore.loading" text="加载中..." />

          <div v-else-if="notificationStore.notifications.length === 0" class="no-notifications">
            暂无通知
          </div>

          <div v-else class="notifications-list">
            <div
              v-for="notification in notificationStore.notifications"
              :key="notification.notificationId"
              class="notification-item"
              :class="{ 'notification-item--unread': !notification.isRead }"
              @click="handleNotificationClick(notification)"
            >
              <div class="notification-content">
                <p class="notification-text">{{ notification.content }}</p>
                <p class="notification-time">{{ formatTime(notification.createTime) }}</p>
              </div>
              <div v-if="!notification.isRead" class="unread-indicator"></div>
            </div>
          </div>
        </div>

        <div class="dropdown-footer">
          <HandButton
            variant="ghost"
            size="sm"
            @click.stop="goToNotificationsPage"
          >
            查看所有通知
          </HandButton>
        </div>
      </div>
    </transition>

    <!-- 点击遮罩关闭 -->
    <div v-if="isDropdownOpen" class="dropdown-overlay" @click="closeDropdown"></div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/store/notification'
import HandButton from '@/components/base/HandButton.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import { useWebSocket } from '@/composables/useWebSocket'

const router = useRouter()
const notificationStore = useNotificationStore()
const isDropdownOpen = ref(false)
const dropdownListener = ref<((e: Event) => void) | null>(null)

// WebSocket连接
const { connect, disconnect } = useWebSocket()

// 计算属性
const hasUnread = computed(() => notificationStore.unreadCount > 0)
const unreadCount = computed(() => {
  const count = notificationStore.unreadCount
  return count > 99 ? '99+' : count.toString()
})

// 切换下拉面板
function toggleDropdown() {
  isDropdownOpen.value = !isDropdownOpen.value

  // 如果打开下拉面板，加载通知
  if (isDropdownOpen.value) {
    notificationStore.loadNotifications()
  }
}

// 关闭下拉面板
function closeDropdown() {
  isDropdownOpen.value = false
}

// 点击通知项
function handleNotificationClick(notification: any) {
  // 标为已读
  if (!notification.isRead) {
    notificationStore.markAsRead(notification.notificationId)
  }

  // 根据通知类型跳转到相应页面
  switch (notification.type) {
    case 'COMMENT':
      router.push(`/post/${notification.relatedId}`)
      break
    case 'FRIEND_REQUEST':
      router.push('/friends')
      break
    case 'NEW_POST':
      router.push(`/post/${notification.relatedId}`)
      break
    case 'MESSAGE':
      router.push(`/chat/${notification.relatedId}`)
      break
  }

  closeDropdown()
}

// 标记所有为已读
async function markAllAsRead() {
  await notificationStore.markAllAsRead()
}

// 跳转到通知页面
function goToNotificationsPage() {
  closeDropdown()
  router.push('/notifications')
}

// 格式化时间
function formatTime(timeString: string): string {
  const date = new Date(timeString)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    if (diffHours === 0) {
      const diffMinutes = Math.floor(diffMs / (1000 * 60))
      return diffMinutes <= 0 ? '刚刚' : `${diffMinutes}分钟前`
    }
    return `${diffHours}小时前`
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

// 点击外部关闭下拉面板
function handleClickOutside(e: Event) {
  const target = e.target as HTMLElement
  if (!target.closest('.notification-bell')) {
    closeDropdown()
  }
}

// 组件挂载
onMounted(() => {
  // 加载未读计数
  notificationStore.loadUnreadCount()

  // 连接WebSocket
  connect()

  // 添加外部点击监听器
  dropdownListener.value = handleClickOutside
  document.addEventListener('click', handleClickOutside)
})

// 组件卸载
onUnmounted(() => {
  // 断开WebSocket连接
  disconnect()

  // 移除外部点击监听器
  if (dropdownListener.value) {
    document.removeEventListener('click', dropdownListener.value)
  }
})
</script>

<style scoped lang="scss">
.notification-bell {
  position: relative;
  display: inline-block;
}

.bell-button {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.2s ease;
  color: var(--color-ink);

  &:hover {
    background: rgba(168, 140, 190, 0.1);
    color: var(--color-soft-purple);
  }

  &--has-unread {
    color: var(--color-soft-purple);
  }
}

.bell-icon {
  width: 20px;
  height: 20px;
  transition: all 0.3s ease;

  &--ring {
    animation: ring 0.5s ease;
  }
}

@keyframes ring {
  0% { transform: rotate(0); }
  15% { transform: rotate(5deg); }
  30% { transform: rotate(-5deg); }
  45% { transform: rotate(4deg); }
  60% { transform: rotate(-4deg); }
  75% { transform: rotate(2deg); }
  85% { transform: rotate(-2deg); }
  92% { transform: rotate(1deg); }
  100% { transform: rotate(0); }
}

.unread-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: var(--color-soft-purple);
  color: white;
  font-size: 0.6rem;
  font-weight: bold;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: translate(30%, -30%);
}

.badge-enter-active,
.badge-leave-active {
  transition: all 0.3s ease;
}

.badge-enter-from,
.badge-leave-to {
  transform: translate(30%, -30%) scale(0);
  opacity: 0;
}

.notification-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  width: 320px;
  max-height: 400px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(168, 140, 190, 0.2);
  z-index: 1000;
  overflow: hidden;
  margin-top: 8px;
  display: flex;
  flex-direction: column;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid rgba(168, 140, 190, 0.1);
}

.dropdown-title {
  font-family: var(--font-handwriting);
  font-size: 1.2rem;
  color: var(--color-soft-purple);
  margin: 0;
}

.mark-all-read {
  font-family: var(--font-body);
  font-size: 0.8rem;
  color: var(--color-ink-light);
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;

  &:hover {
    background: rgba(168, 140, 190, 0.1);
    color: var(--color-soft-purple);
  }
}

.dropdown-content {
  flex: 1;
  overflow-y: auto;
  max-height: 300px;
}

.no-notifications {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-ink-light);
  font-style: italic;
}

.notifications-list {
  display: flex;
  flex-direction: column;
}

.notification-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid rgba(168, 140, 190, 0.05);
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: rgba(168, 140, 190, 0.05);
  }

  &--unread {
    background: rgba(168, 140, 190, 0.03);
  }
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-text {
  font-family: var(--font-body);
  font-size: 0.9rem;
  color: var(--color-ink);
  margin: 0 0 4px 0;
  line-height: 1.4;
  word-break: break-word;
}

.notification-time {
  font-family: var(--font-body);
  font-size: 0.75rem;
  color: var(--color-ink-light);
  margin: 0;
}

.unread-indicator {
  width: 8px;
  height: 8px;
  background: var(--color-soft-purple);
  border-radius: 50%;
  margin-left: 12px;
}

.dropdown-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(168, 140, 190, 0.1);
  text-align: center;
}

.dropdown-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 768px) {
  .notification-dropdown {
    width: 280px;
    right: -30px;
  }
}
</style>