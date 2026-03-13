import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import React from 'react'
import { ArrowLeft, CreditCard, Truck, MapPin, Phone, User, FileText, Loader2 } from 'lucide-react'
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
import { useUserSession } from '@/hooks/use-user'
import { toast } from 'sonner'
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
})

type PaymentFormData = z.infer<typeof paymentSchema>

function CartSummary() {
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
  // Bỏ phí ship - miễn phí vận chuyển cho tất cả đơn hàng
  const total = subtotal

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
  const { data: userSession, isLoading: sessionLoading } = useUserSession()
  const createOrder = useCreateOrder()

  const form = useForm<PaymentFormData>({
    resolver: zodResolver(paymentSchema),
    defaultValues: {
      recipientName: '',
      recipientPhone: '',
      shippingAddress: '',
      shippingWard: '',
      shippingDistrict: '',
      shippingCity: '',
      paymentMethod: 'COD',
      note: '',
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
      paymentMethod: 'COD',
      note: '',
    })
    toast.success('Đã xóa dữ liệu đã lưu!')
  }

  // Auto-fill form when user session data is available
  React.useEffect(() => {
    if (userSession) {
      const fullName = [userSession.firstName, userSession.lastName]
        .filter(Boolean)
        .join(' ')
      
      // Only set values if they're not already filled
      if (!form.getValues('recipientName') && fullName) {
        form.setValue('recipientName', fullName)
      }
      if (!form.getValues('recipientPhone') && userSession.phoneNumber) {
        form.setValue('recipientPhone', userSession.phoneNumber)
      }
      if (!form.getValues('shippingAddress') && userSession.address) {
        form.setValue('shippingAddress', userSession.address)
      }
      if (!form.getValues('shippingCity') && userSession.city) {
        form.setValue('shippingCity', userSession.city)
      }
      if (!form.getValues('shippingDistrict') && userSession.district) {
        form.setValue('shippingDistrict', userSession.district)
      }
      if (!form.getValues('shippingWard') && userSession.ward) {
        form.setValue('shippingWard', userSession.ward)
      }
    }
  }, [userSession, form])

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
  }, [])

  // Redirect if cart is empty
  React.useEffect(() => {
    if (!cartLoading && (!cartItems || cartItems.length === 0)) {
      navigate({ to: '/cart' })
    }
  }, [cartLoading, cartItems, navigate])

  // Don't render if cart is empty
  if (!cartLoading && (!cartItems || cartItems.length === 0)) {
    return null
  }

  const onSubmit = async (data: PaymentFormData) => {
    try {
      console.log('Submitting order:', data)
      const order = await createOrder.mutateAsync(data)
      console.log('Order created successfully:', order)
      // Clear saved form data after successful order
      localStorage.removeItem('payment-form-data')
      toast.success('Đặt hàng thành công!')
      console.log('Navigating to payment-success with orderNumber:', order.orderNumber)
      
      // Use setTimeout to avoid setState during render
      setTimeout(() => {
        navigate({ 
          to: '/payment-success',
          search: { orderNumber: order.orderNumber }
        })
      }, 0)
    } catch (error) {
      console.error('Order creation failed:', error)
      toast.error('Có lỗi xảy ra khi đặt hàng')
    }
  }

  if (cartLoading || sessionLoading) {
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
            <CardContent>
              <CartSummary />
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  )
}
