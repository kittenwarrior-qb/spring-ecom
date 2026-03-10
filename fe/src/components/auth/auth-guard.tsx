import { useEffect } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'
import { useIsAuthenticated, useUser } from '@/stores/auth-store'

interface AuthGuardProps {
  children: React.ReactNode
  requireAdmin?: boolean
}

export function AuthGuard({ children, requireAdmin = false }: AuthGuardProps) {
  const isAuthenticated = useIsAuthenticated()
  const user = useUser()
  const navigate = useNavigate()

  useEffect(() => {
    if (!isAuthenticated) {
      toast.error('Vui lòng đăng nhập để truy cập trang này')
      navigate({
        to: '/sign-in',
        search: { redirect: window.location.pathname },
        replace: true,
      })
      return
    }

    if (requireAdmin && user?.role !== 'ADMIN') {
      toast.error('Bạn không có quyền truy cập trang này')
      navigate({
        to: '/',
        replace: true,
      })
    }
  }, [isAuthenticated, requireAdmin, user, navigate])

  if (!isAuthenticated) {
    return null
  }

  if (requireAdmin && user?.role !== 'ADMIN') {
    return null
  }

  return <>{children}</>
}

export function AdminGuard({ children }: { children: React.ReactNode }) {
  return <AuthGuard requireAdmin>{children}</AuthGuard>
}
