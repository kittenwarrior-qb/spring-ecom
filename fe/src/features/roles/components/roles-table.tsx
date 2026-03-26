import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useRoles } from '@/hooks/use-roles'
import { RoleCreateDialog } from './role-create-dialog'
import { RolePermissionsDialog } from './role-permissions-dialog'
import { Skeleton } from '@/components/ui/skeleton'

// Helper to categorize permissions
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

function groupPermissionsByCategory(permissions: string[]) {
  const groups: Record<PermissionCategory, string[]> = {
    view: [],
    update: [],
    delete: [],
    other: []
  }
  
  permissions.forEach(perm => {
    const category = categorizePermission(perm)
    groups[category].push(perm)
  })
  
  return groups
}

function PermissionBadgeGroup({ permissions, label }: { 
  permissions: string[]
  label: string 
}) {
  if (permissions.length === 0) return null
  
  return (
    <div className="flex flex-col gap-1">
      <div className="text-xs text-muted-foreground font-medium">
        {label}
      </div>
      <div className="flex flex-wrap gap-1">
        {permissions.map(perm => (
          <Badge key={perm} variant="secondary" className="text-xs">
            {perm}
          </Badge>
        ))}
      </div>
    </div>
  )
}

export function RolesTable() {
  const { data: roles, isLoading } = useRoles()

  return (
    <Card>
      <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
        <CardTitle>Quản Lý Vai Trò</CardTitle>
        <RoleCreateDialog />
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-2">
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
            <Skeleton className="h-10 w-full" />
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className='w-[100px]'>ID</TableHead>
                <TableHead className='w-[200px]'>Tên vai trò</TableHead>
                <TableHead>Các quyền</TableHead>
                <TableHead className='text-right'>Hành động</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {roles?.map((role) => {
                const groups = groupPermissionsByCategory(role.permissions || [])
                
                return (
                  <TableRow key={role.id}>
                    <TableCell className='font-medium'>{role.id}</TableCell>
                    <TableCell>
                      <Badge variant="outline">{role.name}</Badge>
                    </TableCell>
                    <TableCell data-slot="table-cell">
                      <div className="flex flex-col gap-4 min-w-[300px]">
                        <PermissionBadgeGroup 
                          permissions={groups.view} 
                          label="Xem" 
                        />
                        <PermissionBadgeGroup 
                          permissions={groups.update} 
                          label="Cập nhật" 
                        />
                        <PermissionBadgeGroup 
                          permissions={groups.delete} 
                          label="Xóa" 
                        />
                        <PermissionBadgeGroup 
                          permissions={groups.other} 
                          label="Khác" 
                        />
                        {(!role.permissions || role.permissions.length === 0) && (
                          <span className="text-xs text-muted-foreground">Chưa có quyền</span>
                        )}
                      </div>
                    </TableCell>
                    <TableCell className='text-right'>
                      <RolePermissionsDialog role={role} />
                    </TableCell>
                  </TableRow>
                )
              })}
              {(!roles || roles.length === 0) && (
                <TableRow>
                  <TableCell colSpan={4} className='h-24 text-center'>
                    Không có dữ liệu.
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  )
}
