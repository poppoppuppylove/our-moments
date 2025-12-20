<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="profile-page">
      <HandCard variant="paper" class="profile-card">
        <Pin variant="blue" position="top" />

        <h1 class="profile-card__title">个人资料</h1>

        <form @submit.prevent="handleUpdateProfile" class="profile-form">
          <div class="profile-form__avatar-section">
            <div class="avatar-wrapper">
              <img :src="form.avatar" :alt="form.nickname" class="profile-avatar" />
              <HandButton
                variant="outline"
                size="sm"
                @click="triggerAvatarUpload"
                class="avatar-upload-btn"
              >
                更换头像
              </HandButton>
              <input
                ref="avatarInput"
                type="file"
                accept="image/*"
                class="hidden-input"
                @change="onAvatarFileChange"
              />
            </div>
          </div>

          <HandInput
            v-model="form.nickname"
            label="昵称"
            placeholder="请输入昵称"
            :error="errors.nickname"
            required
          />

          <HandInput
            v-model="form.bio"
            label="个人简介"
            type="textarea"
            placeholder="介绍一下你自己..."
            :rows="3"
          />

          <div class="profile-form__actions">
            <HandButton
              type="button"
              variant="ghost"
              @click="goBack"
            >
              取消
            </HandButton>
            <HandButton
              type="submit"
              variant="primary"
              :loading="loading"
            >
              保存
            </HandButton>
          </div>
        </form>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api'
import HandCard from '@/components/base/HandCard.vue'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'
import type { User } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const avatarInput = ref<HTMLInputElement | null>(null)

const form = reactive({
  nickname: '',
  bio: '',
  avatar: ''
})

const errors = reactive({
  nickname: ''
})

// 初始化表单数据
onMounted(() => {
  if (userStore.user) {
    form.nickname = userStore.user.nickname || ''
    form.bio = userStore.user.bio || ''
    form.avatar = userStore.user.avatar || ''
  }
})

function validate(): boolean {
  let isValid = true
  errors.nickname = ''

  if (!form.nickname.trim()) {
    errors.nickname = '请输入昵称'
    isValid = false
  }

  return isValid
}

async function handleUpdateProfile() {
  if (!validate()) return

  loading.value = true

  try {
    // 更新用户信息
    const userData: Partial<User> = {
      nickname: form.nickname,
      bio: form.bio,
      avatar: form.avatar
    }

    const updatedUser = await userApi.updateUser(userStore.user!.userId, userData as User)

    // 更新用户存储中的信息
    userStore.setUser(updatedUser)

    alert('个人资料更新成功!')
    router.push('/')
  } catch (error: any) {
    console.error('更新个人资料失败:', error)
    alert('更新失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function triggerAvatarUpload() {
  avatarInput.value?.click()
}

function onAvatarFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input?.files?.[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    alert('图片大小不能超过 5MB')
    input.value = ''
    return
  }

  // 模拟上传头像 - 在实际应用中这里应该调用文件上传API
  const reader = new FileReader()
  reader.onload = () => {
    const dataUrl = reader.result as string
    form.avatar = dataUrl
    input.value = ''
  }
  reader.readAsDataURL(file)
}

function goBack() {
  router.back()
}
</script>

<style scoped lang="scss">
.profile-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.profile-card {
  width: 100%;
  max-width: 500px;
  padding: 40px 30px;
  text-align: center;

  &__title {
    font-family: var(--font-handwriting);
    font-size: 2rem;
    color: var(--color-soft-purple);
    margin-bottom: 30px;
  }
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;

  &__avatar-section {
    display: flex;
    justify-content: center;
    margin-bottom: 10px;
  }

  &__actions {
    display: flex;
    justify-content: space-between;
    gap: 16px;
    margin-top: 20px;
  }
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid var(--color-soft-purple);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  transform: translate(20%, 20%);
}

.hidden-input {
  display: none;
}

@media (max-width: 768px) {
  .profile-page {
    padding: 16px;
  }

  .profile-card {
    padding: 30px 20px;
  }

  .profile-form__actions {
    flex-direction: column;
  }
}
</style>