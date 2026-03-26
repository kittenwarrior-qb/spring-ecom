import { useMemo } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { 
  TrendingUp, 
  TrendingDown, 
  Package, 
  DollarSign, 
  Clock, 
  CheckCircle,
  Users,
  ShoppingCart
} from 'lucide-react'
import type { OrderResponse } from '@/types/api'

type AnalyticsCardsProps = {
  orders: OrderResponse[]
  previousOrders?: OrderResponse[]
}

export function AnalyticsCards({ orders, previousOrders = [] }: AnalyticsCardsProps) {
  const analytics = useMemo(() => {
    const totalOrders = orders.length
    const totalRevenue = orders.reduce((sum, order) => sum + (order.total || 0), 0)
    const pendingOrders = orders.filter(order => order.status === 'PENDING').length
    const completedOrders = orders.filter(order => order.status === 'DELIVERED').length
    const cancelledOrders = orders.filter(order => order.status === 'CANCELLED').length
    const uniqueCustomers = new Set(orders.map(order => order.userId)).size
    const averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0

    // Previous period comparison
    const prevTotalOrders = previousOrders.length
    const prevTotalRevenue = previousOrders.reduce((sum, order) => sum + (order.total || 0), 0)
    const prevUniqueCustomers = new Set(previousOrders.map(order => order.userId)).size

    const orderGrowth = prevTotalOrders > 0 
      ? ((totalOrders - prevTotalOrders) / prevTotalOrders) * 100 
      : 0
    const revenueGrowth = prevTotalRevenue > 0 
      ? ((totalRevenue - prevTotalRevenue) / prevTotalRevenue) * 100 
      : 0
    const customerGrowth = prevUniqueCustomers > 0 
      ? ((uniqueCustomers - prevUniqueCustomers) / prevUniqueCustomers) * 100 
      : 0

    return {
      totalOrders,
      totalRevenue,
      pendingOrders,
      completedOrders,
      cancelledOrders,
      uniqueCustomers,
      averageOrderValue,
      orderGrowth,
      revenueGrowth,
      customerGrowth,
    }
  }, [orders, previousOrders])

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value)
  }

  const formatGrowth = (growth: number) => {
    const isPositive = growth >= 0
    const Icon = isPositive ? TrendingUp : TrendingDown
    const color = isPositive ? 'text-green-600' : 'text-red-600'
    
    return (
      <div className={`flex items-center gap-1 text-xs ${color}`}>
        <Icon className="h-3 w-3" />
        {Math.abs(growth).toFixed(1)}%
      </div>
    )
  }

  const cards = [
    {
      title: 'Tổng Đơn Hàng',
      value: analytics.totalOrders.toLocaleString(),
      icon: Package,
      growth: analytics.orderGrowth,
      description: 'Tổng số đơn hàng'
    },
    {
      title: 'Doanh Thu',
      value: formatCurrency(analytics.totalRevenue),
      icon: DollarSign,
      growth: analytics.revenueGrowth,
      description: 'Tổng doanh thu'
    },
    {
      title: 'Khách Hàng',
      value: analytics.uniqueCustomers.toLocaleString(),
      icon: Users,
      growth: analytics.customerGrowth,
      description: 'Khách hàng duy nhất'
    },
    {
      title: 'Giá Trị TB',
      value: formatCurrency(analytics.averageOrderValue),
      icon: ShoppingCart,
      growth: 0, // Could calculate if needed
      description: 'Giá trị đơn hàng trung bình'
    },
    {
      title: 'Chờ Xử Lý',
      value: analytics.pendingOrders.toLocaleString(),
      icon: Clock,
      description: 'Đơn hàng chờ xử lý',
      badge: analytics.pendingOrders > 0 ? 'warning' : 'default'
    },
    {
      title: 'Đã Giao',
      value: analytics.completedOrders.toLocaleString(),
      icon: CheckCircle,
      description: 'Đơn hàng đã giao thành công',
      badge: 'success'
    }
  ]

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
      {cards.map((card, index) => (
        <Card key={index}>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">{card.title}</CardTitle>
            <card.icon className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-between">
              <div className="text-2xl font-bold">{card.value}</div>
              {card.growth !== undefined && card.growth !== 0 && (
                <div>{formatGrowth(card.growth)}</div>
              )}
              {card.badge && (
                <Badge 
                  variant={
                    card.badge === 'success' ? 'default' : 
                    card.badge === 'success' ? 'destructive' : 
                    'secondary'
                  }
                >
                  {card.badge === 'success' ? 'Đã xử lý' : 
                   card.badge === 'success' ? 'Cần xử lý' : 
                   'Cần xử lý'}
                </Badge>
              )}
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