<template>
  <PaperTexture variant="light" class="min-h-screen chat-wrapper">
    <div class="chat-page">
      <!-- 聊天头部 -->
      <HandCard variant="paper" class="chat-header">
        <div class="header-content">
          <HandButton variant="ghost" size="sm" @click="goBack" class="back-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="19" y1="12" x2="5" y2="12"></line>
              <polyline points="12 19 5 12 12 5"></polyline>
            </svg>
          </HandButton>
          <div v-if="currentFriend" class="friend-info">
            <img :src="currentFriend.avatar" :alt="currentFriend.nickname" class="friend-avatar" />
            <div class="friend-details">
              <h2 class="friend-name">{{ currentFriend.nickname }}</h2>
              <span class="friend-username">@{{ currentFriend.username }}</span>
            </div>
          </div>
          <div v-else class="no-friend">
            <h2>私信聊天</h2>
          </div>
        </div>
      </HandCard>

      <!-- 聊天内容区域 -->
      <div class="chat-container" v-if="currentFriend">
        <!-- 消息列表 -->
        <div class="messages-container" ref="messagesContainer">
          <div v-if="loadingMessages" class="loading-messages">
            <HandLoading text="加载消息中..." />
          </div>
          <div v-else-if="messages.length === 0" class="no-messages">
            <p>还没有消息，开始聊天吧~</p>
          </div>
          <div v-else class="messages-list">
            <div
              v-for="message in messages"
              :key="message.messageId"
              :class="['message-item', message.senderId === userStore.user?.userId ? 'sent' : 'received']"
            >
              <div class="message-content">
                <div class="message-text">{{ message.content }}</div>
                <div class="message-time">
                  {{ formatTime(message.createTime) }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <HandCard variant="paper" class="input-container">
          <div class="input-wrapper">
            <HandInput
              v-model="newMessage"
              placeholder="输入消息..."
              class="message-input"
              @keyup.enter="sendMessage"
            />
            <HandButton
              variant="primary"
              @click="sendMessage"
              :disabled="!newMessage.trim() || !isConnected"
              class="send-button"
            >
              发送
            </HandButton>
          </div>
          <div v-if="!isConnected" class="connection-status">
            正在连接到聊天服务器...
          </div>
        </HandCard>
      </div>

      <!-- 选择好友提示 -->
      <div v-else class="select-friend-prompt">
        <HandCard variant="paper" class="prompt-card">
          <div class="prompt-content">
            <i class="icon-chat icon-large"></i>
            <h3>选择好友开始聊天</h3>
            <p>请从好友列表中选择一位好友开始私信聊天</p>
            <HandButton variant="primary" @click="goToFriends">
              查看好友列表
            </HandButton>
          </div>
        </HandCard>
      </div>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useChatWebSocket } from '@/composables/useChatWebSocket'
import { messageApi, userApi } from '@/api'
import type { Message, User } from '@/types'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandCard from '@/components/base/HandCard.vue'
import HandLoading from '@/components/common/HandLoading.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import { toast } from '@/composables/useToast'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// WebSocket连接
const { isConnected, sendMessage: sendWebSocketMessage } = useChatWebSocket()

// 状态
const currentFriend = ref<User | null>(null)
const messages = ref<Message[]>([])
const newMessage = ref('')
const loadingMessages = ref(false)
const messagesContainer = ref<HTMLElement | null>(null)

// 计算属性
const friendId = computed(() => {
  const id = route.params.friendId
  return id ? Number(id) : null
})

