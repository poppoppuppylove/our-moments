import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { notificationApi } from '@/api'
import type { Notification } from '@/types'
import { useUserStore } from './user'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<Notification[]>([])
  const unreadCount = ref(0)
  const loading = ref(false)

  // 计算属性
  const unreadNotifications = computed(() =>
    notifications.value.filter(n => !n.isRead)
  )

  const readNotifications = computed(() =>
    notifications.value.filter(n => n.isRead)
  )

  // Actions
  async function loadNotifications() {
    const userStore = useUserStore()
    if (!userStore.user?.userId) return

    loading.value = true
    try {
      const data = await notificationApi.getNotifications(userStore.user.userId)
      notifications.value = data
      unreadCount.value = unreadNotifications.value.length
    } catch (error) {
      console.error('Failed to load notifications:', error)
    } finally {
      loading.value = false
    }
  }

  async function loadUnreadCount() {
    const userStore = useUserStore()
    if (!userStore.user?.userId) return

    try {
      const count = await notificationApi.getUnreadCount(userStore.user.userId)
      unreadCount.value = count
    } catch (error) {
      console.error('Failed to load unread count:', error)
    }
  }

  async function markAsRead(notificationId: number) {
    try {
      const updated = await notificationApi.markAsRead(notificationId)

      // 更新本地状态
      const index = notifications.value.findIndex(n => n.notificationId === notificationId)
      if (index !== -1) {
        notifications.value[index] = updated
        unreadCount.value = unreadNotifications.value.length
      }
    } catch (error) {
      console.error('Failed to mark notification as read:', error)
    }
  }

  async function markAllAsRead() {
    const userStore = useUserStore()
    if (!userStore.user?.userId) return

    try {
      await notificationApi.markAllAsRead(userStore.user.userId)

      // 更新本地状态
      notifications.value = notifications.value.map(n => ({
        ...n,
        isRead: true
      }))
      unreadCount.value = 0
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error)
    }
  }

  function addNotification(notification: Notification) {
    // 将新通知添加到列表顶部
    notifications.value.unshift(notification)

    // 如果是未读通知，增加未读计数
    if (!notification.isRead) {
      unreadCount.value++
    }
  }

  function removeNotification(notificationId: number) {
    const index = notifications.value.findIndex(n => n.notificationId === notificationId)
    if (index !== -1) {
      const removed = notifications.value.splice(index, 1)[0]
      // 如果是未读通知，减少未读计数
      if (!removed.isRead) {
        unreadCount.value--
      }
    }
  }

  return {
    notifications,
    unreadCount,
    loading,
    unreadNotifications,
    readNotifications,
    loadNotifications,
    loadUnreadCount,
    markAsRead,
    markAllAsRead,
    addNotification,
    removeNotification
  }
})