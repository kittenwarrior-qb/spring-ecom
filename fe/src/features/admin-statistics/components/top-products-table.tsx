import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { Loader2 } from 'lucide-react'
import type { TopProduct } from '@/types/api'

interface TopProductsTableProps {
  data: TopProduct[] | undefined
  isLoading: boolean
}

export function TopProductsTable({ data, isLoading }: TopProductsTableProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Sản phẩm bán chạy</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-[200px]">
          <Loader2 className="h-6 w-6 animate-spin" />
        </CardContent>
      </Card>
    )
  }

  if (!data || data.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Sản phẩm bán chạy</CardTitle>
        </CardHeader>
        <CardContent className="flex items-center justify-center h-[200px]">
          <div className="text-muted-foreground">Không có dữ liệu</div>
        </CardContent>
      </Card>
    )
  }

  const formatCurrency = (value: number | undefined) => {
    if (value === undefined || value === null) return '-'
    return value.toLocaleString('vi-VN') + ' VND'
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Sản phẩm bán chạy</CardTitle>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead className="w-[50px]">#</TableHead>
              <TableHead>Sản phẩm</TableHead>
              <TableHead className="text-right">Đã bán</TableHead>
              <TableHead className="text-right">Doanh thu</TableHead>
              <TableHead className="text-right">Lợi nhuận</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((product, index) => (
              <TableRow key={product.productId}>
                <TableCell className="font-medium">{index + 1}</TableCell>
                <TableCell className="font-medium">{product.productTitle}</TableCell>
                <TableCell className="text-right">{product.quantitySold}</TableCell>
                <TableCell className="text-right">{formatCurrency(product.revenue)}</TableCell>
                <TableCell className="text-right text-green-600 font-medium">
                  {formatCurrency(product.profit)}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}
