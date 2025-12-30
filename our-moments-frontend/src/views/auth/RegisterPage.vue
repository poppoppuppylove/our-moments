<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="register-page">
      <HandCard variant="paper" class="register-card">
        <Pin variant="blue" position="top" />

        <h1 class="register-card__title">创建账号</h1>
        <p class="register-card__subtitle">加入我们，开始记录你的美好生活</p>

        <form @submit.prevent="handleRegister" class="register-form">
          <HandInput
            v-model="form.username"
            label="登录账号"
            placeholder="请输入登录账号（英文或数字）"
            :error="errors.username"
            required
          />

          <HandInput
            v-model="form.nickname"
            label="显示昵称"
            placeholder="请输入昵称（其他用户看到的名字）"
            :error="errors.nickname"
            required
          />

          <HandInput
            v-model="form.email"
            type="email"
            label="邮箱地址"
            placeholder="请输入邮箱（用于接收好友动态通知）"
            :error="errors.email"
          />

          <HandInput
            v-model="form.password"
            type="password"
            label="密码"
            placeholder="请输入密码"
            :error="errors.password"
            required
          />

          <HandInput
            v-model="form.confirmPassword"
            type="password"
            label="确认密码"
            placeholder="请再次输入密码"
            :error="errors.confirmPassword"
            required
          />

          <div class="register-form__actions">
            <HandButton
              type="submit"
              variant="primary"
              size="lg"
              :loading="loading"
              class="register-form__submit"
            >
              注册
            </HandButton>
          </div>
        </form>

        <div class="register-card__footer">
          <p>已有账号？<router-link to="/login">立即登录</router-link></p>
        </div>
      </HandCard>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import HandCard from '@/components/base/HandCard.vue'
import HandButton from '@/components/base/HandButton.vue'
import HandInput from '@/components/base/HandInput.vue'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import Pin from '@/components/decorative/Pin.vue'
import { useUserStore } from '@/store/user'
import { userApi, authApi } from '@/api'
import type { User } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

const form = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const errors = reactive({
  username: '',
  nickname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

function validate(): boolean {
  let isValid = true
  errors.username = ''
  errors.nickname = ''
  errors.email = ''
  errors.password = ''
  errors.confirmPassword = ''

  if (!form.username.trim()) {
    errors.username = '请输入登录账号'
    isValid = false
  } else if (form.username.length < 3) {
    errors.username = '登录账号长度不能少于3位'
    isValid = false
  } else if (!/^[a-zA-Z0-9_]+$/.test(form.username)) {
    errors.username = '登录账号只能包含英文、数字和下划线'
    isValid = false
  }

  if (!form.nickname.trim()) {
    errors.nickname = '请输入昵称'
    isValid = false
  }

  if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = '请输入有效的邮箱地址'
    isValid = false
  }

  if (!form.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = '密码长度不能少于6位'
    isValid = false
  }

  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = '两次输入的密码不一致'
    isValid = false
  }

  return isValid
}

async function handleRegister() {
  if (!validate()) return

  loading.value = true

  try {
    // 注册用户
    const userData: Partial<User> = {
      username: form.username,
      nickname: form.nickname,
      email: form.email || undefined,
      password: form.password,
      avatar: `https://api.dicebear.com/7.x/adventurer/svg?seed=${form.username}`,
      bio: ''
    }

    await userApi.createUser(userData as User)

    // 自动登录
    const loginResponse = await authApi.login({
      username: form.username,
      password: form.password
    })

    // 通过用户名获取完整的用户信息
    const userInfo = await userApi.getUserByUsername(form.username)

    // 设置用户信息和token
    userStore.setToken(loginResponse.token)
    userStore.setUser(userInfo)

    router.push('/')
  } catch (error: any) {
    console.error('注册失败:', error)
    // 显示错误信息
    if (error.response?.status === 409) {
      errors.username = '用户名已存在'
    } else {
      errors.username = error.message || '注册失败，请稍后重试'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.register-card {
  width: 100%;
  max-width: 400px;
  padding: 40px 30px;
  text-align: center;

  &__title {
    font-family: var(--font-handwriting);
    font-size: 2rem;
    color: var(--color-soft-purple);
    margin-bottom: 8px;
  }

  &__subtitle {
    font-family: var(--font-body);
    font-size: 0.95rem;
    color: var(--color-ink-light);
    margin-bottom: 30px;
  }

  &__footer {
    margin-top: 24px;
    font-size: 0.9rem;
    color: var(--color-ink-light);

    a {
      color: var(--color-soft-purple);
      text-decoration: none;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  text-align: left;

  &__actions {
    margin-top: 10px;
  }

  &__submit {
    width: 100%;
  }
}
</style>