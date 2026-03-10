import { createFileRoute, Link } from '@tanstack/react-router'
import { Minus, Plus, ShoppingCart, Truck, Shield, RotateCcw, Loader2, Star } from 'lucide-react'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { useProductBySlug, useProducts } from '@/hooks/use-product'
import { useAddToCart } from '@/hooks/use-cart'
import { ProductCard } from '@/features/products/components/product-card'
import { toast } from 'sonner'

export const Route = createFileRoute('/products/$slug')({
  component: ProductDetailPage,
})

function ProductDetailPage() {
  const { slug } = Route.useParams()
  const { data: product, isLoading, error } = useProductBySlug(slug)
  const { data: relatedProducts } = useProducts(0, 4, 'id,desc')
  const addToCart = useAddToCart()
  const [quantity, setQuantity] = useState(1)

  if (isLoading) {
    return (
      <div className="flex min-h-[400px] items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    )
  }

  if (error || !product) {
    return (
      <div className="flex min-h-[400px] flex-col items-center justify-center gap-4">
        <p className="text-lg text-muted-foreground">Không tìm thấy sản phẩm</p>
        <Link to="/">
          <Button variant="outline">Về trang chủ</Button>
        </Link>
      </div>
    )
  }

  const hasDiscount = product.discountPrice !== null && product.discountPrice < product.price
  const discountPercent = hasDiscount
    ? Math.round(((product.price - product.discountPrice!) / product.price) * 100)
    : 0
  const savings = hasDiscount ? product.price - product.discountPrice! : 0
  const isOutOfStock = product.stockQuantity <= 0

  const handleAddToCart = () => {
    const finalPrice = product.discountPrice ?? product.price
    addToCart.mutate(
      { productId: product.id, quantity, price: finalPrice },
      {
        onSuccess: () => {
          toast.success('Đã thêm vào giỏ hàng')
        },
        onError: () => {
          toast.error('Không thể thêm vào giỏ hàng')
        },
      }
    )
  }

  const decreaseQuantity = () => {
    if (quantity > 1) setQuantity(quantity - 1)
  }

  const increaseQuantity = () => {
    if (quantity < product.stockQuantity) setQuantity(quantity + 1)
  }

  return (
    <div className=" mx-auto max-w-7xl py-3">
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 mb-12">
        {/* Product Image */}
        <div className="rounded-lg flex justify-center">
          <div className="relative bg-gray-100 rounded-2xl overflow-hidden w-full max-w-md aspect-[3/4] shadow-sm">
            {hasDiscount && (
              <div className="absolute top-4 right-4 bg-red-400 text-white px-3 py-2 rounded-md text-lg font-semibold z-10">
                -{discountPercent}%
              </div>
            )}
            {product.coverImageUrl ? (
              <img
                src={product.coverImageUrl}
                alt={product.title}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="flex h-full items-center justify-center text-muted-foreground">
                Không có ảnh
              </div>
            )}
          </div>
        </div>

        {/* Product Info */}
        <div className="flex flex-col">
          {/* Breadcrumb */}
          <nav className="text-sm text-gray-500 mb-6 flex items-center flex-wrap gap-2">
            <Link to="/" className="hover:text-primary transition-colors">Trang chủ</Link>
            <span className="text-gray-300">/</span>
            <Link to="/products" search={{ category: undefined, keyword: undefined }} className="hover:text-primary transition-colors">
              Sản phẩm
            </Link>
          </nav>

          <h1 className="text-4xl font-bold mb-4 leading-tight">{product.title}</h1>

          {/* Author & Rating */}
          <div className="flex items-center gap-4 mb-6">
            {product.author && (
              <>
                <p className="text-base text-gray-500 font-medium">
                  ✎ {product.author}
                </p>
                <div className="h-4 w-px bg-gray-300"></div>
              </>
            )}
            {product.ratingCount > 0 && (
              <div className="flex items-center gap-1 text-yellow-400">
                <Star className="h-4 w-4 fill-current" />
                <span className="font-bold text-gray-700">{product.ratingAverage.toFixed(1)}</span>
                <span className="text-gray-400 text-sm">({product.ratingCount} đánh giá)</span>
              </div>
            )}
          </div>

          {/* Price */}
          <div className="rounded-2xl mb-8">
            <div className="flex items-center gap-4 mb-2">
              <span className="text-4xl font-extrabold text-red-400">
                {((product.discountPrice ?? product.price)).toLocaleString('vi-VN')}đ
              </span>
              {hasDiscount && (
                <span className="text-2xl line-through text-gray-400">
                  {product.price.toLocaleString('vi-VN')}đ
                </span>
              )}
            </div>
            {hasDiscount && (
              <div className="text-sm text-red-400 font-medium">
                Tiết kiệm: {savings.toLocaleString('vi-VN')}đ
              </div>
            )}
          </div>

          {/* Stock Status */}
          <div className="border-t border-gray-100 py-6 mb-6 space-y-4">
            <div className="flex items-center">
              <span className="text-gray-500 w-32 font-medium">Tình trạng:</span>
              {isOutOfStock ? (
                <span className="bg-red-100 text-red-400 px-3 py-1 rounded-full text-xs font-bold border border-red-200">
                  ✗ Hết hàng
                </span>
              ) : (
                <span className="bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-bold border border-green-200">
                  ✓ Còn hàng ({product.stockQuantity})
                </span>
              )}
            </div>
          </div>

          {/* Add to Cart */}
          {!isOutOfStock ? (
            <div className="mb-8">
              <div className="flex items-center gap-6 mb-6">
                <label className="font-bold uppercase text-xs tracking-wider">Số lượng:</label>
                <div className="flex items-center bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm">
                  <button
                    onClick={decreaseQuantity}
                    className="w-12 h-12 flex items-center justify-center hover:bg-gray-50 transition-colors border-r border-gray-100"
                  >
                    <Minus className="h-4 w-4" />
                  </button>
                  <input
                    type="number"
                    value={quantity}
                    min={1}
                    max={product.stockQuantity}
                    onChange={(e) => setQuantity(Math.max(1, Math.min(product.stockQuantity, Number(e.target.value))))}
                    className="w-16 text-center py-2 font-bold text-lg focus:outline-none appearance-none"
                  />
                  <button
                    onClick={increaseQuantity}
                    className="w-12 h-12 flex items-center justify-center hover:bg-gray-50 transition-colors border-l border-gray-100"
                  >
                    <Plus className="h-4 w-4" />
                  </button>
                </div>
              </div>

              <div className="flex gap-4">
                <Button
                  onClick={handleAddToCart}
                  disabled={addToCart.isPending}
                  className="flex-1 bg-primary text-white py-4 px-8 rounded-2xl font-bold flex items-center justify-center gap-3 hover:opacity-95 transform active:scale-95 transition-all shadow-lg"
                >
                  <ShoppingCart className="h-5 w-5" />
                  <span>{addToCart.isPending ? 'Đang thêm...' : 'Thêm vào giỏ hàng'}</span>
                </Button>
              </div>
            </div>
          ) : (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
              ⚠ Sản phẩm hiện tại đã hết hàng
            </div>
          )}

          {/* Shipping Info */}
          <div className="bg-white border border-gray-100 rounded-2xl p-6 space-y-4 shadow-sm">
            <div className="flex items-start">
              <div className="bg-blue-50 p-2 rounded-lg mr-4">
                <Truck className="text-blue-500 h-5 w-5" />
              </div>
              <div>
                <h4 className="font-bold text-sm">Giao hàng miễn phí</h4>
                <p className="text-xs text-gray-500">Cho đơn hàng trên 500.000đ toàn quốc</p>
              </div>
            </div>
            <div className="flex items-start border-t border-gray-50 pt-4">
              <div className="bg-green-50 p-2 rounded-lg mr-4">
                <Shield className="text-green-500 h-5 w-5" />
              </div>
              <div>
                <h4 className="font-bold text-sm">Bảo hành chính hãng</h4>
                <p className="text-xs text-gray-500">Cam kết 100% sản phẩm chính hãng</p>
              </div>
            </div>
            <div className="flex items-start border-t border-gray-50 pt-4">
              <div className="bg-orange-50 p-2 rounded-lg mr-4">
                <RotateCcw className="text-orange-500 h-5 w-5" />
              </div>
              <div>
                <h4 className="font-bold text-sm">7 ngày đổi trả</h4>
                <p className="text-xs text-gray-500">Hỗ trợ đổi trả nhanh chóng nếu có lỗi</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Product Description */}
      <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-8 mb-16">
        <h2 className="text-2xl font-bold mb-6 flex items-center gap-3">
          <span className="w-1.5 h-8 bg-primary rounded-full"></span>
          Mô tả sản phẩm
        </h2>
        <div className="text-gray-600 leading-relaxed text-lg space-y-4">
          {product.description ? (
            <div
              className="prose prose-lg max-w-none"
              dangerouslySetInnerHTML={{ __html: product.description }}
            />
          ) : (
            <>
              <p>Cuốn sách <strong>{product.title}</strong> là một trong những tác phẩm nổi bật.</p>
              <p>Nội dung cuốn sách mang lại nhiều giá trị kiến thức bổ ích, được biên soạn kỹ lưỡng và trình bày bắt mắt. Đây là lựa chọn tuyệt vời cho các độc giả yêu thích thể loại này.</p>
              <p>Tại Fahaza, chúng tôi cam kết cung cấp sách chính hãng với chất lượng tốt nhất và dịch vụ giao hàng nhanh chóng trên toàn quốc.</p>
            </>
          )}
        </div>
      </div>

      {/* Related Products */}
      {relatedProducts?.content && relatedProducts.content.length > 0 && (
        <div className="mb-12">
          <h2 className="text-2xl font-bold mb-8 flex items-center justify-between">
            <span className="flex items-center gap-3">
              <span className="w-1.5 h-8 bg-primary rounded-full"></span>
              Có thể bạn cũng thích
            </span>
            <Link to="/products" search={{ category: undefined, keyword: undefined }} className="text-primary text-sm font-bold hover:underline">
              Xem tất cả
            </Link>
          </h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {relatedProducts.content.slice(0, 4).map((relProduct) => (
              <ProductCard key={relProduct.id} product={relProduct} showPrice />
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
