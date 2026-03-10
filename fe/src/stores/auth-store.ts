import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { getCookie, setCookie, removeCookie } from '@/lib/cookies'
import type { UserInfo, UserRole } from '@/types/api'

const ACCESS_TOKEN_KEY = 'accessToken'

interface AuthState {
  auth: {
    user: UserInfo | null
    setUser: (user: UserInfo | null) => void
    accessToken: string
    setAccessToken: (accessToken: string, expiresIn?: number) => void
    resetAccessToken: () => void
    reset: () => void
    isAuthenticated: () => boolean
  }
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => {
      const cookieState = getCookie(ACCESS_TOKEN_KEY)
      const initToken = cookieState ? JSON.parse(cookieState) : ''

      return {
        auth: {
          user: null,
          setUser: (user) =>
            set((state) => ({ ...state, auth: { ...state.auth, user } })),
          accessToken: initToken,
          setAccessToken: (accessToken, expiresIn) =>
            set((state) => {
              const maxAge = expiresIn ? Math.floor(expiresIn / 1000) : 60 * 60 * 24 * 7
              setCookie(ACCESS_TOKEN_KEY, JSON.stringify(accessToken), maxAge)
              return { ...state, auth: { ...state.auth, accessToken } }
            }),
          resetAccessToken: () =>
            set((state) => {
              removeCookie(ACCESS_TOKEN_KEY)
              return { ...state, auth: { ...state.auth, accessToken: '' } }
            }),
          reset: () =>
            set((state) => {
              removeCookie(ACCESS_TOKEN_KEY)
              return {
                ...state,
                auth: { ...state.auth, user: null, accessToken: '' },
              }
            }),
          isAuthenticated: () => {
            const state = get()
            return !!state.auth.accessToken
          },
        },
      }
    },
    {
      name: 'auth-storage', // saves to localStorage
    }
  )
)

// Helper hooks
export const useAuth = () => useAuthStore((state) => state.auth)
export const useUser = () => useAuthStore((state) => state.auth.user)
export const useIsAuthenticated = () => {
  const accessToken = useAuthStore((state) => state.auth.accessToken)
  return !!accessToken
}

// Role checking helpers
export const hasRole = (role: UserRole): boolean => {
  const user = useAuthStore.getState().auth.user
  return user?.role === role
}

export const isAdmin = (): boolean => hasRole('ADMIN')
export const isUser = (): boolean => hasRole('USER')
