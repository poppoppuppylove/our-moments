# Our Moments 前端风格指南

本文档定义了 Our Moments 项目的视觉风格规范和组件使用指南，确保开发过程中保持一致的手绘风格。

## 1. 设计原则

### 核心风格关键词
- **手绘风 (Hand-drawn)**: 所有边框、分割线、图标都应带有手绘的轻微不规则感
- **拼贴艺术 (Collage)**: 图片和便签的展示方式模拟实物粘贴效果
- **纸质质感 (Paper Texture)**: 背景使用轻微的纸张纹理，增加温暖感
- **低饱和度**: 使用柔和、自然的配色方案

## 2. 配色方案

### 2.1 CSS 变量

```scss
:root {
  // 背景色
  --color-paper: #F9F5F0;      // 米白 - 主背景
  --color-paper-dark: #F0EAD6; // 蛋壳色 - 次级背景

  // 文字与线条
  --color-ink: #4A4A4A;        // 炭灰 - 主文字/线条
  --color-ink-light: #8C8C8C;  // 中灰 - 辅助文字

  // 点缀色
  --color-dusty-pink: #E0BBE4;
  --color-soft-purple: #957DAD;
  --color-mauve: #D291BC;
  --color-peach: #FEC8D8;
  --color-blush: #FFDFD3;
  --color-mint: #B5EAD7;
}
```

### 2.2 Tailwind 颜色类

| 颜色名称 | 类名 | 用途 |
|---------|------|------|
| 米白 | `bg-paper` | 主背景 |
| 蛋壳色 | `bg-paper-dark` | 次级背景、卡片 |
| 炭灰 | `text-ink` | 主文字 |
| 中灰 | `text-ink-light` | 辅助文字 |
| 淡粉紫 | `text-dusty-pink` | 装饰 |
| 柔和紫 | `text-soft-purple` | 强调、链接 |
| 藕合色 | `text-mauve` | 按钮、交互 |
| 桃红 | `bg-peach` | 标签、徽章 |
| 淡粉 | `bg-blush` | 高亮、提示 |
| 薄荷绿 | `text-mint` | 少量点缀 |

## 3. 字体规范

### 3.1 字体家族

```scss
// 标题字体 - 手写风格
--font-handwriting: 'Ma Shan Zheng', 'ZCOOL XiaoWei', cursive;

// 正文字体 - 衬线体
--font-serif: 'Noto Serif SC', 'Source Han Serif SC', serif;

// 正文阅读字体
--font-body: 'LXGW WenKai', 'Noto Serif SC', serif;
```

### 3.2 使用规则

| 场景 | 字体 | 大小 |
|------|------|------|
| 页面大标题 | `font-handwriting` | 2.5rem - 3.5rem |
| 卡片标题 | `font-handwriting` | 1.5rem - 2rem |
| 正文内容 | `font-body` | 1rem - 1.1rem |
| 辅助信息 | `font-body` | 0.85rem - 0.9rem |

## 4. 手绘风组件

### 4.1 HandButton 手绘按钮

带有悬停抖动效果的手绘风格按钮。

```vue
<HandButton variant="primary" size="md" @click="handleClick">
  点击我
</HandButton>
```

**属性:**
- `variant`: `'primary'` | `'secondary'` | `'outline'` | `'ghost'`
- `size`: `'sm'` | `'md'` | `'lg'`
- `disabled`: boolean
- `loading`: boolean

**特性:**
- 不规则圆角边框
- 悬停时轻微抖动动画
- 带颜色的阴影效果

### 4.2 HandCard 手绘卡片

支持多种样式的手绘风格卡片。

```vue
<HandCard variant="default" hoverable rotated>
  <template #header>标题</template>
  内容区域
  <template #footer>底部</template>
</HandCard>
```

**属性:**
- `variant`: `'default'` | `'paper'` | `'polaroid'` | `'note'`
- `hoverable`: boolean - 是否有悬停效果
- `rotated`: boolean - 是否随机旋转
- `showTape`: boolean - 显示胶带装饰
- `showPin`: boolean - 显示图钉装饰

**变体说明:**
- `default`: 标准手绘边框卡片
- `paper`: 带纸张纹理的卡片
- `polaroid`: 拍立得照片效果
- `note`: 便签纸效果（黄色背景）

### 4.3 HandInput 手绘输入框

手绘风格的表单输入组件。

```vue
<HandInput
  v-model="value"
  label="用户名"
  placeholder="请输入..."
  :error="errorMessage"
/>
```

**属性:**
- `type`: `'text'` | `'password'` | `'email'` | `'textarea'`
- `size`: `'sm'` | `'md'` | `'lg'`
- `label`: string
- `hint`: string
- `error`: string
- `clearable`: boolean

