import { ColumnDef } from '@tanstack/react-table'
import { format } from 'date-fns'
import { Badge } from '@/components/ui/badge'
import { Checkbox } from '@/components/ui/checkbox'
import { DataTableColumnHeader } from '@/components/data-table/data-table-column-header'
import { OrderResponse, OrderStatus } from '@/types/api'
import { DataTableRowActions } from './data-table-row-actions'

export const statusStyles: Record<OrderStatus, { label: string; variant: 'outline' | 'default' | 'secondary' | 'destructive' }> = {
    PENDING: { label: 'Pending', variant: 'outline' },
    CONFIRMED: { label: 'Confirmed', variant: 'secondary' },
    PROCESSING: { label: 'Processing', variant: 'default' },
    SHIPPED: { label: 'Shipped', variant: 'default' },
    DELIVERED: { label: 'Delivered', variant: 'secondary' },
    CANCELLED: { label: 'Cancelled', variant: 'destructive' },
}

export const ordersColumns: ColumnDef<OrderResponse>[] = [
    {
        id: 'select',
        header: ({ table }) => (
            <Checkbox
                checked={
                    table.getIsAllPageRowsSelected() ||
                    (table.getIsSomePageRowsSelected() && 'indeterminate')
                }
                onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
                aria-label='Select all'
                className='translate-y-[2px]'
            />
        ),
        cell: ({ row }) => (
            <Checkbox
                checked={row.getIsSelected()}
                onCheckedChange={(value) => row.toggleSelected(!!value)}
                aria-label='Select row'
                className='translate-y-[2px]'
            />
        ),
        enableSorting: false,
        enableHiding: false,
    },
    {
        accessorKey: 'orderNumber',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Order #' />
        ),
        cell: ({ row }) => <div className='font-medium'>{row.getValue('orderNumber')}</div>,
        enableSorting: true,
        enableHiding: false,
    },
    {
        accessorKey: 'recipientName',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Customer' />
        ),
        cell: ({ row }) => <div>{row.getValue('recipientName')}</div>,
    },
    {
        accessorKey: 'total',
        header: ({ column }) => (
            <DataTableColumnHeader column={column} title='Total' />
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
            <DataTableColumnHeader column={column} title='Status' />
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
            <DataTableColumnHeader column={column} title='Date' />
        ),
        cell: ({ row }) => {
            return (
                <div className='text-muted-foreground'>
                    {format(new Date(row.getValue('createdAt')), 'dd/MM/yyyy HH:mm')}
                </div>
            )
        },
    },
    {
        id: 'actions',
        cell: ({ row }) => <DataTableRowActions row={row} />,
    },
]
