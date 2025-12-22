<template>
  <Teleport to="body">
    <Transition name="toast">
      <div v-if="visible" class="toast-container">
        <div :class="['toast', `toast--${type}`]">
          <span class="toast__icon">{{ iconMap[type] }}</span>
          <span class="toast__message">{{ message }}</span>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

export type ToastType = 'success' | 'error' | 'warning' | 'info'

const props = withDefaults(defineProps<{
  message: string
  type?: ToastType
  duration?: number
  modelValue?: boolean
}>(), {
  type: 'info',
  duration: 3000,
  modelValue: false
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const visible = ref(props.modelValue)

const iconMap: Record<ToastType, string> = {
  success: '✓',
  error: '✕',
  warning: '!',
  info: 'i'
}

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.duration > 0) {
    setTimeout(() => {
      visible.value = false
      emit('update:modelValue', false)
    }, props.duration)
  }
})
</script>

<style scoped lang="scss">
.toast-container {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
}

.toast {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  font-family: var(--font-body);
  font-size: 0.95rem;
  color: var(--color-ink);
  border: 1px solid rgba(168, 140, 190, 0.2);

  &--success {
    border-left: 4px solid var(--color-mint);

    .toast__icon {
      background: var(--color-mint);
      color: white;
    }
  }

  &--error {
    border-left: 4px solid #e57373;

    .toast__icon {
      background: #e57373;
      color: white;
    }
  }

  &--warning {
    border-left: 4px solid #ffb74d;

    .toast__icon {
      background: #ffb74d;
      color: white;
    }
  }

  &--info {
    border-left: 4px solid var(--color-soft-purple);

    .toast__icon {
      background: var(--color-soft-purple);
      color: white;
    }
  }
}

.toast__icon {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: bold;
}

.toast__message {
  max-width: 300px;
}

// 动画
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-20px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
