import { createFileRoute, Link, useSearch } from '@tanstack/react-router'
import { CheckCircle, Package, ArrowRight, Home } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'

export const Route = createFileRoute('/payment-success')({
  component: PaymentSuccessPage,
  validateSearch: (search: Record<string, unknown>) => ({
    orderNumber: search.orderNumber as string | undefined,
  }),
})

function PaymentSuccessPage() {
  const { orderNumber } = useSearch({ from: '/payment-success' })

  return (
    <div className="max-w-2xl mx-auto">
      <div className="text-center space-y-6">
        {/* Success Icon */}
        <div className="flex justify-center">
          <div className="rounded-full bg-green-100 p-6">
            <CheckCircle className="h-16 w-16 text-green-600" />
          </div>
        </div>

        {/* Success Message */}
        <div className="space-y-2">
          <h1 className="text-3xl font-bold text-green-600">Đặt hàng thành công!</h1>
          <p className="text-lg text-muted-foreground">
            Cảm ơn bạn đã mua sắm tại cửa hàng của chúng tôi
          </p>
        </div>

        {/* Order Info */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Package className="h-5 w-5" />
              Thông tin đơn hàng
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {orderNumber && (
              <div className="flex justify-between items-center">
                <span className="text-muted-foreground">Mã đơn hàng:</span>
                <span className="font-semibold">{orderNumber}</span>
              </div>
            )}
            <Separator />
            <div className="text-sm text-muted-foreground space-y-2">
              <p>• Chúng tôi sẽ gửi email xác nhận đơn hàng trong vài phút tới</p>
              <p>• Đơn hàng sẽ được xử lý và giao trong 2-3 ngày làm việc</p>
              <p>• Bạn có thể theo dõi trạng thái đơn hàng trong trang "Đơn hàng của tôi"</p>
            </div>
          </CardContent>
        </Card>

        {/* Action Buttons */}
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

        {/* Continue Shopping */}
        <div className="pt-6">
          <p className="text-muted-foreground mb-4">Hoặc tiếp tục khám phá sản phẩm</p>
          <Link to="/products" search={{ category: undefined, keyword: undefined }}>
            <Button variant="ghost">
              Tiếp tục mua sắm
            </Button>
          </Link>
        </div>
      </div>
    </div>
  )
}