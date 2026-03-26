import { type ColumnDef } from '@tanstack/react-table'
import { cn } from '@/lib/utils'
import { Badge } from '@/components/ui/badge'
import { DataTableColumnHeader } from '@/components/data-table'
import { LongText } from '@/components/long-text'
import { getStatusColor } from '../data/data'
import { type User } from '../data/schema'
import { DataTableRowActions } from './data-table-row-actions'

export const usersColumns: ColumnDef<User>[] = [
  {
    accessorKey: 'id',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='ID' />
    ),
    cell: ({ row }) => <div className='w-fit ps-2'>{row.getValue('id')}</div>,
  },
  {
    accessorKey: 'username',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Tên đăng nhập' />
    ),
    cell: ({ row }) => (
      <LongText className='max-w-36 ps-3'>{row.getValue('username')}</LongText>
    ),
    meta: {
      className: cn(
        'drop-shadow-[0_1px_2px_rgb(0_0_0_/_0.1)] dark:drop-shadow-[0_1px_2px_rgb(255_255_255_/_0.1)]',
        'ps-0.5 max-md:sticky start-6 @4xl/content:table-cell @4xl/content:drop-shadow-none'
      ),
    },
    enableHiding: false,
  },
  {
    id: 'fullName',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Họ tên' />
    ),
    cell: ({ row }) => {
      const { firstName, lastName } = row.original
      const fullName = [firstName, lastName].filter(Boolean).join(' ')
      return <LongText className='max-w-36'>{fullName || '—'}</LongText>
    },
    meta: { className: 'w-36' },
  },
  {
    accessorKey: 'email',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Email' />
    ),
    cell: ({ row }) => (
      <div className='w-fit ps-2 text-nowrap'>{row.getValue('email')}</div>
    ),
  },
  {
    accessorKey: 'phoneNumber',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Số điện thoại' />
    ),
    cell: ({ row }) => <div>{row.getValue('phoneNumber') || '—'}</div>,
    enableSorting: false,
  },
  {
    id: 'status',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Trạng thái' />
    ),
    cell: ({ row }) => {
      const { isActive, isEmailVerified, deletedAt } = row.original
      const statusText = deletedAt ? 'Đã xóa' : !isActive ? 'Không hoạt động' : !isEmailVerified ? 'Chờ xác thực' : 'Hoạt động'
      const badgeColor = getStatusColor(isActive, isEmailVerified, !!deletedAt)
      return (
        <div className='flex space-x-2'>
          <Badge variant='outline' className={cn('capitalize', badgeColor)}>
            {statusText}
          </Badge>
        </div>
      )
    },
    enableHiding: false,
    enableSorting: false,
  },
  {
    id: 'roles',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Vai trò' />
    ),
    cell: ({ row }) => {
      const userRoles = row.original.roles || []
      
      return (
        <div className='flex flex-wrap items-center gap-1'>
          {userRoles.map((roleName: string, index: number) => (
            <Badge key={index} variant='outline' className='capitalize text-xs'>
              {roleName}
            </Badge>
          ))}
          {userRoles.length === 0 && (
            <span className='text-muted-foreground text-xs'>No roles</span>
          )}
        </div>
      )
    },
    filterFn: (row, _id, value) => {
      const userRoles = row.original.roles || []
      return userRoles.some((r: string) => value.includes(r))
    },
    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: 'createdAt',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title='Created' />
    ),
    cell: ({ row }) => {
      const date = row.getValue('createdAt') as string
      return new Date(date).toLocaleDateString()
    },
  },
  {
    id: 'actions',
    cell: DataTableRowActions,
  },
]
