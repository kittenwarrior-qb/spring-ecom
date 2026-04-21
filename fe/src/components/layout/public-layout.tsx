import { Link } from '@tanstack/react-router'
import { ShoppingCart, Search, User } from 'lucide-react'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useDebounce } from '@/hooks/use-debounce'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useAuth, useUser } from '@/stores/auth-store'
import { authApi } from '@/api/auth.api'
import { NotificationBell } from '@/components/notification-bell'
import { useNotificationMqtt } from '@/hooks/use-notification'
import { useNotificationToast } from '@/hooks/use-notification-toast'

interface PublicLayoutProps {
  children: React.ReactNode
}

export function PublicLayout({ children }: PublicLayoutProps) {
  const auth = useAuth()
  const user = useUser()
  const [searchValue, setSearchValue] = useState('')
  useDebounce(searchValue, 1000) // Keep debounced value for future use

  // Debug log
  // eslint-disable-next-line no-console
  console.log('[PublicLayout] user:', user, 'accessToken:', auth.accessToken ? 'present' : 'null')

  // Connect to MQTT for notifications
  const userId = user?.id ? Number(user.id) : null
  const token = auth.accessToken || null
  
  // eslint-disable-next-line no-console
  console.log('[PublicLayout] userId:', userId, 'token:', token ? 'present' : 'null')
  
  useNotificationMqtt(userId, token)
  useNotificationToast()

  const handleLogout = async () => {
    try {
      await authApi.logout()
      auth.reset()
      window.location.href = '/'
    } catch (_error) {
      // Even if API call fails, still logout locally
      auth.reset()
      window.location.href = '/'
    }
  }

  const userRole = user?.role || (user as { roleName?: string })?.roleName || ((user as { roleId?: number })?.roleId === 1 ? 'ADMIN' : (user as { roleId?: number })?.roleId === 2 ? 'SELLER' : 'USER')
  const isAdminOrSeller = userRole === 'ADMIN' || userRole === 'SELLER'

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-backdrop-filter:bg-background/60">
        <div className="container flex h-16 items-center justify-between px-4">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <span className="text-xl font-bold">BookStore</span>
          </Link>

          {/* Search */}
          <div className="hidden flex-1 justify-center px-8 md:flex">
            <div className="relative w-full max-w-md">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                placeholder="Search books..."
                className="pl-10"
                value={searchValue}
                onChange={(e) => setSearchValue(e.target.value)}
              />
            </div>
          </div>

          {/* Actions */}
          <div className="flex items-center space-x-4">
            <NotificationBell />
            
            <Link to="/">
              <Button variant="ghost" size="icon">
                <ShoppingCart className="h-5 w-5" />
              </Button>
            </Link>

            {user ? (
              <DropdownMenu modal={false}>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" size="icon">
                    <User className="h-5 w-5" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuLabel>
                    {user.firstName} {user.lastName}
                  </DropdownMenuLabel>
                  <DropdownMenuLabel className="text-xs text-muted-foreground">
                    {user.email}
                  </DropdownMenuLabel>
                  {isAdminOrSeller && (
                    <>
                      <DropdownMenuSeparator />
                      <DropdownMenuItem asChild>
                        <Link to="/admin">Admin Dashboard</Link>
                      </DropdownMenuItem>
                    </>
                  )}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={handleLogout}
                  >
                    Logout
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            ) : (
              <Link to="/sign-in" search={{ redirect: undefined }}>
                <Button variant="ghost">Sign In</Button>
              </Link>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="container px-4 py-6">
        {children}
      </main>

      {/* Footer */}
      <footer className="border-t py-6">
        <div className="container px-4 text-center text-sm text-muted-foreground">
          <p>&copy; 2026 BookStore. All rights reserved.</p>
        </div>
      </footer>
    </div>
  )
}
