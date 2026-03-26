import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Checkbox } from '@/components/ui/checkbox'
import { ScrollArea } from '@/components/ui/scroll-area'
import { useRoles, useUserRoles, useSetUserRoles } from '@/hooks/use-roles'
import { useUserProfile } from '@/hooks/use-user'
import { usePermissions } from '@/hooks/use-permissions'
import { useUsers } from './users-provider'
import { toast } from 'sonner'
import { ShieldAlert } from 'lucide-react'

// Permission required to assign ADMIN role to others
const ROLE_ADMIN_MANAGE_PERMISSION = 'ROLE_ADMIN_MANAGE'

export function UserRoleDialog() {
  const { open, setOpen, currentRow } = useUsers()
  const { data: roles, isLoading: isLoadingRoles } = useRoles()
  const { data: userRoles, isLoading: isLoadingUserRoles } = useUserRoles(
    open === 'assign-role' ? currentRow?.id || null : null
  )
  const { data: currentUser } = useUserProfile()
  const { hasPermission } = usePermissions()
  const setUserRoles = useSetUserRoles()
  
  const [localSelectedRoleIds, setLocalSelectedRoleIds] = useState<number[]>([])

  // Check if editing self
  const isEditingSelf = currentUser && currentRow && currentUser.id === currentRow.id
  
  // Find ADMIN and SUPERADMIN role ids
  const adminRole = roles?.find(r => r.name === 'ADMIN')
  const superAdminRole = roles?.find(r => r.name === 'SUPERADMIN')
  const adminRoleId = adminRole?.id
  const superAdminRoleId = superAdminRole?.id
  
  // Check if user originally had ADMIN role
  const originallyHadAdmin = userRoles?.some(r => r.name === 'ADMIN') ?? false

  // Check if current user has permission to manage ADMIN role
  const canManageAdminRole = hasPermission(ROLE_ADMIN_MANAGE_PERMISSION)

  // Sync when userRoles data arrives - using key to reset state when userRoles changes
  const userRolesKey = userRoles?.map(r => r.id).join(',') ?? ''
  
  useEffect(() => {
    if (userRoles && userRoles.length > 0) {
      const roleIds = userRoles.map(r => r.id)
      setLocalSelectedRoleIds(roleIds)
    } else {
      setLocalSelectedRoleIds([])
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userRolesKey])

  const handleOpenChange = (value: boolean) => {
    if (!value) {
      setLocalSelectedRoleIds([])
    }
    setOpen(value ? 'assign-role' : null)
  }

  const toggleRole = (roleId: number) => {
    // Nobody can assign SUPERADMIN role to others (only one SUPERADMIN allowed)
    if (roleId === superAdminRoleId && !isEditingSelf) {
      toast.error('Không thể gán vai trò SUPERADMIN cho người khác! Chỉ duy nhất 1 SUPERADMIN được phép.')
      return
    }
    
    // Check if trying to toggle ADMIN role without permission
    if (roleId === adminRoleId && !canManageAdminRole) {
      toast.error('Bạn không có quyền gán/bỏ vai trò ADMIN! (Cần quyền ROLE_ADMIN_MANAGE)')
      return
    }
    
    // Prevent admin from removing their own ADMIN role
    if (isEditingSelf && roleId === adminRoleId && originallyHadAdmin && localSelectedRoleIds.includes(roleId)) {
      toast.error('Bạn không thể tự bỏ quyền ADMIN của chính mình!')
      return
    }
    
    setLocalSelectedRoleIds(prev => 
      prev.includes(roleId)
        ? prev.filter(id => id !== roleId)
        : [...prev, roleId]
    )
  }

  const handleSave = () => {
    if (!currentRow) return

    if (localSelectedRoleIds.length === 0) {
      toast.error('Vui lòng chọn ít nhất một vai trò')
      return
    }

    // Check if trying to assign SUPERADMIN role to others
    if (superAdminRoleId && localSelectedRoleIds.includes(superAdminRoleId) && !isEditingSelf) {
      toast.error('Không thể gán vai trò SUPERADMIN cho người khác! Chỉ duy nhất 1 SUPERADMIN được phép.')
      return
    }

    // Check if trying to assign ADMIN role without permission
    if (adminRoleId && localSelectedRoleIds.includes(adminRoleId) && !canManageAdminRole) {
      toast.error('Bạn không có quyền gán vai trò ADMIN! (Cần quyền ROLE_ADMIN_MANAGE)')
      return
    }

    // Prevent admin from removing their own ADMIN role
    if (isEditingSelf && originallyHadAdmin && adminRoleId && !localSelectedRoleIds.includes(adminRoleId)) {
      toast.error('Bạn không thể tự bỏ quyền ADMIN của chính mình!')
      return
    }

    setUserRoles.mutate(
      { userId: currentRow.id, roleIds: localSelectedRoleIds },
      {
        onSuccess: () => {
          toast.success('Đã cập nhật vai trò người dùng thành công')
          setOpen(null)
        },
        onError: (error: unknown) => {
          const err = error as { response?: { data?: { message?: string } } }
          toast.error(err?.response?.data?.message || 'Không thể cập nhật vai trò')
        }
      }
    )
  }

  const isLoading = isLoadingRoles || isLoadingUserRoles

  return (
    <Dialog open={open === 'assign-role'} onOpenChange={handleOpenChange}>
      <DialogContent className='sm:max-w-[425px]'>
        <DialogHeader>
          <DialogTitle>Quản Lý Vai Trò Người Dùng</DialogTitle>
          <DialogDescription>
            Chọn các vai trò cho người dùng <strong>{currentRow?.username}</strong>.
            Người dùng có thể có nhiều vai trò cùng lúc.
          </DialogDescription>
        </DialogHeader>

        <div className='grid gap-4 py-4'>
          {/* Warning for users without ROLE_ADMIN_MANAGE permission */}
          {!canManageAdminRole && (
            <div className='flex items-center gap-2 rounded-md border border-yellow-500/50 bg-yellow-500/10 p-3 text-sm text-yellow-600 dark:text-yellow-400'>
              <ShieldAlert className='h-4 w-4' />
              <span>Bạn không có quyền gán vai trò ADMIN (cần quyền ROLE_ADMIN_MANAGE)</span>
            </div>
          )}
          
          <div className='flex flex-col gap-2'>
            <label className='text-sm font-medium'>Vai trò hệ thống</label>
            <ScrollArea className='h-[200px] rounded-md border p-4'>
              {isLoading ? (
                <div className='text-sm text-muted-foreground'>Đang tải...</div>
              ) : roles && roles.length > 0 ? (
                <div className='flex flex-col gap-3'>
                  {roles.map((role) => {
                    // Hide SUPERADMIN role completely when not editing self
                    if (role.id === superAdminRoleId && !isEditingSelf) {
                      return null
                    }
                    const isSelfAdminLock = isEditingSelf && role.id === adminRoleId && originallyHadAdmin
                    const isNoPermissionLock = role.id === adminRoleId && !canManageAdminRole
                    const isDisabled = isSelfAdminLock || isNoPermissionLock
                    return (
                      <div key={role.id} className='flex items-center space-x-2'>
                        <Checkbox
                          id={`role-${role.id}`}
                          checked={localSelectedRoleIds.includes(role.id)}
                          onCheckedChange={() => toggleRole(role.id)}
                          disabled={isDisabled}
                        />
                        <label
                          htmlFor={`role-${role.id}`}
                          className={`text-sm font-medium leading-none cursor-pointer flex items-center gap-2 ${isDisabled ? 'opacity-50 cursor-not-allowed' : ''}`}
                        >
                          {role.name}
                          {isSelfAdminLock && (
                            <span className='text-xs text-destructive'>(Không thể tự bỏ)</span>
                          )}
                          {isNoPermissionLock && !isSelfAdminLock && (
                            <span className='text-xs text-yellow-600 dark:text-yellow-400'>(Cần ROLE_ADMIN_MANAGE)</span>
                          )}
                          {role.permissions && role.permissions.length > 0 && !isDisabled && (
                            <span className='text-xs text-muted-foreground'>
                              ({role.permissions.length} quyền)
                            </span>
                          )}
                        </label>
                      </div>
                    )
                  })}
                </div>
              ) : (
                <div className='text-sm text-muted-foreground'>Không có vai trò nào</div>
              )}
            </ScrollArea>
          </div>
          
          {localSelectedRoleIds.length > 0 && (
            <div className='flex flex-wrap gap-2'>
              <span className='text-sm text-muted-foreground'>Đã chọn:</span>
              {localSelectedRoleIds.map(id => {
                const role = roles?.find(r => r.id === id)
                return role ? (
                  <span
                    key={id}
                    className='inline-flex items-center rounded-full bg-primary/10 px-2.5 py-0.5 text-xs font-medium text-primary'
                  >
                    {role.name}
                  </span>
                ) : null
              })}
            </div>
          )}
        </div>

        <DialogFooter>
          <Button variant='outline' onClick={() => setOpen(null)}>
            Hủy
          </Button>
          <Button 
            onClick={handleSave} 
            disabled={setUserRoles.isPending || localSelectedRoleIds.length === 0}
          >
            {setUserRoles.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
