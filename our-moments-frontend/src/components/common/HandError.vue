<template>
  <div class="hand-error" :class="{ 'hand-error--fullscreen': fullscreen }">
    <div class="hand-error__dialog">
      <!-- 手绘对话框 -->
      <div class="hand-error__icon">😅</div>
      <h3 class="hand-error__title">{{ title }}</h3>
      <p class="hand-error__message">{{ message }}</p>
      <HandButton v-if="showRetry" variant="primary" size="sm" @click="$emit('retry')">
        重试
      </HandButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import HandButton from '@/components/base/HandButton.vue'

interface Props {
  title?: string
  message?: string
  fullscreen?: boolean
  showRetry?: boolean
}

withDefaults(defineProps<Props>(), {
  title: '出错了',
  message: '抱歉，发生了一些问题。请稍后再试。',
  fullscreen: false,
  showRetry: true
})

defineEmits<{
  retry: []
}>()
</script>

<style scoped lang="scss">
.hand-error {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;

  &--fullscreen {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(249, 245, 240, 0.95);
    z-index: 1000;
  }

  &__dialog {
    text-align: center;
    background: white;
    padding: 32px 40px;
    border-radius: 4px;
    box-shadow: 3px 5px 15px rgba(0, 0, 0, 0.1);
    max-width: 400px;
    position: relative;

    // 手绘风格的不规则边框
    &::before {
      content: '';
      position: absolute;
      top: -2px;
      left: -2px;
      right: -2px;
      bottom: -2px;
      border: 2px dashed var(--color-ink-light);
      border-radius: 8px;
      transform: rotate(-0.5deg);
      pointer-events: none;
    }
  }

  &__icon {
    font-size: 3rem;
    margin-bottom: 16px;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.5rem;
    color: var(--color-ink);
    margin-bottom: 12px;
  }

  &__message {
    font-family: var(--font-body);
    font-size: 0.95rem;
    color: var(--color-ink-light);
    line-height: 1.6;
    margin-bottom: 20px;
  }
}
</style>
