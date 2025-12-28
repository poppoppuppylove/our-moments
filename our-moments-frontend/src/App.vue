<script setup lang="ts">
import { RouterView } from 'vue-router'
import MessageIndicator from '@/components/messages/MessageIndicator.vue'
</script>

<template>
  <RouterView />
  <MessageIndicator />
</template>

<style>
/* 全局样式已在 main.scss 中定义 */
</style>
