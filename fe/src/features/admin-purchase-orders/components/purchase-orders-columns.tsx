import { type ColumnDef } from '@tanstack/react-table'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { MoreHorizontal, Eye, Pencil, Check, Package, X } from 'lucide-react'
import { DataTableColumnHeader } from '@/components/data-table'
import type { PurchaseOrderResponse, PurchaseOrderStatus } from '@/types/api'

export const statusLabels: Record<PurchaseOrderStatus, { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline' }> = {
  DRAFT: { label: 'Chưa xác nhận', variant: 'secondary' },
  CONFIRMED: { label: 'Đã xác nhận', variant: 'default' },
  RECEIVED: { label: 'Đã nhận', variant: 'outline' },
  PARTIALLY_RECEIVED: { label: 'Nhận một phần', variant: 'outline' },
  CANCELLED: { label: 'Đã hủy', variant: 'destructive' },
}

export interface PurchaseOrdersTableMeta {
  handleView?: (po: PurchaseOrderResponse) => void
  handleEdit?: (po: PurchaseOrderResponse) => void
  handleConfirm?: (po: PurchaseOrderResponse) => void
  handleReceive?: (po: PurchaseOrderResponse) => void
  handleCancel?: (po: PurchaseOrderResponse) => void
  canEdit?: boolean
  canConfirm?: boolean
  canReceive?: boolean
  canCancel?: boolean
}

export const purchaseOrdersColumns: ColumnDef<PurchaseOrderResponse>[] = [
  {
    accessorKey: 'poNumber',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Số PO" />
    ),
    cell: ({ row }) => (
      <span className="font-mono font-medium">{row.original.poNumber}</span>
    ),
  },
  {
    accessorKey: 'supplierName',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Nhà cung cấp" />
    ),
    cell: ({ row }) => (
      <span className="font-medium">{row.original.supplierName}</span>
    ),
  },
  {
    accessorKey: 'status',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Trạng thái" />
    ),
    cell: ({ row }) => {
      const status = row.original.status
      const { label, variant } = statusLabels[status]
      return <Badge variant={variant}>{label}</Badge>
    },
    filterFn: (row, id, value) => {
      const status = row.getValue(id)
      return value.includes(status)
    },
  },
  {
    accessorKey: 'totalAmount',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Tổng tiền" />
    ),
    cell: ({ row }) => {
      const amount = row.original.totalAmount
      return <span className="font-medium">{amount.toLocaleString('vi-VN')}d</span>
    },
  },
  {
    accessorKey: 'expectedDate',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Ngày dự kiến" />
    ),
    cell: ({ row }) => {
      const date = row.original.expectedDate
      if (!date) return <span className="text-muted-foreground">-</span>
      return <span>{new Date(date).toLocaleDateString('vi-VN')}</span>
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
      const meta = table.options.meta as PurchaseOrdersTableMeta | undefined
      const po = row.original
      const isDraft = po.status === 'DRAFT'
      const isConfirmed = po.status === 'CONFIRMED'
      const isReceived = po.status === 'RECEIVED' || po.status === 'CANCELLED'

      return (
        <DropdownMenu modal={false}>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <span className="sr-only">Mở menu</span>
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => meta?.handleView?.(po)}>
              <Eye className="mr-2 h-4 w-4" />
              Xem chi tiết
            </DropdownMenuItem>
            {isDraft && meta?.canEdit && (
              <DropdownMenuItem onClick={() => meta?.handleEdit?.(po)}>
                <Pencil className="mr-2 h-4 w-4" />
                Chỉnh sửa
              </DropdownMenuItem>
            )}
            {isDraft && meta?.canConfirm && (
              <>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => meta?.handleConfirm?.(po)}>
                  <Check className="mr-2 h-4 w-4" />
                  Xác nhận
                </DropdownMenuItem>
              </>
            )}
            {isConfirmed && meta?.canReceive && (
              <>
                <DropdownMenuSeparator />
                <DropdownMenuItem onClick={() => meta?.handleReceive?.(po)}>
                  <Package className="mr-2 h-4 w-4" />
                  Nhận hàng
                </DropdownMenuItem>
              </>
            )}
            {!isReceived && meta?.canCancel && (
              <>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={() => meta?.handleCancel?.(po)}
                  className="text-destructive"
                >
                  <X className="mr-2 h-4 w-4" />
                  Huy
                </DropdownMenuItem>
              </>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      )
    },
  },
]
