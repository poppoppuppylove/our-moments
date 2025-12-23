import { ref, onMounted, onUnmounted, type Ref } from 'vue'

interface TouchGesturesOptions {
  onPinch?: (scale: number) => void
  onLongPress?: (x: number, y: number) => void
  onDrag?: (deltaX: number, deltaY: number) => void
  onDragEnd?: () => void
  longPressDelay?: number
  minPinchDistance?: number
}

interface TouchGesturesReturn {
  scale: Ref<number>
  isDragging: Ref<boolean>
  isLongPressing: Ref<boolean>
}

export function useTouchGestures(
  elementRef: Ref<HTMLElement | null>,
  options: TouchGesturesOptions = {}
): TouchGesturesReturn {
  const {
    onPinch,
    onLongPress,
    onDrag,
    onDragEnd,
    longPressDelay = 500,
    minPinchDistance = 10
  } = options

  const scale = ref(1)
  const isDragging = ref(false)
  const isLongPressing = ref(false)

  let initialDistance = 0
  let initialScale = 1
  let longPressTimer: ReturnType<typeof setTimeout> | null = null
  let touchStartX = 0
  let touchStartY = 0
  let lastTouchX = 0
  let lastTouchY = 0

  function getDistance(touch1: Touch, touch2: Touch): number {
    const dx = touch1.clientX - touch2.clientX
    const dy = touch1.clientY - touch2.clientY
    return Math.sqrt(dx * dx + dy * dy)
  }

  function handleTouchStart(e: TouchEvent) {
    if (e.touches.length === 2) {
      // 双指触摸 - 准备缩放
      initialDistance = getDistance(e.touches[0]!, e.touches[1]!)
      initialScale = scale.value
      cancelLongPress()
    } else if (e.touches.length === 1) {
      // 单指触摸 - 准备长按或拖拽
      const touch = e.touches[0]!
      touchStartX = touch.clientX
      touchStartY = touch.clientY
      lastTouchX = touch.clientX
      lastTouchY = touch.clientY

      // 开始长按计时
      longPressTimer = setTimeout(() => {
        isLongPressing.value = true
        isDragging.value = true
        onLongPress?.(touchStartX, touchStartY)
      }, longPressDelay)
    }
  }

  function handleTouchMove(e: TouchEvent) {
    if (e.touches.length === 2 && initialDistance > 0) {
      // 双指缩放
      const currentDistance = getDistance(e.touches[0]!, e.touches[1]!)
      const distanceChange = currentDistance - initialDistance

      if (Math.abs(distanceChange) > minPinchDistance) {
        const newScale = initialScale * (currentDistance / initialDistance)
        const clampedScale = Math.max(0.5, Math.min(3, newScale))
        scale.value = clampedScale
        onPinch?.(clampedScale)
      }
    } else if (e.touches.length === 1) {
      const touch = e.touches[0]!
      const deltaX = touch.clientX - lastTouchX
      const deltaY = touch.clientY - lastTouchY

      // 如果移动超过阈值，取消长按
      const totalMoveX = Math.abs(touch.clientX - touchStartX)
      const totalMoveY = Math.abs(touch.clientY - touchStartY)

      if (totalMoveX > 10 || totalMoveY > 10) {
        cancelLongPress()

        if (isDragging.value) {
          onDrag?.(deltaX, deltaY)
        }
      }

      lastTouchX = touch.clientX
      lastTouchY = touch.clientY
    }
  }

  function handleTouchEnd() {
    cancelLongPress()
    initialDistance = 0

    if (isDragging.value) {
      isDragging.value = false
      isLongPressing.value = false
      onDragEnd?.()
    }
  }

  function cancelLongPress() {
    if (longPressTimer) {
      clearTimeout(longPressTimer)
      longPressTimer = null
    }
  }

  onMounted(() => {
    const element = elementRef.value
    if (!element) return

    element.addEventListener('touchstart', handleTouchStart, { passive: true })
    element.addEventListener('touchmove', handleTouchMove, { passive: true })
    element.addEventListener('touchend', handleTouchEnd, { passive: true })
    element.addEventListener('touchcancel', handleTouchEnd, { passive: true })
  })

  onUnmounted(() => {
    const element = elementRef.value
    if (!element) return

    element.removeEventListener('touchstart', handleTouchStart)
    element.removeEventListener('touchmove', handleTouchMove)
    element.removeEventListener('touchend', handleTouchEnd)
    element.removeEventListener('touchcancel', handleTouchEnd)
    cancelLongPress()
  })

  return {
    scale,
    isDragging,
    isLongPressing
  }
}

// 检测是否为移动设备
export function isTouchDevice(): boolean {
  return 'ontouchstart' in window || navigator.maxTouchPoints > 0
}

// 检测是否为移动端
export function isMobileDevice(): boolean {
  return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}
