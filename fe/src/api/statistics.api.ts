import apiClient from '@/lib/api-client'
import type {
  ApiResponse,
  DashboardSummary,
  PeriodStatistics,
  TopProduct,
  RevenueByCategory,
  InventoryValuation,
} from '@/types/api'

// Statistics API calls Core service directly on port 8081
const ADMIN_STATS_URL = 'http://localhost:8081/v1/api/admin/statistics'

export type PeriodType = 'daily' | 'weekly' | 'monthly' | 'yearly'
export type Granularity = 'day' | 'week' | 'month'

export const statisticsApi = {
  // Get dashboard summary
  getDashboard: async (
    period: PeriodType = 'monthly',
    startDate?: string,
    endDate?: string
  ): Promise<DashboardSummary> => {
    const response = await apiClient.get<ApiResponse<DashboardSummary>>(
      `${ADMIN_STATS_URL}/dashboard`,
      { 
        params: { 
          period,
          ...(startDate && { startDate }),
          ...(endDate && { endDate })
        } 
      }
    )
    return response.data.data
  },

  // Get revenue statistics by period
  getRevenue: async (
    from: string,
    to: string,
    granularity: Granularity = 'day'
  ): Promise<PeriodStatistics[]> => {
    const response = await apiClient.get<ApiResponse<PeriodStatistics[]>>(
      `${ADMIN_STATS_URL}/revenue`,
      { params: { from, to, granularity } }
    )
    return response.data.data
  },

  // Get profit statistics by period
  getProfit: async (
    from: string,
    to: string,
    granularity: Granularity = 'day'
  ): Promise<PeriodStatistics[]> => {
    const response = await apiClient.get<ApiResponse<PeriodStatistics[]>>(
      `${ADMIN_STATS_URL}/profit`,
      { params: { from, to, granularity } }
    )
    return response.data.data
  },

  // Get top selling products
  getTopProducts: async (
    period: PeriodType = 'monthly',
    limit: number = 10
  ): Promise<TopProduct[]> => {
    const response = await apiClient.get<ApiResponse<TopProduct[]>>(
      `${ADMIN_STATS_URL}/top-products`,
      { params: { period, limit } }
    )
    return response.data.data
  },

  // Get revenue by category
  getRevenueByCategory: async (period: PeriodType = 'monthly'): Promise<RevenueByCategory[]> => {
    const response = await apiClient.get<ApiResponse<RevenueByCategory[]>>(
      `${ADMIN_STATS_URL}/revenue-by-category`,
      { params: { period } }
    )
    return response.data.data
  },

  // Get inventory valuation
  getInventoryValuation: async (): Promise<InventoryValuation> => {
    const response = await apiClient.get<ApiResponse<InventoryValuation>>(
      `${ADMIN_STATS_URL}/inventory-valuation`
    )
    return response.data.data
  },
}
