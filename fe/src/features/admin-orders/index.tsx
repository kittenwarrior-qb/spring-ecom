import { useState, useMemo } from 'react'
import { useNavigate, useSearch } from '@tanstack/react-router'
import { subDays, format, startOfMonth, endOfMonth, startOfYear, endOfYear, isWithinInterval } from 'date-fns'
import { type DateRange } from 'react-day-picker'
import { type NavigateFn } from '@/hooks/use-table-url-state'
import { Button } from '@/components/ui/button'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { OrdersTable } from './components/orders-table'
import { OrdersProvider } from './components/orders-provider'
import { OrderStatusDialog } from './components/order-status-dialog'
import { OrderDetailDialog } from './components/order-detail-dialog'
import { RevenueChart } from './components/revenue-chart'
import { DateRangePicker } from './components/date-range-picker'
import { AnalyticsCards } from './components/analytics-cards'
import { useAdminOrders } from '@/hooks/use-order'
import { useDashboard } from '@/hooks/use-statistics'
import type { PeriodType } from '@/api/statistics.api'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

export function AdminOrders() {
  const navigate = useNavigate()
  const search = useSearch({ from: '/admin/' })
  
  // Period state for statistics API
  const [period, setPeriod] = useState<PeriodType>('monthly')

  // Date range state - default to last 30 days
  const [dateRange, setDateRange] = useState<DateRange | undefined>({
    from: subDays(new Date(), 29),
    to: new Date()
  })

  // State to control whether to show analytics
  const [showAnalytics, setShowAnalytics] = useState(true)

  // Handle period change and update date range
  const handlePeriodChange = (value: PeriodType) => {
    setPeriod(value)
    const now = new Date()
    switch (value) {
      case 'daily':
        setDateRange({ from: now, to: now })
        break
      case 'weekly':
        setDateRange({ from: subDays(now, 6), to: now })
        break
      case 'monthly':
        setDateRange({ from: startOfMonth(now), to: endOfMonth(now) })
        break
      case 'yearly':
        setDateRange({ from: startOfYear(now), to: endOfYear(now) })
        break
    }
  }

  // Format dates for API
  const dateFrom = dateRange?.from ? format(dateRange.from, 'yyyy-MM-dd') : undefined
  const dateTo = dateRange?.to ? format(dateRange.to, 'yyyy-MM-dd') : undefined

  // Fetch dashboard statistics from API with date range
  const { data: dashboardData, isLoading: dashboardLoading } = useDashboard(period, dateFrom, dateTo)

  // Fetch orders for chart - only when analytics are shown
  const { data: analyticsData } = useAdminOrders({ 
    page: 0, 
    size: 1000,
    dateFrom,
    dateTo,
  }, { enabled: showAnalytics })
  
  const allOrders = useMemo(() => analyticsData?.content || [], [analyticsData?.content])

  // Filter orders by date range for chart
  const filteredOrders = useMemo(() => {
    if (!dateRange?.from || !dateRange?.to) return allOrders
    
    return allOrders.filter(order => {
      const orderDate = new Date(order.createdAt)
      return isWithinInterval(orderDate, { start: dateRange.from!, end: dateRange.to! })
    })
  }, [allOrders, dateRange])

  return (
    <OrdersProvider>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        {/* Date Range Filter */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <h2 className='text-2xl font-bold tracking-tight'>Phân Tích Doanh Thu</h2>
            <Button
              variant={showAnalytics ? "default" : "outline"}
              size="sm"
              onClick={() => setShowAnalytics(!showAnalytics)}
            >
              {showAnalytics ? "Hide Analytics" : "Show Analytics"}
            </Button>
          </div>
          <div className="flex items-center gap-4">
            <Select value={period} onValueChange={(v) => handlePeriodChange(v as PeriodType)}>
              <SelectTrigger className="w-[140px]">
                <SelectValue placeholder="Chọn kỳ" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="daily">Hôm nay</SelectItem>
                <SelectItem value="weekly">Tuần nay</SelectItem>
                <SelectItem value="monthly">Tháng nay</SelectItem>
                <SelectItem value="yearly">Năm nay</SelectItem>
              </SelectContent>
            </Select>
            <DateRangePicker 
              dateRange={dateRange} 
              onDateRangeChange={setDateRange} 
            />
          </div>
        </div>

        {/* Analytics Cards */}
        {showAnalytics && (
          <AnalyticsCards 
            dashboard={dashboardData}
            isLoading={dashboardLoading}
          />
        )}

        {/* Revenue Chart */}
        {showAnalytics && dateRange?.from && dateRange?.to && (
          <RevenueChart 
            orders={filteredOrders}
            dateRange={{ from: dateRange.from, to: dateRange.to }}
          />
        )}

        {/* Orders Table */}
        <div className="space-y-4">
          <h3 className="text-lg font-semibold">Danh Sách Đơn Hàng</h3>
          <div className='flex-1 copy-scrollbar -mr-2 pr-2'>
            <OrdersTable 
              search={search} 
              navigate={navigate as NavigateFn} 
              dateFrom={dateFrom}
              dateTo={dateTo}
            />
          </div>
        </div>
      </Main>

      <OrderStatusDialog />
      <OrderDetailDialog />
    </OrdersProvider>
  )
}
