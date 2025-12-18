/**
 * 手绘风格工具函数
 */

/**
 * 生成随机旋转角度
 * @param min 最小角度
 * @param max 最大角度
 * @returns 随机角度值
 */
export function getRandomRotation(min: number = -3, max: number = 3): number {
  return Math.random() * (max - min) + min
}

/**
 * 生成随机偏移量
 * @param maxOffset 最大偏移值
 * @returns { x, y } 偏移对象
 */
export function getRandomOffset(maxOffset: number = 5): { x: number; y: number } {
  return {
    x: (Math.random() - 0.5) * 2 * maxOffset,
    y: (Math.random() - 0.5) * 2 * maxOffset
  }
}

/**
 * 生成手绘风边框半径
 * 模拟手绘的不规则圆角
 */
export function getHandDrawnBorderRadius(): string {
  const values = [
    Math.floor(Math.random() * 50 + 200),
    Math.floor(Math.random() * 20 + 10),
    Math.floor(Math.random() * 50 + 200),
    Math.floor(Math.random() * 20 + 10),
    Math.floor(Math.random() * 20 + 10),
    Math.floor(Math.random() * 50 + 200),
    Math.floor(Math.random() * 20 + 10),
    Math.floor(Math.random() * 50 + 200)
  ]
  return `${values[0]}px ${values[1]}px ${values[2]}px ${values[3]}px / ${values[4]}px ${values[5]}px ${values[6]}px ${values[7]}px`
}

/**
 * 从预设的颜色中随机选择
 */
export function getRandomAccentColor(): string {
  const colors: string[] = [
    '#E0BBE4', // dusty-pink
    '#957DAD', // soft-purple
    '#D291BC', // mauve
    '#FEC8D8', // peach
    '#FFDFD3', // blush
    '#B5EAD7'  // mint
  ]
  return colors[Math.floor(Math.random() * colors.length)]!
}

/**
 * 生成随机的暖色阴影
 */
export function getWarmShadow(intensity: 'sm' | 'md' | 'lg' = 'md'): string {
  const color = getRandomAccentColor()
  const alpha = intensity === 'sm' ? 0.15 : intensity === 'md' ? 0.2 : 0.25
  const blur = intensity === 'sm' ? 5 : intensity === 'md' ? 10 : 20
  const spread = intensity === 'sm' ? 1 : intensity === 'md' ? 2 : 4

  return `${spread}px ${spread * 2}px ${blur}px ${hexToRgba(color, alpha)}`
}

/**
 * 将十六进制颜色转换为 rgba
 */
export function hexToRgba(hex: string, alpha: number = 1): string {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  if (!result) return `rgba(0, 0, 0, ${alpha})`

  const r = parseInt(result[1]!, 16)
  const g = parseInt(result[2]!, 16)
  const b = parseInt(result[3]!, 16)

  return `rgba(${r}, ${g}, ${b}, ${alpha})`
}

/**
 * 生成纸张纹理 SVG 数据 URL
 */
export function getPaperTextureSvg(opacity: number = 0.03): string {
  return `url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)' opacity='${opacity}'/%3E%3C/svg%3E")`
}

/**
 * 胶带颜色预设
 */
export const tapeColors: Record<string, string> = {
  yellow: 'linear-gradient(135deg, rgba(255, 235, 205, 0.8) 0%, rgba(255, 228, 181, 0.6) 100%)',
  pink: 'linear-gradient(135deg, rgba(255, 200, 216, 0.7) 0%, rgba(255, 182, 193, 0.5) 100%)',
  blue: 'linear-gradient(135deg, rgba(173, 216, 230, 0.7) 0%, rgba(135, 206, 235, 0.5) 100%)',
  green: 'linear-gradient(135deg, rgba(181, 234, 215, 0.7) 0%, rgba(152, 251, 152, 0.5) 100%)',
  purple: 'linear-gradient(135deg, rgba(224, 187, 228, 0.7) 0%, rgba(200, 162, 200, 0.5) 100%)'
}

/**
 * 生成手绘线条路径
 * 用于 SVG 边框
 */
export function generateHandDrawnPath(
  width: number,
  height: number,
  roughness: number = 2
): string {
  const points: Array<{ x: number; y: number }> = []
  const segments = 20

  // 顶边
  for (let i = 0; i <= segments; i++) {
    points.push({
      x: (width / segments) * i + (Math.random() - 0.5) * roughness,
      y: (Math.random() - 0.5) * roughness
    })
  }

  // 右边
  for (let i = 1; i <= segments; i++) {
    points.push({
      x: width + (Math.random() - 0.5) * roughness,
      y: (height / segments) * i + (Math.random() - 0.5) * roughness
    })
  }

  // 底边
  for (let i = segments - 1; i >= 0; i--) {
    points.push({
      x: (width / segments) * i + (Math.random() - 0.5) * roughness,
      y: height + (Math.random() - 0.5) * roughness
    })
  }

  // 左边
  for (let i = segments - 1; i >= 1; i--) {
    points.push({
      x: (Math.random() - 0.5) * roughness,
      y: (height / segments) * i + (Math.random() - 0.5) * roughness
    })
  }

  // 生成路径
  const firstPoint = points[0]!
  let path = `M ${firstPoint.x} ${firstPoint.y}`
  for (let i = 1; i < points.length; i++) {
    const point = points[i]!
    path += ` L ${point.x} ${point.y}`
  }
  path += ' Z'

  return path
}

/**
 * 预设的旋转角度类名
 */
export const rotationClasses: string[] = [
  'rotate-slight-left',
  'rotate-slight-right',
  'rotate-random-1',
  'rotate-random-2',
  'rotate-random-3'
]

/**
 * 获取随机旋转类名
 */
export function getRandomRotationClass(): string {
  return rotationClasses[Math.floor(Math.random() * rotationClasses.length)]!
}
