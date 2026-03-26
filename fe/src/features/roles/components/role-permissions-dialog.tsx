import { useState, useMemo } from 'react'
import { Edit2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import { Checkbox } from '@/components/ui/checkbox'
import { useAssignPermissions, usePermissions } from '@/hooks/use-roles'
import type { RoleResponse } from '@/types/api'

interface RolePermissionsDialogProps {
  role: RoleResponse
}

type PermissionCategory = 'view' | 'update' | 'delete' | 'other'

function categorizePermission(name: string): PermissionCategory {
  const upperName = name.toUpperCase()
  if (upperName.includes('_VIEW') || upperName.includes('_GET') || upperName.endsWith('_READ')) {
    return 'view'
  }
  if (upperName.includes('_UPDATE') || upperName.includes('_CREATE') || upperName.includes('_EDIT') || upperName.includes('_WRITE')) {
    return 'update'
  }
  if (upperName.includes('_DELETE') || upperName.includes('_REMOVE')) {
    return 'delete'
  }
  return 'other'
}

interface PermissionItem {
  id: number
  name: string
}

interface GroupedPermissions {
  view: PermissionItem[]
  update: PermissionItem[]
  delete: PermissionItem[]
  other: PermissionItem[]
}

function groupPermissions(permissions: PermissionItem[]): GroupedPermissions {
  const groups: GroupedPermissions = {
    view: [],
    update: [],
    delete: [],
    other: []
  }
  
  permissions.forEach(perm => {
    const category = categorizePermission(perm.name)
    groups[category].push(perm)
  })
  
  return groups
}

export function RolePermissionsDialog({ role }: RolePermissionsDialogProps) {
  const [open, setOpen] = useState(false)

  const { data: permissions } = usePermissions()
  const assignPermissions = useAssignPermissions()

  // Compute initial permissions based on role's current permissions
  const initialPermissions = useMemo(() => {
    if (!permissions) return []
    return role.permissions
      .map((permName) => permissions.find((p) => p.name === permName)?.id)
      .filter((id): id is number => id !== undefined)
  }, [role.permissions, permissions])

  // Selected permissions state
  const [selectedPermissions, setSelectedPermissions] = useState<number[]>([])

  // Reset selected permissions when dialog opens
  const handleOpenChange = (newOpen: boolean) => {
    if (newOpen) {
      setSelectedPermissions(initialPermissions)
    }
    setOpen(newOpen)
  }

  const handleTogglePermission = (id: number) => {
    setSelectedPermissions((prev) =>
      prev.includes(id) ? prev.filter((p) => p !== id) : [...prev, id]
    )
  }

  const handleToggleGroup = (groupIds: number[], checked: boolean) => {
    setSelectedPermissions((prev) => {
      if (checked) {
        const newSet = new Set([...prev, ...groupIds])
        return Array.from(newSet)
      } else {
        return prev.filter(id => !groupIds.includes(id))
      }
    })
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    assignPermissions.mutate(
      { roleId: role.id, data: { permissionIds: selectedPermissions } },
      {
        onSuccess: () => {
          setOpen(false)
        },
      }
    )
  }

  const groupedPermissions = permissions ? groupPermissions(permissions) : { view: [], update: [], delete: [], other: [] }

  const renderPermissionGroup = (
    title: string,
    perms: PermissionItem[],
    colorClass: string
  ) => {
    if (perms.length === 0) return null
    
    const allSelected = perms.every(p => selectedPermissions.includes(p.id))
    const someSelected = perms.some(p => selectedPermissions.includes(p.id))
    
    return (
      <div className="space-y-2">
        <div className="flex items-center gap-2 px-1">
          <Checkbox
            id={`group-${title}`}
            checked={allSelected}
            ref={(el) => {
              if (el) el.checked = allSelected
              if (el) el.indeterminate = someSelected && !allSelected
            }}
            onCheckedChange={(checked) => handleToggleGroup(perms.map(p => p.id), !!checked)}
          />
          <label 
            htmlFor={`group-${title}`}
            className={`text-sm font-medium cursor-pointer ${colorClass}`}
          >
            {title}
          </label>
        </div>
        <div className="pl-6 space-y-1 border-l-2 ml-2">
          {perms.map((permission) => (
            <div key={permission.id} className="flex items-center space-x-2 py-0.5">
              <Checkbox
                id={`edit-perm-${permission.id}`}
                checked={selectedPermissions.includes(permission.id)}
                onCheckedChange={() => handleTogglePermission(permission.id)}
              />
              <label
                htmlFor={`edit-perm-${permission.id}`}
                className='text-sm leading-none peer-disabled:cursor-not-allowed peer-disabled:opacity-70'
              >
                {permission.name}
              </label>
            </div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        <Button variant='ghost' size='sm'>
          <Edit2 className='h-4 w-4' />
        </Button>
      </DialogTrigger>
      <DialogContent className='sm:max-w-[500px]'>
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Chỉnh sửa quyền: {role.name}</DialogTitle>
            <DialogDescription>
              Chọn hoặc bỏ chọn các quyền cho vai trò {role.name}.
            </DialogDescription>
          </DialogHeader>
          <div className='grid gap-4 py-4'>
            <div className='max-h-[400px] overflow-y-auto space-y-4'>
              {renderPermissionGroup('Xem', groupedPermissions.view, 'text-secondary-foreground')}
              {renderPermissionGroup('Cập nhật', groupedPermissions.update, 'text-secondary-foreground')}
              {renderPermissionGroup('Xóa', groupedPermissions.delete, 'text-secondary-foreground')}
              {groupedPermissions.other.length > 0 && 
                renderPermissionGroup('Khác', groupedPermissions.other, 'text-secondary-foreground')
              }
              {(!permissions || permissions.length === 0) && (
                <div className='text-sm text-muted-foreground p-2'>Không có quyền nào được tải</div>
              )}
            </div>
          </div>
          <div className='flex justify-end space-x-2'>
            <Button
              type='button'
              variant='outline'
              onClick={() => setOpen(false)}
            >
              Hủy
            </Button>
            <Button
              type='submit'
              disabled={assignPermissions.isPending}
            >
              {assignPermissions.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}
