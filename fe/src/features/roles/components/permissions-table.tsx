import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { usePermissions } from '@/hooks/use-roles'
import { Skeleton } from '@/components/ui/skeleton'
import { PermissionCreateDialog } from './permission-create-dialog'

export function PermissionsTable() {
  const { data: permissions, isLoading } = usePermissions()

  return (
    <Card>
      <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
        <CardTitle>Quản Lý Quyền Hạn</CardTitle>
        <PermissionCreateDialog />
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
                <TableHead>Tên quyền</TableHead>
                <TableHead>Mô tả</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {permissions?.map((permission) => (
                <TableRow key={permission.id}>
                  <TableCell className='font-medium'>{permission.id}</TableCell>
                  <TableCell>
                    <Badge variant="secondary">{permission.name}</Badge>
                  </TableCell>
                  <TableCell className='text-sm text-muted-foreground'>
                    Quyền truy cập cho {permission.name}
                  </TableCell>
                </TableRow>
              ))}
              {(!permissions || permissions.length === 0) && (
                <TableRow>
                  <TableCell colSpan={3} className='h-24 text-center'>
                    Không có quyền nào được tạo.
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
