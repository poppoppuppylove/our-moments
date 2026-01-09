<template>
  <div class="user-list">
    <header class="page-header">
      <h1 class="page-title">用户管理</h1>
      <HandButton variant="primary" size="sm" @click="loadUsers">
        刷新
      </HandButton>
    </header>

    <!-- 加载状态 -->
    <HandLoading v-if="loading" text="正在加载用户列表..." />

    <!-- 用户列表 -->
    <div v-else class="users-table">
      <div class="table-header">
        <div class="table-cell">用户ID</div>
        <div class="table-cell">用户名</div>
        <div class="table-cell">昵称</div>
        <div class="table-cell">邮箱</div>
        <div class="table-cell">角色</div>
        <div class="table-cell">创建时间</div>
        <div class="table-cell">操作</div>
      </div>

      <div v-for="user in users" :key="user.userId" class="table-row">
        <div class="table-cell">{{ user.userId }}</div>
        <div class="table-cell">{{ user.username }}</div>
        <div class="table-cell">{{ user.nickname }}</div>
        <div class="table-cell">{{ user.email || '-' }}</div>
        <div class="table-cell">
          <span :class="['role-badge', `role-${user.role?.toLowerCase()}`]">
            {{ user.role || 'USER' }}
          </span>
        </div>
        <div class="table-cell">{{ formatDate(user.createTime) }}</div>
        <div class="table-cell actions-cell">
          <HandButton
            variant="ghost"
            size="sm"
            @click="editUserRole(user)"
            :disabled="userStore.user?.userId === user.userId"
          >
            修改角色
          </HandButton>
          <HandButton
            variant="danger"
            size="sm"
            @click="deleteUser(user.userId)"
            :disabled="userStore.user?.userId === user.userId"
            :loading="deletingId === user.userId"
          >
            删除
          </HandButton>
        </div>
      </div>
    </div>

    <!-- 角色编辑弹窗 -->
    <Transition name="modal">
      <div v-if="showRoleModal" class="modal-overlay" @click="closeRoleModal">
        <div class="modal-content" @click.stop>
          <h2 class="modal-title">修改用户角色</h2>
          <p class="modal-subtitle">用户: {{ selectedUser?.username }}</p>

          <div class="role-options">
            <label class="role-option">
              <input
                type="radio"
                v-model="newRole"
                value="USER"
              />
              <span class="role-label">普通用户</span>
            </label>
            <label class="role-option">
              <input
                type="radio"
                v-model="newRole"
                value="ADMIN"
              />
              <span class="role-label">管理员</span>
            </label>
          </div>

          <div class="modal-actions">
            <HandButton variant="ghost" @click="closeRoleModal">
              取消
            </HandButton>
            <HandButton variant="primary" @click="saveUserRole">
              保存
            </HandButton>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { toast } from '@/composables/useToast'
import { adminApi } from '@/api'
import type { User } from '@/types'
import HandButton from '@/components/base/HandButton.vue'
import HandLoading from '@/components/common/HandLoading.vue'

const userStore = useUserStore()
const loading = ref(false)
const deletingId = ref<number | null>(null)
const users = ref<User[]>([])

// 角色编辑弹窗
const showRoleModal = ref(false)
const selectedUser = ref<User | null>(null)
const newRole = ref('USER')

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const data = await adminApi.getAllUsers()
    users.value = data
  } catch (err) {
    console.error('Failed to load users:', err)
    toast.error('加载用户列表失败')
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

function editUserRole(user: User) {
  selectedUser.value = user
  newRole.value = user.role || 'USER'
  showRoleModal.value = true
}

function closeRoleModal() {
  showRoleModal.value = false
  selectedUser.value = null
  newRole.value = 'USER'
}

async function saveUserRole() {
  if (!selectedUser.value) return

  try {
    await adminApi.updateUserRole(selectedUser.value.userId!, newRole.value)
    toast.success('用户角色已更新')

    // 更新本地数据
    const userIndex = users.value.findIndex(u => u.userId === selectedUser.value?.userId)
    if (userIndex !== -1) {
      users.value[userIndex].role = newRole.value
    }

    closeRoleModal()
  } catch (err) {
    console.error('Failed to update user role:', err)
    toast.error('更新用户角色失败')
  }
}

async function deleteUser(userId: number) {
  if (!confirm('确定要删除这个用户吗？这将删除该用户的所有数据！')) return

  deletingId.value = userId
  try {
    await adminApi.deleteUser(userId)
    users.value = users.value.filter(user => user.userId !== userId)
    toast.success('用户已删除')
  } catch (err) {
    console.error('Failed to delete user:', err)
    toast.error('删除用户失败')
  } finally {
    deletingId.value = null
  }
}
</script>

<style scoped lang="scss">
.user-list {
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

.users-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.table-header {
  display: grid;
  grid-template-columns: 80px 120px 120px 150px 100px 150px 180px;
  background: var(--color-paper);
  padding: 12px 16px;
  font-weight: 500;
  border-bottom: 1px solid var(--color-ink-lighter);
}

.table-row {
  display: grid;
  grid-template-columns: 80px 120px 120px 150px 100px 150px 180px;
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
  gap: 8px;
  justify-content: flex-end;
}

.role-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;

  &.role-user {
    background: var(--color-blush);
    color: var(--color-ink);
  }

  &.role-admin {
    background: var(--color-soft-purple);
    color: white;
  }
}

// 角色编辑弹窗
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 16px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.modal-title {
  font-family: var(--font-handwriting);
  font-size: 1.5rem;
  color: var(--color-soft-purple);
  margin: 0 0 8px 0;
}

.modal-subtitle {
  font-family: var(--font-body);
  font-size: 0.9rem;
  color: var(--color-ink-light);
  margin: 0 0 20px 0;
}

.role-options {
  margin: 20px 0;
}

.role-option {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  cursor: pointer;

  input {
    margin-right: 10px;
  }

  .role-label {
    font-family: var(--font-body);
    font-size: 1rem;
    color: var(--color-ink);
  }
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

// 模态框动画
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;

  .modal-content {
    transform: scale(0.95);
  }
}

.modal-enter-active .modal-content,
.modal-leave-active .modal-content {
  transition: transform 0.3s ease;
}

.modal-enter-from .modal-content,
.modal-leave-to .modal-content {
  transform: scale(0.95);
}

// 响应式
@media (max-width: 1200px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 100px 100px 120px 80px 120px 160px;
  }
}

@media (max-width: 992px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 80px 80px 100px 70px 100px 140px;
    font-size: 0.85rem;
  }
}

@media (max-width: 768px) {
  .users-table {
    overflow-x: auto;
  }

  .table-header,
  .table-row {
    grid-template-columns: 60px 100px 100px 120px 80px 120px 140px;
    min-width: 700px;
  }

  .modal-content {
    margin: 20px;
    padding: 20px;
  }
}
</style>