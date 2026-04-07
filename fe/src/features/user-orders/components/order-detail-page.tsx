import { useParams, Link } from '@tanstack/react-router'
import { ArrowLeft, Package, AlertTriangle, Loader2, Copy, Building2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import { useOrderDetail } from '@/hooks/use-order'
import { format } from 'date-fns'
import { statusStyles } from './user-orders-columns'
import { toast } from 'sonner'

// Map payment method to Vietnamese text
const paymentMethodMap: Record<string, string> = {
  BANK_TRANSFER: 'Chuyển khoản ngân hàng',
  COD: 'Thanh toán khi nhận hàng (COD)',
  VNPAY: 'VNPay',
  MOMO: 'MoMo',
  CREDIT_CARD: 'Thẻ tín dụng',
}

// Map payment status to Vietnamese text
const paymentStatusMap: Record<string, string> = {
  PENDING: 'Chờ thanh toán',
  PAID: 'Đã thanh toán',
  FAILED: 'Thanh toán thất bại',
  REFUNDED: 'Đã hoàn tiền',
  CANCELLED: 'Đã hủy',
}

// Payment status badge styles
const paymentStatusStyles: Record<string, { variant: 'default' | 'secondary' | 'destructive' | 'outline'; className?: string }> = {
  PENDING: { variant: 'secondary' },
  PAID: { variant: 'default', className: 'bg-green-600 hover:bg-green-700' },
  FAILED: { variant: 'destructive' },
  REFUNDED: { variant: 'outline' },
  CANCELLED: { variant: 'outline' },
}

function getPaymentMethodLabel(method: string): string {
  return paymentMethodMap[method] || method
}

function getPaymentStatusLabel(status: string): string {
  return paymentStatusMap[status] || status
}

function getPaymentStatusStyle(status: string) {
  return paymentStatusStyles[status] || { variant: 'secondary' }
}

export function OrderDetailPage() {
  const { id } = useParams({ from: '/orders/$id' })
  const orderId = parseInt(id)
  const { data: order, isLoading, error } = useOrderDetail(orderId)

  // Check if order needs payment
  const needsPayment = order?.paymentStatus === 'PENDING' && 
                       order.paymentMethod !== 'COD' &&
                       order.status !== 'CANCELLED'

  if (isLoading) {
    return (
      <div className="flex h-[400px] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
      </div>
    )
  }

  if (error || !order) {
    return (
      <div className="container max-w-4xl py-8">
        <div className="flex flex-col items-center justify-center gap-4 py-12">
          <Package className="h-16 w-16 text-muted-foreground/50" />
          <div className="text-center">
            <p className="text-lg font-medium">Không tìm thấy đơn hàng</p>
            <p className="text-muted-foreground">Đơn hàng #{id} không tồn tại hoặc đã bị xóa</p>
          </div>
          <Button asChild>
            <Link to="/profile/orders">Quay lại danh sách đơn hàng</Link>
          </Button>
        </div>
      </div>
    )
  }

  const hasCancelledItems = order.items.some(item => item.cancelledQuantity > 0)
  const cancelledItems = order.items.filter(item => item.cancelledQuantity > 0)
  const statusStyle = statusStyles[order.status as keyof typeof statusStyles] || statusStyles.PENDING

  return (
    <div className="container max-w-4xl py-8">
      <div className="mb-6">
        <Button variant="ghost" asChild className="mb-4">
          <Link to="/profile/orders">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Quay lại đơn hàng
          </Link>
        </Button>
        
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-bold flex items-center gap-2">
              <Package className="h-6 w-6" />
              Đơn hàng #{order.orderNumber}
              {hasCancelledItems && (
                <Badge variant="outline" className="ml-2">
                  <AlertTriangle className="h-3 w-3 mr-1" />
                  Có sản phẩm bị hủy
                </Badge>
              )}
            </h1>
            <p className="text-muted-foreground">
              Đặt hàng ngày {format(new Date(order.createdAt), 'dd/MM/yyyy HH:mm')}
            </p>
          </div>
          <Badge variant={statusStyle.variant}>
            {statusStyle.label}
          </Badge>
        </div>
      </div>

      <div className="grid gap-6">
        {/* Order Items */}
        <Card>
          <CardHeader>
            <CardTitle>Sản phẩm đã đặt</CardTitle>
          </CardHeader>
          <CardContent>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Sản phẩm</TableHead>
                  <TableHead className="text-right">Đơn giá</TableHead>
                  <TableHead className="text-center">Số lượng</TableHead>
                  <TableHead className="text-right">Thành tiền</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {order.items.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell>
                      <div>
                        <p className="font-medium">{item.productTitle}</p>
                        {item.cancelledQuantity > 0 && (
                          <p className="text-sm text-red-500">
                            Đã hủy: {item.cancelledQuantity}/{item.quantity}
                          </p>
                        )}
                      </div>
                    </TableCell>
                    <TableCell className="text-right">
                      {item.price.toLocaleString('vi-VN')}đ
                    </TableCell>
                    <TableCell className="text-center">
                      {item.quantity}
                    </TableCell>
                    <TableCell className="text-right font-medium">
                      {(item.price * item.quantity).toLocaleString('vi-VN')}đ
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </CardContent>
        </Card>

        {/* Cancelled Items */}
        {cancelledItems.length > 0 && (
          <Card className="border-red-200 bg-red-50">
            <CardHeader>
              <CardTitle className="text-red-600 flex items-center gap-2">
                <AlertTriangle className="h-5 w-5" />
                Sản phẩm đã hủy
              </CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Sản phẩm</TableHead>
                    <TableHead className="text-center">Số lượng hủy</TableHead>
                    <TableHead className="text-right">Tiền hoàn lại</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {cancelledItems.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell>{item.productTitle}</TableCell>
                      <TableCell className="text-center">{item.cancelledQuantity}</TableCell>
                      <TableCell className="text-right text-red-600">
                        {(item.price * item.cancelledQuantity).toLocaleString('vi-VN')}đ
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        )}

        {/* Shipping Info */}
        <div className="grid gap-6 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Thông tin giao hàng</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <p><span className="font-medium">Người nhận:</span> {order.recipientName}</p>
              <p><span className="font-medium">SĐT:</span> {order.recipientPhone}</p>
              <p><span className="font-medium">Địa chỉ:</span> {order.shippingAddress}</p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Thông tin thanh toán</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <p><span className="font-medium">Phương thức:</span> {getPaymentMethodLabel(order.paymentMethod)}</p>
              <p>
                <span className="font-medium">Trạng thái:</span>{' '}
                <Badge 
                  variant={getPaymentStatusStyle(order.paymentStatus).variant}
                  className={getPaymentStatusStyle(order.paymentStatus).className}
                >
                  {getPaymentStatusLabel(order.paymentStatus)}
                </Badge>
              </p>

              {/* Payment Button for unpaid orders */}
              {needsPayment && (
                <div className="pt-3 space-y-3">
                  <div className="bg-blue-50 dark:bg-blue-950 border border-blue-200 dark:border-blue-800 rounded-lg p-4 space-y-3">
                    <div className="flex items-center gap-2 text-blue-700 dark:text-blue-300 font-semibold">
                      <Building2 className="h-5 w-5" />
                      Thông tin chuyển khoản
                    </div>
                    
                    <div className="space-y-2 text-sm">
                      <div className="flex justify-between items-center">
                        <span className="text-muted-foreground">Ngân hàng:</span>
                        <div className="flex items-center gap-2">
                          <span className="font-medium">BIDV</span>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="h-6 w-6 p-0"
                            onClick={() => {
                              navigator.clipboard.writeText('BIDV')
                              toast.success('Đã sao chép!')
                            }}
                          >
                            <Copy className="h-3 w-3" />
                          </Button>
                        </div>
                      </div>
                      
                      <div className="flex justify-between items-center">
                        <span className="text-muted-foreground">Số tài khoản:</span>
                        <div className="flex items-center gap-2">
                          <span className="font-mono font-medium">8858115694</span>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="h-6 w-6 p-0"
                            onClick={() => {
                              navigator.clipboard.writeText('8858115694')
                              toast.success('Đã sao chép số tài khoản!')
                            }}
                          >
                            <Copy className="h-3 w-3" />
                          </Button>
                        </div>
                      </div>
                      
                      <div className="flex justify-between items-center">
                        <span className="text-muted-foreground">Chủ tài khoản:</span>
                        <span className="font-medium">BUI DINH QUOC</span>
                      </div>
                      
                      <div className="flex justify-between items-center">
                        <span className="text-muted-foreground">Số tiền:</span>
                        <span className="font-bold text-primary text-base">
                          {order.total.toLocaleString('vi-VN')}đ
                        </span>
                      </div>
                      
                      <div className="flex justify-between items-center">
                        <span className="text-muted-foreground">Nội dung CK:</span>
                        <div className="flex items-center gap-2">
                          <span className="font-mono font-medium text-primary">
                            DH{order.orderNumber}
                          </span>
                          <Button
                            variant="ghost"
                            size="sm"
                            className="h-6 w-6 p-0"
                            onClick={() => {
                              navigator.clipboard.writeText(`DH${order.orderNumber}`)
                              toast.success('Đã sao chép mã đơn hàng!')
                            }}
                          >
                            <Copy className="h-3 w-3" />
                          </Button>
                        </div>
                      </div>
                    </div>
                  </div>
                  
                  <p className="text-xs text-muted-foreground text-center">
                    Vui lòng chuyển khoản với nội dung <span className="font-mono font-semibold text-primary">DH{order.orderNumber}</span> để đơn hàng được xử lý nhanh nhất
                  </p>
                </div>
              )}

              <div className="pt-2 border-t">
                <div className="flex justify-between">
                  <span>Tạm tính:</span>
                  <span>{order.subtotal?.toLocaleString('vi-VN')}đ</span>
                </div>
                <div className="flex justify-between">
                  <span>Phí vận chuyển:</span>
                  <span>{order.shippingFee?.toLocaleString('vi-VN')}đ</span>
                </div>
                {order.discount > 0 && (
                  <div className="flex justify-between text-green-600">
                    <span>Giảm giá:</span>
                    <span>-{order.discount.toLocaleString('vi-VN')}đ</span>
                  </div>
                )}
                <div className="flex justify-between font-bold text-lg pt-2 border-t">
                  <span>Tổng cộng:</span>
                  <span className="text-primary">{order.total.toLocaleString('vi-VN')}đ</span>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
