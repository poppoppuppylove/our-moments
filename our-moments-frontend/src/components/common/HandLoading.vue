<template>
  <div class="hand-loading" :class="{ 'hand-loading--fullscreen': fullscreen }">
    <div class="hand-loading__container">
      <!-- 手绘进度条 -->
      <div class="hand-loading__bar">
        <div class="hand-loading__progress"></div>
      </div>
      <p class="hand-loading__text">{{ text }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  text?: string
  fullscreen?: boolean
}

withDefaults(defineProps<Props>(), {
  text: '正在加载中...',
  fullscreen: false
})
</script>

<style scoped lang="scss">
.hand-loading {
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
    background: rgba(249, 245, 240, 0.9);
    z-index: 1000;
  }

  &__container {
    text-align: center;
  }

  &__bar {
    width: 200px;
    height: 12px;
    background: #f0ead6;
    border: 2px solid var(--color-ink-light);
    border-radius: 6px;
    overflow: hidden;
    position: relative;
    margin-bottom: 16px;

    // 手绘风格的不规则边框
    &::before {
      content: '';
      position: absolute;
      top: -1px;
      left: -1px;
      right: -1px;
      bottom: -1px;
      border: 2px solid var(--color-ink-light);
      border-radius: 8px;
      transform: rotate(-0.5deg);
      pointer-events: none;
    }
  }

  &__progress {
    height: 100%;
    background: linear-gradient(90deg, var(--color-soft-purple) 0%, var(--color-blush) 100%);
    border-radius: 4px;
    animation: loading-progress 1.5s ease-in-out infinite;
  }

  &__text {
    font-family: var(--font-handwriting);
    font-size: 1rem;
    color: var(--color-ink-light);
    animation: text-bounce 1s ease-in-out infinite;
  }
}

@keyframes loading-progress {
  0% {
    width: 0%;
    margin-left: 0%;
  }
  50% {
    width: 60%;
    margin-left: 20%;
  }
  100% {
    width: 0%;
    margin-left: 100%;
  }
}

@keyframes text-bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}
</style>
