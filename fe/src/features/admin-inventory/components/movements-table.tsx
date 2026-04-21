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
import { useInventoryMovements } from '@/hooks/use-inventory'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { DataTablePagination, DataTableToolbar } from '@/components/data-table'
import { movementsColumns as columns } from './movements-columns'
import type { InventoryTransactionResponse, MovementType } from '@/types/api'

interface MovementsTableProps {
  search: Record<string, unknown>
  navigate: NavigateFn
}

export function MovementsTable({ search, navigate }: MovementsTableProps) {
  const [rowSelection, setRowSelection] = useState({})
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
  const [sorting, setSorting] = useState<SortingState>([
    { id: 'createdAt', desc: true }
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
      { columnId: 'movementType', searchKey: 'movementType', type: 'array' },
    ],
  })

  const movementTypeFilter = (columnFilters as unknown as Record<string, unknown>).movementType as MovementType[] | undefined

  const { data: pageData, isLoading, error } = useInventoryMovements({
    page: pagination.pageIndex,
    size: pagination.pageSize,
    movementType: movementTypeFilter?.[0] as MovementType | undefined,
  })

  const data: InventoryTransactionResponse[] = pageData?.content ?? []

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
        <p className='text-destructive'>Không thể tải lịch sử kho. Vui lòng thử lại.</p>
        <p className='text-muted-foreground text-sm mt-2'>{error.message}</p>
      </div>
    )
  }

  return (
    <div className='flex flex-1 flex-col gap-4'>
      <DataTableToolbar
        table={table}
        searchPlaceholder='Tìm kiếm...'
        filters={[
          {
            columnId: 'movementType',
            title: 'Loại giao dịch',
            options: [
              { label: 'Nhập mua', value: 'PURCHASE_IN' },
              { label: 'Xuất bán', value: 'SALE_OUT' },
              { label: 'Nhập trả', value: 'RETURN_IN' },
              { label: 'Điều chỉnh', value: 'ADJUSTMENT' },
              { label: 'Đặt giữ', value: 'RESERVATION' },
              { label: 'Hủy giữ', value: 'RESERVATION_RELEASE' },
            ],
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
                  Không có giao dịch nào.
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
