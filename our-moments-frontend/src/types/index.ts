// 用户类型
export interface User {
  userId: number
  username: string
  password?: string
  nickname: string
  avatar: string
  bio: string
  createTime: string
  updateTime: string
}

// 博客媒体类型
export interface BlogMedia {
  mediaId: number
  postId: number
  mediaUrl: string
  mediaType: string
  rotation: number
  scale: number
  positionX: number
  positionY: number
  filterStyle: string
  sortOrder: number
  createTime: string
  zindex: number
}

// 分类类型
export interface Category {
  categoryId: number
  name: string
  iconUrl: string
  sortOrder: number
  createTime: string
}

// 标签类型
export interface Tag {
  tagId: number
  name: string
  createTime: string
}

// 博客文章类型
export interface BlogPost {
  postId: number
  userId: number
  categoryId: number
  title: string
  content: string
  weather: string
  mood: string
  location: string
  status: number
  visibility: string // PUBLIC, FRIENDS, PRIVATE
  createTime: string
  updateTime: string
  mediaList: BlogMedia[]
  tagList: Tag[]
  category: Category
  author: User
}

// 认证请求类型
export interface AuthRequest {
  username: string
  password: string
}

// 认证响应类型
export interface AuthResponse {
  token: string
  user: User
}

// API 响应通用类型
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页参数
export interface PaginationParams {
  page: number
  size: number
}

// 分页响应
export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
}

// 文件上传响应
export interface UploadResponse {
  success: boolean
  url: string
  message: string
}

// 评论类型
export interface Comment {
  commentId: number
  postId: number
  userId: number
  content: string
  position: number // 评论在文章中的位置
  createTime: string
  updateTime: string
  author: User
}

// 通知类型
export interface AppNotification {
  notificationId: number
  userId: number
  type: 'COMMENT' | 'FRIEND_REQUEST' | 'NEW_POST'
  content: string
  relatedId: number
  isRead: boolean
  createTime: string
}

// 消息类型
export interface Message {
  messageId: number
  senderId: number
  receiverId: number
  content: string
  isRead: boolean
  createTime: string
  updateTime: string
}
