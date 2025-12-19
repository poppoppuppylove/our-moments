import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUiStore = defineStore('ui', () => {
  // 从 localStorage 初始化背景图片
  const backgroundImage = ref<string | null>(localStorage.getItem('backgroundImage') || null)

  /**
   * 设置背景图片
   * @param dataUrl 图片的 Data URL 或 null
   */
  function setBackgroundImage(dataUrl: string | null) {
    backgroundImage.value = dataUrl
    if (dataUrl) {
      try {
        localStorage.setItem('backgroundImage', dataUrl)
      } catch (e) {
        console.error('Failed to save background image to localStorage', e)
        // 如果存储失败（可能是因为图片太大），我们仍然在内存中更新它，但给予警告
        // 在实际应用中，这里应该上传到服务器
      }
    } else {
      localStorage.removeItem('backgroundImage')
    }
  }

  /**
   * 清除背景图片
   */
  function clearBackgroundImage() {
    setBackgroundImage(null)
  }

  return {
    backgroundImage,
    setBackgroundImage,
    clearBackgroundImage
  }
})
