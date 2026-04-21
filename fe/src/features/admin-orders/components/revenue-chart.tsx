import { useMemo, useState } from 'react'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, Legend } from 'recharts'
import { format, eachDayOfInterval, eachMonthOfInterval, subMonths } from 'date-fns'
import { vi } from 'date-fns/locale'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Button } from '@/components/ui/button'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { BarChart3, Table as TableIcon, Download } from 'lucide-react'
import type { OrderResponse } from '@/types/api'

type RevenueChartProps = {
  orders: OrderResponse[]
  dateRange: {
    from: Date
    to: Date
  }
}

type RevenueData = {
  date: string
  fullDate: string
  revenue: number
  orders: number
}

// Move components outside of render
const CustomLegend = (props: any) => {
  const { payload } = props
  return (
    <div className="flex justify-center gap-6 mt-4">
      {payload?.map((entry: any, index: number) => (
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

const CustomTooltip = (props: unknown) => {
  const { active, payload } = props
  if (active && payload && payload.length) {
    const formatCurrency = (value: number) => {
      return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND',
        minimumFractionDigits: 0,
      }).format(value)
    }

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

const RevenueTable = ({ data, title }: { data: RevenueData[], title: string }) => {
  const totalRevenue = data.reduce((sum, item) => sum + item.revenue, 0)
  const totalOrders = data.reduce((sum, item) => sum + item.orders, 0)

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
      minimumFractionDigits: 0,
    }).format(value)
  }

  const exportToCSV = (data: RevenueData[], filename: string) => {
    const headers = ['Ngày', 'Doanh thu (VNĐ)', 'Số đơn hàng']
    const csvContent = [
      headers.join(','),
      ...data.map(row => [
        row.fullDate,
        row.revenue,
        row.orders
      ].join(','))
    ].join('\n')

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const link = document.createElement('a')
    const url = URL.createObjectURL(blob)
    link.setAttribute('href', url)
    link.setAttribute('download', filename)
    link.style.visibility = 'hidden'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold">{title}</h3>
        <Button
          variant="outline"
          size="sm"
          onClick={() => exportToCSV(data, `doanh-thu-${title.toLowerCase().replace(' ', '-')}.csv`)}
        >
          <Download className="h-4 w-4 mr-2" />
          Xuất CSV
        </Button>
      </div>
      
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-50">Thời gian</TableHead>
              <TableHead className="text-right">Doanh thu (VNĐ)</TableHead>
              <TableHead className="text-right">Số đơn hàng</TableHead>
              <TableHead className="text-right">Doanh thu TB/đơn</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((item, index) => (
              <TableRow key={index}>
                <TableCell className="font-medium">{item.fullDate}</TableCell>
                <TableCell className="text-right font-mono">
                  {formatCurrency(item.revenue)}
                </TableCell>
                <TableCell className="text-right">{item.orders}</TableCell>
                <TableCell className="text-right font-mono">
                  {item.orders > 0 ? formatCurrency(item.revenue / item.orders) : '-'}
                </TableCell>
              </TableRow>
            ))}
            <TableRow className="bg-muted/50 font-semibold">
              <TableCell>Tổng cộng</TableCell>
              <TableCell className="text-right font-mono">
                {formatCurrency(totalRevenue)}
              </TableCell>
              <TableCell className="text-right">{totalOrders}</TableCell>
              <TableCell className="text-right font-mono">
                {totalOrders > 0 ? formatCurrency(totalRevenue / totalOrders) : '-'}
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>
    </div>
  )
}

export function RevenueChart({ orders, dateRange }: RevenueChartProps) {
  const [viewMode, setViewMode] = useState<'chart' | 'table'>('chart')

  const dailyData = useMemo(() => {
    const days = eachDayOfInterval({ start: dateRange.from, end: dateRange.to })
    
    return days.map(day => {
      const dayOrders = orders.filter(order => {
        const orderDate = new Date(order.createdAt)
        return format(orderDate, 'yyyy-MM-dd') === format(day, 'yyyy-MM-dd')
      })
      
      // Only count DELIVERED orders for revenue (same as backend)
      const deliveredOrders = dayOrders.filter(o => o.status === 'DELIVERED')
      const revenue = deliveredOrders.reduce((sum, order) => sum + (order.total || 0), 0)
      const orderCount = deliveredOrders.length
      
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
      
      // Only count DELIVERED orders for revenue (same as backend)
      const deliveredOrders = monthOrders.filter(o => o.status === 'DELIVERED')
      const revenue = deliveredOrders.reduce((sum, order) => sum + (order.total || 0), 0)
      const orderCount = deliveredOrders.length
      
      return {
        date: format(month, 'MM/yyyy', { locale: vi }),
        fullDate: format(month, 'MMMM yyyy', { locale: vi }),
        revenue,
        orders: orderCount,
      }
    })
  }, [orders, dateRange])

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle>Biểu Đồ Doanh Thu</CardTitle>
          <div className="flex items-center gap-2">
            <Button
              variant={viewMode === 'chart' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setViewMode('chart')}
            >
              <BarChart3 className="h-4 w-4 mr-2" />
              Biểu đồ
            </Button>
            <Button
              variant={viewMode === 'table' ? 'default' : 'outline'}
              size="sm"
              onClick={() => setViewMode('table')}
            >
              <TableIcon className="h-4 w-4 mr-2" />
              Bảng dữ liệu
            </Button>
          </div>
        </div>
      </CardHeader>
      <CardContent>
        {viewMode === 'chart' ? (
          <Tabs defaultValue="daily" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="daily">Theo Ngày</TabsTrigger>
              <TabsTrigger value="monthly">Theo Tháng</TabsTrigger>
            </TabsList>
            
            <TabsContent value="daily" className="space-y-4">
              <div className="h-100 w-full min-h-100">
                <ResponsiveContainer width="100%" height="100%" minHeight={400}>
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
                    <Tooltip content={CustomTooltip} />
                    <Legend content={CustomLegend} />
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
              <div className="h-100 w-full min-h-100">
                <ResponsiveContainer width="100%" height="100%" minHeight={400}>
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
                    <Tooltip content={CustomTooltip} />
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
        ) : (
          <Tabs defaultValue="daily" className="w-full">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="daily">Theo Ngày</TabsTrigger>
              <TabsTrigger value="monthly">Theo Tháng</TabsTrigger>
            </TabsList>
            
            <TabsContent value="daily" className="space-y-4">
              <RevenueTable data={dailyData} title="Doanh thu theo ngày" />
            </TabsContent>
            
            <TabsContent value="monthly" className="space-y-4">
              <RevenueTable data={monthlyData} title="Doanh thu theo tháng" />
            </TabsContent>
          </Tabs>
        )}
      </CardContent>
    </Card>
  )
}