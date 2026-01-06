<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import MessageIndicator from '@/components/messages/MessageIndicator.vue'
import { computed } from 'vue'

const route = useRoute()

// 在通知中心页面和聊天页面上隐藏MessageIndicator
const showIndicator = computed(() => {
  return route.name !== 'Notifications' && route.name !== 'Chat'
})
</script>

<template>
  <RouterView />
  <MessageIndicator v-if="showIndicator" />
</template>

<style>
/* 全局样式已在 main.scss 中定义 */
</style>
