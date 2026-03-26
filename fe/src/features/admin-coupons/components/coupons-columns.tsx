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
import type { CouponResponse, DiscountType } from '@/types/api'

export const discountTypeLabels: Record<DiscountType, string> = {
  PERCENTAGE: 'Phần trăm',
  FIXED_AMOUNT: 'Số tiền cố định',
}

export const isValidStyles: Record<string, { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline' }> = {
  true: { label: 'Hợp lệ', variant: 'default' },
  false: { label: 'Không hợp lệ', variant: 'secondary' },
}

export interface CouponsTableMeta {
  handleEdit?: (coupon: CouponResponse) => void
  handleDelete?: (coupon: CouponResponse) => void
  canEdit?: boolean
  canDelete?: boolean
}

export const couponsColumns: ColumnDef<CouponResponse>[] = [
  {
    accessorKey: 'code',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Mã coupon" />
    ),
    cell: ({ row }) => (
      <span className="font-mono font-medium">{row.original.code}</span>
    ),
  },
  {
    accessorKey: 'discountType',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Loại giảm giá" />
    ),
    cell: ({ row }) => {
      const type = row.original.discountType
      return (
        <Badge variant="outline">
          {discountTypeLabels[type]}
        </Badge>
      )
    },
  },
  {
    accessorKey: 'discountValue',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Giá trị" />
    ),
    cell: ({ row }) => {
      const type = row.original.discountType
      const value = row.original.discountValue
      if (type === 'PERCENTAGE') {
        return <span>{value}%</span>
      }
      return <span>{value.toLocaleString('vi-VN')}đ</span>
    },
  },
  {
    accessorKey: 'minOrderValue',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Đơn tối thiểu" />
    ),
    cell: ({ row }) => {
      const value = row.original.minOrderValue
      if (value === 0) return <span className="text-muted-foreground">-</span>
      return <span>{value.toLocaleString('vi-VN')}đ</span>
    },
  },
  {
    accessorKey: 'maxDiscount',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Giảm tối đa" />
    ),
    cell: ({ row }) => {
      const value = row.original.maxDiscount
      if (!value) return <span className="text-muted-foreground">-</span>
      return <span>{value.toLocaleString('vi-VN')}đ</span>
    },
  },
  {
    accessorKey: 'usageLimit',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Lượt dùng" />
    ),
    cell: ({ row }) => {
      const limit = row.original.usageLimit
      const used = row.original.usedCount
      if (!limit) return <span>{used} / ∞</span>
      return <span>{used} / {limit}</span>
    },
  },
  {
    accessorKey: 'isActive',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Trạng thái" />
    ),
    cell: ({ row }) => {
      const isActive = row.original.isActive
      const isValid = row.original.isValid
      return (
        <div className="flex gap-1">
          <Badge variant={isActive ? 'default' : 'secondary'}>
            {isActive ? 'Bật' : 'Tắt'}
          </Badge>
          {isActive && (
            <Badge variant={isValid ? 'default' : 'destructive'}>
              {isValid ? 'Hợp lệ' : 'Hết hạn'}
            </Badge>
          )}
        </div>
      )
    },
    filterFn: (row, id, value) => {
      const isActive = row.getValue(id)
      return value.includes(String(isActive))
    },
  },
  {
    accessorKey: 'startDate',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Ngày bắt đầu" />
    ),
    cell: ({ row }) => {
      const date = new Date(row.original.startDate)
      return <span>{date.toLocaleDateString('vi-VN')}</span>
    },
  },
  {
    accessorKey: 'endDate',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Ngày kết thúc" />
    ),
    cell: ({ row }) => {
      const date = new Date(row.original.endDate)
      return <span>{date.toLocaleDateString('vi-VN')}</span>
    },
  },
  {
    id: 'actions',
    enableHiding: false,
    cell: ({ row, table }) => {
      const meta = table.options.meta as CouponsTableMeta | undefined
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
