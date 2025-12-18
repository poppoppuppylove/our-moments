<template>
  <PaperTexture variant="light" class="min-h-screen">
    <div class="login-page">
      <HandCard variant="paper" class="login-card">
        <Pin variant="red" position="top" />

        <h1 class="login-card__title">欢迎回来</h1>
        <p class="login-card__subtitle">登录你的账号，继续记录美好时光</p>

        <form @submit.prevent="handleLogin" class="login-form">
          <HandInput
            v-model="form.username"
            label="用户名"
            placeholder="请输入用户名"
            :error="errors.username"
            required
          />

          <HandInput
            v-model="form.password"
            type="password"
            label="密码"
            placeholder="请输入密码"
            :error="errors.password"
            required
          />

          <div class="login-form__actions">
            <HandButton
              type="submit"
              variant="primary"
              size="lg"
              :loading="loading"
              class="login-form__submit"
            >
              登录
            </HandButton>
          </div>
        </form>

        <div class="login-card__footer">
          <p>还没有账号？<a href="#">注册一个</a></p>
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

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const errors = reactive({
  username: '',
  password: ''
})

function validate(): boolean {
  let isValid = true
  errors.username = ''
  errors.password = ''

  if (!form.username.trim()) {
    errors.username = '请输入用户名'
    isValid = false
  }

  if (!form.password) {
    errors.password = '请输入密码'
    isValid = false
  } else if (form.password.length < 6) {
    errors.password = '密码长度不能少于6位'
    isValid = false
  }

  return isValid
}

async function handleLogin() {
  if (!validate()) return

  loading.value = true

  try {
    // 模拟登录
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 模拟设置 token
    userStore.setToken('mock-token-12345')
    userStore.setUser({
      userId: 1,
      username: form.username,
      nickname: form.username,
      avatar: `https://api.dicebear.com/7.x/adventurer/svg?seed=${form.username}`,
      bio: '',
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString()
    })

    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.login-card {
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

.login-form {
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
