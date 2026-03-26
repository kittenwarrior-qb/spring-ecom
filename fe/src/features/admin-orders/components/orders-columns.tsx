import type { ColumnDef } from '@tanstack/react-table'
import { format } from 'date-fns'
import { Badge } from '@/components/ui/badge'
import { DataTableColumnHeader } from '@/components/data-table'

import type { OrderResponse, OrderStatus } from '@/types/api'
import { DataTableRowActions } from './data-table-row-actions'

export const statusStyles: Record<OrderStatus, { label: string; variant: 'outline' | 'default' | 'secondary' | 'destructive' }> = {
    PENDING: { label: 'Chờ xử lý', variant: 'outline' },
    PENDING_STOCK: { label: 'Chờ reserve stock', variant: 'outline' },
    STOCK_RESERVED: { label: 'Đã reserve stock', variant: 'secondary' },
    STOCK_FAILED: { label: 'Thất bại (hết hàng)', variant: 'destructive' },
    CONFIRMED: { label: 'Đã xác nhận', variant: 'secondary' },
    PROCESSING: { label: 'Đang xử lý', variant: 'default' },
    SHIPPED: { label: 'Đang giao', variant: 'default' },
    DELIVERED: { label: 'Đã giao', variant: 'secondary' },
    CANCELLED: { label: 'Đã hủy', variant: 'destructive' },
    PARTIALLY_CANCELLED: { label: 'Đã hủy 1 phần', variant: 'outline' },
}

export const ordersColumns: ColumnDef<OrderResponse>[] = [
    {
        accessorKey: 'orderNumber',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Mã đơn hàng' />
        ),
        cell: ({ row }) => <div className='font-medium'>{row.getValue('orderNumber')}</div>,
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'userEmail',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Email khách hàng' />
        ),
        cell: ({ row }) => <div>{row.getValue('userEmail')}</div>,
    },
    {
        accessorKey: 'total',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Tổng tiền' />
        ),
        cell: ({ row }) => {
            const amount = parseFloat(row.getValue('total'))
            const formatted = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND',
            }).format(amount)
            return <div className='font-medium'>{formatted}</div>
        },
    },
    {
        accessorKey: 'status',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Trạng thái' />
        ),
        cell: ({ row }) => {
            const status = row.getValue('status') as OrderStatus
            const style = statusStyles[status] || { label: status, variant: 'outline' }
            return (
                <Badge variant={style.variant}>
                    {style.label}
                </Badge>
            )
        },
        filterFn: (row, id, value) => {
            return value.includes(row.getValue(id))
        },
    },
    {
        accessorKey: 'createdAt',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Ngày tạo' />
        ),
        cell: ({ row }) => {
            return (
                <div className='text-gray-700 dark:text-gray-300'>
                    {format(new Date(row.getValue('createdAt')), 'dd/MM/yyyy HH:mm')}
                </div>
            )
        },
        enableSorting: true,
    },
    {
        id: 'actions',
        cell: ({ row }) => <DataTableRowActions row={row} />,
    },
]
