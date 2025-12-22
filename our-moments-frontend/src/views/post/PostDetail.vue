<template>
  <PaperTexture variant="warm" class="min-h-screen">
    <div class="post-detail" v-if="post">
      <!-- 顶部操作栏 -->
      <div class="post-detail__actions">
        <HandButton variant="ghost" size="sm" @click="goBack">
          ← 返回
        </HandButton>
        <div class="post-detail__action-right">
          <!-- 作者操作按钮 -->
          <template v-if="isAuthor">
            <HandButton variant="outline" size="sm" @click="editPost">
              编辑
            </HandButton>
            <HandButton variant="ghost" size="sm" class="delete-btn" @click="confirmDelete">
              删除
            </HandButton>
          </template>
          <HandShare />
        </div>
      </div>

      <!-- 文章头部 -->
      <header class="post-detail__header">
        <h1 class="post-detail__title">{{ post.title }}</h1>
        <div class="post-detail__meta">
          <span class="post-detail__author">
            <img :src="post.author.avatar" :alt="post.author.nickname" class="post-detail__avatar" />
            {{ post.author.nickname }}
          </span>
          <span class="post-detail__date">{{ formatDate(post.createTime) }}</span>
          <span v-if="post.weather" class="post-detail__weather">{{ post.weather }}</span>
          <span v-if="post.mood" class="post-detail__mood">{{ post.mood }}</span>
        </div>
      </header>

      <!-- 文章内容（图文混排） -->
      <HandCard variant="paper" class="post-detail__content">
        <template v-for="(block, index) in articleBlocks" :key="index">
          <p v-if="block.type === 'text'" class="post-detail__paragraph" v-html="formatParagraph(block.content)"></p>

          <div v-else-if="block.type === 'image'" class="post-detail__inline-image">
            <div
              class="gallery-item gallery-item--inline"
              :style="getMediaStyle(block.media!, index)"
            >
              <div :class="['gallery-item__frame', `frame--${getFrameStyle(block.media!, index)}`]">
                <img :src="block.media!.mediaUrl" :alt="`图片 ${index + 1}`" />

                <!-- 装饰性 SVG：仅在特定样式下显示 -->
                <!-- 爪印 SVG -->
                <svg v-if="getFrameStyle(block.media!, index) === 'paw'" class="frame-overlay frame-overlay--paw" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                  <path d="M32 44c-8 0-14 6-14 10 0 4 6 6 14 6s14-2 14-6c0-4-6-10-14-10z" fill="#D4C4A8" opacity="0.6"/>
                  <circle cx="18" cy="18" r="6" fill="#D4C4A8" opacity="0.6"/>
                  <circle cx="30" cy="10" r="6" fill="#D4C4A8" opacity="0.6"/>
                  <circle cx="44" cy="18" r="6" fill="#D4C4A8" opacity="0.6"/>
                </svg>

                <!-- 三叶草 SVG -->
                <svg v-if="getFrameStyle(block.media!, index) === 'clover'" class="frame-overlay frame-overlay--clover" viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                  <g fill="none" stroke="#8CBF65" stroke-width="2">
                    <circle cx="30" cy="30" r="18" fill="rgba(140, 191, 101, 0.1)"/>
                    <circle cx="70" cy="30" r="18" fill="rgba(140, 191, 101, 0.1)"/>
                    <circle cx="50" cy="55" r="18" fill="rgba(140, 191, 101, 0.1)"/>
                  </g>
                </svg>
              </div>

              <Tape
                v-if="index % 2 === 0"
                :variant="getTapeColor(index)"
                :position="index % 3 === 0 ? 'top-left' : 'top-right'"
              />
            </div>
          </div>
        </template>

        <!-- 标签 -->
        <div class="post-detail__tags">
          <span v-for="tag in post.tagList" :key="tag.tagId" class="post-detail__tag">
            #{{ tag.name }}
          </span>
        </div>

        <!-- 位置信息 -->
        <div v-if="post.location" class="post-detail__location">
          📍 {{ post.location }}
        </div>
      </HandCard>

      <!-- 评论区域 -->
      <PostComments :post-id="post.postId" />

      <!-- 页脚 -->
      <HandFooter />
    </div>

    <!-- 加载状态 -->
    <HandLoading v-else-if="loading" text="正在加载文章..." fullscreen />

    <!-- 错误状态 -->
    <HandError
      v-else
      title="加载失败"
      message="无法加载文章内容，请稍后再试"
      @retry="loadPost"
    />
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { postApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandShare from '@/components/base/HandShare.vue'
import HandFooter from '@/components/common/HandFooter.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import HandError from '@/components/common/HandError.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
// import Tape from '@/components/decorative/Tape.vue'
import PostComments from '@/components/PostComments.vue'
import { mockPosts } from '@/utils/mock'
import type { BlogPost, BlogMedia } from '@/types'
import {toast} from "@/composables/useToast.ts";

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref<BlogPost | null>(null)
const loading = ref(true)
const error = ref(false)

// 判断当前用户是否为作者
const isAuthor = computed(() => {
  if (!post.value || !userStore.user) return false
  return post.value.userId === userStore.user.userId
})

// 计算属性：将文章内容和图片交错排列
const articleBlocks = computed(() => {
  if (!post.value) return []

  // 将内容按段落分割
  const paragraphs = post.value.content.split('\n').filter(p => p.trim() !== '')
  const mediaList = post.value.mediaList

  // 如果没有图片，直接返回段落
  if (mediaList.length === 0) {
    return paragraphs.map(content => ({ type: 'text' as const, content }))
  }

  // 创建包含文本和图片的块数组
  const blocks = []
  const paragraphCount = paragraphs.length
  const mediaCount = mediaList.length

  // 计算每个图片之间应该间隔多少段落
  const interval = Math.max(1, Math.floor(paragraphCount / (mediaCount + 1)))

  let mediaIndex = 0
  paragraphs.forEach((paragraph, index) => {
    // 先添加段落
    blocks.push({ type: 'text' as const, content: paragraph })

    // 在适当的位置插入图片
    if ((index + 1) % interval === 0 && mediaIndex < mediaCount) {
      const media = mediaList[mediaIndex]
      if (media) {
        blocks.push({ type: 'image' as const, media: media })
      }
      mediaIndex++
    }
  })

  // 如果还有剩余的图片，添加到末尾
  while (mediaIndex < mediaCount) {
    const media = mediaList[mediaIndex]
    if (media) {
      blocks.push({ type: 'image' as const, media: media })
    }
    mediaIndex++
  }

  return blocks
})

async function loadPost() {
  const postId = Number(route.params.id)
  loading.value = true
  error.value = false

  try {
    // 尝试调用后端 API
    const response = await postApi.getPost(postId)
    post.value = response
  } catch (err) {
    // 如果 API 调用失败，使用 Mock 数据
    console.warn('API call failed, using mock data')
    post.value = mockPosts.find(p => p.postId === postId) || mockPosts[0] || null
    if (!post.value) {
      error.value = true
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadPost()
})

function goBack() {
  router.back()
}

// 编辑帖子
function editPost() {
  if (post.value) {
    router.push(`/post/${post.value.postId}/edit`)
  }
}

// 确认删除
async function confirmDelete() {
  if (!post.value) return

  if (confirm('确定要删除这篇文章吗？此操作不可撤销。')) {
    try {
      await postApi.deletePost(post.value.postId)
      toast.success('文章已删除')
      router.push('/')
    } catch (err) {
      console.error('删除文章失败:', err)
      toast.error('删除失败，请稍后重试')
    }
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2,'0')
  return `${year}年${month}月${day}日 ${hours}:${minutes}:${seconds}`
}


function formatParagraph(content: string): string {
  // 保持段落原样，不添加额外的换行
  return content
}

function getMediaStyle(media: BlogMedia, index: number) {
  const rotations = [-3, 2, -1, 3, -2]
  const rotation = media.rotation || rotations[index % rotations.length]

  return {
    transform: `rotate(${rotation}deg)`,
    zIndex: media.zindex || index
  }
}

function getTapeColor(index: number): 'yellow' | 'pink' | 'blue' | 'green' | 'purple' {
  const colors: Array<'yellow' | 'pink' | 'blue' | 'green' | 'purple'> = ['yellow', 'pink', 'blue', 'green', 'purple']
  return colors[index % colors.length]!
}


// 相框样式逻辑
const frameStyles = ['polaroid', 'paw', 'clover'] as const
type FrameStyle = typeof frameStyles[number]

function hashCode(str: string) {
  let h = 0
  for (let i = 0; i < str.length; i++) {
    h = ((h << 5) - h) + str.charCodeAt(i)
    h |= 0
  }
  return Math.abs(h)
}

function getFrameStyle(media: BlogMedia, index: number): FrameStyle {
  // 如果后端支持，优先使用 media.frameStyle
  // 目前使用确定性哈希算法，基于 mediaUrl 或 index 来分配样式
  const key = media.mediaUrl || String(index)
  // 使用不同的模数以避免与胶带颜色同步
  const styleIndex = hashCode(key) % frameStyles.length
  return frameStyles[styleIndex]!
}

</script>

<style scoped lang="scss">
.post-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;

  &__actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
  }

  &__action-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .delete-btn {
    color: #c44;

    &:hover {
      color: #a33;
    }
  }

  &__header {
    text-align: center;
    margin-bottom: 40px;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 2.5rem;
    color: var(--color-ink);
    margin-bottom: 20px;
    line-height: 1.3;
  }

  &__meta {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 20px;
    flex-wrap: wrap;
    font-size: 0.9rem;
    color: var(--color-ink-light);
  }

  &__author {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  &__avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
  }

  &__weather,
  &__mood {
    background: var(--color-paper);
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.85rem;
  }

  &__content {
    margin-bottom: 40px;
  }

  &__paragraph {
    font-family: var(--font-body);
    font-size: 1.1rem;
    line-height: 2;
    color: var(--color-ink);
    text-align: justify;
    margin-bottom: 1.5rem;
    text-indent: 2em;
  }

  &__inline-image {
    display: flex;
    justify-content: center;
    margin: 2rem 0;
    scroll-snap-align: start;
  }

  &__tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 30px;
    padding-top: 20px;
    border-top: 1px dashed var(--color-ink-light);
  }

  &__tag {
    font-size: 0.9rem;
    color: var(--color-soft-purple);
    font-style: italic;
  }

  &__location {
    margin-top: 16px;
    font-size: 0.9rem;
    color: var(--color-ink-light);
  }
}

