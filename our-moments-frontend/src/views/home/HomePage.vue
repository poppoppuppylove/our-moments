<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="home-page">
      <!-- 导航栏 -->
      <nav class="home-page__navbar">
        <div class="navbar__brand">
          <h1 class="navbar__title">Our Moments</h1>
        </div>

        <div class="navbar__actions">
          <!-- 筛选按钮 -->
          <div class="navbar__filter">
            <HandButton
              variant="ghost"
              size="sm"
              @click="toggleFilter"
              :class="{ 'filter-active': hasActiveFilter }"
            >
              筛选
              <span v-if="hasActiveFilter" class="filter-badge"></span>
            </HandButton>
          </div>

          <span class="navbar__divider">|</span>

          <!-- 背景上传控件 -->
          <div class="navbar__bg-upload">
            <HandButton variant="ghost" size="sm" @click="triggerBgUpload">
              更换背景
            </HandButton>
            <HandButton
              v-if="ui.backgroundImage"
              variant="ghost"
              size="sm"
              @click="clearBg"
            >
              恢复默认
            </HandButton>
            <input
              ref="bgInput"
              type="file"
              accept="image/*"
              class="hidden-input"
              @change="onBgFileChange"
            />
          </div>

          <span class="navbar__divider">|</span>

          <template v-if="userStore.isLoggedIn">
            <span class="navbar__welcome">欢迎, {{ userStore.nickname }}!</span>
            <HandButton variant="ghost" size="sm" @click="goToFriends">
              好友
            </HandButton>
            <HandButton variant="outline" size="sm" @click="goToProfile">
              个人资料
            </HandButton>
            <HandButton variant="primary" size="sm" @click="handleLogout">
              登出
            </HandButton>
          </template>
          <template v-else>
            <HandButton variant="outline" size="sm" @click="goToLogin">
              登录
            </HandButton>
            <HandButton variant="primary" size="sm" @click="goToRegister">
              注册
            </HandButton>
          </template>
        </div>
      </nav>

      <!-- 筛选弹出面板 -->
      <Transition name="filter-slide">
        <div v-if="showFilter" class="filter-panel">
          <div class="filter-panel__header">
            <span class="filter-panel__title">筛选</span>
            <HandButton variant="ghost" size="sm" @click="clearAllFilters">
              清除全部
            </HandButton>
          </div>

          <!-- 标签筛选 -->
          <div class="filter-panel__section">
            <span class="filter-label">标签</span>
            <div class="tag-list">
              <span
                class="filter-tag"
                :class="{ 'filter-tag--active': selectedTag === null }"
                @click="selectTag(null)"
              >
                全部
              </span>
              <span
                v-for="tag in allTags"
                :key="tag.tagId"
                class="filter-tag"
                :class="{ 'filter-tag--active': selectedTag === tag.tagId }"
                @click="selectTag(tag.tagId)"
              >
                #{{ tag.name }}
              </span>
            </div>
          </div>

          <!-- 心情筛选 -->
          <div class="filter-panel__section">
            <span class="filter-label">心情</span>
            <div class="tag-list">
              <span
                class="filter-tag"
                :class="{ 'filter-tag--active': selectedMood === null }"
                @click="selectMood(null)"
              >
                全部
              </span>
              <span
                v-for="mood in allMoods"
                :key="mood"
                class="filter-tag"
                :class="{ 'filter-tag--active': selectedMood === mood }"
                @click="selectMood(mood)"
              >
                {{ mood }}
              </span>
            </div>
          </div>

          <!-- 日期筛选 -->
          <div class="filter-panel__section">
            <span class="filter-label">日期范围</span>
            <div class="date-row">
              <input
                type="date"
                v-model="dateFrom"
                class="date-input"
                @change="applyFilters"
              />
              <span class="date-separator">至</span>
              <input
                type="date"
                v-model="dateTo"
                class="date-input"
                @change="applyFilters"
              />
            </div>
          </div>
        </div>
      </Transition>

      <!-- 点击遮罩关闭筛选 -->
      <div v-if="showFilter" class="filter-overlay" @click="showFilter = false"></div>

      <!-- 页面头部 -->
      <header class="home-page__header">
        <h1 class="home-page__title">Our Moments</h1>
        <p class="home-page__subtitle">记录生活的点滴，留住美好的瞬间</p>

        <!-- 发布按钮 -->
        <HandButton
          v-if="userStore.isLoggedIn"
          variant="primary"
          size="lg"
          @click="goToNewPost"
          class="home-page__new-post-btn"
        >
          + 记录新时刻
        </HandButton>
      </header>

      <!-- 当前筛选状态提示 -->
      <div v-if="hasActiveFilter" class="active-filters">
        <span class="active-filters__label">当前筛选：</span>
        <span v-if="selectedTag !== null" class="active-filter-tag">
          #{{ getTagName(selectedTag) }}
          <button @click="selectTag(null)">×</button>
        </span>
        <span v-if="selectedMood !== null" class="active-filter-tag">
          心情: {{ selectedMood }}
          <button @click="selectMood(null)">×</button>
        </span>
        <span v-if="dateFrom || dateTo" class="active-filter-tag">
          {{ dateFrom || '...' }} 至 {{ dateTo || '...' }}
          <button @click="clearDateFilter">×</button>
        </span>
      </div>

      <!-- 文章列表 -->
      <main class="home-page__content">
        <!-- 无内容提示 -->
        <div v-if="filteredPosts.length === 0" class="no-posts">
          <p v-if="allPosts.length === 0">还没有任何记录，快去记录你的第一个时刻吧！</p>
          <p v-else>没有符合筛选条件的记录</p>
        </div>

        <div v-else class="posts-grid">
          <HandCard
            v-for="post in filteredPosts"
            :key="post.postId"
            variant="polaroid"
            hoverable
            :rotated="true"
            :rotation="getRandomRotation()"
            class="post-card"
            :class="{ 'post-card--no-image': !post.mediaList || post.mediaList.length === 0 }"
            @click="goToPost(post.postId)"
          >
            <!-- 文章图片 -->
            <div v-if="post.mediaList && post.mediaList.length > 0" class="post-card__image">
              <img :src="post.mediaList[0]?.mediaUrl" :alt="post.title" />
              <Tape v-if="Math.random() > 0.5" :variant="getRandomTapeColor()" position="top-right" />
            </div>

            <!-- 无图片占位区域 -->
            <div v-else class="post-card__no-image">
              <span class="post-card__no-image-icon">📝</span>
            </div>

            <!-- 文章内容 -->
            <div class="post-card__body">
              <h2 class="post-card__title">{{ post.title }}</h2>
              <p class="post-card__excerpt">{{ getExcerpt(post.content) }}</p>

              <div class="post-card__meta">
                <span class="post-card__date">{{ formatDate(post.createTime) }}</span>
                <span v-if="post.mood" class="post-card__mood">{{ post.mood }}</span>
              </div>

              <div class="post-card__tags">
                <span v-for="tag in post.tagList" :key="tag.tagId" class="post-card__tag">
                  #{{ tag.name }}
                </span>
              </div>
            </div>
          </HandCard>
        </div>
      </main>

      <!-- 页脚 -->
      <HandFooter />
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUiStore } from '@/store/ui'
import { useUserStore } from '@/store/user'
import { postApi, tagApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandFooter from '@/components/common/HandFooter.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Tape from '@/components/decorative/Tape.vue'
import { mockPosts } from '@/utils/mock'
import type { BlogPost, Tag } from '@/types'
import {toast} from "@/composables/useToast.ts";

const router = useRouter()
const ui = useUiStore()
const userStore = useUserStore()
const allPosts = ref<BlogPost[]>([])
const allTags = ref<Tag[]>([])
const bgInput = ref<HTMLInputElement | null>(null)

// 所有可能的心情选项
const allMoods = ['开心', '平静', '惬意', '感动', '期待', '思念']

// 筛选状态
const selectedTag = ref<number | null>(null)
const selectedMood = ref<string | null>(null)
const dateFrom = ref<string>('')
const dateTo = ref<string>('')
const showFilter = ref(false)

// 是否有激活的筛选条件
const hasActiveFilter = computed(() => {
  return selectedTag.value !== null || selectedMood.value !== null || dateFrom.value !== '' || dateTo.value !== ''
})

// 计算过滤后的帖子
const filteredPosts = computed(() => {
  let result = allPosts.value

  // 标签筛选
  if (selectedTag.value !== null) {
    result = result.filter(post =>
      post.tagList?.some(tag => tag.tagId === selectedTag.value)
    )
  }

  // 心情筛选
  if (selectedMood.value !== null) {
    result = result.filter(post => post.mood === selectedMood.value)
  }

  // 日期筛选
  if (dateFrom.value) {
    const fromDate = new Date(dateFrom.value)
    fromDate.setHours(0, 0, 0, 0)
    result = result.filter(post => new Date(post.createTime) >= fromDate)
  }

  if (dateTo.value) {
    const toDate = new Date(dateTo.value)
    toDate.setHours(23, 59, 59, 999)
    result = result.filter(post => new Date(post.createTime) <= toDate)
  }

  return result
})

// 切换筛选面板
function toggleFilter() {
  showFilter.value = !showFilter.value
}

// 选择标签
function selectTag(tagId: number | null) {
  selectedTag.value = tagId
}

// 选择心情
function selectMood(mood: string | null) {
  selectedMood.value = mood
}

// 清除日期筛选
function clearDateFilter() {
  dateFrom.value = ''
  dateTo.value = ''
}

// 清除所有筛选
function clearAllFilters() {
  selectedTag.value = null
  selectedMood.value = null
  dateFrom.value = ''
  dateTo.value = ''
  showFilter.value = false
}

// 获取标签名称
function getTagName(tagId: number): string {
  const tag = allTags.value.find(t => t.tagId === tagId)
  return tag?.name || ''
}

// 应用筛选（日期变化时调用）
function applyFilters() {
  // 计算属性会自动更新
}

function triggerBgUpload() {
  bgInput.value?.click()
}

function onBgFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input?.files?.[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    toast.error('图片大小不能超过 5MB')
    input.value = ''
    return
  }

  const reader = new FileReader()
  reader.onload = () => {
    const dataUrl = reader.result as string
    ui.setBackgroundImage(dataUrl)
    input.value = ''
  }
  reader.readAsDataURL(file)
}

