import { useState, useMemo } from 'react'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { subDays, isWithinInterval } from 'date-fns'
import { type DateRange } from 'react-day-picker'
import { type NavigateFn } from '@/hooks/use-table-url-state'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { ThemeSwitch } from '@/components/theme-switch'
import { OrdersTable } from './components/orders-table'
import { OrdersProvider } from './components/orders-provider'
import { OrderStatusDialog } from './components/order-status-dialog'
import { OrderDetailDialog } from './components/order-detail-dialog'
import { RevenueChart } from './components/revenue-chart'
import { DateRangePicker } from './components/date-range-picker'
import { AnalyticsCards } from './components/analytics-cards'
import { useAdminOrders } from '@/hooks/use-orders'

export function AdminOrders() {
  const navigate = useNavigate()
  const search = useSearch({ from: '/admin/' })
  
  // Date range state - default to last 30 days
  const [dateRange, setDateRange] = useState<DateRange | undefined>({
    from: subDays(new Date(), 29),
    to: new Date()
  })

  // Fetch orders with larger page size for analytics
  const { data: ordersData } = useAdminOrders({ page: 0, size: 1000 })
  const allOrders = ordersData?.content || []

  // Filter orders by date range
  const filteredOrders = useMemo(() => {
    if (!dateRange?.from || !dateRange?.to) return allOrders
    
    return allOrders.filter(order => {
      const orderDate = new Date(order.createdAt)
      return isWithinInterval(orderDate, { start: dateRange.from!, end: dateRange.to! })
    })
  }, [allOrders, dateRange])

  // Previous period orders for comparison
  const previousOrders = useMemo(() => {
    if (!dateRange?.from || !dateRange?.to) return []
    
    const daysDiff = Math.ceil((dateRange.to.getTime() - dateRange.from.getTime()) / (1000 * 60 * 60 * 24))
    const previousStart = subDays(dateRange.from, daysDiff)
    const previousEnd = subDays(dateRange.to, daysDiff)
    
    return allOrders.filter(order => {
      const orderDate = new Date(order.createdAt)
      return isWithinInterval(orderDate, { start: previousStart, end: previousEnd })
    })
  }, [allOrders, dateRange])

  return (
    <OrdersProvider>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ThemeSwitch />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        {/* Date Range Filter */}
        <div className="flex items-center justify-between">
          <h2 className='text-2xl font-bold tracking-tight'>Phân Tích Doanh Thu</h2>
          <DateRangePicker 
            dateRange={dateRange} 
            onDateRangeChange={setDateRange} 
          />
        </div>

        {/* Analytics Cards */}
        <AnalyticsCards 
          orders={filteredOrders} 
          previousOrders={previousOrders}
        />

        {/* Revenue Chart */}
        {dateRange?.from && dateRange?.to && (
          <RevenueChart 
            orders={filteredOrders}
            dateRange={{ from: dateRange.from, to: dateRange.to }}
          />
        )}

        {/* Orders Table */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">Danh Sách Đơn Hàng</h3>
          <div className='flex-1 copy-scrollbar -mr-2 pr-2'>
            <OrdersTable search={search} navigate={navigate as NavigateFn} />
          </div>
        </div>
      </Main>

      <OrderStatusDialog />
      <OrderDetailDialog />
    </OrdersProvider>
  )
}
