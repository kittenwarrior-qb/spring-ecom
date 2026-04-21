import { useState } from 'react'
import { format, subDays, subMonths } from 'date-fns'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { PermissionDenied } from '@/components/permission-denied'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { SummaryCards } from './components/summary-cards'
import { RevenueProfitChart } from './components/revenue-profit-chart'
import { TopProductsTable } from './components/top-products-table'
import { CategoryRevenueChart } from './components/category-revenue-chart'
import { useDashboard, useRevenue, useTopProducts, useRevenueByCategory } from '@/hooks/use-statistics'
import type { PeriodType, Granularity } from '@/api/statistics.api'
import { usePermissions } from '@/hooks/use-permissions'

export function AdminStatistics() {
  const { hasPermission } = usePermissions()

  const [period, setPeriod] = useState<PeriodType>('monthly')
  const [dateRange, setDateRange] = useState({
    from: format(subMonths(new Date(), 1), 'yyyy-MM-dd'),
    to: format(new Date(), 'yyyy-MM-dd'),
  })

  const { data: dashboard, isLoading: dashboardLoading } = useDashboard(period)
  const { data: revenueData, isLoading: revenueLoading } = useRevenue(
    dateRange.from,
    dateRange.to,
    'day' as Granularity
  )
  const { data: topProducts, isLoading: topProductsLoading } = useTopProducts(period, 10)
  const { data: categoryRevenue, isLoading: categoryLoading } = useRevenueByCategory(period)

  const handlePeriodChange = (value: PeriodType) => {
    setPeriod(value)
    // Update date range based on period
    const now = new Date()
    switch (value) {
      case 'daily':
        setDateRange({
          from: format(subDays(now, 7), 'yyyy-MM-dd'),
          to: format(now, 'yyyy-MM-dd'),
        })
        break
      case 'weekly':
        setDateRange({
          from: format(subDays(now, 30), 'yyyy-MM-dd'),
          to: format(now, 'yyyy-MM-dd'),
        })
        break
      case 'monthly':
        setDateRange({
          from: format(subMonths(now, 1), 'yyyy-MM-dd'),
          to: format(now, 'yyyy-MM-dd'),
        })
        break
    }
  }

  if (!hasPermission('STATISTICS_VIEW')) {
    return (
      <>
        <Header>
          <div className='ms-auto flex items-center space-x-4 px-4'>
            <Search />
            <ProfileDropdown />
          </div>
        </Header>
        <Main>
          <PermissionDenied permission="STATISTICS_VIEW" />
        </Main>
      </>
    )
  }

  return (
    <>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className='text-2xl font-bold tracking-tight'>Thống Kê & Báo Cáo</h2>
          <Select value={period} onValueChange={(v) => handlePeriodChange(v as PeriodType)}>
            <SelectTrigger className="w-[150px]">
              <SelectValue placeholder="Chọn kỳ" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="daily">Hôm nay</SelectItem>
              <SelectItem value="weekly">Tuần nay</SelectItem>
              <SelectItem value="monthly">Tháng nay</SelectItem>
            </SelectContent>
          </Select>
        </div>

        <SummaryCards data={dashboard} isLoading={dashboardLoading} />

        <div className="grid gap-4 md:grid-cols-2">
          <RevenueProfitChart data={revenueData} isLoading={revenueLoading} />
          <CategoryRevenueChart data={categoryRevenue} isLoading={categoryLoading} />
        </div>

        <TopProductsTable data={topProducts} isLoading={topProductsLoading} />
      </Main>
    </>
  )
}