## 5. 装饰组件

### 5.1 PaperTexture 纸张纹理背景

```vue
<PaperTexture variant="light">
  内容
</PaperTexture>
```

**变体:** `'light'` | `'warm'` | `'kraft'` | `'watercolor'`

### 5.2 Tape 胶带

```vue
<Tape variant="yellow" position="top-right" />
```

**颜色:** `'yellow'` | `'pink'` | `'blue'` | `'green'` | `'purple'`

**位置:** `'top'` | `'top-left'` | `'top-right'` | `'bottom-left'` | `'bottom-right'`

### 5.3 Pin 图钉

```vue
<Pin variant="red" position="top" size="md" />
```

**颜色:** `'red'` | `'blue'` | `'green'` | `'yellow'` | `'purple'`

**尺寸:** `'sm'` | `'md'` | `'lg'`

## 6. 阴影规范

### 暖色阴影

使用带有色彩倾向的柔和阴影，而非生硬的黑色投影。

```scss
// 小阴影
--shadow-warm-sm: 1px 2px 5px rgba(210, 145, 188, 0.15);

// 中阴影
--shadow-warm: 2px 4px 10px rgba(210, 145, 188, 0.2);

// 大阴影
--shadow-warm-lg: 4px 8px 20px rgba(210, 145, 188, 0.25);

// 纸张阴影
--shadow-paper: 2px 3px 8px rgba(74, 74, 74, 0.1);
```

## 7. 手绘边框

### CSS 实现

```scss
// 手绘风圆角
border-radius: 255px 15px 225px 15px / 15px 225px 15px 255px;

// 简化版
border-radius: 15px 255px 15px 225px / 255px 15px 225px 15px;
```

### 使用工具函数

```typescript
import { getHandDrawnBorderRadius } from '@/utils/style'

// 生成随机手绘边框
const borderRadius = getHandDrawnBorderRadius()
```

## 8. 动画规范

### 预设动画

```scss
// 抖动动画 - 用于按钮悬停
@keyframes wiggle {
  0%, 100% { transform: rotate(-1deg); }
  50% { transform: rotate(1deg); }
}

// 浮动动画 - 用于装饰元素
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

// 淡入动画 - 用于页面元素
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
```

### Tailwind 动画类

- `.animate-wiggle` - 抖动
- `.animate-float` - 浮动
- `.animate-fade-in` - 淡入

## 9. 工具函数

### 随机效果

```typescript
import {
  getRandomRotation,
  getRandomOffset,
  getRandomAccentColor,
  getRandomRotationClass
} from '@/utils/style'

// 获取随机旋转角度 (-3 到 3 度)
const rotation = getRandomRotation(-3, 3)

// 获取随机偏移
const { x, y } = getRandomOffset(5)

// 获取随机点缀色
const color = getRandomAccentColor()
```

## 10. 响应式设计

### 断点

| 断点 | 尺寸 | 用途 |
|------|------|------|
| sm | 640px | 小屏手机 |
| md | 768px | 平板 |
| lg | 1024px | 桌面 |
| xl | 1280px | 大屏幕 |
| 2xl | 1536px | 超大屏幕 |

### 使用 Hook

```typescript
import { useResponsive } from '@/composables/useResponsive'

const { isMobile, isTablet, isDesktop, currentBreakpoint } = useResponsive()
```

## 11. 最佳实践

### DO ✓

- 使用预设的配色和字体变量
- 为图片容器添加随机旋转（-3° 到 3°）
- 使用暖色阴影而非纯黑阴影
- 保持元素间的层叠和遮挡关系
- 适当使用胶带、图钉等装饰元素

### DON'T ✗

- 避免使用纯黑色 (#000000)
- 避免使用直角边框
- 避免使用高饱和度颜色
- 避免过度规整的布局
- 避免使用过于机械的动画

## 12. 文件结构

```
src/
├── assets/
│   ├── styles/
│   │   └── main.scss       # 全局样式
│   ├── fonts/              # 本地字体文件
│   └── images/             # 图片资源
├── components/
│   ├── base/               # 基础组件
│   │   ├── HandButton.vue
│   │   ├── HandCard.vue
│   │   └── HandInput.vue
│   └── decorative/         # 装饰组件
│       ├── PaperTexture.vue
│       ├── HandBorder.vue
│       ├── Tape.vue
│       └── Pin.vue
├── composables/            # 组合式函数
│   ├── useLazyImage.ts
│   └── useResponsive.ts
└── utils/
    ├── style.ts            # 风格工具函数
    └── mock.ts             # Mock 数据
```
