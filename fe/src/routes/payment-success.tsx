import { createFileRoute, Link, useSearch } from '@tanstack/react-router'
import { CheckCircle, Package, ArrowRight, Home, Loader2, CreditCard, AlertCircle } from 'lucide-react'
import { useEffect, useState, useRef } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import apiClient from '@/lib/api-client'
import type { OrderResponse } from '@/types/api'

export const Route = createFileRoute('/payment-success')({
  component: PaymentSuccessPage,
  validateSearch: (search: Record<string, unknown>) => ({
    orderNumber: search.orderNumber as string | undefined,
  }),
})

type PaymentStatus = 'loading' | 'waiting_payment' | 'processing' | 'success' | 'error'

function PaymentSuccessPage() {
  const { orderNumber } = useSearch({ from: '/payment-success' })
  const [status, setStatus] = useState<PaymentStatus>('loading')
  const [order, setOrder] = useState<OrderResponse | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [showConfirmDialog, setShowConfirmDialog] = useState(false)
  const hasFetched = useRef(false)

  // Fetch order info on mount
  useEffect(() => {
    if (!orderNumber) {
      setStatus('error')
      setError('Không tìm thấy mã đơn hàng')
      return
    }

    // Prevent double fetch in React StrictMode
    if (hasFetched.current) return
    hasFetched.current = true

    const fetchOrder = async () => {
      try {
        const response = await apiClient.get(`/api/orders/number/${orderNumber}`)
        const orderData = response.data.data as OrderResponse
        setOrder(orderData)
        
        // Determine initial status based on order status
        if (orderData.status === 'CONFIRMED') {
          setStatus('success')
        } else if (orderData.status === 'STOCK_RESERVED') {
          setStatus('waiting_payment')
        } else if (orderData.status === 'PENDING_STOCK') {
          setStatus('waiting_payment')
          setError('Đơn hàng đang chờ xử lý kho. Vui lòng đợi trong giây lát hoặc làm mới trang.')
        } else {
          setStatus('error')
          setError(`Trạng thái đơn hàng không hợp lệ: ${orderData.status}`)
        }
      } catch (_err) {
        setStatus('error')
        setError('Không thể tải thông tin đơn hàng')
      }
    }

    fetchOrder()
  }, [orderNumber])

  const handlePayment = async () => {
    if (!orderNumber) return
    
    setShowConfirmDialog(false)
    setStatus('processing')
    setError(null)
    
    try {
      await apiClient.post(`/api/orders/${orderNumber}/simulate-payment`)
      setStatus('success')
    } catch (err) {
      setStatus('error')
      const message = err instanceof Error ? err.message : 'Không thể xử lý thanh toán'
      setError(message)
    }
  }

  const handleConfirmPayment = () => {
    setShowConfirmDialog(true)
  }

  const handleRefresh = async () => {
    setStatus('loading')
    setError(null)
    
    try {
      const response = await apiClient.get(`/api/orders/number/${orderNumber}`)
      const orderData = response.data.data as OrderResponse
      setOrder(orderData)
      
      if (orderData.status === 'CONFIRMED') {
        setStatus('success')
      } else if (orderData.status === 'STOCK_RESERVED') {
        setStatus('waiting_payment')
      } else if (orderData.status === 'PENDING_STOCK') {
        setStatus('waiting_payment')
        setError('Đơn hàng đang chờ xử lý kho. Vui lòng đợi trong giây lát.')
      } else {
        setStatus('error')
        setError(`Trạng thái đơn hàng: ${orderData.status}`)
      }
    } catch {
      setStatus('error')
      setError('Không thể tải thông tin đơn hàng')
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="text-center space-y-6">
        {/* Loading State */}
        {status === 'loading' && (
          <>
            <div className="flex justify-center">
              <div className="rounded-full bg-blue-100 p-6">
                <Loader2 className="h-16 w-16 text-blue-600 animate-spin" />
              </div>
            </div>
            <div className="space-y-2">
              <h1 className="text-3xl font-bold text-blue-600">Đang tải...</h1>
            </div>
          </>
        )}

        {/* Waiting Payment State */}
        {status === 'waiting_payment' && (
          <>
            <div className="flex justify-center">
              <div className="rounded-full bg-yellow-100 p-6">
                <CreditCard className="h-16 w-16 text-yellow-600" />
              </div>
            </div>
            <div className="space-y-2">
              <h1 className="text-3xl font-bold text-yellow-600">Đơn hàng đã được tạo</h1>
              <p className="text-lg text-muted-foreground">
                Đơn hàng đã sẵn sàng thanh toán
              </p>
              {error && (
                <p className="text-sm text-yellow-600 bg-yellow-50 p-3 rounded-lg">{error}</p>
              )}
            </div>
            <div className="flex gap-4 justify-center">
              <Button 
                size="lg" 
                onClick={handleConfirmPayment}
                disabled={order?.status === 'PENDING_STOCK'}
                className="bg-green-600 hover:bg-green-700"
              >
                <CreditCard className="mr-2 h-5 w-5" />
                Thanh toán ngay
              </Button>
              <Button variant="outline" onClick={handleRefresh}>
                <Loader2 className="mr-2 h-4 w-4" />
                Làm mới
              </Button>
            </div>
          </>
        )}

        {/* Processing State */}
        {status === 'processing' && (
          <>
            <div className="flex justify-center">
              <div className="rounded-full bg-blue-100 p-6">
                <Loader2 className="h-16 w-16 text-blue-600 animate-spin" />
              </div>
            </div>
            <div className="space-y-2">
              <h1 className="text-3xl font-bold text-blue-600">Đang xử lý thanh toán...</h1>
              <p className="text-lg text-muted-foreground">
                Vui lòng đợi trong giây lát
              </p>
            </div>
          </>
        )}

        {/* Error State */}
        {status === 'error' && (
          <>
            <div className="flex justify-center">
              <div className="rounded-full bg-red-100 p-6">
                <AlertCircle className="h-16 w-16 text-red-600" />
              </div>
            </div>
            <div className="space-y-2">
              <h1 className="text-3xl font-bold text-red-600">Có lỗi xảy ra!</h1>
              <p className="text-lg text-muted-foreground">{error}</p>
            </div>
            <div className="flex gap-4 justify-center">
              <Button variant="outline" onClick={handleRefresh}>
                <Loader2 className="mr-2 h-4 w-4" />
                Làm mới
              </Button>
              <Link to="/">
                <Button variant="outline">
                  <Home className="mr-2 h-4 w-4" />
                  Về trang chủ
                </Button>
              </Link>
            </div>
          </>
        )}

        {/* Success State */}
        {status === 'success' && (
          <>
            <div className="flex justify-center">
              <div className="rounded-full bg-green-100 p-6">
                <CheckCircle className="h-16 w-16 text-green-600" />
              </div>
            </div>
            <div className="space-y-2">
              <h1 className="text-3xl font-bold text-green-600">Thanh toán thành công!</h1>
              <p className="text-lg text-muted-foreground">
                Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi
              </p>
            </div>
          </>
        )}

        {/* Order Info */}
        {order && (
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Package className="h-5 w-5" />
                Thông tin đơn hàng
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-muted-foreground">Mã đơn hàng:</span>
                <span className="font-semibold">{order.orderNumber}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-muted-foreground">Trạng thái:</span>
                <span className="font-semibold">{order.status}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-muted-foreground">Tổng tiền:</span>
                <span className="font-semibold text-green-600">
                  {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.total)}
                </span>
              </div>
              <Separator />
              <div className="text-sm text-muted-foreground space-y-2">
                <p>• Chúng tôi sẽ gửi email xác nhận đơn hàng trong vài phút tới</p>
                <p>• Đơn hàng sẽ được xử lý và giao trong 2-3 ngày làm việc</p>
                <p>• Bạn có thể theo dõi trạng thái đơn hàng trong trang "Đơn hàng của tôi"</p>
              </div>
            </CardContent>
          </Card>
        )}

        {/* Action Buttons */}
        {(status === 'success' || status === 'waiting_payment') && (
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/profile/orders">
              <Button size="lg" className="w-full sm:w-auto">
                Xem đơn hàng của tôi
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </Link>
            <Link to="/">
              <Button variant="outline" size="lg" className="w-full sm:w-auto">
                <Home className="mr-2 h-4 w-4" />
                Về trang chủ
              </Button>
            </Link>
          </div>
        )}

        {/* Continue Shopping */}
        {status === 'success' && (
          <div className="pt-6">
            <p className="text-muted-foreground mb-4">Hoặc tiếp tục khám phá sản phẩm</p>
            <Link to="/products" search={{ category: undefined, keyword: undefined }}>
              <Button variant="ghost">
                Tiếp tục mua sắm
              </Button>
            </Link>
          </div>
        )}

        {/* Payment Confirmation Dialog */}
        <AlertDialog open={showConfirmDialog} onOpenChange={setShowConfirmDialog}>
          <AlertDialogContent>
            <AlertDialogHeader>
              <AlertDialogTitle>Xác nhận thanh toán</AlertDialogTitle>
              <AlertDialogDescription>
                Bạn có chắc chắn muốn thanh toán đơn hàng <strong>#{order?.orderNumber}</strong>?
                <br />
                Tổng tiền: <strong className="text-green-600">
                  {order && new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.total)}
                </strong>
              </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter>
              <AlertDialogCancel>Hủy</AlertDialogCancel>
              <AlertDialogAction
                onClick={handlePayment}
                className="bg-green-600 hover:bg-green-700"
              >
                Xác nhận thanh toán
              </AlertDialogAction>
            </AlertDialogFooter>
          </AlertDialogContent>
        </AlertDialog>
      </div>
    </div>
  )
}