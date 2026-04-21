import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { 
  TrendingUp, 
  Package, 
  DollarSign, 
  ShoppingCart
} from 'lucide-react'
import type { DashboardSummary } from '@/types/api'

type AnalyticsCardsProps = {
  dashboard?: DashboardSummary
  isLoading?: boolean
}

export function AnalyticsCards({ dashboard, isLoading }: AnalyticsCardsProps) {
  const formatCurrency = (value: number | undefined) => {
    if (value === undefined || value === null) return '-'
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value)
  }

  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {[...Array(4)].map((_, i) => (
          <Card key={i}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <Skeleton className="h-4 w-24" />
              <Skeleton className="h-4 w-4" />
            </CardHeader>
            <CardContent>
              <Skeleton className="h-8 w-32 mb-1" />
              <Skeleton className="h-3 w-20" />
            </CardContent>
          </Card>
        ))}
      </div>
    )
  }

  if (!dashboard) {
    return null
  }

  const cards = [
    {
      title: 'Đơn Hoàn Thành',
      value: dashboard.deliveredOrders?.toLocaleString() || '0',
      icon: Package,
      description: `Tổng ${dashboard.totalOrders} đơn trong kỳ`
    },
    {
      title: 'Tổng Doanh Thu',
      value: formatCurrency(dashboard.totalRevenue),
      icon: DollarSign,
      description: 'Doanh thu đơn hoàn thành'
    },
    {
      title: 'Lợi Nhuận',
      value: formatCurrency(dashboard.totalProfit),
      icon: TrendingUp,
      description: `Biên: ${(dashboard.profitMargin ?? 0).toFixed(1)}%`,
      highlight: true
    },
    {
      title: 'Giá Trị Đơn TB',
      value: formatCurrency(dashboard.averageOrderValue),
      icon: ShoppingCart,
      description: 'Trung bình đơn hoàn thành'
    }
  ]

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {cards.map((card, index) => (
        <Card key={index} className={card.highlight ? 'border-green-200 bg-green-50/50' : ''}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">{card.title}</CardTitle>
            <card.icon className={`h-4 w-4 ${card.highlight ? 'text-green-600' : 'text-muted-foreground'}`} />
          </CardHeader>
          <CardContent>
            <div className={`text-2xl font-bold ${card.highlight ? 'text-green-700' : ''}`}>
              {card.value}
            </div>
            <p className="text-xs text-muted-foreground mt-1">
              {card.description}
            </p>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}