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
import { useSuppliers, useDeleteSupplier } from '@/hooks/use-supplier'
import { useSuppliersContext } from './suppliers-provider'
import { usePermissions } from '@/hooks/use-permissions'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { DataTablePagination, DataTableToolbar } from '@/components/data-table'
import { suppliersColumns as columns } from './suppliers-columns'
import type { SupplierResponse } from '@/types/api'
import { toast } from 'sonner'

interface SuppliersTableProps {
  search: Record<string, unknown>
  navigate: NavigateFn
}

export function SuppliersTable({ search, navigate }: SuppliersTableProps) {
  const [rowSelection, setRowSelection] = useState({})
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
  const [sorting, setSorting] = useState<SortingState>([
    { id: 'createdAt', desc: true }
  ])

  const { setSelectedSupplier, setIsEditDialogOpen } = useSuppliersContext()
  const { hasPermission } = usePermissions()

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
      { columnId: 'name', searchKey: 'name', type: 'string' },
      { columnId: 'isActive', searchKey: 'isActive', type: 'array' },
    ],
  })

  const { data: pageData, isLoading, error } = useSuppliers({
    page: pagination.pageIndex,
    size: pagination.pageSize,
  })

  const deleteSupplier = useDeleteSupplier()

  const data: SupplierResponse[] = pageData?.content ?? []

  const canEdit = hasPermission('PRODUCT_UPDATE')
  const canDelete = hasPermission('PRODUCT_DELETE')

  const handleEdit = (supplier: SupplierResponse) => {
    setSelectedSupplier(supplier)
    setIsEditDialogOpen(true)
  }

  const handleDelete = (supplier: SupplierResponse) => {
    if (confirm(`Bạn có chắc muốn xóa nhà cung cấp "${supplier.name}"?`)) {
      deleteSupplier.mutate(supplier.id, {
        onSuccess: () => {
          toast.success('Đã xóa nhà cung cấp thành công')
        },
        onError: (error) => {
          toast.error(`Lỗi xóa nhà cung cấp: ${error.message}`)
        }
      })
    }
  }

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
    meta: {
      handleEdit,
      handleDelete,
      canEdit,
      canDelete,
    },
  })

  useEffect(() => {
    ensurePageInRange(table.getPageCount())
  }, [table, ensurePageInRange])

  if (error) {
    return (
      <div className='rounded-md border p-8 text-center'>
        <p className='text-destructive'>Không thể tải danh sách nhà cung cấp. Vui lòng thử lại.</p>
        <p className='text-muted-foreground text-sm mt-2'>{error.message}</p>
      </div>
    )
  }

  return (
    <div className='flex flex-1 flex-col gap-4'>
      <DataTableToolbar
        table={table}
        searchPlaceholder='Tìm kiếm nhà cung cấp...'
        searchKey='name'
        filters={[
          {
            columnId: 'isActive',
            title: 'Trạng thái',
            options: [
              { label: 'Hoạt động', value: 'true' },
              { label: 'Ngừng', value: 'false' },
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
                  Không tìm thấy nhà cung cấp nào.
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
