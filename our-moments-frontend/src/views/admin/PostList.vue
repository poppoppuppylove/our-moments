<template>
  <div class="post-list">
    <header class="post-list__header">
      <h1 class="post-list__title">📝 文章管理</h1>
      <router-link to="/admin/posts/new">
        <HandButton variant="primary" size="sm">
          + 新建文章
        </HandButton>
      </router-link>
    </header>

    <!-- 搜索和筛选 -->
    <div class="post-list__filters">
      <HandInput
        v-model="searchQuery"
        placeholder="搜索文章标题..."
        class="search-input"
      />
      <select v-model="statusFilter" class="status-filter">
        <option value="">全部状态</option>
        <option value="1">已发布</option>
        <option value="0">草稿</option>
      </select>
    </div>

    <!-- 加载状态 -->
    <HandLoading v-if="loading" text="正在加载文章列表..." />

    <!-- 错误状态 -->
    <HandError
      v-else-if="error"
      :message="error"
      @retry="fetchPosts"
    />

    <!-- 文章列表 -->
    <div v-else class="post-list__content">
      <div v-if="filteredPosts.length === 0" class="post-list__empty">
        <p>暂无文章</p>
        <router-link to="/admin/posts/new">
          <HandButton variant="outline" size="sm">创建第一篇文章</HandButton>
        </router-link>
      </div>

      <div v-else class="post-list__table">
        <div class="table-header">
          <div class="col-title">标题</div>
          <div class="col-status">状态</div>
          <div class="col-date">创建时间</div>
          <div class="col-actions">操作</div>
        </div>

        <div
          v-for="post in filteredPosts"
          :key="post.postId"
          class="table-row"
        >
          <div class="col-title">
            <router-link :to="`/post/${post.postId}`" target="_blank" class="post-title">
              {{ post.title }}
            </router-link>
            <div class="post-tags">
              <span v-for="tag in post.tagList" :key="tag.tagId" class="tag">
                #{{ tag.name }}
              </span>
            </div>
          </div>
          <div class="col-status">
            <span :class="['status-badge', post.status === 1 ? 'published' : 'draft']">
              {{ post.status === 1 ? '已发布' : '草稿' }}
            </span>
          </div>
          <div class="col-date">
            {{ formatDate(post.createTime) }}
          </div>
          <div class="col-actions">
            <router-link :to="`/admin/posts/${post.postId}/edit`">
              <HandButton variant="ghost" size="sm">编辑</HandButton>
            </router-link>
            <HandButton
              variant="ghost"
              size="sm"
              class="delete-btn"
              @click="confirmDelete(post)"
            >
              删除
            </HandButton>
          </div>
        </div>
      </div>
    </div>

    <!-- 删除确认弹窗 -->
    <div v-if="showDeleteModal" class="delete-modal">
      <div class="delete-modal__overlay" @click="showDeleteModal = false"></div>
      <div class="delete-modal__content">
        <h3>确认删除</h3>
        <p>确定要删除文章「{{ postToDelete?.title }}」吗？此操作不可恢复。</p>
        <div class="delete-modal__actions">
          <HandButton variant="ghost" size="sm" @click="showDeleteModal = false">
            取消
          </HandButton>
          <HandButton variant="primary" size="sm" @click="handleDelete">
            确认删除
          </HandButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { BlogPost } from '@/types'
import { mockPosts } from '@/utils/mock'
import { postApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import HandError from '@/components/common/HandError.vue'

const posts = ref<BlogPost[]>([])
const loading = ref(false)
const error = ref('')
const searchQuery = ref('')
const statusFilter = ref('')

const showDeleteModal = ref(false)
const postToDelete = ref<BlogPost | null>(null)

// 过滤后的文章列表
const filteredPosts = computed(() => {
  let result = posts.value

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(post =>
      post.title.toLowerCase().includes(query)
    )
  }

  // 状态过滤
  if (statusFilter.value) {
    result = result.filter(post =>
      post.status === Number(statusFilter.value)
    )
  }

  return result
})

