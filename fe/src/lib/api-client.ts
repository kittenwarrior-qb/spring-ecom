import axios, { type AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'
import { getCookie } from './cookies'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/v1'
const AUTH_BASE_URL = import.meta.env.VITE_ADMIN_API_BASE_URL || 'http://localhost:8081/v1'

const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000, 
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, 
})

// Request interceptor - add access token to headers
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (config.url?.includes('/auth/')) {
      config.baseURL = AUTH_BASE_URL
    }

    const tokenCookie = getCookie('accessToken')
    if (tokenCookie) {
      const token = JSON.parse(tokenCookie)
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`
      }
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor - handle errors and token refresh
let isRefreshing = false
let failedQueue: Array<{
  resolve: (value: unknown) => void
  reject: (reason: unknown) => void
}> = []

const processQueue = (error: AxiosError | null, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & {
      _retry?: boolean
    }

    // Skip retry for auth endpoints (login, register, refresh)
    const isAuthEndpoint = originalRequest.url?.includes('/auth/login') ||
                           originalRequest.url?.includes('/auth/register') ||
                           originalRequest.url?.includes('/auth/refresh')

    // If error is 401 and we haven't tried to refresh yet and it's not an auth endpoint
    if (error.response?.status === 401 && !originalRequest._retry && !isAuthEndpoint) {
      if (isRefreshing) {
        // If already refreshing, queue this request
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then((token) => {
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${token}`
            }
            return apiClient(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        // Try to refresh the token
        const response = await apiClient.post('/api/auth/refresh')
        const { accessToken } = response.data.data

        // Update cookie with new token
        document.cookie = `accessToken=${JSON.stringify(accessToken)}; path=/; max-age=${60 * 60 * 24 * 7}`

        // Process queued requests
        processQueue(null, accessToken)

        // Retry original request
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`
        }
        return apiClient(originalRequest)
      } catch (refreshError) {
        processQueue(refreshError as AxiosError, null)
        // Clear token and redirect to login
        document.cookie = 'accessToken=; path=/; max-age=0'
        window.location.href = '/sign-in'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    // For 401 on auth endpoints or refresh failure, redirect to login
    if (error.response?.status === 401 && !isAuthEndpoint) {
      document.cookie = 'accessToken=; path=/; max-age=0'
      window.location.href = '/sign-in'
    }

    return Promise.reject(error)
  }
)

export default apiClient
