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
import { usePurchaseOrders, useConfirmPurchaseOrder, useCancelPurchaseOrder } from '@/hooks/use-purchase-order'
import { usePurchaseOrdersContext } from './purchase-orders-provider'
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
import { purchaseOrdersColumns as columns } from './purchase-orders-columns'
import type { PurchaseOrderResponse, PurchaseOrderStatus } from '@/types/api'
import { toast } from 'sonner'

interface PurchaseOrdersTableProps {
  search: Record<string, unknown>
  navigate: NavigateFn
}

export function PurchaseOrdersTable({ search, navigate }: PurchaseOrdersTableProps) {
  const [rowSelection, setRowSelection] = useState({})
  const [columnVisibility, setColumnVisibility] = useState<VisibilityState>({})
  const [sorting, setSorting] = useState<SortingState>([
    { id: 'createdAt', desc: true }
  ])

  const { setSelectedPO, setIsDetailOpen, setIsEditOpen, setIsReceiveOpen } = usePurchaseOrdersContext()
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
      { columnId: 'status', searchKey: 'status', type: 'array' },
    ],
  })

  const statusFilter = (columnFilters as Record<string, unknown>).status as PurchaseOrderStatus[] | undefined

  const { data: pageData, isLoading, error } = usePurchaseOrders({
    page: pagination.pageIndex,
    size: pagination.pageSize,
    status: statusFilter?.[0] as PurchaseOrderStatus | undefined,
  })

  const confirmPO = useConfirmPurchaseOrder()
  const cancelPO = useCancelPurchaseOrder()

  const data: PurchaseOrderResponse[] = pageData?.content ?? []

  const canEdit = hasPermission('PRODUCT_UPDATE')
  const canConfirm = hasPermission('PRODUCT_UPDATE')
  const canReceive = hasPermission('PRODUCT_UPDATE')
  const canCancel = hasPermission('PRODUCT_UPDATE')

  const handleView = (po: PurchaseOrderResponse) => {
    setSelectedPO(po)
    setIsDetailOpen(true)
  }

  const handleEdit = (po: PurchaseOrderResponse) => {
    setSelectedPO(po)
    setIsEditOpen(true)
  }

  const handleConfirm = (po: PurchaseOrderResponse) => {
    if (confirm(`Bạn có chắc muốn xác nhận đơn nhập "${po.poNumber}"?`)) {
      confirmPO.mutate(po.id, {
        onSuccess: () => {
          toast.success('Đã xác nhận đơn nhập hàng')
        },
        onError: (error) => {
          toast.error(`Lỗi xác nhận: ${error.message}`)
        }
      })
    }
  }

  const handleReceive = (po: PurchaseOrderResponse) => {
    setSelectedPO(po)
    setIsReceiveOpen(true)
  }

  const handleCancel = (po: PurchaseOrderResponse) => {
    if (confirm(`Bạn có chắc muốn hủy đơn nhập "${po.poNumber}"?`)) {
      cancelPO.mutate(po.id, {
        onSuccess: () => {
          toast.success('Đã hủy đơn nhập hàng')
        },
        onError: (error) => {
          toast.error(`Lỗi hủy đơn: ${error.message}`)
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
      handleView,
      handleEdit,
      handleConfirm,
      handleReceive,
      handleCancel,
      canEdit,
      canConfirm,
      canReceive,
      canCancel,
    },
  })

  useEffect(() => {
    ensurePageInRange(table.getPageCount())
  }, [table, ensurePageInRange])

  if (error) {
    return (
      <div className='rounded-md border p-8 text-center'>
        <p className='text-destructive'>Không thể tải danh sách đơn nhập. Vui lòng thử lại.</p>
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
            columnId: 'status',
            title: 'Trạng thái',
            options: [
              { label: 'Ch\u01B0a x\u00E1c nh\u1EADn', value: 'DRAFT' },
              { label: 'Đã xác nhận', value: 'CONFIRMED' },
              { label: 'Đã nhận', value: 'RECEIVED' },
              { label: 'Nhận một phần', value: 'PARTIALLY_RECEIVED' },
              { label: 'Đã hủy', value: 'CANCELLED' },
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
                  Không tìm thấy đơn nhập nào.
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
