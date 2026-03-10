import { createFileRoute } from '@tanstack/react-router'
import { Search, Eye, Truck, X } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useMyOrdersByStatus, useCancelOrder } from '@/hooks/use-order'
import { useState } from 'react'
import { toast } from 'sonner'
import type { OrderStatus } from '@/types/api'

export const Route = createFileRoute('/profile/orders')({
  component: OrdersPage,
})

const statusColors: Record<string, string> = {
  DELIVERED: 'bg-green-100 text-green-800',
  SHIPPED: 'bg-blue-100 text-blue-800',
  PROCESSING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-cyan-100 text-cyan-800',
  PENDING: 'bg-gray-100 text-gray-800',
  CANCELLED: 'bg-red-100 text-red-800',
}

const statusLabels: Record<string, string> = {
  DELIVERED: 'Đã giao',
  SHIPPED: 'Đang giao',
  PROCESSING: 'Đang xử lý',
  CONFIRMED: 'Đã xác nhận',
  PENDING: 'Chờ xác nhận',
  CANCELLED: 'Đã hủy',
}

function OrdersPage() {
  const [status, setStatus] = useState<OrderStatus | undefined>(undefined)
  const [searchQuery, setSearchQuery] = useState('')
  const { data: ordersData, isLoading } = useMyOrdersByStatus(status)
  const cancelOrder = useCancelOrder()

  const orders = ordersData?.content || []

  const filteredOrders = orders.filter(order => 
    order.orderNumber.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleCancelOrder = (orderId: number) => {
    if (confirm('Bạn có chắc muốn hủy đơn hàng này?')) {
      cancelOrder.mutate(orderId, {
        onSuccess: () => {
          toast.success('Đã hủy đơn hàng thành công')
        },
        onError: () => {
          toast.error('Không thể hủy đơn hàng')
        }
      })
    }
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold">Đơn hàng của tôi</h1>
        <p className="text-muted-foreground">Xem và theo dõi đơn hàng</p>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-4">
          <div className="flex flex-col gap-4 sm:flex-row">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input 
                placeholder="Tìm kiếm đơn hàng..." 
                className="pl-9" 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <Select value={status || 'all'} onValueChange={(value) => setStatus(value === 'all' ? undefined : value as OrderStatus)}>
              <SelectTrigger className="w-full sm:w-[180px]">
                <SelectValue placeholder="Trạng thái" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Tất cả</SelectItem>
                <SelectItem value="PENDING">Chờ xác nhận</SelectItem>
                <SelectItem value="CONFIRMED">Đã xác nhận</SelectItem>
                <SelectItem value="PROCESSING">Đang xử lý</SelectItem>
                <SelectItem value="SHIPPED">Đang giao</SelectItem>
                <SelectItem value="DELIVERED">Đã giao</SelectItem>
                <SelectItem value="CANCELLED">Đã hủy</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Orders Table */}
      <Card>
        <CardHeader>
          <CardTitle>Lịch sử đơn hàng</CardTitle>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="text-center py-8">Đang tải...</div>
          ) : filteredOrders.length > 0 ? (
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Mã đơn hàng</TableHead>
                    <TableHead>Ngày đặt</TableHead>
                    <TableHead>Tổng</TableHead>
                    <TableHead>Trạng thái</TableHead>
                    <TableHead className="text-right">Thao tác</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {filteredOrders.map((order) => (
                    <TableRow key={order.id}>
                      <TableCell className="font-medium">{order.orderNumber}</TableCell>
                      <TableCell>
                        {new Date(order.createdAt).toLocaleDateString('vi-VN')}
                      </TableCell>
                      <TableCell>{order.total.toLocaleString('vi-VN')}đ</TableCell>
                      <TableCell>
                        <Badge className={statusColors[order.status]}>
                          {statusLabels[order.status] || order.status}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <div className="flex justify-end gap-2">
                          <Button variant="ghost" size="icon" title="Xem chi tiết">
                            <Eye className="h-4 w-4" />
                          </Button>
                          {(order.status === 'PENDING' || order.status === 'CONFIRMED') && (
                            <Button 
                              variant="ghost" 
                              size="icon" 
                              title="Hủy đơn hàng"
                              onClick={() => handleCancelOrder(order.id)}
                              disabled={cancelOrder.isPending}
                            >
                              <X className="h-4 w-4 text-red-500" />
                            </Button>
                          )}
                        </div>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          ) : (
            <div className="text-center py-8 text-muted-foreground">
              <Truck className="h-12 w-12 mx-auto mb-4 opacity-50" />
              <p>Không tìm thấy đơn hàng nào</p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