// 格式化时间（显示完整的年月日时分秒）
function formatTime(timeString: string): string {
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 滚动到最新消息
function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 加载聊天历史
async function loadChatHistory() {
  if (!userStore.user || !friendId.value) return

  loadingMessages.value = true
  try {
    const history = await messageApi.getChatHistory(friendId.value)
    messages.value = history
    scrollToBottom()
  } catch (error) {
    toast.error('加载聊天记录失败')
  } finally {
    loadingMessages.value = false
  }
}

// 获取好友信息
async function loadFriendInfo() {
  if (!friendId.value) {
    currentFriend.value = null
    return
  }

  try {
    const friend = await userApi.getUser(friendId.value)
    currentFriend.value = friend
  } catch (error) {
    toast.error('获取好友信息失败')
  }
}

// 发送消息
async function sendMessage() {
  if (!newMessage.value.trim() || !userStore.user || !friendId.value) return

  try {
    // 通过WebSocket发送消息
    sendWebSocketMessage({
      senderId: userStore.user.userId,
      receiverId: friendId.value,
      content: newMessage.value,
      timestamp: new Date().toISOString()
    })

    // 同时保存到数据库
    const message = await messageApi.sendMessage(
      friendId.value,
      newMessage.value
    )

    messages.value.push(message)
    newMessage.value = ''
    scrollToBottom()
  } catch (error) {
    toast.error('发送消息失败')
  }
}

// 返回上一页
function goBack() {
  router.back()
}

// 跳转到好友列表
function goToFriends() {
  router.push('/friends')
}

// 监听路由变化
watch(
  () => route.params.friendId,
  async (newFriendId) => {
    if (newFriendId) {
      await loadFriendInfo()
      await loadChatHistory()
    } else {
      currentFriend.value = null
      messages.value = []
    }
  }
)

// 监听消息变化，自动滚动到底部
watch(
  () => messages.value.length,
  () => {
    scrollToBottom()
  }
)

onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }

  if (friendId.value) {
    loadFriendInfo()
    loadChatHistory()
  }
})
</script>

<style scoped lang="scss">
.chat-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  position: relative;
  z-index: 1001; /* 确保聊天页面在其他元素之上 */
}

.chat-header {
  padding: 16px 20px;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.back-button {
  min-width: 40px;
}

.friend-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.friend-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--color-paper-dark);
}

.friend-details {
  display: flex;
  flex-direction: column;
}

.friend-name {
  font-weight: 600;
  color: var(--color-ink);
  margin: 0;
  font-size: 1.2rem;
}

.friend-username {
  color: var(--color-ink-light);
  font-size: 0.9rem;
}

.no-friend h2 {
  margin: 0;
  color: var(--color-ink);
  font-family: var(--font-handwriting);
}

.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0; /* 关键：允许flex子项收缩 */
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 16px;
  border: 1px solid rgba(168, 140, 190, 0.2);
  min-height: 0; /* 关键：允许flex子项收缩 */
}

.loading-messages,
.no-messages {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: var(--color-ink-light);
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-item {
  display: flex;
  max-width: 80%;

  &.sent {
    align-self: flex-end;
  }

  &.received {
    align-self: flex-start;
  }
}

.message-content {
  padding: 12px 16px;
  border-radius: 18px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.message-item.sent .message-content {
  background: var(--color-soft-purple);
  color: white;
  border-bottom-right-radius: 4px;
}

.message-item.received .message-content {
  background: var(--color-paper-dark);
  color: var(--color-ink);
  border-bottom-left-radius: 4px;
}

.message-text {
  word-wrap: break-word;
  line-height: 1.4;
}

.message-time {
  font-size: 0.75rem;
  margin-top: 4px;
  opacity: 0.8;
}

.message-item.sent .message-time {
  text-align: right;
  color: rgba(255, 255, 255, 0.9);
}

.message-item.received .message-time {
  text-align: left;
  color: var(--color-ink-light);
}

.input-container {
  padding: 16px 20px;
  flex-shrink: 0;
  margin-top: 16px;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
}

.send-button {
  height: 44px;
}

.connection-status {
  margin-top: 8px;
  font-size: 0.85rem;
  color: var(--color-ink-light);
  text-align: center;
}

.select-friend-prompt {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.prompt-card {
  padding: 40px 30px;
  text-align: center;
  max-width: 400px;
}

.prompt-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.icon-large {
  font-size: 3rem;
  color: var(--color-soft-purple);
}

.prompt-card h3 {
  margin: 0;
  color: var(--color-ink);
  font-family: var(--font-handwriting);
  font-size: 1.5rem;
}

.prompt-card p {
  margin: 0;
  color: var(--color-ink-light);
}

@media (max-width: 768px) {
  .chat-page {
    padding: 12px;
    height: 100vh;
    height: 100dvh; /* 使用动态视口高度，处理移动端地址栏 */
  }

  .chat-header {
    padding: 12px 16px;
    margin-bottom: 12px;
  }

  .friend-info {
    gap: 8px;
  }

  .friend-avatar {
    width: 40px;
    height: 40px;
  }

  .friend-name {
    font-size: 1.1rem;
  }

  .messages-container {
    padding: 12px;
  }

  .message-item {
    max-width: 90%;
  }

  .input-container {
    padding: 12px 16px;
    margin-top: 12px;
  }

  .input-wrapper {
    gap: 8px;
  }
}
</style>