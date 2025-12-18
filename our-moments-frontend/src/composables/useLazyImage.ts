import { ref, onMounted, onUnmounted } from 'vue'
import type { Ref } from 'vue'

interface UseLazyImageOptions {
  threshold?: number
  rootMargin?: string
  placeholder?: string
}

interface UseLazyImageReturn {
  imageRef: Ref<HTMLImageElement | null>
  isLoaded: Ref<boolean>
  isLoading: Ref<boolean>
  error: Ref<boolean>
}

/**
 * 图片懒加载 Hook
 * @param src 图片源地址
 * @param options 配置选项
 */
export function useLazyImage(
  src: string,
  options: UseLazyImageOptions = {}
): UseLazyImageReturn {
  const { threshold = 0.1, rootMargin = '50px' } = options

  const imageRef = ref<HTMLImageElement | null>(null)
  const isLoaded = ref(false)
  const isLoading = ref(false)
  const error = ref(false)

  let observer: IntersectionObserver | null = null

  function loadImage() {
    if (!imageRef.value || isLoading.value || isLoaded.value) return

    isLoading.value = true

    const img = new Image()
    img.src = src

    img.onload = () => {
      if (imageRef.value) {
        imageRef.value.src = src
        isLoaded.value = true
        isLoading.value = false
      }
    }

    img.onerror = () => {
      error.value = true
      isLoading.value = false
    }
  }

  function handleIntersection(entries: IntersectionObserverEntry[]) {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        loadImage()
        observer?.unobserve(entry.target)
      }
    })
  }

  onMounted(() => {
    if (!imageRef.value) return

    if ('IntersectionObserver' in window) {
      observer = new IntersectionObserver(handleIntersection, {
        threshold,
        rootMargin
      })
      observer.observe(imageRef.value)
    } else {
      // 降级方案：直接加载
      loadImage()
    }
  })

  onUnmounted(() => {
    observer?.disconnect()
  })

  return {
    imageRef,
    isLoaded,
    isLoading,
    error
  }
}

/**
 * 懒加载指令
 * 使用: v-lazy="imageUrl"
 */
export const vLazy = {
  mounted(el: HTMLImageElement, binding: { value: string }) {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const img = entry.target as HTMLImageElement
            img.src = binding.value
            img.classList.add('lazy-loaded')
            observer.unobserve(img)
          }
        })
      },
      { threshold: 0.1, rootMargin: '50px' }
    )

    el.classList.add('lazy-image')
    observer.observe(el)
  }
}