.gallery-item {
  position: relative;
  transition: transform 0.3s ease;

  &--inline {
    transform: scale(0.9); // 内联图片稍微小一点

    @media (max-width: 768px) {
      transform: scale(0.8);
    }
  }

  &:hover {
    transform: rotate(0deg) scale(1.02) !important;
    z-index: 10 !important;
  }

  &__frame {
    background: white;
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.15);
    border-radius: 2px;
    position: relative; // 允许绝对定位装饰物
    transition: all 0.3s ease;

    // 默认样式 (polaroid)
    padding: 12px 12px 40px 12px;

    img {
      width: 280px;
      height: 280px; // 拍立得通常是正方形
      object-fit: cover;
      border: 1px solid rgba(0, 0, 0, 0.05);

      @media (max-width: 768px) {
        width: 200px;
        height: 200px;
      }
    }
  }

  // 样式2：米黄爪印边框 (Paw)
  .frame--paw {
    background: #f8f3e9; // 米黄色背景
    padding: 20px;
    border-radius: 4px;

    // 爪印装饰 SVG 定位
    .frame-overlay--paw {
      position: absolute;
      bottom: 8px;
      right: 8px;
      width: 48px;
      height: 48px;
      pointer-events: none;
      transform: rotate(-15deg);
    }

    img {
      width: 280px;
      height: 210px; // 恢复为原来的长宽比，更像相册照片
      border: none;
      border-radius: 2px;

      @media (max-width: 768px) {
        width: 200px;
        height: 150px;
      }
    }
  }

  // 样式3：白色三叶草边框 (Clover)
  .frame--clover {
    background: #fff;
    padding: 16px;
    border-radius: 8px; // 更圆润的角
    border: 1px solid #e0e0e0;

    // 三叶草装饰 SVG 定位
    .frame-overlay--clover {
      position: absolute;
      top: -12px;
      left: -12px;
      width: 64px;
      height: 64px;
      pointer-events: none;
      transform: rotate(-10deg);
    }

    img {
      width: 280px;
      height: 210px; // 恢复为原来的长宽比
      border-radius: 4px;

      @media (max-width: 768px) {
        width: 200px;
        height: 150px;
      }
    }
  }

  // 样式1：Polaroid (显式类名，与默认样式一致)
  .frame--polaroid {
    // 继承默认样式，不需要额外覆盖
  }
}

@media (max-width: 768px) {
  .post-detail {
    padding: 20px 16px;

    &__title {
      font-size: 1.8rem;
    }

    &__meta {
      gap: 12px;
    }

    &__paragraph {
      font-size: 1rem;
      line-height: 1.8;
    }
  }
}
</style>
