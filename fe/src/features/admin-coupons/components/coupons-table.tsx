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
import { useAdminCoupons, useDeleteCoupon } from '@/hooks/use-coupon'
import { useCouponsContext } from './coupons-provider'
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
import { couponsColumns as columns } from './coupons-columns'
import { type CouponResponse } from '@/types/api'
import { toast } from 'sonner'

interface CouponsTableProps {
  search: Record<string, unknown>
  navigate: NavigateFn
}

export function CouponsTable({ search, navigate }: CouponsTableProps) {
  const [rowSelection, setRowSelection] = useState({})
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
  const [sorting, setSorting] = useState<SortingState>([
    { id: 'createdAt', desc: true }
  ])

  const { setSelectedCoupon, setIsEditDialogOpen } = useCouponsContext()
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
      { columnId: 'code', searchKey: 'code', type: 'string' },
      { columnId: 'isActive', searchKey: 'isActive', type: 'array' },
    ],
  })

  const { data: pageData, isLoading, error } = useAdminCoupons({
    page: pagination.pageIndex,
    size: pagination.pageSize,
  })

  const deleteCoupon = useDeleteCoupon()

  const data: CouponResponse[] = pageData?.content ?? []

  const canEdit = hasPermission('COUPON_UPDATE')
  const canDelete = hasPermission('COUPON_DELETE')

  const handleEdit = (coupon: CouponResponse) => {
    setSelectedCoupon(coupon)
    setIsEditDialogOpen(true)
  }

  const handleDelete = (coupon: CouponResponse) => {
    if (confirm(`Bạn có chắc muốn xóa coupon "${coupon.code}"?`)) {
      deleteCoupon.mutate(coupon.id, {
        onSuccess: () => {
          toast.success('Đã xóa coupon thành công')
        },
        onError: (error) => {
          toast.error(`Lỗi xóa coupon: ${error.message}`)
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
        <p className='text-destructive'>Không thể tải danh sách coupon. Vui lòng thử lại.</p>
        <p className='text-muted-foreground text-sm mt-2'>{error.message}</p>
      </div>
    )
  }

  return (
    <div className='flex flex-1 flex-col gap-4'>
      <DataTableToolbar
        table={table}
        searchPlaceholder='Tìm kiếm mã coupon...'
        searchKey='code'
        filters={[
          {
            columnId: 'isActive',
            title: 'Trạng thái',
            options: [
              { label: 'Hoạt động', value: 'true' },
              { label: 'Không hoạt động', value: 'false' },
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
                  Không tìm thấy coupon nào.
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
