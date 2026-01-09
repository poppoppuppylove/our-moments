<template>
  <div class="hand-select" :class="{ 'hand-select--open': isOpen, 'hand-select--disabled': disabled }">
    <div
      class="hand-select__trigger"
      @click="toggle"
      :tabindex="disabled ? -1 : 0"
      @keydown.enter="toggle"
      @keydown.space.prevent="toggle"
      @blur="onBlur"
    >
      <span class="hand-select__value">{{ displayValue }}</span>
      <span class="hand-select__arrow">▼</span>
    </div>
    <Transition name="hand-select">
      <div v-if="isOpen && !disabled" class="hand-select__dropdown">
        <div
          v-for="option in options"
          :key="option.value"
          class="hand-select__option"
          :class="{ 'hand-select__option--selected': modelValue === option.value }"
          @click="select(option)"
        >
          {{ option.label }}
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface SelectOption {
  value: string | number
  label: string
}

const props = defineProps<{
  modelValue: string | number
  options: SelectOption[]
  placeholder?: string
  disabled?: boolean
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void
}>()

const isOpen = ref(false)

const displayValue = computed(() => {
  const selected = props.options.find(option => option.value === props.modelValue)
  return selected ? selected.label : (props.placeholder || '请选择')
})

function toggle() {
  if (props.disabled) return
  isOpen.value = !isOpen.value
}

function select(option: SelectOption) {
  emit('update:modelValue', option.value)
  isOpen.value = false
}

function onBlur() {
  // 延迟关闭以允许选项点击
  setTimeout(() => {
    isOpen.value = false
  }, 150)
}

// 点击外部关闭
watch(isOpen, (newVal) => {
  if (newVal) {
    const handleClickOutside = (e: MouseEvent) => {
      const el = document.querySelector('.hand-select')
      if (el && !el.contains(e.target as Node)) {
        isOpen.value = false
      }
    }
    document.addEventListener('click', handleClickOutside)

    // 清理事件监听器
    setTimeout(() => {
      document.removeEventListener('click', handleClickOutside)
    }, 100)
  }
})
</script>

<style scoped lang="scss">
.hand-select {
  position: relative;
  font-family: var(--font-body);

  &__trigger {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 10px 14px;
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.5);
    cursor: pointer;
    transition: all 0.2s;
    min-height: 40px;

    &:hover:not(.hand-select__trigger--disabled) {
      border-color: var(--color-soft-purple);
      background: rgba(149, 125, 173, 0.1);
    }

    &:focus {
      outline: none;
      border-color: var(--color-soft-purple);
      box-shadow: 0 0 0 2px rgba(149, 125, 173, 0.2);
    }
  }

  &__value {
    flex: 1;
    text-align: left;
    color: var(--color-ink);
  }

  &__arrow {
    margin-left: 8px;
    color: var(--color-ink-light);
    transition: transform 0.2s;
  }

  &--open {
    .hand-select__arrow {
      transform: rotate(180deg);
    }

    .hand-select__trigger {
      border-color: var(--color-soft-purple);
    }
  }

  &--disabled {
    .hand-select__trigger {
      cursor: not-allowed;
      opacity: 0.6;
      background: var(--color-paper);
    }
  }

  &__dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    z-index: 100;
    margin-top: 4px;
    padding: 8px 0;
    background: white;
    border: 1px dashed var(--color-ink-light);
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    max-height: 200px;
    overflow-y: auto;
  }

  &__option {
    padding: 10px 14px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: var(--color-blush);
    }

    &--selected {
      background: var(--color-soft-purple);
      color: white;
    }
  }
}

// 下拉动画
.hand-select-enter-active,
.hand-select-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.hand-select-enter-from,
.hand-select-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>