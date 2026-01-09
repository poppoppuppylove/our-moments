<template>
  <div class="friendship-list">
    <header class="page-header">
      <h1 class="page-title">好友管理</h1>
      <HandButton variant="primary" size="sm" @click="loadFriendships">
        刷新
      </HandButton>
    </header>

    <!-- 加载状态 -->
    <HandLoading v-if="loading" text="正在加载好友关系列表..." />

    <!-- 好友关系列表 -->
    <div v-else-if="friendships.length > 0" class="friendships-table">
      <div class="table-header">
        <div class="table-cell">关系ID</div>
        <div class="table-cell">用户ID</div>
        <div class="table-cell">好友ID</div>
        <div class="table-cell">状态</div>
        <div class="table-cell">创建时间</div>
        <div class="table-cell">更新时间</div>
        <div class="table-cell">操作</div>
      </div>

      <div v-for="friendship in friendships" :key="friendship.friendshipId" class="table-row">
        <div class="table-cell">{{ friendship.friendshipId }}</div>
        <div class="table-cell">{{ friendship.userId }}</div>
        <div class="table-cell">{{ friendship.friendId }}</div>
        <div class="table-cell">
          <span :class="['status-badge', `status-${friendship.status?.toLowerCase()}`]">
            {{ getStatusText(friendship.status) }}
          </span>
        </div>
        <div class="table-cell">{{ formatDate(friendship.createTime) }}</div>
        <div class="table-cell">{{ formatDate(friendship.updateTime) }}</div>
        <div class="table-cell actions-cell">
          <HandButton
            variant="danger"
            size="sm"
            @click="deleteFriendship(friendship.friendshipId)"
            :loading="deletingId === friendship.friendshipId"
          >
            删除关系
          </HandButton>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <div class="empty-icon">🤝</div>
      <p class="empty-text">暂无好友关系</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { toast } from '@/composables/useToast'
import { adminApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandLoading from '@/components/common/HandLoading.vue'

const loading = ref(false)
const deletingId = ref<number | null>(null)
const friendships = ref<any[]>([])

onMounted(() => {
  loadFriendships()
})

async function loadFriendships() {
  loading.value = true
  try {
    const data = await adminApi.getAllFriendships()
    friendships.value = data
  } catch (err) {
    console.error('Failed to load friendships:', err)
    toast.error('加载好友关系列表失败')
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

function getStatusText(status: string): string {
  const statusMap: Record<string, string> = {
    'PENDING': '待处理',
    'ACCEPTED': '已接受',
    'REJECTED': '已拒绝'
  }
  return statusMap[status] || status
}

async function deleteFriendship(friendshipId: number) {
  if (!confirm('确定要删除这条好友关系吗？')) return

  deletingId.value = friendshipId
  try {
    await adminApi.deleteFriendship(friendshipId)
    friendships.value = friendships.value.filter(f => f.friendshipId !== friendshipId)
    toast.success('好友关系已删除')
  } catch (err) {
    console.error('Failed to delete friendship:', err)
    toast.error('删除好友关系失败')
  } finally {
    deletingId.value = null
  }
}
</script>

<style scoped lang="scss">
.friendship-list {
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

.friendships-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.table-header {
  display: grid;
  grid-template-columns: 80px 80px 80px 100px 150px 150px 120px;
  background: var(--color-paper);
  padding: 12px 16px;
  font-weight: 500;
  border-bottom: 1px solid var(--color-ink-lighter);
}

.table-row {
  display: grid;
  grid-template-columns: 80px 80px 80px 100px 150px 150px 120px;
  padding: 12px 16px;
  border-bottom: 1px solid var(--color-ink-lighter);
  font-size: 0.9rem;

  &:hover {
    background: var(--color-paper);
  }

  &:last-child {
    border-bottom: none;
  }
}

.table-cell {
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.actions-cell {
  display: flex;
  justify-content: flex-end;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;

  &.status-pending {
    background: var(--color-blush);
    color: var(--color-ink);
  }

  &.status-accepted {
    background: var(--color-soft-purple);
    color: white;
  }

  &.status-rejected {
    background: #c44;
    color: white;
  }
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
@media (max-width: 1200px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 60px 60px 80px 120px 120px 100px;
    font-size: 0.85rem;
  }
}

@media (max-width: 992px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 60px 60px 80px 100px 100px 100px;
  }
}

@media (max-width: 768px) {
  .friendships-table {
    overflow-x: auto;
  }

  .table-header,
  .table-row {
    grid-template-columns: 60px 60px 60px 80px 120px 120px 100px;
    min-width: 600px;
  }

  .friendship-list {
    .page-header {
      flex-direction: column;
      gap: 16px;
      align-items: flex-start;
    }

    .page-title {
      font-size: 1.6rem;
    }
  }
}
</style>