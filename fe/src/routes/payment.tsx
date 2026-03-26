import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import React from 'react'
import { ArrowLeft, CreditCard, Truck, MapPin, Phone, User, FileText, Loader2, Tag, X, CheckCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Separator } from '@/components/ui/separator'
import { PaymentMethods } from '@/components/payment-methods'
import { useCartItems } from '@/hooks/use-cart'
import { useCreateOrder } from '@/hooks/use-order'
import { useProduct } from '@/hooks/use-product'
import { useUserProfile } from '@/hooks/use-user'
import { useValidateCoupon } from '@/hooks/use-coupon'
import { toast } from 'sonner'
import { getErrorMessage, isInsufficientStockError } from '@/lib/error-utils'
import type { CartItemResponse } from '@/types/api'

export const Route = createFileRoute('/payment')({
  component: PaymentPage,
})

const paymentSchema = z.object({
  recipientName: z.string().min(1, 'Tên người nhận là bắt buộc'),
  recipientPhone: z.string().min(10, 'Số điện thoại phải có ít nhất 10 số'),
  shippingAddress: z.string().min(1, 'Địa chỉ là bắt buộc'),
  shippingWard: z.string().min(1, 'Phường/Xã là bắt buộc'),
  shippingDistrict: z.string().min(1, 'Quận/Huyện là bắt buộc'),
  shippingCity: z.string().min(1, 'Tỉnh/Thành phố là bắt buộc'),
  paymentMethod: z.enum(['COD', 'PAYOS', 'BANK_TRANSFER']),
  note: z.string().optional(),
  couponCode: z.string().optional(),
})

type PaymentFormData = z.infer<typeof paymentSchema>

function CartSummary({ appliedCoupon, discountAmount }: { appliedCoupon?: { code: string; discountAmount: number } | null; discountAmount?: number }) {
  const { data: cartItems, isLoading } = useCartItems()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-8">
        <Loader2 className="h-6 w-6 animate-spin" />
      </div>
    )
  }

  if (!cartItems || cartItems.length === 0) {
    return (
      <div className="text-center py-8">
        <p className="text-muted-foreground">Giỏ hàng trống</p>
      </div>
    )
  }

  const subtotal = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const shippingFee = 0
  const discount = discountAmount ?? 0
  const total = subtotal + shippingFee - discount

  return (
    <div className="space-y-4">
      {cartItems.map((item) => (
        <CartItemSummary key={item.id} item={item} />
      ))}
      
      <Separator />
      
      <div className="space-y-2 text-sm">
        <div className="flex justify-between">
          <span className="text-muted-foreground">Tạm tính</span>
          <span>{subtotal.toLocaleString('vi-VN')}đ</span>
        </div>
        <div className="flex justify-between">
          <span className="text-muted-foreground">Phí vận chuyển</span>
          <span className="text-green-600">Miễn phí</span>
        </div>
        {appliedCoupon && discount > 0 && (
          <div className="flex justify-between text-green-600">
            <span className="flex items-center gap-1">
              <Tag className="h-3 w-3" />
              Giảm giá ({appliedCoupon.code})
            </span>
            <span>-{discount.toLocaleString('vi-VN')}đ</span>
          </div>
        )}
      </div>
      <Separator />
      
      <div className="flex justify-between font-bold text-lg">
        <span>Tổng cộng</span>
        <span className="text-primary">{total.toLocaleString('vi-VN')}đ</span>
      </div>
    </div>
  )
}

