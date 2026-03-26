import { useEffect, useState } from 'react'
import {
    type SortingState,
    type VisibilityState,
    flexRender,
    getCoreRowModel,
    getFacetedRowModel,
    getFacetedUniqueValues,
    getFilteredRowModel,
    getPaginationRowModel,
    getSortedRowModel,
    useReactTable,
} from '@tanstack/react-table'
import { Loader2 } from 'lucide-react'
import { type NavigateFn, useTableUrlState } from '@/hooks/use-table-url-state'

import { useAdminOrders } from '@/hooks/use-order'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'
import { DataTablePagination, DataTableToolbar } from '@/components/data-table'
import { ordersColumns as columns, statusStyles } from './orders-columns'
import { type OrderResponse } from '@/types/api'

interface OrdersTableProps {
    search: Record<string, unknown>
    navigate: NavigateFn
}

export function OrdersTable({ search, navigate }: OrdersTableProps) {
    const [rowSelection, setRowSelection] = useState({})
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
    const [sorting, setSorting] = useState<SortingState>([
        { id: 'createdAt', desc: true } // Default sort by newest orders first
    ])

    const {
        columnFilters,
        onColumnFiltersChange,
        pagination,
        onPaginationChange,
        ensurePageInRange,
    } = useTableUrlState({
        search,
        navigate,
        pagination: { defaultPage: 1, defaultPageSize: 10 },
        globalFilter: { enabled: false },
        columnFilters: [
            { columnId: 'orderNumber', searchKey: 'orderNumber', type: 'string' },
            { columnId: 'status', searchKey: 'status', type: 'array' },
        ],
    })

    const { data: pageData, isLoading, error } = useAdminOrders({
        page: pagination.pageIndex,
        size: pagination.pageSize,
    })

    const data: OrderResponse[] = pageData?.content ?? []

    const table = useReactTable({
        data,
        columns,
        state: {
            sorting,
            pagination,
            rowSelection,
            columnFilters,
            columnVisibility,
        },
        enableRowSelection: true,
        onPaginationChange,
        onColumnFiltersChange,
        onRowSelectionChange: setRowSelection,
        onSortingChange: setSorting,
        onColumnVisibilityChange: setColumnVisibility,
        getPaginationRowModel: getPaginationRowModel(),
        getCoreRowModel: getCoreRowModel(),
        getFilteredRowModel: getFilteredRowModel(),
        getSortedRowModel: getSortedRowModel(),
        getFacetedRowModel: getFacetedRowModel(),
        getFacetedUniqueValues: getFacetedUniqueValues(),
        manualPagination: true,
        pageCount: pageData?.totalPages ?? -1,
    })

    useEffect(() => {
        ensurePageInRange(table.getPageCount())
    }, [table, ensurePageInRange])

    if (error) {
        return (
            <div className='rounded-md border p-8 text-center'>
                <p className='text-destructive'>Không thể tải danh sách đơn hàng. Vui lòng thử lại.</p>
                <p className='text-muted-foreground text-sm mt-2'>{error.message}</p>
            </div>
        )
    }

    return (
        <div className='flex flex-1 flex-col gap-4'>
            <DataTableToolbar
                table={table}
                searchPlaceholder='Tìm kiếm mã đơn hàng...'
                searchKey='orderNumber'
                filters={[
                    {
                        columnId: 'status',
                        title: 'Trạng thái',
                        options: Object.entries(statusStyles).map(([key, value]) => ({
                            label: value.label,
                            value: key,
                        })),
                    },
                ]}
            />
            <div className='overflow-hidden rounded-md border'>
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id}>
                                {headerGroup.headers.map((header) => (
                                    <TableHead key={header.id} colSpan={header.colSpan}>
                                        {header.isPlaceholder
                                            ? null
                                            : flexRender(
                                                header.column.columnDef.header,
                                                header.getContext()
                                            )}
                                    </TableHead>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={columns.length} className='h-24 text-center'>
                                    <Loader2 className='mx-auto h-6 w-6 animate-spin' />
                                </TableCell>
                            </TableRow>
                        ) : data.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow key={row.id}>
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id}>
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={columns.length} className='h-24 text-center'>
                                    Không tìm thấy đơn hàng nào.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <DataTablePagination table={table} />
        </div>
    )
}
