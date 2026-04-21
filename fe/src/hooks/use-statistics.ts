import { useQuery } from '@tanstack/react-query'
import { statisticsApi, type PeriodType, type Granularity } from '@/api/statistics.api'

// Query keys
export const statsKeys = {
  all: ['statistics'] as const,
  dashboard: (period: PeriodType, startDate?: string, endDate?: string) => 
    [...statsKeys.all, 'dashboard', { period, startDate, endDate }] as const,
  revenue: (from: string, to: string, granularity: Granularity) =>
    [...statsKeys.all, 'revenue', { from, to, granularity }] as const,
  profit: (from: string, to: string, granularity: Granularity) =>
    [...statsKeys.all, 'profit', { from, to, granularity }] as const,
  topProducts: (period: PeriodType, limit: number) =>
    [...statsKeys.all, 'topProducts', { period, limit }] as const,
  revenueByCategory: (period: PeriodType) =>
    [...statsKeys.all, 'revenueByCategory', period] as const,
  inventoryValuation: () => [...statsKeys.all, 'inventoryValuation'] as const,
}

// Get dashboard summary
export function useDashboard(period: PeriodType = 'monthly', startDate?: string, endDate?: string) {
  return useQuery({
    queryKey: statsKeys.dashboard(period, startDate, endDate),
    queryFn: () => statisticsApi.getDashboard(period, startDate, endDate),
  })
}

// Get revenue statistics
export function useRevenue(
  from: string,
  to: string,
  granularity: Granularity = 'day'
) {
  return useQuery({
    queryKey: statsKeys.revenue(from, to, granularity),
    queryFn: () => statisticsApi.getRevenue(from, to, granularity),
    enabled: !!from && !!to,
  })
}

// Get profit statistics
export function useProfit(
  from: string,
  to: string,
  granularity: Granularity = 'day'
) {
  return useQuery({
    queryKey: statsKeys.profit(from, to, granularity),
    queryFn: () => statisticsApi.getProfit(from, to, granularity),
    enabled: !!from && !!to,
  })
}

// Get top selling products
export function useTopProducts(period: PeriodType = 'monthly', limit: number = 10) {
  return useQuery({
    queryKey: statsKeys.topProducts(period, limit),
    queryFn: () => statisticsApi.getTopProducts(period, limit),
  })
}

// Get revenue by category
export function useRevenueByCategory(period: PeriodType = 'monthly') {
  return useQuery({
    queryKey: statsKeys.revenueByCategory(period),
    queryFn: () => statisticsApi.getRevenueByCategory(period),
  })
}

// Get inventory valuation
export function useInventoryValuation() {
  return useQuery({
    queryKey: statsKeys.inventoryValuation(),
    queryFn: () => statisticsApi.getInventoryValuation(),
  })
}
