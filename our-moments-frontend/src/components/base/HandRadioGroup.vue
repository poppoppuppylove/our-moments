<template>
  <div class="hand-radio-group">
    <label
      v-for="option in options"
      :key="option.value"
      class="hand-radio-option"
      :class="{ 'hand-radio-option--selected': modelValue === option.value, 'hand-radio-option--disabled': disabled }"
    >
      <input
        type="radio"
        :value="option.value"
        :checked="modelValue === option.value"
        :disabled="disabled"
        @change="emit('update:modelValue', option.value)"
        class="hand-radio-option__input"
      />
      <div class="hand-radio-option__content">
        <span class="hand-radio-option__marker"></span>
        <span class="hand-radio-option__label">{{ option.label }}</span>
      </div>
    </label>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'

interface RadioOption {
  value: string
  label: string
}

const props = defineProps<{
  modelValue: string
  options: RadioOption[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()
</script>

<style scoped lang="scss">
.hand-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  font-family: var(--font-body);
}

.hand-radio-option {
  position: relative;
  cursor: pointer;
  transition: all 0.2s;

  &__input {
    position: absolute;
    opacity: 0;
    pointer-events: none;
  }

  &__content {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    background: white;
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 8px;
    transition: all 0.2s;
  }

  &:hover:not(.hand-radio-option--disabled) {
    .hand-radio-option__content {
      border-color: var(--color-soft-purple);
      background: rgba(149, 125, 173, 0.1);
    }
  }

  &--selected {
    .hand-radio-option__content {
      border-color: var(--color-soft-purple);
      border-style: solid;
      background: rgba(149, 125, 173, 0.15);
    }

    .hand-radio-option__marker {
      background: var(--color-soft-purple);
      border-color: var(--color-soft-purple);

      &::after {
        opacity: 1;
      }
    }
  }

  &--disabled {
    cursor: not-allowed;
    opacity: 0.6;

    .hand-radio-option__content {
      background: var(--color-paper);
    }
  }

  &__marker {
    position: relative;
    width: 20px;
    height: 20px;
    border: 2px solid var(--color-ink-light);
    border-radius: 50%;
    background: white;
    transition: all 0.2s;
    flex-shrink: 0;

    &::after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 8px;
      height: 8px;
      background: white;
      border-radius: 50%;
      opacity: 0;
      transition: opacity 0.2s;
    }
  }

  &__label {
    color: var(--color-ink);
    font-size: 0.95rem;
  }
}
</style>