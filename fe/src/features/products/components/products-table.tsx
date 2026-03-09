import { useState } from 'react'
import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import { ArrowUpDown, Edit, Trash2, MoreHorizontal, Loader2 } from 'lucide-react'
import { format } from 'date-fns'
import { useProducts } from '@/hooks/use-product'
import { Button } from '@/components/ui/button'
import { Checkbox } from '@/components/ui/checkbox'
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
import { useProductsContext } from './products-provider'
import type { ProductResponse } from '@/types/api'

const columns: ColumnDef<ProductResponse>[] = [
  {
    id: 'select',
    header: ({ table }) => (
      <Checkbox
        checked={
          table.getIsAllPageRowsSelected() ||
          (table.getIsSomePageRowsSelected() && 'indeterminate')
        }
        onCheckedChange={(value) => table.toggleAllPageRowsSelected(!!value)}
        aria-label="Select all"
      />
    ),
    cell: ({ row }) => (
      <Checkbox
        checked={row.getIsSelected()}
        onCheckedChange={(value) => row.toggleSelected(!!value)}
        aria-label="Select row"
      />
    ),
    enableSorting: false,
    enableHiding: false,
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
        Title
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
    cell: ({ row }) => <div className='font-medium max-w-[200px]'>{row.getValue('title')}</div>,
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
        Price
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
    cell: ({ row }) => {
      const price = row.getValue('price') as number
      const discountPrice = row.original.discountPrice
      return (
        <div className='whitespace-nowrap'>
          {discountPrice ? (
            <>
              <span className='text-destructive line-through mr-2'>
                {price.toLocaleString('vi-VN')}đ
              </span>
              <span className='font-semibold'>
                {discountPrice.toLocaleString('vi-VN')}đ
              </span>
            </>
          ) : (
            <span className='font-semibold'>{price.toLocaleString('vi-VN')}đ</span>
          )}
        </div>
      )
    },
  },
  {
    accessorKey: 'stockQuantity',
    header: 'Stock',
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
    header: 'Bestseller',
    cell: ({ row }) => {
      const isBestseller = row.getValue('isBestseller') as boolean
      return isBestseller ? (
        <Badge variant='default'>Yes</Badge>
      ) : (
        <Badge variant='secondary'>No</Badge>
      )
    },
  },
  {
    accessorKey: 'isActive',
    header: 'Status',
    cell: ({ row }) => {
      const isActive = row.getValue('isActive') as boolean
      return (
        <Badge variant={isActive ? 'default' : 'secondary'}>
          {isActive ? 'Active' : 'Inactive'}
        </Badge>
      )
    },
  },
  {
    accessorKey: 'createdAt',
    header: 'Created',
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
          <span className='sr-only'>Open menu</span>
          <MoreHorizontal className='h-4 w-4' />
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align='end'>
        <DropdownMenuLabel>Actions</DropdownMenuLabel>
        <DropdownMenuItem
          onClick={() => navigator.clipboard.writeText(product.id.toString())}
        >
          Copy product ID
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={() => {
            setSelectedProduct(product)
            setIsEditOpen(true)
          }}
        >
          <Edit className='mr-2 h-4 w-4' />
          Edit
        </DropdownMenuItem>
        <DropdownMenuItem
          className='text-destructive'
          onClick={() => {
            setSelectedProduct(product)
            setIsDeleteOpen(true)
          }}
        >
          <Trash2 className='mr-2 h-4 w-4' />
          Delete
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}

export function ProductsTable() {
  const [rowSelection, setRowSelection] = useState({})
  const { data, isLoading, error } = useProducts()

  const products = data?.content ?? []

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
        <p className='text-destructive'>Failed to load products. Please try again.</p>
        <p className='text-muted-foreground text-sm mt-2'>{error.message}</p>
      </div>
    )
  }

  return (
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
                No products found.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </div>
  )
}
