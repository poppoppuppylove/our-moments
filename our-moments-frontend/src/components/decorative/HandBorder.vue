<template>
  <div class="hand-border" :style="containerStyle">
    <svg class="hand-border__svg" :viewBox="`0 0 ${width} ${height}`" preserveAspectRatio="none">
      <path
        :d="borderPath"
        :stroke="color"
        :stroke-width="strokeWidth"
        fill="none"
        stroke-linecap="round"
        stroke-linejoin="round"
      />
    </svg>
    <div class="hand-border__content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import { generateHandDrawnPath } from '@/utils/style'

interface Props {
  color?: string
  strokeWidth?: number
  roughness?: number
  padding?: string
}

const props = withDefaults(defineProps<Props>(), {
  color: '#4A4A4A',
  strokeWidth: 2,
  roughness: 2,
  padding: '16px'
})

const width = ref(200)
const height = ref(100)

const containerStyle = computed(() => ({
  padding: props.padding
}))

const borderPath = computed(() => {
  return generateHandDrawnPath(width.value, height.value, props.roughness)
})

onMounted(() => {
  // 可以在这里添加 ResizeObserver 来动态更新尺寸
})
</script>

<style scoped lang="scss">
.hand-border {
  position: relative;
  display: inline-block;

  &__svg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
  }

  &__content {
    position: relative;
    z-index: 1;
  }
}
</style>
