import { type ColumnDef } from '@tanstack/react-table'
import { Badge } from '@/components/ui/badge'
import { DataTableColumnHeader } from '@/components/data-table'
import { ArrowRight, Package, ShoppingCart, ClipboardEdit, Undo2, Lock, LockOpen } from 'lucide-react'
import type { InventoryTransactionResponse, MovementType } from '@/types/api'

export const movementTypeLabels: Record<MovementType, { label: string; variant: 'default' | 'secondary' | 'destructive' | 'outline'; icon: React.ReactNode; color: string }> = {
  IMPORT: { label: 'Nhập kho', variant: 'default', icon: <Package className="h-3 w-3" />, color: 'text-green-600 bg-green-50 border-green-200' },
  EXPORT: { label: 'Xuất kho', variant: 'destructive', icon: <Package className="h-3 w-3" />, color: 'text-red-600 bg-red-50 border-red-200' },
  SALE_OUT: { label: 'Xuất bán', variant: 'destructive', icon: <ShoppingCart className="h-3 w-3" />, color: 'text-orange-600 bg-orange-50 border-orange-200' },
  RETURN: { label: 'Nhập trả', variant: 'outline', icon: <Undo2 className="h-3 w-3" />, color: 'text-blue-600 bg-blue-50 border-blue-200' },
  ADJUSTMENT: { label: 'Điều chỉnh', variant: 'secondary', icon: <ClipboardEdit className="h-3 w-3" />, color: 'text-purple-600 bg-purple-50 border-purple-200' },
  RESERVATION: { label: 'Đặt giữ', variant: 'secondary', icon: <Lock className="h-3 w-3" />, color: 'text-amber-600 bg-amber-50 border-amber-200' },
  RESERVATION_RELEASE: { label: 'Hủy giữ', variant: 'outline', icon: <LockOpen className="h-3 w-3" />, color: 'text-gray-600 bg-gray-50 border-gray-200' },
}

export const movementsColumns: ColumnDef<InventoryTransactionResponse>[] = [
  {
    accessorKey: 'createdAt',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Thời gian" />
    ),
    cell: ({ row }) => {
      const date = new Date(row.original.createdAt)
      return (
        <div className="flex flex-col">
          <span className="font-medium">{date.toLocaleDateString('vi-VN')}</span>
          <span className="text-xs text-muted-foreground">{date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' })}</span>
        </div>
      )
    },
  },
  {
    accessorKey: 'productName',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Sản phẩm" />
    ),
    cell: ({ row }) => (
      <div className="flex items-center gap-3">
        {row.original.productImage ? (
          <img 
            src={row.original.productImage} 
            alt={row.original.productName ?? ''} 
            className="h-10 w-10 rounded-md object-cover border"
          />
        ) : (
          <div className="h-10 w-10 rounded-md bg-primary/10 flex items-center justify-center border">
            <Package className="h-5 w-5 text-primary" />
          </div>
        )}
        <span className="font-medium">{row.original.productName ?? `#${row.original.productId}`}</span>
      </div>
    ),
  },
  
  {
    accessorKey: 'quantity',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Số lượng" />
    ),
    cell: ({ row }) => {
      const qty = row.original.quantity
      const isPositive = qty > 0
      return (
        <span className={`text-lg font-bold ${isPositive ? 'text-green-600' : 'text-red-600'}`}>
          {isPositive ? '+' : ''}{qty}
        </span>
      )
    },
  },
  {
    id: 'stockChange',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Tồn kho" />
    ),
    cell: ({ row }) => {
      const { stockBefore, stockAfter } = row.original
      if (stockBefore === null || stockAfter === null) return <span className="text-muted-foreground">-</span>
      return (
        <div className="flex items-center gap-1.5 text-sm">
          <span className="text-muted-foreground">{stockBefore}</span>
          <ArrowRight className="h-3 w-3 text-muted-foreground" />
          <span className="font-bold text-primary">{stockAfter}</span>
        </div>
      )
    },
  },
  {
    accessorKey: 'referenceType',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Tham chiếu" />
    ),
    cell: ({ row }) => {
      const { referenceType, referenceId } = row.original
      if (!referenceType || !referenceId) return <span className="text-muted-foreground">-</span>

      if (referenceType === 'PURCHASE_ORDER') {
        return (
          <Badge variant="outline" className="bg-blue-50 text-blue-700 border-blue-200">
            PO #{referenceId}
          </Badge>
        )
      }
      if (referenceType === 'ORDER') {
        return (
          <Badge variant="outline" className="bg-orange-50 text-orange-700 border-orange-200">
            Đơn #{referenceId}
          </Badge>
        )
      }
      return <Badge variant="outline">{referenceType}</Badge>
    },
  },
  {
    accessorKey: 'note',
    header: ({ column }) => (
      <DataTableColumnHeader column={column} title="Ghi chú" />
    ),
    cell: ({ row }) => {
      const note = row.original.note
      return note ? (
        <div className="max-w-[250px]">
          <span className="text-sm text-muted-foreground line-clamp-2" title={note}>{note}</span>
        </div>
      ) : (
        <span className="text-muted-foreground">-</span>
      )
    },
  },
]
