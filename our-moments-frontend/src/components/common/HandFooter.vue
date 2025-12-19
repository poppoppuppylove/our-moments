<template>
  <footer class="hand-footer">
    <div class="hand-footer__content">
      <!-- 手绘分隔线 -->
      <div class="hand-footer__divider">
        <span class="divider-line"></span>
        <span class="divider-icon">✿</span>
        <span class="divider-line"></span>
      </div>

      <!-- 版权信息 -->
      <p class="hand-footer__copyright">
        © {{ currentYear }} Our Moments · 用心记录每一刻
      </p>

      <!-- 装饰性文字 -->
      <p class="hand-footer__quote">
        "生活不是等待风暴过去，而是学会在雨中起舞"
      </p>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style scoped lang="scss">
.hand-footer {
  margin-top: 60px;
  padding: 40px 20px 30px;

  &__content {
    max-width: 800px;
    margin: 0 auto;
    text-align: center;
  }

  &__divider {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-bottom: 24px;

    .divider-line {
      width: 80px;
      height: 1px;
      background: linear-gradient(90deg, transparent, var(--color-ink-light), transparent);
    }

    .divider-icon {
      font-size: 1.2rem;
      color: var(--color-soft-purple);
      animation: spin-slow 10s linear infinite;
    }
  }

  &__copyright {
    font-family: var(--font-handwriting);
    font-size: 0.9rem;
    color: var(--color-ink-light);
    margin-bottom: 12px;
  }

  &__quote {
    font-family: var(--font-body);
    font-size: 0.85rem;
    color: var(--color-ink-light);
    font-style: italic;
    opacity: 0.7;
  }
}

@keyframes spin-slow {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
