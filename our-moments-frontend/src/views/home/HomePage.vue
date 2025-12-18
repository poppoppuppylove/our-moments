<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="home-page">
      <!-- 页面头部 -->
      <header class="home-page__header">
        <h1 class="home-page__title">Our Moments</h1>
        <p class="home-page__subtitle">记录生活的点滴，留住美好的瞬间</p>
      </header>

      <!-- 文章列表 -->
      <main class="home-page__content">
        <div class="posts-grid">
          <HandCard
            v-for="post in posts"
            :key="post.postId"
            variant="default"
            hoverable
            rotated
            class="post-card"
            @click="goToPost(post.postId)"
          >
            <!-- 文章图片 -->
            <div v-if="post.mediaList.length" class="post-card__image">
              <img :src="post.mediaList[0]?.mediaUrl" :alt="post.title" />
              <Tape v-if="Math.random() > 0.5" :variant="getRandomTapeColor()" position="top-right" />
            </div>

            <!-- 文章内容 -->
            <div class="post-card__body">
              <h2 class="post-card__title">{{ post.title }}</h2>
              <p class="post-card__excerpt">{{ getExcerpt(post.content) }}</p>

              <div class="post-card__meta">
                <span class="post-card__date">{{ formatDate(post.createTime) }}</span>
                <span class="post-card__mood">{{ post.mood }}</span>
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
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import HandCard from '@/components/base/HandCard.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Tape from '@/components/decorative/Tape.vue'
import { mockPosts } from '@/utils/mock'
import type { BlogPost } from '@/types'

const router = useRouter()
const posts = ref<BlogPost[]>([])

onMounted(() => {
  // 使用 mock 数据
  posts.value = mockPosts
})

function goToPost(postId: number) {
  router.push(`/post/${postId}`)
}

function getExcerpt(content: string, length: number = 80): string {
  if (content.length <= length) return content
  return content.slice(0, length) + '...'
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
</script>

<style scoped lang="scss">
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;

  &__header {
    text-align: center;
    margin-bottom: 60px;
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
  }

  &__content {
    margin-top: 40px;
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
  }

  .posts-grid {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 10px;
  }
}
</style>
