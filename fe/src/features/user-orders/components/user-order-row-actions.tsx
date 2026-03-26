import { useState } from 'react'
import type { Row } from '@tanstack/react-table'
import { MoreHorizontal, Eye, X, CreditCard } from 'lucide-react'
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
  const [dropdownOpen, setDropdownOpen] = useState(false)

  const canCancel = !['CANCELLED', 'DELIVERED', 'SHIPPED'].includes(order.status)
  // Show payment button if paymentStatus is UNPAID, PENDING, or undefined (not PAID/REFUNDED/FAILED) for non-COD orders
  const needsPayment = order.paymentMethod !== 'COD' && 
    (!order.paymentStatus || ['UNPAID', 'PENDING'].includes(order.paymentStatus))

  const handleViewDetail = () => {
    setDropdownOpen(false)
    setTimeout(() => {
      setCurrentRow(order)
      setOpen('detail')
    }, 0)
  }

  const handleCancel = () => {
    setDropdownOpen(false)
    setTimeout(() => {
      setCurrentRow(order)
      setOpen('cancel')
    }, 0)
  }

  const handlePayment = () => {
    setDropdownOpen(false)
    // Navigate to payment-success page which handles payment for existing orders
    window.location.href = `/payment-success?orderNumber=${encodeURIComponent(order.orderNumber)}`
  }

  return (
    <DropdownMenu modal={false} open={dropdownOpen} onOpenChange={setDropdownOpen}>
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
        <DropdownMenuItem onSelect={handleViewDetail}>
          <Eye className='mr-2 h-4 w-4' />
          Xem chi tiết
        </DropdownMenuItem>
        {needsPayment && (
          <>
            <DropdownMenuSeparator />
            <DropdownMenuItem onSelect={handlePayment} className='text-green-600'>
              <CreditCard className='mr-2 h-4 w-4' />
              Thanh toán
            </DropdownMenuItem>
          </>
        )}
        {canCancel && (
          <>
            <DropdownMenuSeparator />
            <DropdownMenuItem onSelect={handleCancel} className='text-red-600'>
              <X className='mr-2 h-4 w-4' />
              Hủy đơn hàng
            </DropdownMenuItem>
          </>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  )
}