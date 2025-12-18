import { ref, onMounted, onUnmounted, computed } from 'vue'

// 断点配置
const breakpoints = {
  sm: 640,
  md: 768,
  lg: 1024,
  xl: 1280,
  '2xl': 1536
}

type Breakpoint = keyof typeof breakpoints

/**
 * 响应式工具 Hook
 */
export function useResponsive() {
  const windowWidth = ref(typeof window !== 'undefined' ? window.innerWidth : 0)
  const windowHeight = ref(typeof window !== 'undefined' ? window.innerHeight : 0)

  function updateSize() {
    windowWidth.value = window.innerWidth
    windowHeight.value = window.innerHeight
  }

  onMounted(() => {
    window.addEventListener('resize', updateSize)
    updateSize()
  })

  onUnmounted(() => {
    window.removeEventListener('resize', updateSize)
  })

  // 断点判断
  const isMobile = computed(() => windowWidth.value < breakpoints.md)
  const isTablet = computed(() => windowWidth.value >= breakpoints.md && windowWidth.value < breakpoints.lg)
  const isDesktop = computed(() => windowWidth.value >= breakpoints.lg)

  // 当前断点
  const currentBreakpoint = computed<Breakpoint | 'xs'>(() => {
    if (windowWidth.value >= breakpoints['2xl']) return '2xl'
    if (windowWidth.value >= breakpoints.xl) return 'xl'
    if (windowWidth.value >= breakpoints.lg) return 'lg'
    if (windowWidth.value >= breakpoints.md) return 'md'
    if (windowWidth.value >= breakpoints.sm) return 'sm'
    return 'xs'
  })

  // 断点比较函数
  function isAbove(breakpoint: Breakpoint): boolean {
    return windowWidth.value >= breakpoints[breakpoint]
  }

  function isBelow(breakpoint: Breakpoint): boolean {
    return windowWidth.value < breakpoints[breakpoint]
  }

  function isBetween(minBreakpoint: Breakpoint, maxBreakpoint: Breakpoint): boolean {
    return windowWidth.value >= breakpoints[minBreakpoint] && windowWidth.value < breakpoints[maxBreakpoint]
  }

  return {
    windowWidth,
    windowHeight,
    isMobile,
    isTablet,
    isDesktop,
    currentBreakpoint,
    isAbove,
    isBelow,
    isBetween,
    breakpoints
  }
}

/**
 * 媒体查询 Hook
 * @param query CSS 媒体查询字符串
 */
export function useMediaQuery(query: string) {
  const matches = ref(false)

  let mediaQuery: MediaQueryList | null = null

  function handleChange(e: MediaQueryListEvent | MediaQueryList) {
    matches.value = e.matches
  }

  onMounted(() => {
    mediaQuery = window.matchMedia(query)
    handleChange(mediaQuery)

    if (mediaQuery.addEventListener) {
      mediaQuery.addEventListener('change', handleChange)
    } else {
      // 兼容旧浏览器
      mediaQuery.addListener(handleChange)
    }
  })

  onUnmounted(() => {
    if (mediaQuery) {
      if (mediaQuery.removeEventListener) {
        mediaQuery.removeEventListener('change', handleChange)
      } else {
        mediaQuery.removeListener(handleChange)
      }
    }
  })

  return matches
}

/**
 * 预设的常用媒体查询
 */
export function usePreferredColorScheme() {
  return useMediaQuery('(prefers-color-scheme: dark)')
}

export function usePreferredReducedMotion() {
  return useMediaQuery('(prefers-reduced-motion: reduce)')
}
