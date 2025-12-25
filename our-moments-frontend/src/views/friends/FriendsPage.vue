<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="friends-page">
      <HandCard variant="paper" class="friends-card">
        <Pin variant="blue" position="top" />

        <header class="friends-header">
          <h1 class="friends-title">我的好友</h1>
          <HandButton variant="ghost" size="sm" @click="goBack">
            返回首页
          </HandButton>
        </header>

        <!-- 添加好友区域 -->
        <div class="add-friend-section">
          <HandInput
            v-model="searchUsername"
            placeholder="输入用户名搜索..."
            class="search-input"
          />
          <HandButton
            variant="primary"
            size="sm"
            @click="searchUser"
            :loading="searching"
          >
            搜索
          </HandButton>
        </div>

        <!-- 搜索结果 -->
        <div v-if="searchResult" class="search-result">
          <div class="user-card">
            <img :src="searchResult.avatar" :alt="searchResult.nickname" class="user-avatar" />
            <div class="user-info">
              <span class="user-nickname">{{ searchResult.nickname }}</span>
              <span class="user-username">@{{ searchResult.username }}</span>
            </div>
            <HandButton
              v-if="!isFriend(searchResult.userId) && searchResult.userId !== userStore.user?.userId"
              variant="outline"
              size="sm"
              @click="sendFriendRequest(searchResult.userId)"
              :loading="sendingRequest"
            >
              添加好友
            </HandButton>
            <span v-else-if="searchResult.userId === userStore.user?.userId" class="self-tag">
              这是你自己
            </span>
            <span v-else class="friend-tag">
              已是好友
            </span>
          </div>
        </div>

        <!-- 好友请求列表 -->
        <div v-if="pendingRequests.length > 0" class="friends-section">
          <h2 class="section-title">待处理的好友请求</h2>
          <div class="friends-list">
            <div
              v-for="request in pendingRequests"
              :key="request.friendshipId"
              class="friend-item friend-item--pending"
            >
              <img :src="request.friend?.avatar" :alt="request.friend?.nickname" class="friend-avatar" />
              <div class="friend-info">
                <span class="friend-nickname">{{ request.friend?.nickname }}</span>
                <span class="friend-username">@{{ request.friend?.username }}</span>
              </div>
              <div class="friend-actions">
                <HandButton
                  variant="primary"
                  size="sm"
                  @click="acceptRequest(request.friendshipId)"
                >
                  接受
                </HandButton>
                <HandButton
                  variant="ghost"
                  size="sm"
                  @click="rejectRequest(request.friendshipId)"
                >
                  拒绝
                </HandButton>
              </div>
            </div>
          </div>
        </div>

        <!-- 好友列表 -->
        <div class="friends-section">
          <h2 class="section-title">我的好友 ({{ friends.length }})</h2>

          <HandLoading v-if="loading" text="加载中..." />

          <div v-else-if="friends.length === 0" class="empty-state">
            <p>还没有好友，快去添加吧~</p>
          </div>

          <div v-else class="friends-list">
            <div
              v-for="friendship in friends"
              :key="friendship.friendshipId"
              class="friend-item"
            >
              <img :src="friendship.friend?.avatar" :alt="friendship.friend?.nickname" class="friend-avatar" />
              <div class="friend-info">
                <span class="friend-nickname">{{ friendship.friend?.nickname }}</span>
                <span class="friend-username">@{{ friendship.friend?.username }}</span>
              </div>
              <HandButton
                variant="ghost"
                size="sm"
                class="delete-btn"
                @click="removeFriend(friendship.friend?.userId)"
              >
                删除
              </HandButton>
            </div>
          </div>
        </div>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { friendshipApi, userApi } from '@/api'
import type { Friendship } from '@/api'
import type { User } from '@/types'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'
import {toast} from "@/composables/useToast.ts";

interface FriendshipWithUser extends Friendship {
  friend?: User
}

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const searching = ref(false)
const sendingRequest = ref(false)
const searchUsername = ref('')
const searchResult = ref<User | null>(null)
const friendships = ref<FriendshipWithUser[]>([])

// 已接受的好友
const friends = computed(() => {
  return friendships.value.filter(f => f.status === 'ACCEPTED')
})

// 待处理的好友请求（别人发给我的）
const pendingRequests = computed(() => {
  return friendships.value.filter(
    f => f.status === 'PENDING' && f.friendId === userStore.user?.userId
  )
})

// 检查是否已是好友
function isFriend(userId: number): boolean {
  return friends.value.some(f => f.friend?.userId === userId)
}

