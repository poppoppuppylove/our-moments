import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { BlogPost } from '@/types'

export const usePostStore = defineStore('post', () => {
  // 状态
  const posts = ref<BlogPost[]>([])
  const currentPost = ref<BlogPost | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 方法
  function setPosts(newPosts: BlogPost[]) {
    posts.value = newPosts
  }

  function setCurrentPost(post: BlogPost | null) {
    currentPost.value = post
  }

  function addPost(post: BlogPost) {
    posts.value.unshift(post)
  }

  function updatePost(updatedPost: BlogPost) {
    const index = posts.value.findIndex(p => p.postId === updatedPost.postId)
    if (index !== -1) {
      posts.value[index] = updatedPost
    }
    if (currentPost.value?.postId === updatedPost.postId) {
      currentPost.value = updatedPost
    }
  }

  function removePost(postId: number) {
    posts.value = posts.value.filter(p => p.postId !== postId)
    if (currentPost.value?.postId === postId) {
      currentPost.value = null
    }
  }

  function setLoading(state: boolean) {
    loading.value = state
  }

  function setError(message: string | null) {
    error.value = message
  }

  return {
    posts,
    currentPost,
    loading,
    error,
    setPosts,
    setCurrentPost,
    addPost,
    updatePost,
    removePost,
    setLoading,
    setError
  }
})
