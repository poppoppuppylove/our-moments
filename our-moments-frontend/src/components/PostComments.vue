<template>
  <div class="post-comments">
    <h3 class="comments-title">评论 ({{ comments.length }})</h3>

    <!-- 评论列表 -->
    <div class="comments-list">
      <div
        v-for="comment in comments"
        :key="comment.commentId"
        class="comment-item"
      >
        <div class="comment-header">
          <img
            :src="comment.author.avatar"
            :alt="comment.author.nickname"
            class="comment-avatar"
          />
          <div class="comment-author-info">
            <span class="comment-author">{{ comment.author.nickname }}</span>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <!-- 删除按钮：评论作者或管理员可见 -->
          <button
            v-if="canDeleteComment(comment)"
            class="comment-delete-btn"
            @click="confirmDeleteComment(comment)"
            title="删除评论"
          >
            ×
          </button>
        </div>
        <div class="comment-content">
          {{ comment.content }}
        </div>
      </div>

      <!-- 没有评论时的提示 -->
      <div v-if="comments.length === 0" class="no-comments">
        还没有评论，快来抢沙发吧！
      </div>
    </div>

    <!-- 评论输入框 -->
    <div v-if="userStore.isLoggedIn" class="comment-form">
      <div class="comment-form-header">
        <img
          :src="userStore.user?.avatar"
          :alt="userStore.user?.nickname"
          class="comment-form-avatar"
        />
        <span class="comment-form-author">{{ userStore.user?.nickname }}</span>
      </div>
      <textarea
        v-model="newComment"
        placeholder="写下你的评论..."
        class="comment-textarea"
        rows="3"
      ></textarea>
      <div class="comment-form-actions">
        <HandButton
          variant="primary"
          size="sm"
          @click="submitComment"
          :disabled="!newComment.trim() || submitting"
        >
          {{ submitting ? '提交中...' : '发表评论' }}
        </HandButton>
      </div>
    </div>

    <!-- 未登录提示 -->
    <div v-else class="login-prompt">
      <p>登录后可以发表评论</p>
      <HandButton variant="primary" size="sm" @click="goToLogin">
        登录
      </HandButton>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { commentApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import type { Comment } from '@/types'
import {toast} from "@/composables/useToast.ts";

const props = defineProps<{
  postId: number
}>()

const emit = defineEmits<{
  (e: 'comment-added'): void
}>()

const router = useRouter()
const userStore = useUserStore()
const comments = ref<Comment[]>([])
const newComment = ref('')
const submitting = ref(false)
const loading = ref(false)

// 加载评论
async function loadComments() {
  loading.value = true
  try {
    const response = await commentApi.getCommentsByPostId(props.postId)
    comments.value = response
  } catch (error) {
    console.error('Failed to load comments:', error)
  } finally {
    loading.value = false
  }
}

// 提交评论
async function submitComment() {
  if (!newComment.value.trim() || !userStore.user) return

  submitting.value = true
  try {
    const commentData: Comment = {
      commentId: 0,
      postId: props.postId,
      userId: userStore.user.userId,
      content: newComment.value.trim(),
      position: 0,
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString(),
      author: userStore.user
    }

    await commentApi.createComment(commentData)
    newComment.value = ''
    await loadComments()
    emit('comment-added')
  } catch (error) {
    console.error('Failed to submit comment:', error)
    toast.error('评论提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 格式化时间
function formatTime(timeString: string): string {
  const date = new Date(timeString)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

  if (diffDays === 0) {
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    if (diffHours === 0) {
      const diffMinutes = Math.floor(diffMs / (1000 * 60))
      return diffMinutes <= 0 ? '刚刚' : `${diffMinutes}分钟前`
    }
    return `${diffHours}小时前`
  } else if (diffDays === 1) {
    return '昨天'
  } else if (diffDays < 7) {
    return `${diffDays}天前`
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`
  }
}

// 判断是否可以删除评论（评论作者或管理员）
function canDeleteComment(comment: Comment): boolean {
  if (!userStore.user) return false
  return comment.userId === userStore.user.userId || userStore.isAdmin
}

// 确认删除评论
async function confirmDeleteComment(comment: Comment) {
  if (!confirm('确定要删除这条评论吗？')) return

  try {
    await commentApi.deleteComment(comment.commentId)
    toast.success('评论已删除')
    await loadComments()
  } catch (error) {
    console.error('Failed to delete comment:', error)
    toast.error('删除评论失败，请稍后重试')
  }
}

function goToLogin() {
  router.push('/login')
}

onMounted(() => {
  loadComments()
})

// 暴露方法给父组件
defineExpose({
  loadComments
})
</script>

<style scoped lang="scss">
.post-comments {
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px dashed var(--color-ink-light);
}

.comments-title {
  font-family: var(--font-handwriting);
  font-size: 1.5rem;
  color: var(--color-ink);
  margin-bottom: 24px;
  text-align: center;
}

.comments-list {
  margin-bottom: 30px;
}

.comment-item {
  background: var(--color-paper);
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.comment-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 12px;
}

.comment-author-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.comment-delete-btn {
  background: none;
  border: none;
  color: var(--color-ink-light);
  font-size: 1.2rem;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s ease;
  opacity: 0;

  &:hover {
    color: #c44;
    background: rgba(204, 68, 68, 0.1);
  }
}

.comment-item:hover .comment-delete-btn {
  opacity: 1;
}

.comment-author {
  font-family: var(--font-body);
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-ink);
}

.comment-time {
  font-size: 0.8rem;
  color: var(--color-ink-light);
}

.comment-content {
  font-family: var(--font-body);
  font-size: 0.95rem;
  line-height: 1.6;
  color: var(--color-ink);
  white-space: pre-wrap;
}

.no-comments {
  text-align: center;
  color: var(--color-ink-light);
  font-style: italic;
  padding: 30px 0;
}

.comment-form {
  background: var(--color-paper);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.comment-form-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.comment-form-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin-right: 12px;
}

.comment-form-author {
  font-family: var(--font-body);
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--color-ink);
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--color-ink-light);
  border-radius: 4px;
  font-family: var(--font-body);
  font-size: 0.95rem;
  line-height: 1.5;
  resize: vertical;
  margin-bottom: 16px;

  &:focus {
    outline: none;
    border-color: var(--color-soft-purple);
  }
}

.comment-form-actions {
  display: flex;
  justify-content: flex-end;
}

.login-prompt {
  text-align: center;
  padding: 30px 0;
  color: var(--color-ink-light);

  p {
    margin-bottom: 16px;
  }
}

@media (max-width: 768px) {
  .post-comments {
    margin-top: 30px;
    padding-top: 20px;
  }

  .comments-title {
    font-size: 1.3rem;
    margin-bottom: 20px;
  }

  .comment-item {
    padding: 12px;
  }

  .comment-form {
    padding: 16px;
  }
}
</style>