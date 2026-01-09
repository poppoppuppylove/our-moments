<template>
  <div class="friendship-list">
    <header class="page-header">
      <h1 class="page-title">好友管理</h1>
      <div class="header-actions">
        <HandButton variant="primary" size="sm" @click="showCreateFriendshipModal">
          新增好友关系
        </HandButton>
        <HandButton variant="ghost" size="sm" @click="loadFriendships">
          刷新
        </HandButton>
      </div>
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
            variant="ghost"
            size="sm"
            @click="editFriendshipStatus(friendship)"
          >
            修改状态
          </HandButton>
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

    <!-- 新增/编辑好友关系弹窗 -->
    <Transition name="modal">
      <div v-if="showFriendshipModal" class="modal-overlay" @click="closeFriendshipModal">
        <div class="modal-content" @click.stop>
          <h2 class="modal-title">{{ editingFriendship ? '修改好友关系状态' : '新增好友关系' }}</h2>
          <p v-if="editingFriendship" class="modal-subtitle">
            关系ID: {{ editingFriendship.friendshipId }}
          </p>

          <div class="form-group" v-if="!editingFriendship">
            <label class="form-label">用户ID</label>
            <HandInput v-model="friendshipForm.userId" type="number" placeholder="请输入用户ID" />
          </div>

          <div class="form-group" v-if="!editingFriendship">
            <label class="form-label">好友ID</label>
            <HandInput v-model="friendshipForm.friendId" type="number" placeholder="请输入好友ID" />
          </div>

          <div class="form-group">
            <label class="form-label">状态</label>
            <HandSelect
              v-model="friendshipForm.status"
              :options="[
                { value: 'PENDING', label: '待处理' },
                { value: 'ACCEPTED', label: '已接受' },
                { value: 'REJECTED', label: '已拒绝' }
              ]"
              placeholder="请选择状态"
            />
          </div>

          <div class="modal-actions">
            <HandButton variant="ghost" @click="closeFriendshipModal">
              取消
            </HandButton>
            <HandButton variant="primary" @click="saveFriendship" :loading="savingFriendship">
              保存
            </HandButton>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { toast } from '@/composables/useToast'
import { adminApi } from '@/api'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandSelect from '@/components/base/HandSelect.vue'
import HandLoading from '@/components/common/HandLoading.vue'

const loading = ref(false)
const deletingId = ref<number | null>(null)
const savingFriendship = ref(false)
const friendships = ref<any[]>([])

// 好友关系编辑弹窗
const showFriendshipModal = ref(false)
const editingFriendship = ref<any | null>(null)
const friendshipForm = reactive({
  userId: 0,
  friendId: 0,
  status: 'PENDING'
})

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

function showCreateFriendshipModal() {
  editingFriendship.value = null
  friendshipForm.userId = 0
  friendshipForm.friendId = 0
  friendshipForm.status = 'PENDING'
  showFriendshipModal.value = true
}

function editFriendshipStatus(friendship: any) {
  editingFriendship.value = friendship
  friendshipForm.userId = friendship.userId
  friendshipForm.friendId = friendship.friendId
  friendshipForm.status = friendship.status || 'PENDING'
  showFriendshipModal.value = true
}

function closeFriendshipModal() {
  showFriendshipModal.value = false
  editingFriendship.value = null
}

async function saveFriendship() {
  savingFriendship.value = true
  try {
    let savedFriendship: any

    if (editingFriendship.value) {
      // 修改好友关系状态
      savedFriendship = await adminApi.updateFriendshipStatus(
        editingFriendship.value.friendshipId,
        friendshipForm.status
      )
      toast.success('好友关系状态已更新')
    } else {
      // 创建新的好友关系
      savedFriendship = await adminApi.createFriendship({
        userId: friendshipForm.userId,
        friendId: friendshipForm.friendId,
        status: friendshipForm.status
      })
      toast.success('好友关系已创建')
    }

    // 更新本地数据
    if (editingFriendship.value) {
      const index = friendships.value.findIndex(f => f.friendshipId === editingFriendship.value?.friendshipId)
      if (index !== -1) {
        friendships.value[index] = savedFriendship
      }
    } else {
      friendships.value.push(savedFriendship)
    }

    closeFriendshipModal()
  } catch (err) {
    console.error('Failed to save friendship:', err)
    toast.error(editingFriendship.value ? '更新好友关系状态失败' : '创建好友关系失败')
  } finally {
    savingFriendship.value = false
  }
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
  grid-template-columns: 80px 80px 80px 100px 150px 150px 150px;
  background: var(--color-paper);
  padding: 12px 16px;
  font-weight: 500;
  border-bottom: 1px solid var(--color-ink-lighter);
}

.table-row {
  display: grid;
  grid-template-columns: 80px 80px 80px 100px 150px 150px 150px;
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

.form-group {
  margin-bottom: 20px;
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
    grid-template-columns: 60px 60px 60px 80px 120px 120px 120px;
    font-size: 0.85rem;
  }
}

@media (max-width: 992px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 60px 60px 80px 100px 100px 120px;
  }
}

@media (max-width: 768px) {
  .friendships-table {
    overflow-x: auto;
  }

  .table-header,
  .table-row {
    grid-template-columns: 60px 60px 60px 80px 120px 120px 120px;
    min-width: 620px;
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