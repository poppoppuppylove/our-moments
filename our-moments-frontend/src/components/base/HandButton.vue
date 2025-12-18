<template>
  <button
    :class="[
      'hand-button',
      `hand-button--${variant}`,
      `hand-button--${size}`,
      { 'hand-button--disabled': disabled, 'hand-button--loading': loading }
    ]"
    :disabled="disabled || loading"
    @mouseenter="onMouseEnter"
    @mouseleave="onMouseLeave"
    @click="onClick"
  >
    <span v-if="loading" class="hand-button__loader"></span>
    <span class="hand-button__content">
      <slot></slot>
    </span>
  </button>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false
})

const emit = defineEmits<{
  click: [event: MouseEvent]
}>()

const isHovering = ref(false)

function onMouseEnter() {
  if (!props.disabled && !props.loading) {
    isHovering.value = true
  }
}

function onMouseLeave() {
  isHovering.value = false
}

function onClick(event: MouseEvent) {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style scoped lang="scss">
.hand-button {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: var(--font-body);
  font-weight: 500;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.3s ease;

  // 手绘风边框
  border-radius: 255px 15px 225px 15px / 15px 225px 15px 255px;

  &:focus {
    outline: none;
  }

  &:hover:not(.hand-button--disabled):not(.hand-button--loading) {
    animation: wiggle 0.4s ease-in-out;
  }

  &:active:not(.hand-button--disabled):not(.hand-button--loading) {
    transform: scale(0.98);
  }

  // 尺寸变体
  &--sm {
    padding: 6px 16px;
    font-size: 0.875rem;
  }

  &--md {
    padding: 10px 24px;
    font-size: 1rem;
  }

  &--lg {
    padding: 14px 32px;
    font-size: 1.125rem;
  }

  // 样式变体
  &--primary {
    background-color: var(--color-soft-purple);
    color: white;
    border-color: var(--color-soft-purple);
    box-shadow: 2px 3px 0 var(--color-mauve);

    &:hover:not(.hand-button--disabled) {
      background-color: var(--color-mauve);
      border-color: var(--color-mauve);
      box-shadow: 3px 4px 0 var(--color-dusty-pink);
    }
  }

  &--secondary {
    background-color: var(--color-peach);
    color: var(--color-ink);
    border-color: var(--color-peach);
    box-shadow: 2px 3px 0 var(--color-blush);

    &:hover:not(.hand-button--disabled) {
      background-color: var(--color-blush);
      border-color: var(--color-blush);
    }
  }

  &--outline {
    background-color: transparent;
    color: var(--color-soft-purple);
    border-color: var(--color-soft-purple);
    border-width: 2px;
    border-style: dashed;

    &:hover:not(.hand-button--disabled) {
      background-color: rgba(149, 125, 173, 0.1);
      border-style: solid;
    }
  }

  &--ghost {
    background-color: transparent;
    color: var(--color-ink);
    border-color: transparent;

    &:hover:not(.hand-button--disabled) {
      background-color: var(--color-paper-dark);
    }
  }

  // 禁用状态
  &--disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  // 加载状态
  &--loading {
    cursor: wait;

    .hand-button__content {
      opacity: 0.7;
    }
  }

  &__loader {
    position: absolute;
    width: 16px;
    height: 16px;
    border: 2px solid currentColor;
    border-top-color: transparent;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  &__content {
    display: inline-flex;
    align-items: center;
    gap: 8px;
  }
}

@keyframes wiggle {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(-2deg);
  }
  75% {
    transform: rotate(2deg);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
