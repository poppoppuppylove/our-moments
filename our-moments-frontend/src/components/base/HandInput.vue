<template>
  <div
    :class="[
      'hand-input',
      `hand-input--${size}`,
      { 'hand-input--focused': isFocused, 'hand-input--error': error, 'hand-input--disabled': disabled }
    ]"
  >
    <label v-if="label" :for="inputId" class="hand-input__label">
      {{ label }}
      <span v-if="required" class="hand-input__required">*</span>
    </label>

    <div class="hand-input__wrapper">
      <span v-if="$slots.prefix" class="hand-input__prefix">
        <slot name="prefix"></slot>
      </span>

      <input
        v-if="type !== 'textarea'"
        :id="inputId"
        ref="inputRef"
        :type="type"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        class="hand-input__field"
        @input="onInput"
        @focus="onFocus"
        @blur="onBlur"
      />

      <textarea
        v-else
        :id="inputId"
        ref="inputRef"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        :rows="rows"
        class="hand-input__field hand-input__textarea"
        @input="onInput"
        @focus="onFocus"
        @blur="onBlur"
      ></textarea>

      <span v-if="$slots.suffix" class="hand-input__suffix">
        <slot name="suffix"></slot>
      </span>

      <span v-if="clearable && modelValue" class="hand-input__clear" @click="onClear">
        ×
      </span>
    </div>

    <div v-if="error || hint" class="hand-input__message">
      <span v-if="error" class="hand-input__error">{{ error }}</span>
      <span v-else-if="hint" class="hand-input__hint">{{ hint }}</span>
    </div>

    <div v-if="maxlength && showCount" class="hand-input__count">
      {{ modelValue?.length || 0 }} / {{ maxlength }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Props {
  modelValue?: string
  type?: 'text' | 'password' | 'email' | 'number' | 'textarea'
  placeholder?: string
  label?: string
  hint?: string
  error?: string
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  readonly?: boolean
  required?: boolean
  clearable?: boolean
  maxlength?: number
  showCount?: boolean
  rows?: number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  type: 'text',
  placeholder: '',
  label: '',
  hint: '',
  error: '',
  size: 'md',
  disabled: false,
  readonly: false,
  required: false,
  clearable: false,
  maxlength: undefined,
  showCount: false,
  rows: 4
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
  focus: [event: FocusEvent]
  blur: [event: FocusEvent]
  clear: []
}>()

const inputRef = ref<HTMLInputElement | HTMLTextAreaElement | null>(null)
const isFocused = ref(false)

const inputId = computed(() => `hand-input-${Math.random().toString(36).slice(2, 9)}`)

function onInput(event: Event) {
  const target = event.target as HTMLInputElement | HTMLTextAreaElement
  emit('update:modelValue', target.value)
}

function onFocus(event: FocusEvent) {
  isFocused.value = true
  emit('focus', event)
}

function onBlur(event: FocusEvent) {
  isFocused.value = false
  emit('blur', event)
}

function onClear() {
  emit('update:modelValue', '')
  emit('clear')
  inputRef.value?.focus()
}

function focus() {
  inputRef.value?.focus()
}

function blur() {
  inputRef.value?.blur()
}

defineExpose({ focus, blur })
</script>

<style scoped lang="scss">
.hand-input {
  display: flex;
  flex-direction: column;
  gap: 6px;

  &__label {
    font-family: var(--font-body);
    font-size: 0.9rem;
    color: var(--color-ink);
    font-weight: 500;
  }

  &__required {
    color: var(--color-mauve);
    margin-left: 2px;
  }

  &__wrapper {
    position: relative;
    display: flex;
    align-items: center;
    background-color: white;
    border: 2px solid var(--color-ink-light);
    border-radius: 255px 15px 225px 15px / 15px 225px 15px 255px;
    transition: all 0.3s ease;
    overflow: hidden;

    .hand-input--focused & {
      border-color: var(--color-soft-purple);
      box-shadow: 0 0 0 3px rgba(149, 125, 173, 0.15);
    }

    .hand-input--error & {
      border-color: #e57373;
      box-shadow: 0 0 0 3px rgba(229, 115, 115, 0.15);
    }

    .hand-input--disabled & {
      background-color: var(--color-paper-dark);
      cursor: not-allowed;
    }
  }

  &__field {
    flex: 1;
    border: none;
    outline: none;
    background: transparent;
    font-family: var(--font-body);
    color: var(--color-ink);

    &::placeholder {
      color: var(--color-ink-light);
      font-style: italic;
    }

    &:disabled {
      cursor: not-allowed;
      color: var(--color-ink-light);
    }
  }

  &__textarea {
    resize: vertical;
    min-height: 80px;
    line-height: 1.6;
  }

  &__prefix,
  &__suffix {
    display: flex;
    align-items: center;
    color: var(--color-ink-light);
  }

  &__clear {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 20px;
    height: 20px;
    margin-right: 8px;
    color: var(--color-ink-light);
    cursor: pointer;
    border-radius: 50%;
    transition: all 0.2s ease;

    &:hover {
      background-color: var(--color-paper-dark);
      color: var(--color-ink);
    }
  }

  &__message {
    font-size: 0.8rem;
  }

  &__error {
    color: #e57373;
  }

  &__hint {
    color: var(--color-ink-light);
  }

  &__count {
    font-size: 0.75rem;
    color: var(--color-ink-light);
    text-align: right;
  }

  // 尺寸变体
  &--sm {
    .hand-input__field {
      padding: 8px 12px;
      font-size: 0.875rem;
    }
    .hand-input__prefix { padding-left: 10px; }
    .hand-input__suffix { padding-right: 10px; }
  }

  &--md {
    .hand-input__field {
      padding: 12px 16px;
      font-size: 1rem;
    }
    .hand-input__prefix { padding-left: 14px; }
    .hand-input__suffix { padding-right: 14px; }
  }

  &--lg {
    .hand-input__field {
      padding: 16px 20px;
      font-size: 1.125rem;
    }
    .hand-input__prefix { padding-left: 18px; }
    .hand-input__suffix { padding-right: 18px; }
  }
}
</style>
