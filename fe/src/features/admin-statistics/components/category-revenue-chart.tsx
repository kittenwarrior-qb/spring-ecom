import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts'
import { Loader2 } from 'lucide-react'
import type { RevenueByCategory } from '@/types/api'

interface CategoryRevenueChartProps {
  data: RevenueByCategory[] | undefined
  isLoading: boolean
}

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#82CA9D']

export function CategoryRevenueChart({ data, isLoading }: CategoryRevenueChartProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Doanh thu theo danh mục</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-[300px]">
          <Loader2 className="h-6 w-6 animate-spin" />
        </CardContent>
      </Card>
    )
  }

  if (!data || data.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Doanh thu theo danh mục</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-[300px]">
          <div className="text-muted-foreground">Không có dữ liệu</div>
        </CardContent>
      </Card>
    )
  }

  const formatCurrency = (value: number | undefined) => {
    if (value === undefined || value === null) return '-'
    if (value >= 1000000) {
      return `${(value / 1000000).toFixed(1)}M VND`
    }
    return `${(value / 1000).toFixed(0)}K VND`
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Doanh thu theo danh mục</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={300}>
          <PieChart>
            <Pie
              data={data as unknown as Record<string, unknown>[]}
              dataKey="revenue"
              nameKey="categoryName"
              cx="50%"
              cy="50%"
              outerRadius={100}
              label={({ name, percent }) => `${name}: ${((percent ?? 0) * 100).toFixed(1)}%`}
            >
              {data.map((_, index) => (
                <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value) => value != null ? [formatCurrency(value as number)] : ['-']}
            />
            <Legend />
          </PieChart>
        </ResponsiveContainer>
      </CardContent>
    </Card>
  )
}
