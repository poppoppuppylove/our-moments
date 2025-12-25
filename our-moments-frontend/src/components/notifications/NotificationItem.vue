<template>
  <div
    class="notification-item"
    :class="{ 'notification-item--unread': !notification.isRead }"
    @click="$emit('click', notification)"
  >
    <div class="notification-icon">
      <svg
        v-if="notification.type === 'COMMENT'"
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
      </svg>
      <svg
        v-else-if="notification.type === 'FRIEND_REQUEST'"
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
        <circle cx="8.5" cy="7" r="4"></circle>
        <line x1="20" y1="8" x2="20" y2="14"></line>
        <line x1="23" y1="11" x2="17" y2="11"></line>
      </svg>
      <svg
        v-else-if="notification.type === 'NEW_POST'"
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
        <polyline points="14 2 14 8 20 8"></polyline>
        <line x1="16" y1="13" x2="8" y2="13"></line>
        <line x1="16" y1="17" x2="8" y2="17"></line>
        <polyline points="10 9 9 9 8 9"></polyline>
      </svg>
    </div>

    <div class="notification-content">
      <p class="notification-text">{{ notification.content }}</p>
      <p class="notification-time">{{ formatTime(notification.createTime) }}</p>
    </div>

    <div class="notification-actions">
      <button
        v-if="!notification.isRead"
        class="mark-read-button"
        @click.stop="$emit('mark-as-read', notification.notificationId)"
        title="标为已读"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="16"
          height="16"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
        >
          <polyline points="20 6 9 17 4 12"></polyline>
        </svg>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { AppNotification } from '@/types'

interface Props {
  notification: AppNotification
}

interface Emits {
  (e: 'click', notification: AppNotification): void
  (e: 'mark-as-read', notificationId: number): void
}

defineProps<Props>()
defineEmits<Emits>()

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
</script>

<style scoped lang="scss">
.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(168, 140, 190, 0.15);
  transition: all 0.2s;
  cursor: pointer;

  &:hover {
    background: rgba(255, 255, 255, 0.8);
    border-color: rgba(168, 140, 190, 0.3);
    transform: translateY(-2px);
  }

  &--unread {
    background: rgba(168, 140, 190, 0.05);
    border-color: rgba(168, 140, 190, 0.2);

    &:hover {
      background: rgba(168, 140, 190, 0.1);
    }
  }
}

.notification-icon {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  background: rgba(168, 140, 190, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-soft-purple);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-text {
  font-family: var(--font-body);
  font-size: 0.95rem;
  color: var(--color-ink);
  margin: 0 0 6px 0;
  line-height: 1.5;
  word-break: break-word;
}

.notification-time {
  font-family: var(--font-body);
  font-size: 0.8rem;
  color: var(--color-ink-light);
  margin: 0;
}

.notification-actions {
  flex-shrink: 0;
}

.mark-read-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
  color: var(--color-ink-light);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    background: rgba(168, 140, 190, 0.1);
    color: var(--color-soft-purple);
  }
}
</style>