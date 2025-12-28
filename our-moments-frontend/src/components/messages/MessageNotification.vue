<template>
  <div class="message-notification" @click="openChat">
    <div class="notification-content">
      <img :src="messageSender?.avatar" :alt="messageSender?.nickname" class="sender-avatar" />
      <div class="notification-text">
        <div class="sender-name">{{ messageSender?.nickname }}</div>
        <div class="message-preview">{{ messagePreview }}</div>
      </div>
      <div class="notification-time">{{ timeAgo }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { Message, User } from '@/types'

const props = defineProps<{
  message: Message
  messageSender?: User
}>()

const router = useRouter()

// 计算消息预览（截取前50个字符）
const messagePreview = computed(() => {
  if (!props.message.content) return ''
  return props.message.content.length > 50
    ? props.message.content.substring(0, 50) + '...'
    : props.message.content
})

// 计算时间差
const timeAgo = computed(() => {
  if (!props.message.createTime) return ''

  const now = new Date()
  const messageTime = new Date(props.message.createTime)
  const diffInMinutes = Math.floor((now.getTime() - messageTime.getTime()) / (1000 * 60))

  if (diffInMinutes < 1) return '刚刚'
  if (diffInMinutes < 60) return `${diffInMinutes}分钟前`

  const diffInHours = Math.floor(diffInMinutes / 60)
  if (diffInHours < 24) return `${diffInHours}小时前`

  const diffInDays = Math.floor(diffInHours / 24)
  if (diffInDays < 7) return `${diffInDays}天前`

  return messageTime.toLocaleDateString('zh-CN')
})

// 打开聊天页面
function openChat() {
  if (props.message.senderId) {
    router.push(`/chat/${props.message.senderId}`)
  }
}
</script>

<style scoped lang="scss">
.message-notification {
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-radius: 12px;

  &:hover {
    background-color: rgba(168, 140, 190, 0.1);
  }
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sender-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--color-paper);
}

.notification-text {
  flex: 1;
  min-width: 0;
}

.sender-name {
  font-weight: 500;
  color: var(--color-ink);
  margin-bottom: 2px;
}

.message-preview {
  font-size: 0.9rem;
  color: var(--color-ink-light);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.notification-time {
  font-size: 0.8rem;
  color: var(--color-ink-lighter);
  white-space: nowrap;
}
</style>
</script>
</file>