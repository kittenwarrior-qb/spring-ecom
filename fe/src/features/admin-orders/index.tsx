import { useNavigate, useSearch } from '@tanstack/react-router'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { ThemeSwitch } from '@/components/theme-switch'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Package, DollarSign, Clock, CheckCircle } from 'lucide-react'
import { OrdersTable } from './components/orders-table'
import { OrdersProvider } from './components/orders-provider'
import { OrderStatusDialog } from './components/order-status-dialog'
import { OrderDetailDialog } from './components/order-detail-dialog'
import { useAdminOrders } from '@/hooks/use-orders'

export function AdminOrders() {
  const navigate = useNavigate()
  const search = useSearch({ from: '/admin/' })
  const { data: ordersData } = useAdminOrders({ page: 0, size: 100 }) // Load some for stats

  const orders = ordersData?.content || []
  const totalRevenue = orders.reduce((sum, order) => sum + (order.total || 0), 0)
  const pendingOrders = orders.filter((order) => order.status === 'PENDING').length
  const completedOrders = orders.filter((order) => order.status === 'DELIVERED').length

  return (
    <OrdersProvider>
      <Header>
        <div className='flex items-center gap-2 px-4'>
          <h1 className='text-xl font-bold'>Quản Lý Đơn Hàng</h1>
        </div>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ThemeSwitch />
          <ProfileDropdown />
        </div>
      </Header>

      <Main>
        <div className='mb-6 flex flex-col gap-4'>
          <h2 className='text-2xl font-bold tracking-tight'>Tổng Quan Đơn Hàng</h2>

          <div className='grid gap-4 md:grid-cols-2 lg:grid-cols-4'>
            <Card>
              <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                <CardTitle className='text-sm font-medium'>Tổng Đơn Hàng</CardTitle>
                <Package className='h-4 w-4 text-muted-foreground' />
              </CardHeader>
              <CardContent>
                <div className='text-2xl font-bold'>{ordersData?.totalElements || 0}</div>
                <p className='text-sm text-gray-600 dark:text-gray-400'>Từ tất cả khách hàng</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                <CardTitle className='text-sm font-medium'>Doanh Thu</CardTitle>
                <DollarSign className='h-4 w-4 text-muted-foreground' />
              </CardHeader>
              <CardContent>
                <div className='text-2xl font-bold'>
                  {totalRevenue.toLocaleString('vi-VN')}đ
                </div>
                <p className='text-sm text-gray-600 dark:text-gray-400'>Tổng giá trị bán hàng</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                <CardTitle className='text-sm font-medium'>Chờ Xử Lý</CardTitle>
                <Clock className='h-4 w-4 text-muted-foreground' />
              </CardHeader>
              <CardContent>
                <div className='text-2xl font-bold'>{pendingOrders}</div>
                <p className='text-sm text-gray-600 dark:text-gray-400'>Đang chờ xử lý</p>
              </CardContent>
            </Card>

            <Card>
              <CardHeader className='flex flex-row items-center justify-between space-y-0 pb-2'>
                <CardTitle className='text-sm font-medium'>Đã Giao</CardTitle>
                <CheckCircle className='h-4 w-4 text-muted-foreground' />
              </CardHeader>
              <CardContent>
                <div className='text-2xl font-bold'>{completedOrders}</div>
                <p className='text-sm text-gray-600 dark:text-gray-400'>Giao hàng thành công</p>
              </CardContent>
            </Card>
          </div>
        </div>

        <div className='flex-1 copy-scrollbar -mr-2 pr-2'>
          <OrdersTable search={search} navigate={navigate as unknown} />
        </div>
      </Main>

      <OrderStatusDialog />
      <OrderDetailDialog />
    </OrdersProvider>
  )
}