// 加载好友列表
async function loadFriendships() {
  if (!userStore.user) return

  loading.value = true
  try {
    const response = await friendshipApi.getFriendships(userStore.user.userId)
    // 为每个好友关系加载用户信息
    const friendshipsWithUsers: FriendshipWithUser[] = []
    for (const friendship of response) {
      const friendId = friendship.userId === userStore.user.userId
        ? friendship.friendId
        : friendship.userId
      try {
        const friendUser = await userApi.getUser(friendId)
        friendshipsWithUsers.push({
          ...friendship,
          friend: friendUser
        })
      } catch {
        friendshipsWithUsers.push(friendship)
      }
    }
    friendships.value = friendshipsWithUsers
  } catch (error) {
    console.error('加载好友列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索用户
async function searchUser() {
  if (!searchUsername.value.trim()) return

  searching.value = true
  searchResult.value = null

  try {
    const user = await userApi.getUserByUsername(searchUsername.value.trim())
    searchResult.value = user
  } catch (error) {
    toast.info('未找到该用户')
  } finally {
    searching.value = false
  }
}

// 发送好友请求
async function sendFriendRequest(friendId: number) {
  if (!userStore.user) return

  sendingRequest.value = true
  try {
    await friendshipApi.sendFriendRequest(userStore.user.userId, friendId)
    toast.success('好友请求已发送')
    searchResult.value = null
    searchUsername.value = ''
    await loadFriendships()
  } catch (error) {
    toast.error('发送好友请求失败')
  } finally {
    sendingRequest.value = false
  }
}

// 接受好友请求
async function acceptRequest(friendshipId: number) {
  if (!userStore.user) return

  try {
    await friendshipApi.acceptFriendRequest(friendshipId, userStore.user.userId)
    await loadFriendships()
  } catch (error) {
    toast.error('操作失败')
  }
}

// 拒绝好友请求
async function rejectRequest(friendshipId: number) {
  if (!userStore.user) return

  try {
    await friendshipApi.rejectFriendRequest(friendshipId, userStore.user.userId)
    await loadFriendships()
  } catch (error) {
    toast.error('操作失败')
  }
}

// 删除好友
async function removeFriend(friendId: number | undefined) {
  if (!userStore.user || !friendId) return

  if (!confirm('确定要删除这位好友吗？')) return

  try {
    await friendshipApi.deleteFriendship(userStore.user.userId, friendId)
    await loadFriendships()
  } catch (error) {
    toast.error('删除失败')
  }
}

function goBack() {
  router.push('/')
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadFriendships()
})
</script>

<style scoped lang="scss">
.friends-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 40px 20px;
}

.friends-card {
  padding: 40px 30px;
}

.friends-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.friends-title {
  font-family: var(--font-handwriting);
  font-size: 2rem;
  color: var(--color-soft-purple);
  margin: 0;
}

.add-friend-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;

  .search-input {
    flex: 1;
  }
}

.search-result {
  margin-bottom: 24px;
  padding: 16px;
  background: rgba(168, 140, 190, 0.08);
  border-radius: 12px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.user-nickname {
  font-weight: 500;
  color: var(--color-ink);
}

.user-username {
  font-size: 0.85rem;
  color: var(--color-ink-light);
}

.self-tag,
.friend-tag {
  font-size: 0.85rem;
  color: var(--color-ink-light);
  padding: 4px 12px;
  background: var(--color-paper-dark);
  border-radius: 16px;
}

.friends-section {
  margin-top: 24px;
}

.section-title {
  font-family: var(--font-handwriting);
  font-size: 1.3rem;
  color: var(--color-ink);
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px dashed var(--color-ink-light);
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: var(--color-ink-light);
}

.friends-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.friend-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(168, 140, 190, 0.15);
  transition: all 0.2s;

  &:hover {
    background: rgba(255, 255, 255, 0.8);
    border-color: rgba(168, 140, 190, 0.3);
  }

  &--pending {
    background: rgba(254, 200, 216, 0.15);
    border-color: rgba(254, 200, 216, 0.3);
  }
}

.friend-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--color-paper);
}

.friend-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.friend-nickname {
  font-weight: 500;
  color: var(--color-ink);
}

.friend-username {
  font-size: 0.85rem;
  color: var(--color-ink-light);
}

.friend-actions {
  display: flex;
  gap: 8px;
}

.delete-btn {
  color: #c44;

  &:hover {
    color: #a33;
  }
}

@media (max-width: 768px) {
  .friends-page {
    padding: 20px 16px;
  }

  .friends-card {
    padding: 24px 20px;
  }

  .add-friend-section {
    flex-direction: column;
  }
}
</style>
