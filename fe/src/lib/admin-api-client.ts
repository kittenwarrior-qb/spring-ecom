import axios, { type AxiosError, type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'
import { getCookie } from './cookies'

// Admin API client points to Landing module (port 8080) which uses gRPC to Core
const ADMIN_BASE_URL = import.meta.env.VITE_ADMIN_API_BASE_URL || 'http://localhost:8080/v1'

const adminApiClient: AxiosInstance = axios.create({
  baseURL: ADMIN_BASE_URL,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

// Request interceptor - add access token to headers
adminApiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
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

// Response interceptor - handle token refresh
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

adminApiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean }

    const isAuthEndpoint =
      originalRequest.url?.includes('/auth/login') ||
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
            return adminApiClient(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        // Try to refresh the token
        const response = await adminApiClient.post('/api/auth/refresh')
        const { accessToken } = response.data.data

        // Update cookie with new token
        document.cookie = `accessToken=${JSON.stringify(accessToken)}; path=/; max-age=${60 * 60 * 24 * 7}`

        // Process queued requests
        processQueue(null, accessToken)

        // Retry original request
        if (originalRequest.headers) {
          originalRequest.headers.Authorization = `Bearer ${accessToken}`
        }
        return adminApiClient(originalRequest)
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

export default adminApiClient
