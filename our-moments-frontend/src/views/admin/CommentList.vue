<template>
  <div class="comment-list">
    <header class="page-header">
      <h1 class="page-title">评论管理</h1>
      <HandButton variant="primary" size="sm" @click="loadComments">
        刷新
      </HandButton>
    </header>

    <!-- 加载状态 -->
    <HandLoading v-if="loading" text="正在加载评论列表..." />

    <!-- 评论列表 -->
    <div v-else-if="comments.length > 0" class="comments-list">
      <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
        <div class="comment-header">
          <div class="comment-author">
            <img
              v-if="comment.author?.avatar"
              :src="comment.author.avatar"
              :alt="comment.author.nickname"
              class="author-avatar"
            />
            <span class="author-name">{{ comment.author?.nickname || '未知用户' }}</span>
          </div>
          <span class="comment-date">{{ formatDate(comment.createTime) }}</span>
        </div>
        <p class="comment-content">{{ comment.content }}</p>
        <div class="comment-meta">
          <span class="post-reference">文章ID: {{ comment.postId }}</span>
          <HandButton
            variant="danger"
            size="sm"
            @click="deleteComment(comment.commentId)"
            :loading="deletingId === comment.commentId"
          >
            删除评论
          </HandButton>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">💬</div>
      <p class="empty-text">暂无评论</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { toast } from '@/composables/useToast'
import { adminApi } from '@/api'
import type { Comment } from '@/types'
import HandButton from '@/components/base/HandButton.vue'
import HandLoading from '@/components/common/HandLoading.vue'

const loading = ref(false)
const deletingId = ref<number | null>(null)
const comments = ref<Comment[]>([])

onMounted(() => {
  loadComments()
})

async function loadComments() {
  loading.value = true
  try {
    const data = await adminApi.getAllComments()
    comments.value = data
  } catch (err) {
    console.error('Failed to load comments:', err)
    toast.error('加载评论列表失败')
  } finally {
    loading.value = false
  }
}

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

async function deleteComment(commentId: number) {
  if (!confirm('确定要删除这条评论吗？')) return

  deletingId.value = commentId
  try {
    await adminApi.deleteComment(commentId)
    comments.value = comments.value.filter(comment => comment.commentId !== commentId)
    toast.success('评论已删除')
  } catch (err) {
    console.error('Failed to delete comment:', err)
    toast.error('删除评论失败')
  } finally {
    deletingId.value = null
  }
}
</script>

<style scoped lang="scss">
.comment-list {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  .page-title {
    font-family: var(--font-handwriting);
    font-size: 2rem;
    color: var(--color-soft-purple);
    margin: 0;
  }
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 10px;

  .author-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    object-fit: cover;
    border: 2px solid var(--color-ink-lighter);
  }

  .author-name {
    font-family: var(--font-body);
    font-size: 0.95rem;
    font-weight: 500;
    color: var(--color-ink);
  }
}

.comment-date {
  font-family: var(--font-body);
  font-size: 0.8rem;
  color: var(--color-ink-light);
}

.comment-content {
  font-family: var(--font-body);
  font-size: 0.95rem;
  line-height: 1.6;
  color: var(--color-ink);
  margin: 0 0 12px 0;
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--color-ink-lighter);
}

.post-reference {
  font-family: var(--font-body);
  font-size: 0.85rem;
  color: var(--color-ink-light);
}

.empty-state {
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
    margin: 0;
  }
}

// 响应式
@media (max-width: 768px) {
  .comment-list {
    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }

    .page-title {
      font-size: 1.6rem;
    }
  }

  .comment-item {
    padding: 16px;
  }

  .comment-meta {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>