function formatDate(dateString: string): string {
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

async function fetchPosts() {
  loading.value = true
  error.value = ''

  try {
    // 尝试调用后端 API
    const response = await postApi.getPosts()
    posts.value = response
  } catch (err: any) {
    // 如果 API 调用失败，使用 Mock 数据
    console.warn('API call failed, using mock data:', err.message)
    posts.value = mockPosts
  } finally {
    loading.value = false
  }
}

function confirmDelete(post: BlogPost) {
  postToDelete.value = post
  showDeleteModal.value = true
}

async function handleDelete() {
  if (!postToDelete.value) return

  try {
    // 尝试调用后端 API
    await postApi.deletePost(postToDelete.value.postId)
  } catch (err) {
    // 如果 API 调用失败，本地删除 (Mock 模式)
    console.warn('API call failed, deleting locally')
  }

  // 从列表中移除
  posts.value = posts.value.filter(p => p.postId !== postToDelete.value?.postId)
  showDeleteModal.value = false
  postToDelete.value = null
}

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped lang="scss">
.post-list {
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
  }

  &__title {
    font-family: var(--font-handwriting);
    font-size: 1.8rem;
    color: var(--color-ink);
    margin: 0;
  }

  &__filters {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;

    .search-input {
      flex: 1;
      max-width: 300px;
    }

    .status-filter {
      padding: 8px 12px;
      border: 1px solid var(--color-ink-light);
      border-radius: 4px;
      font-family: var(--font-body);
      font-size: 0.9rem;
      background: white;
      cursor: pointer;

      &:focus {
        outline: none;
        border-color: var(--color-soft-purple);
      }
    }
  }

  &__empty {
    text-align: center;
    padding: 60px 20px;
    color: var(--color-ink-light);

    p {
      font-family: var(--font-body);
      font-size: 1rem;
      margin-bottom: 16px;
    }
  }

  &__table {
    background: white;
    border-radius: 8px;
    box-shadow: 2px 4px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;

    .table-header,
    .table-row {
      display: grid;
      grid-template-columns: 1fr 100px 120px 150px;
      gap: 16px;
      padding: 16px 20px;
      align-items: center;
    }

    .table-header {
      background: var(--color-paper);
      font-family: var(--font-body);
      font-size: 0.85rem;
      color: var(--color-ink-light);
      font-weight: 500;
    }

    .table-row {
      border-bottom: 1px dashed var(--color-ink-light);

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: rgba(0, 0, 0, 0.02);
      }
    }

    .col-title {
      .post-title {
        font-family: var(--font-body);
        font-size: 0.95rem;
        color: var(--color-ink);
        text-decoration: none;
        display: block;
        margin-bottom: 4px;

        &:hover {
          color: var(--color-soft-purple);
        }
      }

      .post-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;

        .tag {
          font-size: 0.75rem;
          color: var(--color-ink-light);
        }
      }
    }

    .col-status {
      .status-badge {
        display: inline-block;
        padding: 4px 10px;
        border-radius: 12px;
        font-size: 0.75rem;
        font-family: var(--font-body);

        &.published {
          background: rgba(76, 175, 80, 0.1);
          color: #2e7d32;
        }

        &.draft {
          background: rgba(255, 152, 0, 0.1);
          color: #e65100;
        }
      }
    }

    .col-date {
      font-family: var(--font-body);
      font-size: 0.85rem;
      color: var(--color-ink-light);
    }

    .col-actions {
      display: flex;
      gap: 8px;

      .delete-btn {
        color: #c00;

        &:hover {
          background: rgba(204, 0, 0, 0.1);
        }
      }
    }
  }
}

// 删除确认弹窗
.delete-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;

  &__overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
  }

  &__content {
    position: relative;
    background: white;
    padding: 32px;
    border-radius: 8px;
    max-width: 400px;
    text-align: center;

    h3 {
      font-family: var(--font-handwriting);
      font-size: 1.3rem;
      color: var(--color-ink);
      margin-bottom: 12px;
    }

    p {
      font-family: var(--font-body);
      font-size: 0.95rem;
      color: var(--color-ink-light);
      line-height: 1.6;
      margin-bottom: 24px;
    }
  }

  &__actions {
    display: flex;
    justify-content: center;
    gap: 12px;
  }
}

// 响应式
@media (max-width: 768px) {
  .post-list {
    &__filters {
      flex-direction: column;

      .search-input {
        max-width: none;
      }
    }

    &__table {
      .table-header {
        display: none;
      }

      .table-row {
        grid-template-columns: 1fr;
        gap: 8px;
      }

      .col-status,
      .col-date {
        font-size: 0.8rem;
      }

      .col-actions {
        justify-content: flex-start;
      }
    }
  }
}
</style>