function clearBg() {
  ui.clearBackgroundImage()
}

function goToLogin() {
  router.push('/login')
}

function goToRegister() {
  router.push('/register')
}

function goToProfile() {
  router.push('/profile')
}

function goToFriends() {
  router.push('/friends')
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

onMounted(async () => {
  // 尝试从后端获取文章
  try {
    const response = await postApi.getPosts()
    // 后端已经处理了可见性控制，直接使用返回的数据
    allPosts.value = response
  } catch (err) {
    // 如果 API 调用失败，使用 Mock 数据
    console.warn('API call failed, using mock data')
    allPosts.value = mockPosts
  }

  // 加载所有标签
  try {
    const tagsResponse = await tagApi.getTags()
    allTags.value = tagsResponse
  } catch (err) {
    console.warn('Failed to load tags')
  }
})

function goToPost(postId: number) {
  router.push(`/post/${postId}`)
}

function goToNewPost() {
  router.push('/post/new')
}

function getExcerpt(content: string, length: number = 80): string {
  // 移除 HTML 标签和图片标记
  let text = content
    .replace(/<img[^>]*>/gi, '') // 移除 img 标签
    .replace(/<img-src="[^"]*"\/>/gi, '') // 移除自定义图片标记
    .replace(/<[^>]+>/g, '') // 移除其他 HTML 标签
    .replace(/&nbsp;/g, ' ') // 替换 &nbsp;
    .trim()

  if (text.length <= length) return text
  return text.slice(0, length) + '...'
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${month}月${day}日`
}

function getRandomTapeColor(): 'yellow' | 'pink' | 'blue' | 'green' | 'purple' {
  const colors: Array<'yellow' | 'pink' | 'blue' | 'green' | 'purple'> = ['yellow', 'pink', 'blue', 'green', 'purple']
  return colors[Math.floor(Math.random() * colors.length)]!
}

function getRandomRotation(): number {
  // 生成 -5 到 5 度之间的随机旋转角度
  return (Math.random() - 0.5) * 10
}
</script>

<style scoped lang="scss">
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  position: relative; // 启用绝对定位子元素

  &__navbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(4px);
    border-radius: 12px;
    margin-bottom: 30px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
  }

  &__header {
    text-align: center;
    margin-bottom: 60px;
    padding-top: 20px; // 为头部增加空间，如果需要
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 3.5rem;
    color: var(--color-soft-purple);
    margin-bottom: 16px;
    letter-spacing: 2px;
  }

  &__subtitle {
    font-family: var(--font-body);
    font-size: 1.1rem;
    color: var(--color-ink-light);
    margin-bottom: 24px;
  }

  .hidden-input {
    display: none;
  }

  &__content {
    margin-top: 40px;
  }
}

.navbar {
  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.8rem;
    color: var(--color-soft-purple);
    margin: 0;
  }

  &__actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  &__bg-upload {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  &__divider {
    color: var(--color-ink-light);
    opacity: 0.4;
    margin: 0 4px;
  }

  &__welcome {
    font-family: var(--font-body);
    font-size: 0.95rem;
    color: var(--color-ink-light);
    margin-right: 8px;
  }
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 40px;
  padding: 20px;
}

.post-card {
  cursor: pointer;
  background: white;
  display: flex;
  flex-direction: column;
  min-height: 280px;

  &__image {
    position: relative;
    margin: -20px -20px 16px -20px;
    border-radius: 15px 255px 0 0 / 255px 15px 0 0;
    overflow: hidden;

    img {
      width: 100%;
      height: 200px;
      object-fit: cover;
    }
  }

  &__body {
    padding: 0 4px;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.5rem;
    color: var(--color-ink);
    margin-bottom: 12px;
    line-height: 1.3;
  }

  &__excerpt {
    font-family: var(--font-body);
    font-size: 0.95rem;
    color: var(--color-ink-light);
    line-height: 1.6;
    margin-bottom: 16px;
    flex: 1;
  }

  &__meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.85rem;
    color: var(--color-ink-light);
    margin-bottom: 12px;
  }

  &__mood {
    background: var(--color-blush);
    padding: 2px 10px;
    border-radius: 20px;
    font-size: 0.8rem;
  }

  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  &__tag {
    font-size: 0.8rem;
    color: var(--color-soft-purple);
    font-style: italic;
  }

  &__no-image {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: -20px -20px 16px -20px;
    height: 200px;
    background: linear-gradient(
      135deg,
      var(--color-paper-dark) 0%,
      var(--color-paper) 50%,
      rgba(210, 145, 188, 0.08) 100%
    );
    border-radius: 15px 255px 0 0 / 255px 15px 0 0;
    position: relative;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 400 400' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noiseFilter'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='3' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noiseFilter)'/%3E%3C/svg%3E");
      opacity: 0.03;
      pointer-events: none;
    }
  }

  &__no-image-icon {
    font-size: 2.5rem;
    opacity: 0.3;
    color: var(--color-soft-purple);
  }

  &--no-image {
    .post-card__body {
      padding-top: 8px;
    }

    .post-card__excerpt {
      -webkit-line-clamp: 4;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}

// 筛选弹出面板
.filter-panel {
  position: absolute;
  top: 80px;
  left: 20px;
  right: 20px;
  max-width: 500px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-radius: 16px;
  padding: 20px 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(168, 140, 190, 0.2);
  z-index: 100;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px dashed rgba(168, 140, 190, 0.3);
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.3rem;
    color: var(--color-soft-purple);
  }

  &__section {
    margin-bottom: 18px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

.filter-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 99;
}

// 筛选动画
.filter-slide-enter-active,
.filter-slide-leave-active {
  transition: all 0.25s ease;
}

.filter-slide-enter-from,
.filter-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// 筛选按钮激活状态
.navbar__filter {
  position: relative;

  .filter-active {
    color: var(--color-soft-purple);
  }

  .filter-badge {
    position: absolute;
    top: -2px;
    right: -2px;
    width: 8px;
    height: 8px;
    background: var(--color-soft-purple);
    border-radius: 50%;
  }
}

// 当前筛选状态提示
.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
  padding: 12px 16px;
  background: rgba(168, 140, 190, 0.08);
  border-radius: 12px;

  &__label {
    font-family: var(--font-body);
    font-size: 0.85rem;
    color: var(--color-ink-light);
  }
}

.active-filter-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  background: var(--color-soft-purple);
  color: white;
  border-radius: 16px;
  font-family: var(--font-body);
  font-size: 0.8rem;

  button {
    background: none;
    border: none;
    color: white;
    cursor: pointer;
    padding: 0;
    font-size: 1rem;
    line-height: 1;
    opacity: 0.8;

    &:hover {
      opacity: 1;
    }
  }
}

.filter-label {
  font-family: var(--font-handwriting);
  font-size: 1rem;
  color: var(--color-ink);
  display: block;
  margin-bottom: 10px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-tag {
  font-family: var(--font-body);
  font-size: 0.85rem;
  padding: 5px 14px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(168, 140, 190, 0.1);
  color: var(--color-ink-light);
  border: 1px solid transparent;

  &:hover {
    background: rgba(168, 140, 190, 0.2);
    color: var(--color-soft-purple);
  }

  &--active {
    background: var(--color-soft-purple);
    color: white;

    &:hover {
      background: var(--color-soft-purple);
      color: white;
    }
  }
}

.date-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.date-input {
  font-family: var(--font-body);
  font-size: 0.85rem;
  padding: 8px 12px;
  border: 1px dashed rgba(168, 140, 190, 0.4);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.6);
  color: var(--color-ink);
  outline: none;
  transition: all 0.2s;

  &:focus {
    border-color: var(--color-soft-purple);
    border-style: solid;
  }
}

.date-separator {
  font-family: var(--font-body);
  color: var(--color-ink-light);
  font-size: 0.85rem;
}

.no-posts {
  text-align: center;
  padding: 60px 20px;
  font-family: var(--font-handwriting);
  font-size: 1.2rem;
  color: var(--color-ink-light);
}

@media (max-width: 768px) {
  .home-page {
    padding: 20px 16px;

    &__title {
      font-size: 2.5rem;
    }

    &__header {
      margin-bottom: 40px;
    }

    &__navbar {
      flex-direction: column;
      gap: 12px;
      padding: 12px;
    }

    .navbar__actions {
      width: 100%;
      justify-content: center;
      flex-wrap: wrap;
    }
  }

  .filter-panel {
    left: 16px;
    right: 16px;
    top: 140px;
    padding: 16px 20px;
  }

  .date-row {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }

  .date-input {
    width: 100%;
  }

  .posts-grid {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 10px;
  }
}
</style>