function CartItemSummary({ item }: { item: CartItemResponse }) {
  const { data: product } = useProduct(item.productId)

  if (!product) return null

  return (
    <div className="flex gap-3">
      <div className="h-16 w-12 rounded bg-gray-100 flex items-center justify-center overflow-hidden">
        {product.coverImageUrl ? (
          <img
            src={product.coverImageUrl}
            alt={product.title}
            className="h-full w-full object-cover"
          />
        ) : (
          <span className="text-xs text-muted-foreground">Không ảnh</span>
        )}
      </div>
      <div className="flex-1">
        <h4 className="font-medium text-sm">{product.title}</h4>
        {product.author && (
          <p className="text-xs text-muted-foreground">{product.author}</p>
        )}
        <div className="flex justify-between items-center mt-1">
          <span className="text-xs text-muted-foreground">x{item.quantity}</span>
          <span className="font-semibold text-sm">{(item.price * item.quantity).toLocaleString('vi-VN')}đ</span>
        </div>
      </div>
    </div>
  )
}

function PaymentPage() {
  const navigate = useNavigate()
  const { data: cartItems, isLoading: cartLoading } = useCartItems()
  const { data: userProfile, isLoading: profileLoading } = useUserProfile()
  const createOrder = useCreateOrder()
  const validateCoupon = useValidateCoupon()
  const [isSubmitting, setIsSubmitting] = React.useState(false)
  const [appliedCoupon, setAppliedCoupon] = React.useState<{ code: string; discountAmount: number } | null>(null)
  const [couponInput, setCouponInput] = React.useState('')
  const [isValidatingCoupon, setIsValidatingCoupon] = React.useState(false)

  const form = useForm<PaymentFormData>({
    resolver: zodResolver(paymentSchema),
    defaultValues: {
      recipientName: '',
      recipientPhone: '',
      shippingAddress: '',
      shippingWard: '',
      shippingDistrict: '',
      shippingCity: '',
      paymentMethod: 'BANK_TRANSFER',
      note: '',
      couponCode: '',
    },
  })

  // Function to fill sample data for testing
  const fillSampleData = () => {
    form.setValue('recipientName', 'Nguyễn Văn A')
    form.setValue('recipientPhone', '0123456789')
    form.setValue('shippingAddress', '123 Đường ABC')
    form.setValue('shippingWard', 'Phường 1')
    form.setValue('shippingDistrict', 'Quận 1')
    form.setValue('shippingCity', 'TP. Hồ Chí Minh')
    form.setValue('note', 'Giao hàng trong giờ hành chính')
    toast.success('Đã điền thông tin mẫu!')
  }

  // Function to clear saved data
  const clearSavedData = () => {
    localStorage.removeItem('payment-form-data')
    form.reset({
      paymentMethod: 'BANK_TRANSFER',
      note: '',
    })
    toast.success('Đã xóa dữ liệu đã lưu!')
  }

  // Calculate subtotal for coupon validation
  const subtotal = cartItems?.reduce((sum, item) => sum + item.price * item.quantity, 0) ?? 0

  // Handle coupon validation
  const handleApplyCoupon = async () => {
    if (!couponInput.trim()) {
      toast.error('Vui lòng nhập mã coupon')
      return
    }

    setIsValidatingCoupon(true)
    try {
      const result = await validateCoupon.mutateAsync({
        code: couponInput.trim().toUpperCase(),
        orderTotal: subtotal,
      })

      if (result.valid && result.coupon) {
        setAppliedCoupon({
          code: result.coupon.code,
          discountAmount: result.discountAmount,
        })
        form.setValue('couponCode', result.coupon.code)
        toast.success(`Áp dụng coupon thành công! Giảm ${result.discountAmount.toLocaleString('vi-VN')}đ`)
      } else {
        toast.error(result.message || 'Coupon không hợp lệ')
      }
    } catch (error) {
      toast.error('Không thể xác thực coupon. Vui lòng thử lại.')
    } finally {
      setIsValidatingCoupon(false)
    }
  }

  // Handle remove coupon
  const handleRemoveCoupon = () => {
    setAppliedCoupon(null)
    setCouponInput('')
    form.setValue('couponCode', '')
    toast.info('Đã xóa coupon')
  }

  // Auto-fill form when user profile data is available
  React.useEffect(() => {
    if (userProfile) {
      const fullName = [userProfile.firstName, userProfile.lastName]
        .filter(Boolean)
        .join(' ')
      
      // Reset form with user profile data
      form.reset({
        recipientName: fullName || '',
        recipientPhone: userProfile.phoneNumber || '',
        shippingAddress: userProfile.address || '',
        shippingWard: userProfile.ward || '',
        shippingDistrict: userProfile.district || '',
        shippingCity: userProfile.city || '',
        paymentMethod: 'BANK_TRANSFER',
        note: '',
      })
    }
  }, [userProfile, form])

  // Auto-save form data to localStorage
  React.useEffect(() => {
    const subscription = form.watch((value) => {
      localStorage.setItem('payment-form-data', JSON.stringify(value))
    })
    return () => subscription.unsubscribe()
  }, [form])

  // Load saved form data from localStorage
  React.useEffect(() => {
    const savedData = localStorage.getItem('payment-form-data')
    if (savedData) {
      try {
        const parsedData = JSON.parse(savedData)
        Object.keys(parsedData).forEach((key) => {
          if (parsedData[key] && !form.getValues(key as keyof PaymentFormData)) {
            form.setValue(key as keyof PaymentFormData, parsedData[key])
          }
        })
      } catch (error) { /* empty */ }
    }
  }, [form])

  // Redirect if cart is empty (but not when submitting)
  React.useEffect(() => {
    if (!cartLoading && !isSubmitting && (!cartItems || cartItems.length === 0)) {
      navigate({ to: '/cart' })
    }
  }, [cartLoading, cartItems, navigate, isSubmitting])

  // Don't render if cart is empty (but allow rendering when submitting)
  if (!cartLoading && !isSubmitting && (!cartItems || cartItems.length === 0)) {
    return null
  }

  const onSubmit = async (data: PaymentFormData) => {
    try {
      setIsSubmitting(true)
      const order = await createOrder.mutateAsync(data)
      
      // Clear saved form data after successful order
      localStorage.removeItem('payment-form-data')
      
      
      // Show success message and redirect after a short delay
      toast.success('Đặt hàng thành công! Đang chuyển hướng...')
      
      // Wait a bit for toast to show and state to update
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      // Force navigation with window.location.replace
      const url = `/payment-success?orderNumber=${encodeURIComponent(order.orderNumber)}`
      
      // Use replace to avoid back button issues
      window.location.replace(url)
      
    } catch (error: any) {
      console.error('Order creation error:', error)
      
      // Use error utility to get appropriate message
      const errorMessage = getErrorMessage(error)
      
      // Check if it's a stock error and show additional guidance
      const { isStockError, productId } = isInsufficientStockError(error)
      if (isStockError) {
        toast.error(errorMessage, {
          duration: 6000, // Show longer for stock errors
          action: {
            label: 'Xem giỏ hàng',
            onClick: () => navigate({ to: '/cart' })
          }
        })
      } else {
        toast.error(errorMessage)
      }
      
      setIsSubmitting(false)
    }
  }

  if (cartLoading || profileLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  return (
    <div className="max-w-6xl mx-auto">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => navigate({ to: '/cart' })}
          >
            <ArrowLeft className="h-5 w-5" />
          </Button>
          <h1 className="text-2xl font-bold">Thanh toán</h1>
        </div>
        
        {/* Control buttons for testing */}
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={fillSampleData}
            className="text-xs"
          >
            Điền thông tin mẫu
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={clearSavedData}
            className="text-xs"
          >
            Xóa dữ liệu
          </Button>
        </div>
      </div>

      <div className="grid gap-8 lg:grid-cols-3">
        {/* Payment Form */}
        <div className="lg:col-span-2">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              {/* Shipping Information */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Truck className="h-5 w-5" />
                    Thông tin giao hàng
                  </CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid gap-4 md:grid-cols-2">
                    <FormField
                      control={form.control}
                      name="recipientName"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="flex items-center gap-2">
                            <User className="h-4 w-4" />
                            Tên người nhận
                          </FormLabel>
                          <FormControl>
                            <Input placeholder="Nhập tên người nhận" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="recipientPhone"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="flex items-center gap-2">
                            <Phone className="h-4 w-4" />
                            Số điện thoại
                          </FormLabel>
                          <FormControl>
                            <Input placeholder="Nhập số điện thoại" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <FormField
                    control={form.control}
                    name="shippingAddress"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="flex items-center gap-2">
                          <MapPin className="h-4 w-4" />
                          Địa chỉ cụ thể
                        </FormLabel>
                        <FormControl>
                          <Input placeholder="Số nhà, tên đường..." {...field} />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  <div className="grid gap-4 md:grid-cols-3">
                    <FormField
                      control={form.control}
                      name="shippingWard"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Phường/Xã</FormLabel>
                          <FormControl>
                            <Input placeholder="Phường/Xã" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="shippingDistrict"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Quận/Huyện</FormLabel>
                          <FormControl>
                            <Input placeholder="Quận/Huyện" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name="shippingCity"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>Tỉnh/Thành phố</FormLabel>
                          <FormControl>
                            <Input placeholder="Tỉnh/Thành phố" {...field} />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>
                </CardContent>
              </Card>

              {/* Payment Method */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <CreditCard className="h-5 w-5" />
                    Phương thức thanh toán
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <FormField
                    control={form.control}
                    name="paymentMethod"
                    render={({ field }) => (
                      <FormItem>
                        <FormControl>
                          <PaymentMethods
                            value={field.value}
                            onValueChange={field.onChange}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </CardContent>
              </Card>

              {/* Order Note */}
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <FileText className="h-5 w-5" />
                    Ghi chú đơn hàng
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <FormField
                    control={form.control}
                    name="note"
                    render={({ field }) => (
                      <FormItem>
                        <FormControl>
                          <Textarea
                            placeholder="Ghi chú thêm cho đơn hàng (tùy chọn)"
                            className="min-h-25"
                            {...field}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </CardContent>
              </Card>

              {/* Submit Button */}
              <Button
                type="submit"
                size="lg"
                className="w-full"
                disabled={createOrder.isPending}
              >
                {createOrder.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Đang xử lý...
                  </>
                ) : (
                  'Đặt hàng'
                )}
              </Button>
            </form>
          </Form>
        </div>

        {/* Order Summary */}
        <div className="lg:col-span-1">
          <Card className="sticky top-20">
            <CardHeader>
              <CardTitle>Đơn hàng của bạn</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* Coupon Input */}
              <div className="space-y-2">
                <label className="text-sm font-medium">Mã giảm giá</label>
                {appliedCoupon ? (
                  <div className="flex items-center justify-between p-3 bg-green-50 dark:bg-green-950 border border-green-200 dark:border-green-800 rounded-lg">
                    <div className="flex items-center gap-2">
                      <CheckCircle className="h-4 w-4 text-green-600" />
                      <span className="font-mono text-sm font-medium">{appliedCoupon.code}</span>
                    </div>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={handleRemoveCoupon}
                      className="h-8 w-8 p-0"
                    >
                      <X className="h-4 w-4" />
                    </Button>
                  </div>
                ) : (
                  <div className="flex gap-2">
                    <Input
                      placeholder="Nhập mã coupon"
                      value={couponInput}
                      onChange={(e) => setCouponInput(e.target.value.toUpperCase())}
                      className="flex-1"
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault()
                          handleApplyCoupon()
                        }
                      }}
                    />
                    <Button
                      type="button"
                      variant="outline"
                      onClick={handleApplyCoupon}
                      disabled={isValidatingCoupon || !couponInput.trim()}
                    >
                      {isValidatingCoupon ? (
                        <Loader2 className="h-4 w-4 animate-spin" />
                      ) : (
                        'Áp dụng'
                      )}
                    </Button>
                  </div>
                )}
              </div>
              
              <Separator />
              
              <CartSummary 
                appliedCoupon={appliedCoupon} 
                discountAmount={appliedCoupon?.discountAmount} 
              />
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
