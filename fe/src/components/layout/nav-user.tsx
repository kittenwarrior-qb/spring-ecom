import { Link } from '@tanstack/react-router'
import {
  BadgeCheck,
  ChevronsUpDown,
  CreditCard,
  LogOut,
  Sparkles,
  LayoutDashboard,
} from 'lucide-react'
import useDialogState from '@/hooks/use-dialog-state'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@/components/ui/sidebar'
import { SignOutDialog } from '@/components/sign-out-dialog'
import type { UserInfo, UserResponse } from '@/types/api'

type NavUserProps = {
  user: UserInfo | UserResponse | null
}

export function NavUser({ user }: NavUserProps) {
  const { isMobile } = useSidebar()
  const [open, setOpen] = useDialogState()

  // Generate avatar fallback from user's name or email
  const getAvatarFallback = (user: UserInfo | UserResponse | null) => {
    if (!user) return 'U'
    
    // Check if user has firstName and lastName
    if ('firstName' in user && user.firstName && user.lastName) {
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase()
    }
    
    if ('firstName' in user && user.firstName) {
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
  const getDisplayName = (user: UserInfo | UserResponse | null) => {
    if (!user) return 'User'
    
    if ('firstName' in user && user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`
    }
    
    if ('firstName' in user && user.firstName) {
      return user.firstName
    }
    
    return user.username || user.email || 'User'
  }

  const avatarFallback = getAvatarFallback(user)
  const displayName = getDisplayName(user)
  const displayEmail = user?.email || ''
  const avatarUrl = 'avatarUrl' in (user || {}) ? (user as UserResponse)?.avatarUrl || undefined : undefined
  const isAdmin = user?.role === 'ADMIN'
  

  return (
    <>
      <SidebarMenu>
        <SidebarMenuItem>
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <SidebarMenuButton
                size='lg'
                className='data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground'
              >
                <Avatar className='h-8 w-8 rounded-lg'>
                  <AvatarImage src={avatarUrl} alt={displayName} />
                  <AvatarFallback className='rounded-lg'>{avatarFallback}</AvatarFallback>
                </Avatar>
                <div className='grid flex-1 text-start text-sm leading-tight'>
                  <span className='truncate font-semibold'>{displayName}</span>
                  <span className='truncate text-xs'>{displayEmail}</span>
                </div>
                <ChevronsUpDown className='ms-auto size-4' />
              </SidebarMenuButton>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              className='w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg'
              side={isMobile ? 'bottom' : 'right'}
              align='end'
              sideOffset={4}
            >
              <DropdownMenuLabel className='p-0 font-normal'>
                <div className='flex items-center gap-2 px-1 py-1.5 text-start text-sm'>
                  <Avatar className='h-8 w-8 rounded-lg'>
                    <AvatarImage src={avatarUrl} alt={displayName} />
                    <AvatarFallback className='rounded-lg'>{avatarFallback}</AvatarFallback>
                  </Avatar>
                  <div className='grid flex-1 text-start text-sm leading-tight'>
                    <span className='truncate font-semibold'>{displayName}</span>
                    <span className='truncate text-xs'>{displayEmail}</span>
                  </div>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              {isAdmin && (
                <>
                  <DropdownMenuGroup>
                    <DropdownMenuItem asChild>
                      <Link to='/admin'>
                        <LayoutDashboard />
                        Quản Trị Viên
                      </Link>
                    </DropdownMenuItem>
                  </DropdownMenuGroup>
                  <DropdownMenuSeparator />
                </>
              )}
              <DropdownMenuGroup>
                <DropdownMenuItem>
                  <Sparkles />
                  Upgrade to Pro
                </DropdownMenuItem>
              </DropdownMenuGroup>
              <DropdownMenuSeparator />
              <DropdownMenuGroup>
                <DropdownMenuItem asChild>
                  <Link to='/profile'>
                    <BadgeCheck />
                    Tài khoản của tôi
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuItem asChild>
                  <Link to='/profile/orders'>
                    <CreditCard />
                    Đơn hàng của tôi
                  </Link>
                </DropdownMenuItem>
              </DropdownMenuGroup>
              <DropdownMenuSeparator />
              <DropdownMenuItem
                variant='destructive'
                onClick={() => setOpen(true)}
              >
                <LogOut />
                Đăng xuất
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </SidebarMenuItem>
      </SidebarMenu>

      <SignOutDialog open={!!open} onOpenChange={setOpen} />
    </>
  )
}
