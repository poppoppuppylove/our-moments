<template>
  <div
    :class="[
      'hand-card',
      `hand-card--${variant}`,
      { 'hand-card--hoverable': hoverable, 'hand-card--rotated': rotated }
    ]"
    :style="cardStyle"
    @mouseenter="onMouseEnter"
    @mouseleave="onMouseLeave"
  >
    <div v-if="$slots.header" class="hand-card__header">
      <slot name="header"></slot>
    </div>
    <div class="hand-card__body">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="hand-card__footer">
      <slot name="footer"></slot>
    </div>

    <!-- 可选的装饰元素 -->
    <div v-if="showTape" class="hand-card__tape" :class="`hand-card__tape--${tapePosition}`"></div>
    <div v-if="showPin" class="hand-card__pin"></div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { getRandomRotation } from '@/utils/style'

interface Props {
  variant?: 'default' | 'paper' | 'polaroid' | 'note'
  hoverable?: boolean
  rotated?: boolean
  rotation?: number
  showTape?: boolean
  showPin?: boolean
  tapePosition?: 'top' | 'top-left' | 'top-right'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  hoverable: false,
  rotated: false,
  rotation: undefined,
  showTape: false,
  showPin: false,
  tapePosition: 'top'
})

const isHovering = ref(false)

const randomRotation = props.rotation ?? getRandomRotation(-3, 3)

const cardStyle = computed(() => {
  if (props.rotated && !isHovering.value) {
    return {
      transform: `rotate(${randomRotation}deg)`
    }
  }
  return {}
})

function onMouseEnter() {
  isHovering.value = true
}

function onMouseLeave() {
  isHovering.value = false
}
</script>

<style scoped lang="scss">
.hand-card {
  position: relative;
  background-color: white;
  transition: all 0.3s ease;

  // 默认样式
  &--default {
    border-radius: 15px 255px 15px 225px / 255px 15px 225px 15px;
    box-shadow: var(--shadow-warm);
    padding: 20px;
    border: 1px solid rgba(74, 74, 74, 0.1);
  }

  // 纸张样式
  &--paper {
    background-color: var(--color-paper);
    border-radius: 2px;
    box-shadow:
      2px 2px 5px rgba(0, 0, 0, 0.05),
      inset 0 0 40px rgba(0, 0, 0, 0.03);
    padding: 24px;
    border: none;

    // 纸张纹理
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
      opacity: 0.03;
      pointer-events: none;
      border-radius: inherit;
    }
  }

  // 拍立得样式
  &--polaroid {
    background-color: white;
    padding: 12px 12px 48px 12px;
    box-shadow: var(--shadow-paper);
    border-radius: 2px;

    &::after {
      content: '';
      position: absolute;
      bottom: 12px;
      left: 12px;
      right: 12px;
      height: 24px;
      background: linear-gradient(to right, transparent, rgba(0,0,0,0.02), transparent);
    }
  }

  // 便签样式
  &--note {
    background: linear-gradient(to bottom, #ffffa5 0%, #fff9c4 100%);
    padding: 20px;
    box-shadow:
      2px 2px 8px rgba(0, 0, 0, 0.1),
      inset 0 -2px 4px rgba(0, 0, 0, 0.05);
    border-radius: 0 0 0 30px / 0 0 0 30px;

    // 便签线条
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(to right, #ff6b6b, #ff8e8e);
      border-radius: 2px 2px 0 0;
    }
  }

  // 可悬停效果
  &--hoverable {
    cursor: pointer;

    &:hover {
      transform: rotate(0deg) translateY(-4px) !important;
      box-shadow: var(--shadow-warm-lg);
    }
  }

  // 旋转效果
  &--rotated {
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }

  &__header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px dashed var(--color-ink-light);
  }

  &__body {
    position: relative;
  }

  &__footer {
    margin-top: 16px;
    padding-top: 12px;
    border-top: 1px dashed var(--color-ink-light);
  }

  // 胶带装饰
  &__tape {
    position: absolute;
    width: 80px;
    height: 24px;
    background: linear-gradient(
      135deg,
      rgba(255, 235, 205, 0.8) 0%,
      rgba(255, 228, 181, 0.6) 100%
    );
    z-index: 10;

    &--top {
      top: -12px;
      left: 50%;
      transform: translateX(-50%) rotate(-2deg);
    }

    &--top-left {
      top: -8px;
      left: -10px;
      transform: rotate(-45deg);
    }

    &--top-right {
      top: -8px;
      right: -10px;
      transform: rotate(45deg);
    }
  }

  // 图钉装饰
  &__pin {
    position: absolute;
    top: -8px;
    left: 50%;
    transform: translateX(-50%);
    width: 16px;
    height: 16px;
    background: radial-gradient(circle at 30% 30%, #ff6b6b, #cc5555);
    border-radius: 50%;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    z-index: 10;

    &::after {
      content: '';
      position: absolute;
      top: 3px;
      left: 3px;
      width: 4px;
      height: 4px;
      background: rgba(255, 255, 255, 0.5);
      border-radius: 50%;
    }
  }
}
</style>
