<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="not-found">
      <HandCard variant="note" class="not-found__card">
        <div class="not-found__content">
          <h1 class="not-found__code">404</h1>
          <h2 class="not-found__title">页面走丢了</h2>
          <p class="not-found__text">
            抱歉，你访问的页面不存在。<br>
            也许它去了某个美好的地方记录生活了~
          </p>
          <HandButton variant="primary" @click="goHome">
            返回首页
          </HandButton>
        </div>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import HandCard from '@/components/base/HandCard.vue'
import HandButton from '@/components/base/HandButton.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped lang="scss">
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;

  &__card {
    max-width: 400px;
    text-align: center;
  }

  &__content {
    padding: 20px;
  }

  &__code {
    font-family: var(--font-handwriting);
    font-size: 6rem;
    color: var(--color-soft-purple);
    margin-bottom: 0;
    line-height: 1;
    opacity: 0.8;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.8rem;
    color: var(--color-ink);
    margin: 16px 0;
  }

  &__text {
    font-family: var(--font-body);
    font-size: 1rem;
    color: var(--color-ink-light);
    line-height: 1.8;
    margin-bottom: 30px;
  }
}
</style>
