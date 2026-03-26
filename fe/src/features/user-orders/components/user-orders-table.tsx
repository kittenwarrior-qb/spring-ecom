import { useState } from 'react'
import {
    type SortingState,
    type VisibilityState,
    type ColumnFiltersState,
    type PaginationState,
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
import { useMyOrdersWithItems } from '@/hooks/use-order'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'
import { DataTablePagination, DataTableToolbar } from '@/components/data-table'
import { userOrdersColumns as columns, statusStyles } from './user-orders-columns'
import type { OrderDetailResponse } from '@/types/api'

export function UserOrdersTable() {
    const [rowSelection, setRowSelection] = useState({})
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
    const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([])
    const [sorting, setSorting] = useState<SortingState>([
        { id: 'createdAt', desc: true } // Default sort by newest orders first
    ])
    const [pagination, setPagination] = useState<PaginationState>({
        pageIndex: 0,
        pageSize: 10,
    })

    const { data: pageData, isLoading, error } = useMyOrdersWithItems({
        page: pagination.pageIndex,
        size: pagination.pageSize,
    })

    const data: OrderDetailResponse[] = pageData?.content ?? []

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
        onPaginationChange: setPagination,
        onColumnFiltersChange: setColumnFilters,
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