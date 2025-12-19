<template>
  <PaperTexture variant="light" class="admin-login">
    <div class="admin-login__container">
      <div class="admin-login__card">
        <!-- Logo -->
        <div class="admin-login__logo">
          <h1>Our Moments</h1>
          <p>管理后台</p>
        </div>

        <!-- 登录表单 -->
        <form class="admin-login__form" @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <HandInput
              id="username"
              v-model="form.username"
              placeholder="请输入用户名"
              :disabled="loading"
            />
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <HandInput
              id="password"
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              :disabled="loading"
            />
          </div>

          <HandError
            v-if="error"
            :message="error"
            :show-retry="false"
            class="login-error"
          />

          <HandButton
            type="submit"
            variant="primary"
            size="lg"
            :disabled="loading"
            class="login-btn"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </HandButton>
        </form>

        <!-- 返回首页 -->
        <div class="admin-login__back">
          <router-link to="/">← 返回首页</router-link>
        </div>
      </div>
    </div>
  </PaperTexture>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'
import PaperTexture from '@/components/decorative/PaperTexture.vue'
import HandInput from '@/components/base/HandInput.vue'
import HandButton from '@/components/base/HandButton.vue'
import HandError from '@/components/common/HandError.vue'
import { authApi } from '@/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const error = ref('')

const form = reactive({
  username: '',
  password: ''
})

async function handleLogin() {
  if (!form.username || !form.password) {
    error.value = '请输入用户名和密码'
    return
  }

  loading.value = true
  error.value = ''

  try {
    // 尝试调用后端 API
    const response = await authApi.login({
      username: form.username,
      password: form.password
    })

    userStore.setToken(response.token)

    // 跳转到重定向地址或管理后台首页
    const redirect = route.query.redirect as string || '/admin'
    router.push(redirect)
  } catch (err: any) {
    // 如果 API 调用失败，使用 Mock 登录 (开发阶段)
    if (form.username === 'admin' && form.password === 'admin123') {
      userStore.setToken('mock-token-123')
      const redirect = route.query.redirect as string || '/admin'
      router.push(redirect)
    } else {
      error.value = err.message || '登录失败，请检查用户名和密码'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.admin-login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;

  &__container {
    width: 100%;
    max-width: 400px;
    padding: 20px;
  }

  &__card {
    background: white;
    padding: 40px;
    border-radius: 8px;
    box-shadow: 3px 5px 20px rgba(0, 0, 0, 0.1);
    position: relative;

    // 手绘风格边框
    &::before {
      content: '';
      position: absolute;
      top: -3px;
      left: -3px;
      right: -3px;
      bottom: -3px;
      border: 2px dashed var(--color-ink-light);
      border-radius: 12px;
      transform: rotate(-0.5deg);
      pointer-events: none;
    }
  }

  &__logo {
    text-align: center;
    margin-bottom: 32px;

    h1 {
      font-family: var(--font-handwriting);
      font-size: 2rem;
      color: var(--color-soft-purple);
      margin-bottom: 8px;
    }

    p {
      font-family: var(--font-body);
      font-size: 0.9rem;
      color: var(--color-ink-light);
    }
  }

  &__form {
    .form-group {
      margin-bottom: 20px;

      label {
        display: block;
        font-family: var(--font-body);
        font-size: 0.9rem;
        color: var(--color-ink);
        margin-bottom: 8px;
      }
    }

    .login-error {
      margin-bottom: 16px;
      padding: 12px;
      background: rgba(255, 0, 0, 0.05);
      border-radius: 4px;

      :deep(.hand-error__dialog) {
        padding: 0;
        box-shadow: none;
        background: transparent;

        &::before {
          display: none;
        }
      }

      :deep(.hand-error__icon),
      :deep(.hand-error__title),
      :deep(button) {
        display: none;
      }

      :deep(.hand-error__message) {
        margin: 0;
        color: #c00;
        font-size: 0.85rem;
      }
    }

    .login-btn {
      width: 100%;
      margin-top: 8px;
    }
  }

  &__back {
    text-align: center;
    margin-top: 24px;

    a {
      font-family: var(--font-body);
      font-size: 0.85rem;
      color: var(--color-ink-light);
      text-decoration: none;
      transition: color 0.2s;

      &:hover {
        color: var(--color-soft-purple);
      }
    }
  }
}
</style>
