import { useEffect } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'
import { useIsAuthenticated, isAdmin } from '@/stores/auth-store'

interface AuthGuardProps {
  children: React.ReactNode
  requireAdmin?: boolean
}

export function AuthGuard({ children, requireAdmin = false }: AuthGuardProps) {
  const isAuthenticated = useIsAuthenticated()
  const navigate = useNavigate()

  useEffect(() => {
    if (!isAuthenticated) {
      toast.error('Please sign in to access this page')
      navigate({
        to: '/sign-in',
        search: { redirect: window.location.pathname },
        replace: true,
      })
      return
    }

    if (requireAdmin && !isAdmin()) {
      toast.error('You do not have permission to access this page')
      navigate({
        to: '/sign-in',
        search: { redirect: window.location.pathname },
        replace: true,
      })
    }
  }, [isAuthenticated, requireAdmin, navigate])

  if (!isAuthenticated) {
    return null
  }

  if (requireAdmin && !isAdmin()) {
    return null
  }

  return <>{children}</>
}

export function AdminGuard({ children }: { children: React.ReactNode }) {
  return <AuthGuard requireAdmin>{children}</AuthGuard>
}
