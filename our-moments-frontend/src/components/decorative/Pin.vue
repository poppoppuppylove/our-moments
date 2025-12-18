<template>
  <div
    :class="['pin', `pin--${variant}`, `pin--${position}`]"
    :style="pinStyle"
  >
    <div class="pin__head"></div>
    <div class="pin__shadow"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'red' | 'blue' | 'green' | 'yellow' | 'purple'
  position?: 'top' | 'top-left' | 'top-right'
  size?: 'sm' | 'md' | 'lg'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'red',
  position: 'top',
  size: 'md'
})

const colors = {
  red: { main: '#ff6b6b', dark: '#cc5555' },
  blue: { main: '#74b9ff', dark: '#5a9fd4' },
  green: { main: '#55efc4', dark: '#44c9a0' },
  yellow: { main: '#ffeaa7', dark: '#dbc782' },
  purple: { main: '#a29bfe', dark: '#8a82e0' }
}

const sizes = {
  sm: { size: 12, shadow: 6 },
  md: { size: 16, shadow: 8 },
  lg: { size: 20, shadow: 10 }
}

const pinStyle = computed(() => {
  const color = colors[props.variant]
  const sizeConfig = sizes[props.size]

  return {
    '--pin-color': color.main,
    '--pin-dark': color.dark,
    '--pin-size': `${sizeConfig.size}px`,
    '--shadow-size': `${sizeConfig.shadow}px`
  }
})
</script>

<style scoped lang="scss">
.pin {
  position: absolute;
  z-index: 10;

  &__head {
    width: var(--pin-size);
    height: var(--pin-size);
    background: radial-gradient(circle at 30% 30%, var(--pin-color), var(--pin-dark));
    border-radius: 50%;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    position: relative;

    // 高光
    &::after {
      content: '';
      position: absolute;
      top: 20%;
      left: 20%;
      width: 25%;
      height: 25%;
      background: rgba(255, 255, 255, 0.5);
      border-radius: 50%;
    }
  }

  &__shadow {
    position: absolute;
    bottom: -2px;
    left: 50%;
    transform: translateX(-50%);
    width: var(--shadow-size);
    height: 3px;
    background: rgba(0, 0, 0, 0.15);
    border-radius: 50%;
    filter: blur(1px);
  }

  // 位置
  &--top {
    top: -8px;
    left: 50%;
    transform: translateX(-50%);
  }

  &--top-left {
    top: -6px;
    left: 10px;
  }

  &--top-right {
    top: -6px;
    right: 10px;
  }
}
</style>
