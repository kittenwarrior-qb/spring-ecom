import { useEffect } from 'react'
import { useUserProfile } from '@/hooks/use-user'
import { useAuth } from '@/stores/auth-store'

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, setUser } = useAuth()
  const { data: profile, error } = useUserProfile()

  // Sync user profile to auth store when it's loaded
  useEffect(() => {
    if (profile && isAuthenticated()) {
      setUser({
        id: profile.id,
        username: profile.email, // Use email as username since UserInfo expects username
        email: profile.email,
        firstName: profile.firstName,
        lastName: profile.lastName,
        role: profile.role,
      })
    }
  }, [profile, isAuthenticated, setUser])

  // Clear user from store if not authenticated or profile fetch fails
  useEffect(() => {
    if (!isAuthenticated() || error) {
      setUser(null)
    }
  }, [isAuthenticated, error, setUser])

  return <>{children}</>
}