import { Link } from '@tanstack/react-router'
import { Eye, ShoppingCart, Check, Ban, Star } from 'lucide-react'
import { useState, useMemo } from 'react'
import type { ProductResponse } from '@/types/api'
import { useAddToCart, useCartItems } from '@/hooks/use-cart'
import { toast } from 'sonner'

// Import fallback image
import noImage from '@/assets/images/no-image.jpg'

interface ProductCardProps {
  product: ProductResponse
  showPrice?: boolean
}

export function ProductCard({
  product,
  showPrice = false,
}: ProductCardProps) {
  const [isHovered, setIsHovered] = useState(false)

  const { data: cartItems } = useCartItems()
  const addToCart = useAddToCart()

  const isInCart = useMemo(() => {
    return cartItems?.some(item => item.productId === product.id) || false
  }, [cartItems, product.id])

  const isOutOfStock = product.stockQuantity <= 0

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault()
    e.stopPropagation()

    if (isOutOfStock) return
    if (isInCart) return

    const finalPrice = product.discountPrice ?? product.price

    addToCart.mutate(
      { productId: product.id, quantity: 1, price: finalPrice }
    )
  }

  const discountPercentage = product.discountPrice && product.discountPrice < product.price
    ? Math.round(((product.price - product.discountPrice) / product.price) * 100)
    : 0

  // Use real rating data from API
  const ratingAverage = product.ratingAverage || 0
  const ratingCount = product.ratingCount || 0

  return (
    <div
      className="rounded-2xl transition-all duration-300 overflow-hidden group block"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Book Cover Image */}
      <div className="relative overflow-hidden bg-gray-100 aspect-[3/4]">
        <img
          src={product.coverImageUrl || noImage}
          alt={product.title}
          className="w-full h-full object-cover transition-transform duration-500"
          onError={(e) => {
            const target = e.target as HTMLImageElement
            target.src = noImage
          }}
        />

        {/* Dark Overlay on Hover */}
        <div className={`absolute inset-0 bg-black transition-all duration-300 pointer-events-none ${isHovered ? 'opacity-40' : 'opacity-0'
          }`} />

        {/* Discount Badge */}
        {showPrice && discountPercentage > 0 && (
          <div className="absolute top-3 right-3 bg-red-400 text-white px-3 py-1 rounded-full text-xs font-bold">
            -{discountPercentage}%
          </div>
        )}

        {/* Out of Stock Badge */}
        {isOutOfStock && (
          <div className="absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center">
            <span className="bg-red-400 text-white text-xs font-bold px-3 py-1 rounded-full uppercase tracking-wider shadow-lg">
              Hết hàng
            </span>
          </div>
        )}

        {/* Action Buttons */}
        <div className={`absolute inset-0 flex flex-col items-center justify-center gap-3 transition-all duration-300 transform px-4 ${isHovered ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'
          }`}>
          <Link
            to="/products/$slug"
            params={{ slug: product.slug }}
            className="w-full bg-white text-gray-800 px-6 py-2.5 rounded-lg font-semibold hover:bg-gray-100 transition-colors duration-200 flex items-center justify-center gap-2 shadow-lg"
          >
            <Eye className="h-4 w-4" />
            <span>Xem chi tiết</span>
          </Link>

          {!isOutOfStock && (
            <button
              onClick={handleAddToCart}
              disabled={isInCart || addToCart.isPending}
              className={`w-full px-6 py-2.5 rounded-lg font-semibold transition-colors duration-200 flex items-center justify-center gap-2 shadow-lg ${isInCart
                  ? 'bg-gray-200 text-gray-400 cursor-not-allowed'
                  : 'bg-white text-gray-800 hover:bg-gray-100'
                }`}
            >
              {isInCart ? (
                <>
                  <Check className="h-4 w-4" />
                  <span>Đã thêm</span>
                </>
              ) : (
                <>
                  <ShoppingCart className="h-4 w-4" />
                  <span>{addToCart.isPending ? 'Đang thêm...' : 'Thêm vào giỏ'}</span>
                </>
              )}
            </button>
          )}

          {isOutOfStock && (
            <span className="w-full bg-gray-200 text-gray-400 px-6 py-2.5 rounded-lg font-semibold cursor-not-allowed flex items-center justify-center gap-2 shadow-sm select-none">
              <Ban className="h-4 w-4" />
              <span>Hết hàng</span>
            </span>
          )}
        </div>
      </div>

      {/* Book Info */}
      <Link
        to="/products/$slug"
        params={{ slug: product.slug }}
        className="block"
      >
        <div className={`p-4 ${showPrice ? '' : 'px-2 pt-3'}`}>
          {/* Rating - Use real data from API */}
          {ratingCount > 0 && (
            <div className="flex items-center gap-2 mb-2">
              <div className="flex items-center text-yellow-400">
                <Star className="h-3 w-3 fill-current" />
                <span className="text-sm font-semibold text-gray-700 ml-1">
                  {ratingAverage.toFixed(1)}
                </span>
              </div>
              <span className="text-xs text-gray-400">
                ({ratingCount})
              </span>
            </div>
          )}

          {/* Book Title */}
          <h3 className="font-bold text-base line-clamp-2 text-gray-800 transition-colors min-h-[3rem]">
            {product.title}
          </h3>

          {/* Author */}
          <p className="text-sm text-gray-500 mb-1">
            {product.author || 'Không rõ'}
          </p>

          {/* Category */}
          {product.categoryName && (
            <p className="text-xs text-primary/70 mb-1">
              {product.categoryName}
            </p>
          )}

          {/* Price */}
          {showPrice && (
            <div className="flex items-center gap-2 mt-3">
              <span className="text-lg font-bold text-primary">
                {(product.discountPrice ?? product.price).toLocaleString('vi-VN')}đ
              </span>
              {product.discountPrice && product.discountPrice < product.price && (
                <span className="text-sm text-gray-400 line-through">
                  {product.price.toLocaleString('vi-VN')}đ
                </span>
              )}
            </div>
          )}
        </div>
      </Link>
    </div>
  )
}
