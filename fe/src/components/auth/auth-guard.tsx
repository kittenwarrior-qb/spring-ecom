import { useEffect, useRef } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { toast } from 'sonner'
import { useIsAuthenticated, useUser } from '@/stores/auth-store'
import { useUserProfile } from '@/hooks/use-user'

// Permission required to access admin area
const ADMIN_ACCESS_PERMISSION = 'ADMIN_ACCESS'

interface AuthGuardProps {
  children: React.ReactNode
  requireAdmin?: boolean
  requiredPermissions?: string[]
  requireAnyPermission?: string[]
}

export function AuthGuard({ 
  children, 
  requireAdmin = false,
  requiredPermissions = [],
  requireAnyPermission = []
}: AuthGuardProps) {
  const isAuthenticated = useIsAuthenticated()
  const user = useUser()
  const { data: userProfile, isLoading: isProfileLoading, error: profileError } = useUserProfile()
  const navigate = useNavigate()
  const hasShownToast = useRef(false)

  // Use profile data if available, otherwise fall back to auth store user
  const currentUser = userProfile || user
  const userPermissions = currentUser?.permissions ?? []

  useEffect(() => {
    // Reset toast flag when authentication state changes
    if (!isAuthenticated) {
      hasShownToast.current = false
    }
  }, [isAuthenticated])

  useEffect(() => {
    if (!isAuthenticated) {
      if (!hasShownToast.current) {
        toast.error('Vui lòng đăng nhập để truy cập trang này')
        hasShownToast.current = true
      }
      navigate({
        to: '/sign-in',
        search: { redirect: window.location.pathname },
        replace: true,
      })
      return
    }

    // Handle profile loading error
    if (profileError && !hasShownToast.current) {
      toast.error('Không thể tải thông tin người dùng. Vui lòng đăng nhập lại.')
      hasShownToast.current = true
      navigate({
        to: '/sign-in',
        search: { redirect: window.location.pathname },
        replace: true,
      })
      return
    }

    // Check admin access by permission (not role)
    if (isAuthenticated && !isProfileLoading && requireAdmin) {
      console.log('[AuthGuard] Checking admin access:', {
        userPermissions,
        requiredPermission: ADMIN_ACCESS_PERMISSION,
        hasAdminAccess: userPermissions.includes(ADMIN_ACCESS_PERMISSION)
      })
      const hasAdminAccess = userPermissions.includes(ADMIN_ACCESS_PERMISSION)
      if (!hasAdminAccess && !hasShownToast.current) {
        console.log('[AuthGuard] No admin access, redirecting to home')
        toast.error('Bạn không có quyền truy cập trang quản lý. Vui lòng liên hệ quản trị viên.')
        hasShownToast.current = true
        navigate({
          to: '/',
          replace: true,
        })
      }
      return
    }

    // Check required permissions (all must be present)
    if (isAuthenticated && !isProfileLoading && requiredPermissions.length > 0) {
      const hasAll = requiredPermissions.every(p => userPermissions.includes(p))
      if (!hasAll && !hasShownToast.current) {
        toast.error('Bạn không có quyền truy cập trang này.')
        hasShownToast.current = true
        navigate({ to: '/', replace: true })
      }
      return
    }

    // Check any permission (at least one must be present)
    if (isAuthenticated && !isProfileLoading && requireAnyPermission.length > 0) {
      const hasAny = requireAnyPermission.some(p => userPermissions.includes(p))
      if (!hasAny && !hasShownToast.current) {
        toast.error('Bạn không có quyền truy cập trang này.')
        hasShownToast.current = true
        navigate({ to: '/', replace: true })
      }
    }
  }, [isAuthenticated, requireAdmin, requiredPermissions, requireAnyPermission, currentUser, userPermissions, navigate, isProfileLoading, profileError])

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Đang chuyển hướng đến trang đăng nhập...</p>
        </div>
      </div>
    )
  }

  // Show loading while profile is being fetched
  if (isAuthenticated && isProfileLoading && (requireAdmin || requiredPermissions.length > 0 || requireAnyPermission.length > 0)) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Đang xác thực quyền truy cập...</p>
        </div>
      </div>
    )
  }

  // Show error state if profile failed to load
  if (profileError) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <p className="text-destructive mb-4">Không thể tải thông tin người dùng</p>
          <p className="text-muted-foreground">Đang chuyển hướng đến trang đăng nhập...</p>
        </div>
      </div>
    )
  }

  // Check admin access by permission (not role)
  if (requireAdmin) {
    const hasAdminAccess = userPermissions.includes(ADMIN_ACCESS_PERMISSION)
    if (!hasAdminAccess) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <p className="text-destructive mb-4">Bạn không có quyền truy cập trang này</p>
            <p className="text-muted-foreground">Đang chuyển hướng về trang chủ...</p>
          </div>
        </div>
      )
    }
  }

  // Check required permissions (all must be present)
  if (requiredPermissions.length > 0) {
    const hasAll = requiredPermissions.every(p => userPermissions.includes(p))
    if (!hasAll) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <p className="text-destructive mb-4">Bạn không có quyền truy cập trang này</p>
            <p className="text-muted-foreground">Yêu cầu quyền: {requiredPermissions.join(', ')}</p>
          </div>
        </div>
      )
    }
  }

  // Check any permission (at least one must be present)
  if (requireAnyPermission.length > 0) {
    const hasAny = requireAnyPermission.some(p => userPermissions.includes(p))
    if (!hasAny) {
      return (
        <div className="flex items-center justify-center min-h-screen">
          <div className="text-center">
            <p className="text-destructive mb-4">Bạn không có quyền truy cập trang này</p>
            <p className="text-muted-foreground">Yêu cầu một trong các quyền: {requireAnyPermission.join(', ')}</p>
          </div>
        </div>
      )
    }
  }

  return <>{children}</>
}

export function AdminGuard({ children }: { children: React.ReactNode }) {
  return <AuthGuard requireAdmin>{children}</AuthGuard>
}

export function PermissionGuard({ 
  children, 
  permissions,
  anyPermission 
}: { 
  children: React.ReactNode
  permissions?: string[]
  anyPermission?: string[]
}) {
  return (
    <AuthGuard requiredPermissions={permissions} requireAnyPermission={anyPermission}>
      {children}
    </AuthGuard>
  )
}
