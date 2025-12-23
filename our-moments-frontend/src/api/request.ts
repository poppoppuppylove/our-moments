import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true, // 关键1：开启跨域携带凭证（必须加！）
})

// 请求拦截器
request.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const userStore = useUserStore()

      // 添加 token 到请求头
      if (userStore.token) {
        config.headers.Authorization = `Bearer ${userStore.token}`
      }

      // 兼容文件上传的 Content-Type（可选，防止覆盖）
      if (config.headers['Content-Type'] !== 'multipart/form-data') {
        config.headers['Content-Type'] = 'application/json'
      }

      return config
    },
    (error) => {
      console.error('请求配置错误:', error)
      return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    (response: AxiosResponse) => {
      return response.data
    },
    (error) => {
      const { response } = error

      if (response) {
        switch (response.status) {
          case 401:
            // 未授权，清除 token 并跳转登录页
            const userStore = useUserStore()
            userStore.logout()
            router.push('/login')
            break
          case 403:
            console.error('没有权限访问该资源（前端排查：1.跨域凭证 2.Token失效 3.接口路径）')
            break
          case 404:
            console.error('请求的资源不存在')
            break
          case 500:
            console.error('服务器内部错误')
            break
          default:
            console.error(`请求错误: ${response.status}`)
        }
      } else if (error.request) {
        console.error('网络错误，请检查网络连接（排查：1.穿透工具是否启动 2.代理域名是否正确）')
      } else {
        console.error('请求配置错误:', error.message)
      }

      return Promise.reject(error)
    }
)

// 封装 GET 请求
export function get<T>(url: string, params?: object, config?: AxiosRequestConfig): Promise<T> {
  return request.get(url, { params, ...config })
}

// 封装 POST 请求
export function post<T>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
  return request.post(url, data, config)
}

// 封装 PUT 请求
export function put<T>(url: string, data?: object, config?: AxiosRequestConfig): Promise<T> {
  return request.put(url, data, config)
}

// 封装 DELETE 请求
export function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return request.delete(url, config)
}

// 文件上传
export function upload<T>(url: string, file: File, onProgress?: (percent: number) => void): Promise<T> {
  const formData = new FormData()
  formData.append('file', file)

  return request.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      if (onProgress && progressEvent.total) {
        const percent = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(percent)
      }
    }
  })
}

export default request