<template>
  <div
      :class="['tape', `tape--${variant}`, `tape--${position}`]"
      :style="tapeStyle"
  ></div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { tapeColors } from '@/utils/style'

interface Props {
  variant?: 'yellow' | 'pink' | 'blue' | 'green' | 'purple'
  position?: 'top' | 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right'
  rotation?: number
  width?: string
  height?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'yellow',
  position: 'top',
  rotation: undefined,
  width: '80px',
  height: '24px'
})

const tapeStyle = computed(() => {
  const baseRotation = getBaseRotation()
  const finalRotation = props.rotation ?? baseRotation

  return {
    width: props.width,
    height: props.height,
    background: tapeColors[props.variant],
    transform: `rotate(${finalRotation}deg)`
  }
})

function getBaseRotation(): number {
  switch (props.position) {
    case 'top':
      return -2 + Math.random() * 4
    case 'top-left':
      return -45 + Math.random() * 10
    case 'top-right':
      return 45 + Math.random() * 10
    case 'bottom-left':
      return 45 + Math.random() * 10
    case 'bottom-right':
      return -45 + Math.random() * 10
    default:
      return 0
  }
}
</script>

<style scoped lang="scss">
.tape {
  position: absolute;
  z-index: 10;
  opacity: 0.85;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

  // 胶带纹理
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: repeating-linear-gradient(
            90deg,
            transparent,
            transparent 2px,
            rgba(255, 255, 255, 0.1) 2px,
            rgba(255, 255, 255, 0.1) 4px
    );
  }

  // 位置
  &--top {
    top: -12px;
    left: 50%;
    transform: translateX(-50%);
  }

  &--top-left {
    top: -8px;
    left: -10px;
  }

  &--top-right {
    // 右上角对角线粘贴，3/4在卡片内，1/4在卡片外（上下各一半）
    // 计算：距离右上角向左向上各偏移25px，使胶带中心在右上角
    top: 0;
    right: 0;
    transform-origin: top right;
    transform: translate(50%, -50%) rotate(45deg); // 先移动再旋转
  }

  &--bottom-left {
    bottom: -8px;
    left: -10px;
  }

  &--bottom-right {
    bottom: -8px;
    right: -10px;
  }
}
</style>
