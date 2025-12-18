<template>
  <PaperTexture variant="warm" class="min-h-screen">
    <div class="post-detail" v-if="post">
      <!-- 返回按钮 -->
      <div class="post-detail__back">
        <HandButton variant="ghost" size="sm" @click="goBack">
          ← 返回
        </HandButton>
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
          <span class="post-detail__weather">{{ post.weather }}</span>
          <span class="post-detail__mood">{{ post.mood }}</span>
        </div>
      </header>

      <!-- 文章内容（图文混排） -->
      <HandCard variant="paper" class="post-detail__content">
        <template v-for="(block, index) in articleBlocks" :key="index">
          <p v-if="block.type === 'text'" class="post-detail__paragraph" v-html="formatParagraph(block.content)"></p>

          <div v-else-if="block.type === 'image'" class="post-detail__inline-image">
            <div
              class="gallery-item gallery-item--inline"
              :style="getMediaStyle(block.media, index)"
            >
              <div class="gallery-item__frame">
                <img :src="block.media.mediaUrl" :alt="`图片 ${index + 1}`" />
              </div>
              <Tape
                v-if="index % 2 === 0"
                :variant="getTapeColor(index)"
                :position="index % 3 === 0 ? 'top-left' : 'top-right'"
              />
              <Pin
                v-else
                :variant="getPinColor(index)"
                position="top"
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
    </div>

    <!-- 加载状态 -->
    <div v-else class="post-detail__loading">
      <p>正在加载...</p>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HandButton from '@/components/base/HandButton.vue'
import HandCard from '@/components/base/HandCard.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Tape from '@/components/decorative/Tape.vue'
import Pin from '@/components/decorative/Pin.vue'
import { mockPosts } from '@/utils/mock'
import type { BlogPost, BlogMedia } from '@/types'

const route = useRoute()
const router = useRouter()

const post = ref<BlogPost | null>(null)

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
      blocks.push({ type: 'image' as const, media: mediaList[mediaIndex] })
      mediaIndex++
    }
  })

  // 如果还有剩余的图片，添加到末尾
  while (mediaIndex < mediaCount) {
    blocks.push({ type: 'image' as const, media: mediaList[mediaIndex] })
    mediaIndex++
  }

  return blocks
})

onMounted(() => {
  const postId = Number(route.params.id)
  // 使用 mock 数据
  post.value = mockPosts.find(p => p.postId === postId) || mockPosts[0] || null
})

function goBack() {
  router.back()
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  return `${year}年${month}月${day}日`
}

function formatContent(content: string): string {
  return content.replace(/\n/g, '<br>')
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

function getPinColor(index: number): 'red' | 'blue' | 'green' | 'yellow' | 'purple' {
  const colors: Array<'red' | 'blue' | 'green' | 'yellow' | 'purple'> = ['red', 'blue', 'green', 'yellow', 'purple']
  return colors[index % colors.length]!
}
</script>

<style scoped lang="scss">
.post-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;

  &__back {
    margin-bottom: 30px;
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

  &__loading {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 60vh;
    font-family: var(--font-body);
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
    padding: 10px 10px 30px 10px;
    box-shadow: var(--shadow-paper);

    img {
      width: 280px;
      height: 210px;
      object-fit: cover;

      @media (max-width: 768px) {
        width: 200px;
        height: 150px;
      }
    }
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
