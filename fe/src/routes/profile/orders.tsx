import { createFileRoute } from '@tanstack/react-router'
import { Search, Eye, Truck, X, ChevronDown, ChevronUp } from 'lucide-react'
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
import { OrderItemWithCancel } from '@/components/order-item-with-cancel'
import { useMyOrdersByStatus, useCancelOrder, usePartialCancelOrder } from '@/hooks/use-order'
import { useState } from 'react'
import { toast } from 'sonner'
import type { OrderStatus, OrderDetailResponse } from '@/types/api'

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
  PARTIALLY_CANCELLED: 'bg-orange-100 text-orange-800',
}

const statusLabels: Record<string, string> = {
  DELIVERED: 'Đã giao',
  SHIPPED: 'Đang giao',
  PROCESSING: 'Đang xử lý',
  CONFIRMED: 'Đã xác nhận',
  PENDING: 'Chờ xác nhận',
  CANCELLED: 'Đã hủy',
  PARTIALLY_CANCELLED: 'Đã hủy một phần',
}

function OrdersPage() {
  const [status, setStatus] = useState<OrderStatus | undefined>(undefined)
  const [searchQuery, setSearchQuery] = useState('')
  const [expandedOrders, setExpandedOrders] = useState<Set<number>>(new Set())
  const { data: ordersData, isLoading } = useMyOrdersByStatus(status)
  const cancelOrder = useCancelOrder()
  const partialCancelOrder = usePartialCancelOrder()

  const orders = ordersData?.content || []

  const filteredOrders = orders.filter(order => 
    order.orderNumber.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const toggleOrderExpansion = (orderId: number) => {
    const newExpanded = new Set(expandedOrders)
    if (newExpanded.has(orderId)) {
      newExpanded.delete(orderId)
    } else {
      newExpanded.add(orderId)
    }
    setExpandedOrders(newExpanded)
  }

  const handleCancelOrder = (orderId: number) => {
    if (confirm('Bạn có chắc muốn hủy toàn bộ đơn hàng này?')) {
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

  const handlePartialCancel = (orderId: number, itemId: number, quantity: number) => {
    const request = {
      items: [{ orderItemId: itemId, quantityToCancel: quantity }]
    }
    
    partialCancelOrder.mutate({ id: orderId, request }, {
      onSuccess: () => {
        toast.success(`Đã hủy ${quantity} sản phẩm thành công`)
      },
      onError: () => {
        toast.error('Không thể hủy sản phẩm')
      }
    })
  }

  const canCancelOrder = (order: OrderDetailResponse) => {
    return ['PENDING', 'CONFIRMED', 'PARTIALLY_CANCELLED'].includes(order.status)
  }

  const canPartialCancel = (order: OrderDetailResponse) => {
    return ['PENDING', 'CONFIRMED', 'PARTIALLY_CANCELLED'].includes(order.status)
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
                <SelectItem value="PARTIALLY_CANCELLED">Đã hủy một phần</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Orders List */}
      <div className="space-y-4">
        {isLoading ? (
          <div className="text-center py-8">Đang tải...</div>
        ) : filteredOrders.length > 0 ? (
          filteredOrders.map((order) => (
            <Card key={order.id}>
              <CardHeader>
                <div className="flex items-center justify-between">
                  <div className="space-y-1">
                    <div className="flex items-center gap-4">
                      <h3 className="font-semibold">{order.orderNumber}</h3>
                      <Badge className={statusColors[order.status]}>
                        {statusLabels[order.status] || order.status}
                      </Badge>
                    </div>
                    <div className="text-sm text-muted-foreground">
                      Đặt ngày: {new Date(order.createdAt).toLocaleDateString('vi-VN')}
                    </div>
                    <div className="font-semibold text-primary">
                      Tổng: {order.total.toLocaleString('vi-VN')}đ
                    </div>
                  </div>
                  
                  <div className="flex items-center gap-2">
                    {canCancelOrder(order) && (
                      <Button 
                        variant="destructive" 
                        size="sm"
                        onClick={() => handleCancelOrder(order.id)}
                        disabled={cancelOrder.isPending}
                      >
                        <X className="h-4 w-4 mr-1" />
                        Hủy đơn hàng
                      </Button>
                    )}
                    
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => toggleOrderExpansion(order.id)}
                    >
                      {expandedOrders.has(order.id) ? (
                        <>
                          <ChevronUp className="h-4 w-4 mr-1" />
                          Thu gọn
                        </>
                      ) : (
                        <>
                          <ChevronDown className="h-4 w-4 mr-1" />
                          Xem chi tiết
                        </>
                      )}
                    </Button>
                  </div>
                </div>
              </CardHeader>
              
              {expandedOrders.has(order.id) && (
                <CardContent className="pt-0">
                  <div className="space-y-3">
                    <div className="text-sm">
                      <p><strong>Người nhận:</strong> {order.recipientName}</p>
                      <p><strong>Điện thoại:</strong> {order.recipientPhone}</p>
                      <p><strong>Địa chỉ:</strong> {order.shippingAddress}, {order.shippingWard}, {order.shippingDistrict}, {order.shippingCity}</p>
                      {order.note && <p><strong>Ghi chú:</strong> {order.note}</p>}
                    </div>
                    
                    <div className="space-y-2">
                      <h4 className="font-medium">Sản phẩm:</h4>
                      {order.items && order.items.length > 0 ? (
                        order.items.map((item) => (
                          <OrderItemWithCancel
                            key={item.id}
                            item={item}
                            canCancel={canPartialCancel(order)}
                            onCancelQuantity={(itemId, quantity) => 
                              handlePartialCancel(order.id, itemId, quantity)
                            }
                          />
                        ))
                      ) : (
                        <p className="text-muted-foreground">Không có sản phẩm</p>
                      )}
                    </div>
                  </div>
                </CardContent>
              )}
            </Card>
          ))
        ) : (
          <Card>
            <CardContent className="text-center py-8">
              <Truck className="h-12 w-12 mx-auto mb-4 opacity-50" />
              <p className="text-muted-foreground">Không tìm thấy đơn hàng nào</p>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  )
}
