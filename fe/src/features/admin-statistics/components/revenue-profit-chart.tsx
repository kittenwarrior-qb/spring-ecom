import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts'
import type { PeriodStatistics } from '@/types/api'

interface RevenueProfitChartProps {
  data: PeriodStatistics[] | undefined
  isLoading: boolean
}

export function RevenueProfitChart({ data, isLoading }: RevenueProfitChartProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Doanh thu & Lợi nhuận</CardTitle>
        </CardHeader>
        <CardContent className="h-[300px] flex items-center justify-center">
          <div className="text-muted-foreground">Đang tải...</div>
        </CardContent>
      </Card>
    )
  }

  if (!data || data.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Doanh thu & Lợi nhuận</CardTitle>
        </CardHeader>
        <CardContent className="h-[300px] flex items-center justify-center">
          <div className="text-muted-foreground">Không có dữ liệu</div>
        </CardContent>
      </Card>
    )
  }

  const formatCurrency = (value: number | undefined) => {
    if (value === undefined || value === null) return '-'
    if (value >= 1000000) {
      return `${(value / 1000000).toFixed(1)}M`
    }
    if (value >= 1000) {
      return `${(value / 1000).toFixed(0)}K`
    }
    return value.toString()
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Doanh thu & Lợi nhuận</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis
              dataKey="date"
              tick={{ fontSize: 12 }}
              tickFormatter={(value) => {
                const date = new Date(value)
                return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit' })
              }}
            />
            <YAxis
              tick={{ fontSize: 12 }}
              tickFormatter={formatCurrency}
            />
            <Tooltip
              formatter={(value) => value != null ? [value.toLocaleString('vi-VN') + ' VND'] : ['-']}
              labelFormatter={(label) => {
                const date = new Date(label as string)
                return date.toLocaleDateString('vi-VN')
              }}
            />
            <Legend />
            <Line
              type="monotone"
              dataKey="revenue"
              name="Doanh thu"
              stroke="#8884d8"
              strokeWidth={2}
              dot={{ r: 3 }}
            />
            <Line
              type="monotone"
              dataKey="profit"
              name="Lợi nhuận"
              stroke="#82ca9d"
              strokeWidth={2}
              dot={{ r: 3 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}
