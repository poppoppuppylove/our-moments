import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes: RouteRecordRaw[] = [
  // ========== 前端阅读端 ==========
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/HomePage.vue'),
    meta: { title: '首页 - Our Moments' }
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('@/views/post/PostDetail.vue'),
    meta: { title: '文章详情 - Our Moments' }
  },
  {
    path: '/post/new',
    name: 'PostNew',
    component: () => import('@/views/post/PostNew.vue'),
    meta: { title: '记录新时刻 - Our Moments', requiresAuth: true }
  },
  {
    path: '/post/:id/edit',
    name: 'PostUserEdit',
    component: () => import('@/views/post/PostNew.vue'),
    meta: { title: '编辑时刻 - Our Moments', requiresAuth: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/RegisterPage.vue'),
    meta: { title: '注册 - Our Moments' }
  },
  {
    path: '/profile',
    name: 'ProfileEdit',
    component: () => import('@/views/auth/ProfileEdit.vue'),
    meta: { title: '个人资料 - Our Moments', requiresAuth: true }
  },
  {
    path: '/friends',
    name: 'Friends',
    component: () => import('@/views/friends/FriendsPage.vue'),
    meta: { title: '我的好友 - Our Moments', requiresAuth: true }
  },

  // ========== 后台管理端 ==========
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue'),
    meta: { title: '管理员登录 - Our Moments' }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/admin/posts'
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/PostList.vue'),
        meta: { title: '文章管理 - Our Moments' }
      },
      {
        path: 'posts/new',
        name: 'AdminPostNew',
        component: () => import('@/views/admin/PostEdit.vue'),
        meta: { title: '新建文章 - Our Moments' }
      },
      {
        path: 'posts/:id/edit',
        name: 'AdminPostEdit',
        component: () => import('@/views/admin/PostEdit.vue'),
        meta: { title: '编辑文章 - Our Moments' }
      }
    ]
  },

  // ========== 其他 ==========
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/LoginPage.vue'),
    meta: { title: '登录 - Our Moments' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面未找到 - Our Moments' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }
    return { top: 0 }
  }
})

// 路由守卫 - 更新页面标题 + 权限验证
router.beforeEach((to, _from, next) => {
  // 更新页面标题
  document.title = (to.meta.title as string) || 'Our Moments'

  // 权限验证
  if (to.meta.requiresAuth) {
    const userStore = useUserStore()
    if (!userStore.isLoggedIn) {
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }
  }

  next()
})

export default router
