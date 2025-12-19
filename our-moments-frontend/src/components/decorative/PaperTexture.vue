<template>
  <div
    :class="['paper-texture', `paper-texture--${variant}`]"
    :style="{ backgroundColor: bgColor }"
    @mousemove="handleMouseMove"
  >
    <!-- Background Image Layer with Parallax -->
    <div
      class="paper-texture__bg-image"
      :style="{
        backgroundImage: bgImageUrl ? `url(${bgImageUrl})` : undefined,
        transform: `translate(${mouseX * -20}px, ${mouseY * -20}px) scale(1.1)`
      }"
    ></div>

    <div class="paper-texture__overlay"></div>
    <div class="paper-texture__content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useUiStore } from '@/store/ui'
// @ts-ignore
import defaultBg from '@/assets/images/backgroundstyle.jpg'

interface Props {
  variant?: 'light' | 'warm' | 'kraft' | 'watercolor'
  bgColor?: string
  bgImage?: string
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'light',
  bgColor: undefined,
  bgImage: undefined
})

const ui = useUiStore()

const mouseX = ref(0)
const mouseY = ref(0)

function handleMouseMove(event: MouseEvent) {
  // Calculate normalized mouse position (-0.5 to 0.5)
  const { clientWidth, clientHeight } = document.documentElement
  mouseX.value = (event.clientX / clientWidth) - 0.5
  mouseY.value = (event.clientY / clientHeight) - 0.5
}

const bgImageUrl = computed(() => {
  return props.bgImage || ui.backgroundImage || defaultBg
})

const bgColor = computed(() => {
  if (props.bgColor) return props.bgColor

  switch (props.variant) {
    case 'light':
      return 'rgba(249, 245, 240, 0.85)' // Increased transparency for image visibility
    case 'warm':
      return 'rgba(240, 234, 214, 0.85)'
    case 'kraft':
      return 'rgba(212, 196, 168, 0.9)'
    case 'watercolor':
      return 'rgba(250, 250, 250, 0.8)'
    default:
      return 'rgba(249, 245, 240, 0.85)'
  }
})
</script>

<style scoped lang="scss">
.paper-texture {
  position: relative;
  min-height: 100%;
  overflow: hidden; // Prevent background from spilling out
  transition: background-color 0.3s ease;

  &__bg-image {
    position: fixed; // Fixed to cover the whole screen
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-size: cover;
    background-position: center;
    z-index: 0;
    pointer-events: none;
    transition: transform 0.1s ease-out;
    opacity: 0.6; // Blend with the background color
    filter: blur(2px); // Slight blur for depth
  }

  &__overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    z-index: 1; // Above image
  }

  &__content {
    position: relative;
    z-index: 2; // Above overlay
  }

  // Ensure direct children are z-indexed correctly if not using the content wrapper
  > :deep(*:not(.paper-texture__overlay):not(.paper-texture__bg-image):not(.paper-texture__content)) {
    position: relative;
    z-index: 2;
  }

  &--light {
    .paper-texture__overlay {
      background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.8' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
      opacity: 0.02;
    }
  }

  &--warm {
    .paper-texture__overlay {
      background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.7' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
      opacity: 0.04;
    }
  }

  &--kraft {
    .paper-texture__overlay {
      background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.6' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E");
      opacity: 0.08;
    }
  }

  &--watercolor {
    .paper-texture__overlay {
      background-image:
        url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='turbulence' baseFrequency='0.02' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E"),
        url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise2'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise2)'/%3E%3C/svg%3E");
      opacity: 0.03;
    }
  }
}
</style>
