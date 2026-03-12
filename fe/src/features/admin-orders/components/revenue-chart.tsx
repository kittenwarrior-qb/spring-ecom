import { useMemo } from 'react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, Legend } from 'recharts'
import { format, subDays, eachDayOfInterval, eachMonthOfInterval, subMonths } from 'date-fns'
import { vi } from 'date-fns/locale'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import type { OrderResponse } from '@/types/api'

type RevenueChartProps = {
  orders: OrderResponse[]
  dateRange: {
    from: Date
    to: Date
  }
}

export function RevenueChart({ orders, dateRange }: RevenueChartProps) {
  const dailyData = useMemo(() => {
    const days = eachDayOfInterval({ start: dateRange.from, end: dateRange.to })
    
    return days.map(day => {
      const dayOrders = orders.filter(order => {
        const orderDate = new Date(order.createdAt)
        return format(orderDate, 'yyyy-MM-dd') === format(day, 'yyyy-MM-dd')
      })
      
      const revenue = dayOrders.reduce((sum, order) => sum + (order.total || 0), 0)
      const orderCount = dayOrders.length
      
      return {
        date: format(day, 'dd/MM', { locale: vi }),
        fullDate: format(day, 'dd/MM/yyyy', { locale: vi }),
        revenue,
        orders: orderCount,
      }
    })
  }, [orders, dateRange])

  const monthlyData = useMemo(() => {
    const months = eachMonthOfInterval({ 
      start: subMonths(dateRange.to, 11), 
      end: dateRange.to 
    })
    
    return months.map(month => {
      const monthOrders = orders.filter(order => {
        const orderDate = new Date(order.createdAt)
        return format(orderDate, 'yyyy-MM') === format(month, 'yyyy-MM')
      })
      
      const revenue = monthOrders.reduce((sum, order) => sum + (order.total || 0), 0)
      const orderCount = monthOrders.length
      
      return {
        date: format(month, 'MM/yyyy', { locale: vi }),
        fullDate: format(month, 'MMMM yyyy', { locale: vi }),
        revenue,
        orders: orderCount,
      }
    })
  }, [orders, dateRange])

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value)
  }

  const CustomLegend = (props: any) => {
    const { payload } = props
    return (
      <div className="flex justify-center gap-6 mt-4">
        {payload.map((entry: any, index: number) => (
          <div key={index} className="flex items-center gap-2">
            <div 
              className="w-3 h-3 rounded-full" 
              style={{ backgroundColor: entry.color }}
            />
            <span className="text-sm text-muted-foreground">
              {entry.dataKey === 'revenue' ? 'Doanh thu (VNĐ)' : 'Số đơn hàng'}
            </span>
          </div>
        ))}
      </div>
    )
  }
  const CustomTooltip = ({ active, payload, label }: unknown) => {
    if (active && payload && payload.length) {
      return (
        <div className="bg-background border rounded-lg p-3 shadow-lg">
          <p className="font-medium">{payload[0]?.payload?.fullDate}</p>
          <p className="text-sm text-blue-600">
            Doanh thu: {formatCurrency(payload[0]?.value || 0)}
          </p>
          <p className="text-sm text-green-600">
            Đơn hàng: {payload[1]?.value || 0}
          </p>
        </div>
      )
    }
    return null
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Biểu Đồ Doanh Thu</CardTitle>
      </CardHeader>
      <CardContent>
        <Tabs defaultValue="daily" className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="daily">Theo Ngày</TabsTrigger>
            <TabsTrigger value="monthly">Theo Tháng</TabsTrigger>
          </TabsList>
          
          <TabsContent value="daily" className="space-y-4">
            <div className="h-[400px]">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={dailyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="date" 
                    tick={{ fontSize: 12 }}
                    interval="preserveStartEnd"
                  />
                  <YAxis 
                    yAxisId="revenue"
                    orientation="left"
                    tick={{ fontSize: 12 }}
                    tickFormatter={(value) => `${(value / 1000).toFixed(0)}K`}
                  />
                  <YAxis 
                    yAxisId="orders"
                    orientation="right"
                    tick={{ fontSize: 12 }}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend content={<CustomLegend />} />
                  <Line 
                    yAxisId="revenue"
                    type="monotone" 
                    dataKey="revenue" 
                    stroke="#3b82f6" 
                    strokeWidth={2}
                    dot={{ fill: '#3b82f6', strokeWidth: 2, r: 4 }}
                    activeDot={{ r: 6 }}
                    name="Doanh thu (VNĐ)"
                  />
                  <Line 
                    yAxisId="orders"
                    type="monotone" 
                    dataKey="orders" 
                    stroke="#10b981" 
                    strokeWidth={2}
                    dot={{ fill: '#10b981', strokeWidth: 2, r: 4 }}
                    name="Số đơn hàng"
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </TabsContent>
          
          <TabsContent value="monthly" className="space-y-4">
            <div className="h-[400px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="date" 
                    tick={{ fontSize: 12 }}
                  />
                  <YAxis 
                    tick={{ fontSize: 12 }}
                    tickFormatter={(value) => `${(value / 1000000).toFixed(0)}M`}
                  />
                  <Tooltip content={<CustomTooltip />} />
                  <Legend 
                    content={() => (
                      <div className="flex justify-center mt-4">
                        <div className="flex items-center gap-2">
                          <div className="w-3 h-3 rounded" style={{ backgroundColor: '#3b82f6' }} />
                          <span className="text-sm text-muted-foreground">Doanh thu (VNĐ)</span>
                        </div>
                      </div>
                    )}
                  />
                  <Bar 
                    dataKey="revenue" 
                    fill="#3b82f6" 
                    radius={[4, 4, 0, 0]}
                    name="Doanh thu (VNĐ)"
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  )
}