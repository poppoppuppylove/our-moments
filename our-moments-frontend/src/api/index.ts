import { get, post, put, del, upload } from './request'
import type { User, BlogPost, Category, Tag, AuthRequest, UploadResponse } from '@/types'

// 认证相关 API
export const authApi = {
  login: (data: AuthRequest) => post<{ token: string }>('/auth/login', data),
}

// 用户相关 API
export const userApi = {
  getUser: (id: number) => get<User>(`/users/${id}`),
  createUser: (data: User) => post<User>('/users', data),
  updateUser: (id: number, data: User) => put<User>(`/users/${id}`, data),
  deleteUser: (id: number) => del<void>(`/users/${id}`),
}

// 文章相关 API
export const postApi = {
  getPosts: (params?: { userId?: number; categoryId?: number; status?: number }) =>
    get<BlogPost[]>('/posts', params),
  getPost: (id: number) => get<BlogPost>(`/posts/${id}`),
  createPost: (data: Partial<BlogPost>) => post<BlogPost>('/posts', data),
  updatePost: (id: number, data: Partial<BlogPost>) => put<BlogPost>(`/posts/${id}`, data),
  deletePost: (id: number) => del<void>(`/posts/${id}`),
}

// 分类相关 API
export const categoryApi = {
  getAllCategories: () => get<Category[]>('/categories'),
  getCategory: (id: number) => get<Category>(`/categories/${id}`),
  createCategory: (data: Category) => post<Category>('/categories', data),
  updateCategory: (id: number, data: Category) => put<Category>(`/categories/${id}`, data),
  deleteCategory: (id: number) => del<void>(`/categories/${id}`),
}

// 标签相关 API
export const tagApi = {
  getAllTags: () => get<Tag[]>('/tags'),
  getTag: (id: number) => get<Tag>(`/tags/${id}`),
  createTag: (data: Tag) => post<Tag>('/tags', data),
  deleteTag: (id: number) => del<void>(`/tags/${id}`),
}

// 文件上传 API
export const fileApi = {
  upload: (file: File, onProgress?: (percent: number) => void) =>
    upload<UploadResponse>('/files/upload', file, onProgress),
}
