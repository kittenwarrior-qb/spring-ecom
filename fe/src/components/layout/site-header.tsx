import { Link, useNavigate } from '@tanstack/react-router'
import { ShoppingCart, Menu, X, User, LogOut, Package, ChevronDown, LayoutDashboard } from 'lucide-react'
import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Badge } from '@/components/ui/badge'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { useIsAuthenticated, useUser, useAuthStore } from '@/stores/auth-store'
import { useCartCount } from '@/hooks/use-cart'
import { useUserProfile } from '@/hooks/use-user'
import { toast } from 'sonner'
import { cn } from '@/lib/utils'
import { authApi } from '@/api/auth.api'
import { NotificationBell } from '@/components/notification-bell'

// Import logo
import logoImg from '@/assets/images/logo.png'

export function SiteHeader() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const [isScrolled, setIsScrolled] = useState(false)
  const navigate = useNavigate()

  // Use real auth store
  const isLoggedIn = useIsAuthenticated()
  const user = useUser()
  const { data: userProfile, isLoading: isProfileLoading } = useUserProfile()
  const cartCount = useCartCount()

  // Use profile data if available, otherwise fall back to auth store user
  // Show loading state while profile is being fetched
  const currentUser = userProfile || user
  const isUserDataLoading = isLoggedIn && !userProfile && isProfileLoading

  // Generate avatar fallback
  const getAvatarFallback = (user: any) => {
    if (!user) return 'U'
    
    // Check if user has firstName and lastName
    if (user.firstName && user.lastName) {
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase()
    }
    
    if (user.firstName) {
      return user.firstName[0].toUpperCase()
    }
    
    if (user.username) {
      const username = user.username.trim()
      if (username.length >= 2) {
        return username.substring(0, 2).toUpperCase()
      }
      return username[0].toUpperCase()
    }
    
    if (user.email) {
      return user.email[0].toUpperCase()
    }
    
    return 'U'
  }

  // Get display name
  const getDisplayName = (user: any) => {
    if (!user) return 'User'
    
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`
    }
    
    if (user.firstName) {
      return user.firstName
    }
    
    return user.username || user.email || 'User'
  }

  const avatarFallback = getAvatarFallback(currentUser)
  const displayName = getDisplayName(currentUser)
  const userPermissions = currentUser?.permissions ?? []
  const hasAdminAccess = userPermissions.includes('ADMIN_ACCESS')
  const avatarUrl = currentUser && 'avatarUrl' in currentUser ? (currentUser.avatarUrl as string) : undefined

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 10)
    }
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  const handleLogout = async () => {
    try {
      await authApi.logout()
      useAuthStore.getState().auth.reset()
      toast.success('Đăng xuất thành công. Hẹn gặp lại!')
      navigate({ to: '/' })
    } catch (error) {
      // Even if API call fails, still logout locally
      useAuthStore.getState().auth.reset()
      toast.success('Đăng xuất thành công. Hẹn gặp lại!')
      navigate({ to: '/' })
    }
  }

  return (
    <nav
      className={cn(
        "sticky top-0 z-50 transition-all duration-300",
        isScrolled ? "bg-white/90 backdrop-blur-md border-b shadow-sm" : "bg-transparent"
      )}
    >
      <div className="mx-auto max-w-7xl py-4 px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2 hover:opacity-80 transition-opacity">
            <img src={logoImg} alt="Fahaza Logo" className="h-10" />
          </Link>

          {/* Desktop Navigation */}
          <div className="hidden md:flex md:items-center md:space-x-1">
            <Link
              to="/"
              className="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-md font-medium transition-colors"
            >
              Trang chủ
            </Link>
            <Link
              to="/products"
              search={{ category: undefined, keyword: undefined }}
              className="text-gray-700 hover:text-gray-900 px-3 py-2 rounded-md text-md font-medium transition-colors"
            >
              Sách
            </Link>
          </div>

          {/* Right Actions */}
          <div className="hidden md:flex md:items-center md:space-x-2">
            {/* Notifications */}
            <NotificationBell />

            {/* Cart */}
            <Link to="/cart">
              <Button variant="ghost" className="relative text-gray-700 hover:text-gray-900">
                <ShoppingCart className="h-5 w-5" />
                {cartCount > 0 && (
                  <Badge className="absolute -top-1 -right-1 bg-red-600 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center font-semibold">
                    {cartCount}
                  </Badge>
                )}
              </Button>
            </Link>

            {/* User Section */}
            {isLoggedIn ? (
              isUserDataLoading ? (
                <div className="flex items-center space-x-2">
                  <div className="h-8 w-8 rounded-full bg-gray-200 animate-pulse" />
                  <div className="h-4 w-4 bg-gray-200 animate-pulse" />
                </div>
              ) : currentUser ? (
                <DropdownMenu modal={false}>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="flex items-center space-x-2 text-gray-700 hover:bg-gray-50">
                    <Avatar className="h-8 w-8">
                      <AvatarImage src={avatarUrl} alt={displayName} />
                      <AvatarFallback className="bg-[#EBF2FA] text-gray-800 font-semibold">
                        {avatarFallback}
                      </AvatarFallback>
                    </Avatar>
                    <ChevronDown className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-56">
                  <div className="px-4 py-3 border-b">
                    <p className="text-sm font-semibold text-gray-900 truncate">{displayName}</p>
                    <p className="text-xs text-gray-500 truncate">{currentUser?.email || 'No email'}</p>
                  </div>
                  {hasAdminAccess && (
                    <>
                      <DropdownMenuItem asChild>
                        <Link to="/admin" className="flex items-center gap-3">
                          <LayoutDashboard className="h-4 w-4" />
                          Quản Trị Viên
                        </Link>
                      </DropdownMenuItem>
                      <DropdownMenuSeparator />
                    </>
                  )}
                  <DropdownMenuItem asChild>
                    <Link to="/profile" className="flex items-center gap-3">
                      <User className="h-4 w-4" />
                      Tài khoản của tôi
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/profile/orders" className="flex items-center gap-3">
                      <Package className="h-4 w-4" />
                      Đơn hàng của tôi
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onSelect={(e) => {
                      e.preventDefault()
                      handleLogout()
                    }}
                    className="text-red-600 focus:text-red-600"
                  >
                    <LogOut className="mr-3 h-4 w-4" />
                    Đăng xuất
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
              ) : (
                <Link to="/sign-in" search={{ redirect: undefined }}>
                  <Button className="bg-primary text-white rounded-[40px] py-2 px-4 text-sm font-semibold hover:opacity-90 transition-opacity">
                    Đăng nhập
                  </Button>
                </Link>
              )
            ) : (
              <Link to="/sign-in" search={{ redirect: undefined }}>
                <Button className="bg-primary text-white rounded-[40px] py-2 px-4 text-sm font-semibold hover:opacity-90 transition-opacity">
                  Đăng nhập
                </Button>
              </Link>
              )}
            </div>

          {/* Mobile menu button */}
          <Button
            variant="ghost"
            size="icon"
            className="md:hidden"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
          >
            {isMenuOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </Button>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <div className="md:hidden pb-4 border-t mt-2 pt-4">
            <div className="flex flex-col space-y-1">
              <Link
                to="/"
                className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                onClick={() => setIsMenuOpen(false)}
              >
                Trang chủ
              </Link>
              <Link
                to="/products"
                search={{ category: undefined, keyword: undefined }}
                className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                onClick={() => setIsMenuOpen(false)}
              >
                Sách
              </Link>
              <Link
                to="/notifications"
                className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                onClick={() => setIsMenuOpen(false)}
              >
                Thông báo
              </Link>
              <a href="#" className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md">
                Giới thiệu
              </a>
              <Link
                to="/cart"
                className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                onClick={() => setIsMenuOpen(false)}
              >
                <ShoppingCart className="h-4 w-4 inline mr-2" />
                Giỏ hàng ({cartCount})
              </Link>
              {isLoggedIn ? (
                isUserDataLoading ? (
                  <div className="px-4 py-2 text-sm text-gray-500">Đang tải...</div>
                ) : currentUser ? (
                <>
                  {hasAdminAccess && (
                    <Link
                      to="/admin"
                      className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                      onClick={() => setIsMenuOpen(false)}
                    >
                      <LayoutDashboard className="h-4 w-4 inline mr-2" />
                      Quản Trị Viên
                    </Link>
                  )}
                  <Link
                    to="/profile"
                    className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    <User className="h-4 w-4 inline mr-2" />
                    Tài khoản
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="px-4 py-2 text-sm text-red-600 hover:bg-red-50 rounded-md text-left"
                  >
                    <LogOut className="h-4 w-4 inline mr-2" />
                    Đăng xuất
                  </button>
                </>
              ) : (
                <Link
                  to="/sign-in"
                  search={{ redirect: undefined }}
                  className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                  onClick={() => setIsMenuOpen(false)}
                >
                  Đăng nhập
                </Link>
              )
              ) : (
                <Link
                  to="/sign-in"
                  search={{ redirect: undefined }}
                  className="px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 rounded-md"
                  onClick={() => setIsMenuOpen(false)}
                >
                  Đăng nhập
                </Link>
              )}
            </div>
          </div>
        )}
      </div>
    </nav>
  )
}
