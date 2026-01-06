<template>
  <PaperTexture variant="light" class="min-h-screen notifications-wrapper">
    <div class="notifications-page">
      <HandCard variant="paper" class="notifications-card">
        <Pin variant="blue" position="top" />

        <header class="notifications-header">
          <div class="header-left">
            <HandButton variant="ghost" size="sm" @click="goBack" class="back-button">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="19" y1="12" x2="5" y2="12"></line>
                <polyline points="12 19 5 12 12 5"></polyline>
              </svg>
            </HandButton>
            <h1 class="notifications-title">通知中心</h1>
          </div>
          <div class="header-actions">
            <HandButton
              v-if="notificationStore.unreadCount > 0"
              variant="outline"
              size="sm"
              @click="markAllAsRead"
            >
              全部标为已读
            </HandButton>
          </div>
        </header>

        <div class="notifications-content">
          <HandLoading v-if="notificationStore.loading" text="加载通知中..." />

          <div v-else-if="notificationStore.notifications.length === 0" class="empty-state">
            <div class="empty-icon">🔔</div>
            <p>暂无通知</p>
            <p class="empty-subtext">当有人评论你的文章或发送好友请求时，你会在这里收到通知</p>
          </div>

          <div v-else class="notifications-list">
            <!-- 未读通知 -->
            <div v-if="notificationStore.unreadNotifications.length > 0" class="notifications-section">
              <h2 class="section-title">未读通知</h2>
              <div class="notifications-items">
                <NotificationItem
                  v-for="notification in notificationStore.unreadNotifications"
                  :key="notification.notificationId"
                  :notification="notification"
                  @mark-as-read="handleMarkAsRead"
                  @click="handleNotificationClick"
                />
              </div>
            </div>

            <!-- 已读通知 -->
            <div v-if="notificationStore.readNotifications.length > 0" class="notifications-section">
              <h2 class="section-title">已读通知</h2>
              <div class="notifications-items">
                <NotificationItem
                  v-for="notification in notificationStore.readNotifications"
                  :key="notification.notificationId"
                  :notification="notification"
                  @mark-as-read="handleMarkAsRead"
                  @click="handleNotificationClick"
                />
              </div>
            </div>
          </div>
        </div>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/store/notification'
import HandButton from '@/components/base/HandButton.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'
import NotificationItem from '@/components/notifications/NotificationItem.vue'

const router = useRouter()
const notificationStore = useNotificationStore()

// 标记所有为已读
async function markAllAsRead() {
  await notificationStore.markAllAsRead()
}

// 处理标记为已读
async function handleMarkAsRead(notificationId: number) {
  await notificationStore.markAsRead(notificationId)
}

// 处理通知点击
function handleNotificationClick(notification: any) {
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
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 组件挂载时加载通知
onMounted(() => {
  notificationStore.loadNotifications()
})
</script>

<style scoped lang="scss">
.notifications-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999; /* 确保整个通知页面固定在所有元素之上 */
  overflow-y: auto;
}

.notifications-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
  position: relative;
  z-index: 10000; /* 确保通知页面内容在其之上 */
}

.notifications-card {
  padding: 40px 30px;
}

.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-button {
  min-width: 40px;
}

.notifications-title {
  font-family: var(--font-handwriting);
  font-size: 2rem;
  color: var(--color-soft-purple);
  margin: 0;
}

.notifications-content {
  min-height: 300px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--color-ink-light);

  .empty-icon {
    font-size: 3rem;
    margin-bottom: 20px;
  }

  p {
    margin: 10px 0;
    font-family: var(--font-body);
  }

  .empty-subtext {
    font-size: 0.9rem;
    opacity: 0.7;
  }
}

.notifications-section {
  margin-bottom: 30px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-family: var(--font-handwriting);
  font-size: 1.3rem;
  color: var(--color-ink);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--color-ink-light);
}

.notifications-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

@media (max-width: 768px) {
  .notifications-page {
    padding: 20px 16px;
  }

  .notifications-card {
    padding: 24px 20px;
  }

  .notifications-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .notifications-title {
    font-size: 1.5rem;
  }
}
</style>