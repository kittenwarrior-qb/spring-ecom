import { useState } from 'react'
import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import { ArrowUpDown, Edit, Trash2, MoreHorizontal, Loader2, ChevronLeft, ChevronRight } from 'lucide-react'
import { format } from 'date-fns'
import { useProducts } from '@/hooks/use-product'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useProductsContext } from './products-provider'
import type { ProductResponse } from '@/types/api'

type ProductsTableProps = {
  search: {
    page: number
    size: number
    sort: string
  }
  navigate: (options: { search: { page?: number; size?: number; sort?: string } }) => void
}

/* react-compiler-ignore */
const columns: ColumnDef<ProductResponse>[] = [
  {
    accessorKey: 'coverImageUrl',
    header: 'Ảnh bìa',
    cell: ({ row }) => {
      const imageUrl = row.getValue('coverImageUrl') as string | null
      return (
        <div className="w-12 h-16 rounded overflow-hidden bg-muted">
          {imageUrl ? (
            <img 
              src={imageUrl} 
              alt={row.getValue('title') as string}
              className="w-full h-full object-cover"
              onError={(e) => {
                e.currentTarget.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="48" height="64" viewBox="0 0 48 64"%3E%3Crect width="48" height="64" fill="%23f3f4f6"/%3E%3Ctext x="24" y="32" text-anchor="middle" dy=".3em" font-family="sans-serif" font-size="8" fill="%239ca3af"%3EKhông ảnh%3C/text%3E%3C/svg%3E'
              }}
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center text-muted-foreground text-xs">
              Không ảnh
            </div>
          )}
        </div>
      )
    },
    enableSorting: false,
  },
  {
    accessorKey: 'id',
    header: ({ column }) => (
      <Button
        variant='ghost'
        onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
      >
        ID
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
  },
  {
    accessorKey: 'title',
    header: ({ column }) => (
      <Button
        variant='ghost'
        onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
      >
        Tiêu đề
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
    cell: ({ row }) => <div className='font-medium max-w-[200px]'>{row.getValue('title')}</div>,
  },
  {
    accessorKey: 'categoryName',
    header: 'Danh mục',
    cell: ({ row }) => {
      const categoryName = row.getValue('categoryName') as string | null
      return categoryName ? (
        <Badge variant='outline'>{categoryName}</Badge>
      ) : (
        <span className='text-muted-foreground'>—</span>
      )
    },
  },
  {
    accessorKey: 'slug',
    header: 'Slug',
    cell: ({ row }) => (
      <code className='rounded bg-muted px-2 py-1 text-sm'>{row.getValue('slug')}</code>
    ),
  },
  {
    accessorKey: 'author',
    header: 'Author',
    cell: ({ row }) => {
      const author = row.getValue('author') as string | null
      return author ? <span>{author}</span> : <span className='text-muted-foreground'>—</span>
    },
  },
  {
    accessorKey: 'price',
    header: ({ column }) => (
      <Button
        variant='ghost'
        onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
      >
        Giá gốc
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
    cell: ({ row }) => {
      const price = row.getValue('price') as number
      return (
        <div className='whitespace-nowrap'>
          <span className='font-semibold'>{price.toLocaleString('vi-VN')}đ</span>
        </div>
      )
    },
  },
  {
    accessorKey: 'discountPrice',
    header: ({ column }) => (
      <Button
        variant='ghost'
        onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
      >
        Giá khuyến mãi
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
    cell: ({ row }) => {
      const discountPrice = row.getValue('discountPrice') as number | null
      return (
        <div className='whitespace-nowrap'>
          {discountPrice ? (
            <span className='font-semibold text-green-600'>{discountPrice.toLocaleString('vi-VN')}đ</span>
          ) : (
            <span className='text-muted-foreground'>—</span>
          )}
        </div>
      )
    },
  },
  {
    accessorKey: 'stockQuantity',
    header: 'Tồn kho',
    cell: ({ row }) => {
      const stock = row.getValue('stockQuantity') as number
      return (
        <Badge variant={stock > 0 ? 'default' : 'destructive'}>
          {stock}
        </Badge>
      )
    },
  },
  {
    accessorKey: 'isBestseller',
    header: 'Bán chạy',
    cell: ({ row }) => {
      const isBestseller = row.getValue('isBestseller') as boolean
      return isBestseller ? (
        <Badge variant='default'>Có</Badge>
      ) : (
        <Badge variant='secondary'>Không</Badge>
      )
    },
  },
  {
    accessorKey: 'isActive',
    header: 'Trạng thái',
    cell: ({ row }) => {
      const isActive = row.getValue('isActive') as boolean
      return (
        <Badge variant={isActive ? 'default' : 'secondary'}>
          {isActive ? 'Hoạt động' : 'Không hoạt động'}
        </Badge>
      )
    },
  },
  {
    accessorKey: 'createdAt',
    header: 'Ngày tạo',
    cell: ({ row }) => {
      const date = row.getValue('createdAt') as string
      return format(new Date(date), 'MMM d, yyyy')
    },
  },
  {
    id: 'actions',
    enableHiding: false,
    cell: ({ row }) => <ProductActions product={row.original} />,
  },
]

function ProductActions({ product }: { product: ProductResponse }) {
  const { setSelectedProduct, setIsEditOpen, setIsDeleteOpen } = useProductsContext()

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant='ghost' className='h-8 w-8 p-0'>
          <span className='sr-only'>Mở menu</span>
          <MoreHorizontal className='h-4 w-4' />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align='end'>
        <DropdownMenuLabel>Thao tác</DropdownMenuLabel>
        <DropdownMenuItem
          onClick={() => navigator.clipboard.writeText(product.id.toString())}
        >
          Sao chép ID sản phẩm
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={() => {
            setSelectedProduct(product)
            setIsEditOpen(true)
          }}
        >
          <Edit className='mr-2 h-4 w-4' />
          Chỉnh sửa
        </DropdownMenuItem>
        <DropdownMenuItem
          className='text-destructive'
          onClick={() => {
            setSelectedProduct(product)
            setIsDeleteOpen(true)
          }}
        >
          <Trash2 className='mr-2 h-4 w-4' />
          Xóa
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export function ProductsTable({ search, navigate }: ProductsTableProps) {
  const [rowSelection, setRowSelection] = useState({})
  const { data, isLoading, error } = useProducts(search.page, search.size, search.sort)

  const products = data?.content ?? []
  const totalPages = data?.totalPages ?? 0
  const totalElements = data?.totalElements ?? 0
  const currentPage = search.page

  // React Compiler warning expected here - TanStack Table API returns functions that cannot be safely memoized
  // This is known limitation and doesn't affect functionality
  const table = useReactTable({
    data: products,
    columns,
    getCoreRowModel: getCoreRowModel(),
    onRowSelectionChange: setRowSelection,
    state: {
      rowSelection,
    },
  })

  if (error) {
    return (
      <div className='rounded-md border p-8 text-center'>
        <p className='text-destructive'>Không thể tải danh sách sản phẩm. Vui lòng thử lại.</p>
        <p className='text-muted-foreground text-sm mt-2'>{error.message}</p>
      </div>
    )
  }

  return (
    <div className='space-y-4'>
      <div className='rounded-md border'>
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => (
                  <TableHead key={header.id}>
                    {header.isPlaceholder
                      ? null
                      : flexRender(header.column.columnDef.header, header.getContext())}
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
            ) : table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && 'selected'}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(cell.column.columnDef.cell, cell.getContext())}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={columns.length} className='h-24 text-center'>
                  Không tìm thấy sản phẩm nào.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      <div className='flex items-center justify-between'>
        <div className='flex items-center space-x-2'>
          <p className='text-sm font-medium'>Hiển thị</p>
          <Select
            value={search.size.toString()}
            onValueChange={(value) => {
              navigate({
                search: {
                  ...search,
                  size: parseInt(value),
                  page: 0, // Reset to first page when changing page size
                },
              })
            }}
          >
            <SelectTrigger className='h-8 w-[70px]'>
              <SelectValue />
            </SelectTrigger>
            <SelectContent side='top'>
              {[5, 10, 20, 30, 50].map((pageSize) => (
                <SelectItem key={pageSize} value={pageSize.toString()}>
                  {pageSize}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <p className='text-sm font-medium'>
            trên tổng số {totalElements} sản phẩm
          </p>
        </div>

        <div className='flex items-center space-x-2'>
          <p className='text-sm font-medium'>
            Trang {currentPage + 1} / {Math.max(1, totalPages)}
          </p>
          <div className='flex items-center space-x-2'>
            <Button
              variant='outline'
              size='sm'
              onClick={() => {
                navigate({
                  search: {
                    ...search,
                    page: currentPage - 1,
                  },
                })
              }}
              disabled={currentPage === 0}
            >
              <ChevronLeft className='h-4 w-4' />
              Trước
            </Button>
            <Button
              variant='outline'
              size='sm'
              onClick={() => {
                navigate({
                  search: {
                    ...search,
                    page: currentPage + 1,
                  },
                })
              }}
              disabled={currentPage >= totalPages - 1}
            >
              Sau
              <ChevronRight className='h-4 w-4' />
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
