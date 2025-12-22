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
              <div v-if="uploadingAvatar" class="avatar-uploading">
                <span class="uploading-spinner"></span>
                <span>上传中...</span>
              </div>
              <HandButton
                v-else
                type="button"
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
import { toast } from '@/composables/useToast'
import { userApi, fileApi } from '@/api'
import HandCard from '@/components/base/HandCard.vue'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'
import type { User } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const uploadingAvatar = ref(false)
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

    toast.success('个人资料更新成功!')
    router.push('/')
  } catch (error: any) {
    console.error('更新个人资料失败:', error)
    toast.error('更新失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function triggerAvatarUpload() {
  avatarInput.value?.click()
}

async function onAvatarFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  const file = input?.files?.[0]
  if (!file) return

  if (file.size > 5 * 1024 * 1024) {
    toast.error('图片大小不能超过 5MB')
    input.value = ''
    return
  }

  uploadingAvatar.value = true

  try {
    // 上传头像到 OSS
    const response = await fileApi.uploadAvatar(file)
    if (response.success && response.url) {
      form.avatar = response.url
    } else {
      toast.error('头像上传失败')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    toast.error('头像上传失败，请稍后重试')
  } finally {
    uploadingAvatar.value = false
    input.value = ''
  }
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

.avatar-uploading {
  position: absolute;
  bottom: 0;
  right: 0;
  transform: translate(20%, 20%);
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.9);
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 0.8rem;
  color: var(--color-soft-purple);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.uploading-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid var(--color-soft-purple);
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
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