import { type ColumnDef } from '@tanstack/react-table'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { MoreHorizontal, Pencil, Trash2 } from 'lucide-react'
import { DataTableColumnHeader } from '@/components/data-table'
import type { SupplierResponse } from '@/types/api'

export interface SuppliersTableMeta {
  handleEdit?: (supplier: SupplierResponse) => void
  handleDelete?: (supplier: SupplierResponse) => void
  canEdit?: boolean
  canDelete?: boolean
}

export const suppliersColumns: ColumnDef<SupplierResponse>[] = [
  {
    accessorKey: 'name',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Tên nhà cung cấp" />
    ),
    cell: ({ row }) => (
      <span className="font-medium">{row.original.name}</span>
    ),
  },
  {
    accessorKey: 'contactName',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Người liên hệ" />
    ),
    cell: ({ row }) => {
      const contact = row.original.contactName
      return contact ? <span>{contact}</span> : <span className="text-muted-foreground">-</span>
    },
  },
  {
    accessorKey: 'phone',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Điện thoại" />
    ),
    cell: ({ row }) => {
      const phone = row.original.phone
      return phone ? <span>{phone}</span> : <span className="text-muted-foreground">-</span>
    },
  },
  {
    accessorKey: 'email',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Email" />
    ),
    cell: ({ row }) => {
      const email = row.original.email
      return email ? <span className="text-blue-600">{email}</span> : <span className="text-muted-foreground">-</span>
    },
  },
  {
    accessorKey: 'isActive',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Trạng thái" />
    ),
    cell: ({ row }) => {
      const isActive = row.original.isActive
      return (
        <Badge variant={isActive ? 'default' : 'secondary'}>
          {isActive ? 'Hoạt động' : 'Ngừng'}
        </Badge>
      )
    },
    filterFn: (row, id, value) => {
      const isActive = row.getValue(id)
      return value.includes(String(isActive))
    },
  },
  {
    accessorKey: 'createdAt',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Ngày tạo" />
    ),
    cell: ({ row }) => {
      const date = new Date(row.original.createdAt)
      return <span>{date.toLocaleDateString('vi-VN')}</span>
    },
  },
  {
    id: 'actions',
    enableHiding: false,
    cell: ({ row, table }) => {
      const meta = table.options.meta as SuppliersTableMeta | undefined
      const canEdit = meta?.canEdit ?? false
      const canDelete = meta?.canDelete ?? false

      if (!canEdit && !canDelete) return null

      return (
        <DropdownMenu modal={false}>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <span className="sr-only">Mở menu</span>
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            {canEdit && (
              <DropdownMenuItem onClick={() => meta?.handleEdit?.(row.original)}>
                <Pencil className="mr-2 h-4 w-4" />
                Chỉnh sửa
              </DropdownMenuItem>
            )}
            {canDelete && (
              <DropdownMenuItem
                onClick={() => meta?.handleDelete?.(row.original)}
                className="text-destructive"
              >
                <Trash2 className="mr-2 h-4 w-4" />
                Xóa
              </DropdownMenuItem>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      )
    },
  },
]
