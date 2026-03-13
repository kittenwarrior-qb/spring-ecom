import type { Row } from '@tanstack/react-table'
import { MoreHorizontal, Eye, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import type { OrderDetailResponse } from '@/types/api'
import { useUserOrders } from './user-orders-provider'

interface UserOrderRowActionsProps {
  row: Row<OrderDetailResponse>
}

export function UserOrderRowActions({ row }: UserOrderRowActionsProps) {
  const { setOpen, setCurrentRow } = useUserOrders()
  const order = row.original

  const canCancel = ['PENDING', 'CONFIRMED', 'PARTIALLY_CANCELLED'].includes(order.status)

  const handleViewDetail = () => {
    setCurrentRow(order)
    setOpen('detail')
  }

  const handleCancel = () => {
    setCurrentRow(order)
    setOpen('cancel')
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant='ghost'
          className='flex h-8 w-8 p-0 data-[state=open]:bg-muted'
        >
          <MoreHorizontal className='h-4 w-4' />
          <span className='sr-only'>Mở menu</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align='end' className='w-40'>
        <DropdownMenuItem onClick={handleViewDetail}>
          <Eye className='mr-2 h-4 w-4' />
          Xem chi tiết
        </DropdownMenuItem>
        {canCancel && (
          <>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={handleCancel} className='text-red-600'>
              <X className='mr-2 h-4 w-4' />
              Hủy đơn hàng
            </DropdownMenuItem>
          </>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}