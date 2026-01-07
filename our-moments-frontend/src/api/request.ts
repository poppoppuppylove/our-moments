import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
    timeout: 15000
    // 不设置默认的 Content-Type，让 axios 根据请求体自动设置
    // 对于 JSON 请求，axios 会自动设置为 application/json
    // 对于 FormData 请求，axios 会自动设置为 multipart/form-data
})

// 请求拦截器
request.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        // 直接从 localStorage 获取 token，避免 Pinia 初始化问题
        const token = localStorage.getItem('token')

        // 添加 token 到请求头
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
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
                    console.error('没有权限访问该资源')
                    console.error('403 Error Details:', {
                        status: response.status,
                        statusText: response.statusText,
                        headers: response.headers,
                        data: response.data,
                        config: error.config
                    })
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
            console.error('网络错误，请检查网络连接')
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
export function post<T>(url: string, data?: object | null, config?: AxiosRequestConfig): Promise<T> {
    return request.post(url, data, config)
}

// 封装 PUT 请求
export function put<T>(url: string, data?: object | null, config?: AxiosRequestConfig): Promise<T> {
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

    // 明确设置正确的 Content-Type，避免 axios 的 transformRequest 覆盖
    return request.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data' // 明确设置为 multipart/form-data
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
