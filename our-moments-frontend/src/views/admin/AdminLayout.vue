<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="admin-layout__sidebar">
      <div class="sidebar-header">
        <router-link to="/" class="sidebar-logo">
          <h1>Our Moments</h1>
        </router-link>
        <p class="sidebar-subtitle">管理后台</p>
      </div>

      <nav class="sidebar-nav">
        <router-link
          to="/admin/posts"
          class="nav-item"
          :class="{ active: isActive('/admin/posts') }"
        >
          📝 文章管理
        </router-link>
        <router-link
          to="/admin/posts/new"
          class="nav-item"
          :class="{ active: isActive('/admin/posts/new') }"
        >
          ✏️ 新建文章
        </router-link>
        <router-link
          to="/admin/users"
          class="nav-item"
          :class="{ active: isActive('/admin/users') }"
        >
          👥 用户管理
        </router-link>
        <router-link
          to="/admin/comments"
          class="nav-item"
          :class="{ active: isActive('/admin/comments') }"
        >
          💬 评论管理
        </router-link>
        <router-link
          to="/admin/friendships"
          class="nav-item"
          :class="{ active: isActive('/admin/friendships') }"
        >
          🤝 好友管理
        </router-link>
      </nav>

      <div class="sidebar-footer">
        <div class="user-info">
          <span class="user-name">{{ userStore.nickname }}</span>
        </div>
        <HandButton variant="ghost" size="sm" @click="handleLogout">
          退出登录
        </HandButton>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="admin-layout__main">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import HandButton from '@/components/base/HandButton.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

function isActive(path: string) {
  return route.path === path
}

function handleLogout() {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped lang="scss">
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: var(--color-paper);

  &__sidebar {
    width: 240px;
    background: white;
    border-right: 1px dashed var(--color-ink-light);
    display: flex;
    flex-direction: column;
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    z-index: 100;

    .sidebar-header {
      padding: 24px 20px;
      border-bottom: 1px dashed var(--color-ink-light);
    }

    .sidebar-logo {
      text-decoration: none;

      h1 {
        font-family: var(--font-handwriting);
        font-size: 1.5rem;
        color: var(--color-soft-purple);
        margin: 0;
      }
    }

    .sidebar-subtitle {
      font-family: var(--font-body);
      font-size: 0.8rem;
      color: var(--color-ink-light);
      margin-top: 4px;
    }

    .sidebar-nav {
      flex: 1;
      padding: 20px 0;

      .nav-item {
        display: block;
        padding: 12px 20px;
        font-family: var(--font-body);
        font-size: 0.95rem;
        color: var(--color-ink);
        text-decoration: none;
        transition: all 0.2s;
        border-left: 3px solid transparent;

        &:hover {
          background: var(--color-paper);
          color: var(--color-soft-purple);
        }

        &.active {
          background: var(--color-paper);
          color: var(--color-soft-purple);
          border-left-color: var(--color-soft-purple);
        }
      }
    }

    .sidebar-footer {
      padding: 16px 20px;
      border-top: 1px dashed var(--color-ink-light);

      .user-info {
        margin-bottom: 12px;

        .user-name {
          font-family: var(--font-body);
          font-size: 0.9rem;
          color: var(--color-ink);
        }
      }
    }
  }

  &__main {
    flex: 1;
    margin-left: 240px;
    min-height: 100vh;
    padding: 32px;
  }
}

// 移动端响应式
@media (max-width: 768px) {
  .admin-layout {
    &__sidebar {
      width: 100%;
      height: auto;
      position: relative;
      border-right: none;
      border-bottom: 1px dashed var(--color-ink-light);

      .sidebar-nav {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        padding: 12px;

        .nav-item {
          padding: 8px 12px;
          border-left: none;
          border-radius: 4px;
          font-size: 0.85rem;

          &.active {
            background: var(--color-soft-purple);
            color: white;
          }
        }
      }

      .sidebar-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }
    }

    &__main {
      margin-left: 0;
      padding: 16px;
    }
  }
}
</style>
