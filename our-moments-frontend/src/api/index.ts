import { get, post, put, del, upload } from './request'
import type { User, BlogPost, Category, Tag, AuthRequest, UploadResponse } from '@/types'

// 认证相关 API
export const authApi = {
  login: (data: AuthRequest) => post<{ token: string }>('/auth/login', data),
}

// 用户相关 API
export const userApi = {
  getUser: (id: number) => get<User>(`/users/${id}`),
  getUserByUsername: (username: string) => get<User>(`/users/username/${username}`),
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
  // 通用图片上传
  upload: (file: File, onProgress?: (percent: number) => void) =>
    upload<UploadResponse>('/files/upload', file, onProgress),
  // 头像上传
  uploadAvatar: (file: File, onProgress?: (percent: number) => void) =>
    upload<UploadResponse>('/files/upload/avatar', file, onProgress),
  // 背景图上传
  uploadBackground: (file: File, onProgress?: (percent: number) => void) =>
    upload<UploadResponse>('/files/upload/background', file, onProgress),
  // 删除文件
  deleteFile: (url: string) => del<void>('/files/delete', { params: { url } }),
}

// 好友关系 API
export const friendshipApi = {
  getFriendships: (userId: number) => get<Friendship[]>(`/friendships/user/${userId}`),
  sendFriendRequest: (userId: number, friendId: number) =>
    post<Friendship>('/friendships/request', null, { params: { userId, friendId } }),
  acceptFriendRequest: (friendshipId: number, userId: number) =>
    put<Friendship>(`/friendships/${friendshipId}/accept`, null, { params: { userId } }),
  rejectFriendRequest: (friendshipId: number, userId: number) =>
    put<Friendship>(`/friendships/${friendshipId}/reject`, null, { params: { userId } }),
  deleteFriendship: (userId: number, friendId: number) =>
    del<void>(`/friendships/user/${userId}/friend/${friendId}`),
  checkFriendship: (userId: number, friendId: number) =>
    get<boolean>('/friendships/check', { params: { userId, friendId } }),
}

// 好友关系类型
export interface Friendship {
  friendshipId: number
  userId: number
  friendId: number
  status: string // PENDING, ACCEPTED, REJECTED
  createTime: string
  updateTime: string
}

// 评论 API
export const commentApi = {
  getCommentsByPostId: (postId: number) => get<Comment[]>(`/comments/post/${postId}`),
  getCommentsByPostIdAndPosition: (postId: number, position: number) =>
    get<Comment[]>(`/comments/post/${postId}/position/${position}`),
  createComment: (data: Comment) => post<Comment>('/comments', data),
  updateComment: (id: number, data: Partial<Comment>) => put<Comment>(`/comments/${id}`, data),
  deleteComment: (id: number) => del<void>(`/comments/${id}`),
}
