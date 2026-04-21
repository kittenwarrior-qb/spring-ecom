import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { DollarSign, TrendingUp, ShoppingCart, Package } from 'lucide-react'
import type { DashboardSummary } from '@/types/api'

interface SummaryCardsProps {
  data: DashboardSummary | undefined
  isLoading: boolean
}

export function SummaryCards({ data, isLoading }: SummaryCardsProps) {
  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {[...Array(4)].map((_, i) => (
          <Card key={i}>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Loading...</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="h-6 w-24 bg-muted animate-pulse rounded" />
            </CardContent>
          </Card>
        ))}
      </div>
    )
  }

  if (!data) return null

  const formatCurrency = (value: number | undefined) => {
    if (value === undefined || value === null) return '-'
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(value)
  }

  const cards = [
    {
      title: 'Tong doanh thu',
      value: formatCurrency(data.totalRevenue),
      icon: DollarSign,
      description: `${data.orderCount} don hang`,
    },
    {
      title: 'Von hang ban',
      value: formatCurrency(data.totalCogs),
      icon: Package,
      description: 'Gia von san pham',
    },
    {
      title: 'Loi nhuan gop',
      value: formatCurrency(data.grossProfit),
      icon: TrendingUp,
      description: `Bien loi nhuan: ${(data.profitMargin ?? 0).toFixed(1)}%`,
    },
    {
      title: 'Gia tri don TB',
      value: formatCurrency(data.avgOrderValue),
      icon: ShoppingCart,
      description: 'Trung binh moi don',
    },
  ]

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      {cards.map((card) => (
        <Card key={card.title}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">{card.title}</CardTitle>
            <card.icon className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{card.value}</div>
            <p className="text-xs text-muted-foreground">{card.description}</p>
          </CardContent>
        </Card>
      ))}
    </div>
  )
}
