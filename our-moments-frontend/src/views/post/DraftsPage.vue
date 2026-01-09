<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="drafts-page">
      <HandCard variant="paper" class="drafts-page__card">
        <Pin variant="blue" position="top" />

        <header class="drafts-page__header">
          <h1 class="drafts-page__title">_drafts_</h1>
          <HandButton variant="ghost" size="sm" @click="goBack">
            返回
          </HandButton>
        </header>

        <!-- 加载状态 -->
        <HandLoading v-if="loading" text="正在加载草稿..." />

        <!-- 空状态 -->
        <div v-else-if="drafts.length === 0" class="drafts-empty">
          <div class="empty-icon">📝</div>
          <p class="empty-text">暂无草稿</p>
          <HandButton variant="primary" @click="goToNewPost">
            记录新时刻
          </HandButton>
        </div>

        <!-- 草稿列表 -->
        <div v-else class="drafts-list">
          <HandCard
            v-for="draft in drafts"
            :key="draft.postId"
            variant="light"
            class="draft-item"
            @click="editDraft(draft.postId)"
          >
            <div class="draft-item__content">
              <h3 class="draft-item__title">
                {{ draft.title || '未命名草稿' }}
              </h3>
              <p class="draft-item__preview" v-html="getPreviewContent(draft.content)"></p>
              <div class="draft-item__meta">
                <span class="draft-item__date">
                  {{ formatDate(draft.createTime) }}
                </span>
                <span v-if="draft.updateTime !== draft.createTime" class="draft-item__updated">
                  更新于 {{ formatDate(draft.updateTime) }}
                </span>
              </div>
            </div>
            <div class="draft-item__actions">
              <HandButton
                variant="ghost"
                size="sm"
                @click.stop="deleteDraft(draft.postId)"
                :loading="deletingId === draft.postId"
              >
                删除
              </HandButton>
            </div>
          </HandCard>
        </div>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { toast } from '@/composables/useToast'
import { draftApi, postApi } from '@/api'
import type { BlogPost } from '@/types'
import HandButton from '@/components/base/HandButton.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const deletingId = ref<number | null>(null)
const drafts = ref<BlogPost[]>([])

onMounted(() => {
  // 检查登录状态
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  loadDrafts()
})

async function loadDrafts() {
  loading.value = true
  try {
    const data = await draftApi.getDrafts()
    drafts.value = data
  } catch (err) {
    console.error('Failed to load drafts:', err)
    toast.error('加载草稿失败')
  } finally {
    loading.value = false
  }
}

function getPreviewContent(content: string = ''): string {
  // 移除 HTML 标签并截取前100个字符
  const text = content.replace(/<[^>]*>/g, '').trim()
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function editDraft(postId: number) {
  router.push(`/post/${postId}/edit`)
}

async function deleteDraft(postId: number) {
  if (!confirm('确定要删除这个草稿吗？')) return

  deletingId.value = postId
  try {
    await postApi.deletePost(postId)
    drafts.value = drafts.value.filter(draft => draft.postId !== postId)
    toast.success('草稿已删除')
  } catch (err) {
    console.error('Failed to delete draft:', err)
    toast.error('删除草稿失败')
  } finally {
    deletingId.value = null
  }
}

function goToNewPost() {
  router.push('/post/new')
}

function goBack() {
  router.back()
}
</script>

<style scoped lang="scss">
.drafts-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;

  &__card {
    padding: 40px 30px;
  }

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 2rem;
    color: var(--color-soft-purple);
    margin: 0;
  }

  .drafts-empty {
    text-align: center;
    padding: 60px 20px;

    .empty-icon {
      font-size: 3rem;
      margin-bottom: 20px;
    }

    .empty-text {
      font-family: var(--font-handwriting);
      font-size: 1.2rem;
      color: var(--color-ink-light);
      margin-bottom: 24px;
    }
  }

  .drafts-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .draft-item {
    cursor: pointer;
    transition: all 0.2s;
    position: relative;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
    }

    &__content {
      padding: 20px;
    }

    &__title {
      font-family: var(--font-handwriting);
      font-size: 1.3rem;
      color: var(--color-ink);
      margin: 0 0 12px 0;
      line-height: 1.3;
    }

    &__preview {
      font-family: var(--font-body);
      font-size: 0.95rem;
      color: var(--color-ink-light);
      line-height: 1.6;
      margin: 0 0 16px 0;
    }

    &__meta {
      display: flex;
      gap: 16px;
      font-size: 0.8rem;
      color: var(--color-ink-lighter);
    }

    &__actions {
      position: absolute;
      top: 16px;
      right: 16px;
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .drafts-page {
    padding: 16px 12px;

    &__card {
      padding: 20px 16px;
    }

    &__header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }

    &__title {
      font-size: 1.6rem;
    }

    .drafts-empty {
      padding: 40px 16px;
    }

    .draft-item {
      &__content {
        padding: 16px;
      }

      &__title {
        font-size: 1.2rem;
      }

      &__preview {
        font-size: 0.9rem;
      }

      &__meta {
        flex-direction: column;
        gap: 4px;
      }
    }
  }
}

@media (max-width: 480px) {
  .drafts-page {
    padding: 12px 8px;

    &__card {
      padding: 16px 12px;
    }

    &__title {
      font-size: 1.4rem;
    }

    .draft-item {
      &__content {
        padding: 12px;
      }

      &__title {
        font-size: 1.1rem;
      }
    }
  }
}
</style>