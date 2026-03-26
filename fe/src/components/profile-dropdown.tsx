import { Link } from '@tanstack/react-router'
import { Package, Settings, CreditCard, MapPin, LogOut } from 'lucide-react'
import useDialogState from '@/hooks/use-dialog-state'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { SignOutDialog } from '@/components/sign-out-dialog'
import { useUserProfile } from '@/hooks/use-user'

export function ProfileDropdown() {
  const [open, setOpen] = useDialogState()
  const { data: profile } = useUserProfile()

  const displayName = profile 
    ? [profile.firstName, profile.lastName].filter(Boolean).join(' ') || profile.username
    : 'User'

  return (
    <>
      <DropdownMenu modal={false}>
        <DropdownMenuTrigger asChild>
          <Button variant='ghost' className='relative h-8 w-8 rounded-full'>
            <Avatar className='h-8 w-8'>
              <AvatarFallback className="bg-primary text-white">
                {profile?.username?.charAt(0).toUpperCase() || 'U'}
              </AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent className='w-64' align='end' forceMount>
          <DropdownMenuLabel className='font-normal'>
            <div className='flex flex-col gap-1.5'>
              <p className='text-sm leading-none font-medium'>{displayName}</p>
              <p className='text-xs leading-none text-muted-foreground'>
                {profile?.email || 'user@example.com'}
              </p>
              {profile?.createdAt && (
                <p className='text-xs leading-none text-muted-foreground'>
                  Thành viên từ {new Date(profile.createdAt).toLocaleDateString('vi-VN', { month: 'long', year: 'numeric' })}
                </p>
              )}
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem asChild>
              <Link to='/profile'>
                <Package className="mr-2 h-4 w-4" />
                Trang cá nhân
              </Link>
            </DropdownMenuItem>
            <DropdownMenuItem asChild>
              <Link to='/profile/orders'>
                <Package className="mr-2 h-4 w-4" />
                Đơn hàng của tôi
              </Link>
            </DropdownMenuItem>
            <DropdownMenuItem asChild>
              <Link to='/profile/settings'>
                <Settings className="mr-2 h-4 w-4" />
                Cài đặt tài khoản
              </Link>
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem variant='destructive' onClick={() => setOpen(true)}>
            <LogOut className="mr-2 h-4 w-4" />
            Đăng xuất
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <SignOutDialog open={!!open} onOpenChange={setOpen} />
    </>
  )
}
