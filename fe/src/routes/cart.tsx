import { createFileRoute, Link } from '@tanstack/react-router'
import { Trash2, Minus, Plus, ShoppingBag, ArrowRight, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Separator } from '@/components/ui/separator'
import { useCartItems, useUpdateCartItem, useRemoveCartItem, useClearCart } from '@/hooks/use-cart'
import { useProduct } from '@/hooks/use-product'
import { toast } from 'sonner'

export const Route = createFileRoute('/cart')({
  component: CartPage,
})

function CartItem({ 
  productId, 
  quantity, 
  price 
}: { 
  productId: number
  quantity: number
  price: number 
}) {
  const { data: product, isLoading } = useProduct(productId)
  const updateCartItem = useUpdateCartItem()
  const removeCartItem = useRemoveCartItem()

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-8">
        <Loader2 className="h-6 w-6 animate-spin" />
      </div>
    )
  }

  if (!product) return null

  const handleUpdateQuantity = (newQuantity: number) => {
    if (newQuantity < 1) return
    if (newQuantity > product.stockQuantity) {
      toast.error('Số lượng vượt quá tồn kho')
      return
    }
    updateCartItem.mutate(
      { productId, request: { quantity: newQuantity } },
      {
        onError: () => toast.error('Không thể cập nhật số lượng'),
      }
    )
  }

  const handleRemove = () => {
    removeCartItem.mutate(productId, {
      onSuccess: () => toast.success('Đã xóa sản phẩm'),
      onError: () => toast.error('Không thể xóa sản phẩm'),
    })
  }

  return (
    <Card>
      <CardContent className="p-4">
        <div className="flex gap-4">
          {/* Product Image */}
          <div className="h-24 w-20 rounded-md bg-gray-100 flex items-center justify-center overflow-hidden">
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

          {/* Product Info */}
          <div className="flex-1">
            <Link to="/products/$slug" params={{ slug: product.slug }}>
              <h3 className="font-semibold hover:text-primary">{product.title}</h3>
            </Link>
            {product.author && (
              <p className="text-sm text-muted-foreground">{product.author}</p>
            )}
            <p className="mt-2 font-bold text-primary">
              {price.toLocaleString('vi-VN')}đ
            </p>
          </div>

          {/* Quantity Controls */}
          <div className="flex items-center gap-2">
            <Button 
              variant="outline" 
              size="icon" 
              className="h-8 w-8"
              onClick={() => handleUpdateQuantity(quantity - 1)}
              disabled={updateCartItem.isPending}
            >
              <Minus className="h-4 w-4" />
            </Button>
            <span className="w-8 text-center font-semibold">{quantity}</span>
            <Button 
              variant="outline" 
              size="icon" 
              className="h-8 w-8"
              onClick={() => handleUpdateQuantity(quantity + 1)}
              disabled={updateCartItem.isPending}
            >
              <Plus className="h-4 w-4" />
            </Button>
          </div>

          {/* Remove Button */}
          <Button 
            variant="ghost" 
            size="icon" 
            className="text-red-500 hover:text-red-600 flex items-center justify-center"
            onClick={handleRemove}
            disabled={removeCartItem.isPending}
          >
            <Trash2 className="h-5 w-5" />
          </Button>
        </div>
      </CardContent>
    </Card>
  )
}

function CartPage() {
  const { data: cartItems, isLoading, error } = useCartItems()
  const clearCart = useClearCart()

  if (isLoading) {
    return (
      <div className="flex min-h-[60vh] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  if (error) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-6">
        <p className="text-lg text-muted-foreground">Không thể tải giỏ hàng</p>
        <Link to="/products" search={{ category: undefined, keyword: undefined }}>
          <Button>Tiếp tục mua sắm</Button>
        </Link>
      </div>
    )
  }

  if (!cartItems || cartItems.length === 0) {
    return (
      <div className="flex min-h-[60vh] flex-col items-center justify-center gap-6">
        <div className="rounded-full bg-muted p-6">
          <ShoppingBag className="h-12 w-12 text-muted-foreground" />
        </div>
        <div className="text-center">
          <h1 className="text-2xl font-bold">Giỏ hàng trống</h1>
          <p className="mt-2 text-muted-foreground">
            Bạn chưa thêm sản phẩm nào vào giỏ hàng.
          </p>
        </div>
        <Link to="/products" search={{ category: undefined, keyword: undefined }}>
          <Button>
            Tiếp tục mua sắm
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </Link>
      </div>
    )
  }

  const subtotal = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const shipping = subtotal > 500000 ? 0 : 30000
  const total = subtotal + shipping

  return (
    <div className="grid gap-8 lg:grid-cols-3">
      {/* Cart Items */}
      <div className="lg:col-span-2 space-y-4">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">Giỏ hàng ({cartItems.length} sản phẩm)</h1>
          <Button 
            variant="ghost" 
            className="text-red-500 hover:text-red-600"
            onClick={() => clearCart.mutate()}
            disabled={clearCart.isPending}
          >
            Xóa tất cả
          </Button>
        </div>
        
        {cartItems.map((item) => (
          <CartItem 
            key={item.id}
            productId={item.productId}
            quantity={item.quantity}
            price={item.price}
          />
        ))}
      </div>

      {/* Order Summary */}
      <div className="lg:col-span-1">
        <Card className="sticky top-20">
          <CardHeader>
            <CardTitle>Tóm tắt đơn hàng</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Tạm tính</span>
              <span>{subtotal.toLocaleString('vi-VN')}đ</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-muted-foreground">Phí vận chuyển</span>
              <span>{shipping === 0 ? 'Miễn phí' : `${shipping.toLocaleString('vi-VN')}đ`}</span>
            </div>
            {shipping > 0 && (
              <p className="text-xs text-muted-foreground">
                Mua thêm {(500000 - subtotal).toLocaleString('vi-VN')}đ để được miễn phí vận chuyển
              </p>
            )}
            <Separator />
            <div className="flex justify-between font-bold text-lg">
              <span>Tổng cộng</span>
              <span className="text-primary">{total.toLocaleString('vi-VN')}đ</span>
            </div>
          </CardContent>
          <CardFooter className="flex-col gap-2">
            <Button className="w-full" size="lg">
              Tiến hành thanh toán
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
            <Link to="/products" search={{ category: undefined, keyword: undefined }} className="w-full">
              <Button variant="outline" className="w-full">
                Tiếp tục mua sắm
              </Button>
            </Link>
          </CardFooter>
        </Card>
      </div>
    </div>
  )
}
