<template>
  <div class="hand-share">
    <HandButton variant="outline" size="sm" @click="handleShare">
      <span class="icon">📤</span> 分享
    </HandButton>

    <div v-if="showToast" class="share-toast">
      链接已复制到剪贴板！
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import HandButton from '@/components/base/HandButton.vue'
import {toast} from "@/composables/useToast.ts";

const showToast = ref(false)

async function handleShare() {
  const url = window.location.href
  const title = document.title

  // 尝试使用原生分享 API
  if (navigator.share) {
    try {
      await navigator.share({
        title: title,
        text: '来看看这篇手账！',
        url: url
      })
      return
    } catch (err) {
      console.log('Share canceled or failed', err)
    }
  }

  // 降级方案：复制链接
  try {
    await navigator.clipboard.writeText(url)
    showToast.value = true
    setTimeout(() => {
      showToast.value = false
    }, 2000)
  } catch (err) {
    console.error('Failed to copy', err)
    toast.error('复制链接失败，请手动复制浏览器地址')
  }
}
</script>

<style scoped lang="scss">
.hand-share {
  position: relative;
  display: inline-block;
}

.icon {
  margin-right: 4px;
}

.share-toast {
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  background: var(--color-ink);
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 0.8rem;
  white-space: nowrap;
  pointer-events: none;
  margin-bottom: 8px;
  animation: fade-in-out 2s forwards;

  &::after {
    content: '';
    position: absolute;
    top: 100%;
    left: 50%;
    margin-left: -4px;
    border-width: 4px;
    border-style: solid;
    border-color: var(--color-ink) transparent transparent transparent;
  }
}

@keyframes fade-in-out {
  0% { opacity: 0; transform: translate(-50%, 10px); }
  10% { opacity: 1; transform: translate(-50%, 0); }
  80% { opacity: 1; transform: translate(-50%, 0); }
  100% { opacity: 0; transform: translate(-50%, -10px); }
}
</style>
