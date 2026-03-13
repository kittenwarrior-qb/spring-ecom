import { createFileRoute } from '@tanstack/react-router'
import { UserOrdersTable } from '@/features/user-orders/components/user-orders-table'
import { UserOrdersProvider } from '@/features/user-orders/components/user-orders-provider'
import { UserOrderDetailDialog } from '@/features/user-orders/components/user-order-detail-dialog'
import { UserOrderCancelDialog } from '@/features/user-orders/components/user-order-cancel-dialog'

export const Route = createFileRoute('/profile/orders')({
  component: OrdersPage,
})

function OrdersPage() {
  return (
    <UserOrdersProvider>
      <div className="space-y-6">
        <div className="space-y-4">
          <div>
            <h2 className='text-2xl font-bold tracking-tight'>Đơn hàng của tôi</h2>
            <p className="text-muted-foreground">Xem và quản lý đơn hàng của bạn</p>
          </div>
          
          <div className='flex-1 copy-scrollbar -mr-2 pr-2'>
            <UserOrdersTable />
          </div>
        </div>
      </div>

      <UserOrderDetailDialog />
      <UserOrderCancelDialog />
    </UserOrdersProvider>
  )
}
