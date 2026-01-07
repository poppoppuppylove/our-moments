import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@/types'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))

  // 初始化时尝试从 localStorage 恢复用户数据
  if (token.value) {
    const storedUser = localStorage.getItem('user')
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (e) {
        console.error('Failed to parse stored user data:', e)
      }
    }
  }

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => user.value?.username || '')
  const nickname = computed(() => user.value?.nickname || user.value?.username || '访客')
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 方法
  function setUser(userData: User) {
    user.value = userData
    // 同时存储到 localStorage
    localStorage.setItem('user', JSON.stringify(userData))
  }

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function logout() {
    user.value = null
    token.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function clearUser() {
    user.value = null
    localStorage.removeItem('user')
  }

  return {
    user,
    token,
    isLoggedIn,
    isAdmin,
    username,
    nickname,
    setUser,
    setToken,
    logout,
    clearUser
  }
})
