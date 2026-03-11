import { useState } from 'react'
import { flexRender, getCoreRowModel, useReactTable, type ColumnDef } from '@tanstack/react-table'
import { ArrowUpDown, Edit, Trash2, MoreHorizontal, Loader2 } from 'lucide-react'
import { format } from 'date-fns'
import { useCategories } from '@/hooks/use-category'
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
import { useCategoriesContext } from './categories-provider'
import type { CategoryResponse } from '@/types/api'

const columns: ColumnDef<CategoryResponse>[] = [
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
    accessorKey: 'name',
    header: ({ column }) => (
      <Button
        variant='ghost'
        onClick={() => column.toggleSorting(column.getIsSorted() === 'asc')}
      >
        Tên danh mục
        <ArrowUpDown className='ml-2 h-4 w-4' />
      </Button>
    ),
    cell: ({ row }) => <div className='font-medium'>{row.getValue('name')}</div>,
  },
  {
    accessorKey: 'slug',
    header: 'Slug',
    cell: ({ row }) => (
      <code className='rounded bg-muted px-2 py-1 text-sm'>{row.getValue('slug')}</code>
    ),
  },
  {
    accessorKey: 'description',
    header: 'Mô tả',
    cell: ({ row }) => {
      const description = row.getValue('description') as string | null
      return description ? (
        <span className='line-clamp-1 max-w-[200px]'>{description}</span>
      ) : (
        <span className='text-muted-foreground'>—</span>
      )
    },
  },
  {
    accessorKey: 'parentName',
    header: 'Danh mục cha',
    cell: ({ row }) => {
      const parentName = row.getValue('parentName') as string | null
      return parentName ? (
        <Badge variant='secondary'>{parentName}</Badge>
      ) : (
        <span className='text-muted-foreground'>—</span>
      )
    },
  },
  {
    accessorKey: 'displayOrder',
    header: 'Order',
    cell: ({ row }) => {
      const order = row.getValue('displayOrder') as number | null
      return order ?? <span className='text-muted-foreground'>—</span>
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
    cell: ({ row }) => <CategoryActions category={row.original} />,
  },
]

function CategoryActions({ category }: { category: CategoryResponse }) {
  const { setSelectedCategory, setIsEditOpen, setIsDeleteOpen } = useCategoriesContext()

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
          onClick={() => navigator.clipboard.writeText(category.id.toString())}
        >
          Copy category ID
        </DropdownMenuItem>
        <DropdownMenuSeparator />
        <DropdownMenuItem
          onClick={() => {
            setSelectedCategory(category)
            setIsEditOpen(true)
          }}
        >
          <Edit className='mr-2 h-4 w-4' />
          Edit
        </DropdownMenuItem>
        <DropdownMenuItem
          className='text-destructive'
          onClick={() => {
            setSelectedCategory(category)
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

export function CategoriesTable() {
  const [rowSelection, setRowSelection] = useState({})
  const { data: categories = [], isLoading, error } = useCategories()

  const table = useReactTable({
    data: categories,
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
        <p className='text-destructive'>Failed to load categories. Please try again.</p>
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
                No categories found.
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </div>
  )
}
