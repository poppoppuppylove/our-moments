<template>
  <div class="hand-textarea">
    <textarea
      :value="modelValue"
      @input="handleInput"
      :placeholder="placeholder"
      :disabled="disabled"
      :rows="rows"
      class="hand-textarea__field"
      :class="{ 'hand-textarea__field--disabled': disabled }"
    ></textarea>
    <div v-if="showCount" class="hand-textarea__count">
      {{ modelValue?.length || 0 }}{{ maxLength ? `/${maxLength}` : '' }}
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  modelValue?: string
  placeholder?: string
  disabled?: boolean
  rows?: number
  maxLength?: number
  showCount?: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
}>()

const handleInput = (event: Event) => {
  const target = event.target as HTMLTextAreaElement
  let value = target.value

  // 如果设置了最大长度，截取字符串
  if (props.maxLength && value.length > props.maxLength) {
    value = value.substring(0, props.maxLength)
    target.value = value
  }

  emit('update:modelValue', value)
}
</script>

<style scoped lang="scss">
.hand-textarea {
  position: relative;
  font-family: var(--font-body);

  &__field {
    width: 100%;
    padding: 12px 14px;
    border: 1px dashed rgba(168, 140, 190, 0.4);
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.5);
    font-family: inherit;
    font-size: 0.95rem;
    line-height: 1.6;
    color: var(--color-ink);
    resize: vertical;
    min-height: 100px;
    transition: all 0.2s;

    &::placeholder {
      color: var(--color-ink-light);
    }

    &:focus {
      outline: none;
      border-color: var(--color-soft-purple);
      box-shadow: 0 0 0 2px rgba(149, 125, 173, 0.2);
    }

    &--disabled {
      cursor: not-allowed;
      opacity: 0.6;
      background: var(--color-paper);
    }
  }

  &__count {
    position: absolute;
    bottom: 8px;
    right: 12px;
    font-size: 0.8rem;
    color: var(--color-ink-light);
    background: rgba(255, 255, 255, 0.8);
    padding: 2px 6px;
    border-radius: 4px;
  }
}
</style>