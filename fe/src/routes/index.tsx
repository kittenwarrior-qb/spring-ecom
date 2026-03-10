import { createFileRoute, Link } from '@tanstack/react-router'
import { ArrowRight, Book } from 'lucide-react'
import { HeroBanner } from '@/features/home/components/hero-banner'
import { ProductCard } from '@/features/products/components/product-card'
import { useProducts } from '@/hooks/use-product'

export const Route = createFileRoute('/')({
  component: HomePage,
})

function HomePage() {
  const { data: featuredProducts, isLoading: isLoadingFeatured } = useProducts(0, 8, 'id,desc')
  const { data: saleProducts, isLoading: isLoadingSale } = useProducts(1, 8, 'id,desc')

  return (
    <div className="space-y-8">
      {/* Hero Banner */}
      <HeroBanner />

      {/* Featured Products */}
      <div className="container mx-auto px-4 max-w-7xl">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-3xl font-bold text-gray-900">Sản phẩm nổi bật</h2>
          <Link 
            to="/products"
            search={{ category: undefined, keyword: undefined }}
            className="text-primary hover:text-primary-dark transition-colors font-semibold flex items-center gap-1"
          >
            Xem tất cả 
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
        
        {isLoadingFeatured ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="animate-pulse">
                <div className="bg-gray-200 aspect-[3/4] rounded-2xl mb-4"></div>
                <div className="h-4 bg-gray-200 rounded mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-3/4"></div>
              </div>
            ))}
          </div>
        ) : featuredProducts?.content && featuredProducts.content.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
            {featuredProducts.content.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        ) : (
          <div className="bg-gray-50 rounded-lg p-12 text-center">
            <div className="text-6xl mb-4">📚</div>
            <h3 className="text-2xl font-semibold text-gray-700 mb-2">Chưa có sản phẩm nào</h3>
            <p className="text-gray-600">Vui lòng kiểm tra kết nối database hoặc import dữ liệu mẫu</p>
          </div>
        )}
      </div>

      {/* Sale Products */}
      {saleProducts?.content && saleProducts.content.length > 0 && (
        <div className="container mx-auto px-4 max-w-7xl">
          <div className="flex items-center justify-between mb-6">
            <div className="flex items-center gap-3">
              <h2 className="text-3xl font-bold text-gray-900">Giảm giá sốc</h2>
            </div>
            <Link 
              to="/products"
              search={{ category: undefined, keyword: undefined }}
              className="font-semibold flex items-center gap-1 transition-colors text-orange-500"
            >
              Xem tất cả 
              <ArrowRight className="h-4 w-4" />
            </Link>
          </div>

          {isLoadingSale ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
              {Array.from({ length: 8 }).map((_, i) => (
                <div key={i} className="animate-pulse">
                  <div className="bg-gray-200 aspect-[3/4] rounded-2xl mb-4"></div>
                  <div className="h-4 bg-gray-200 rounded mb-2"></div>
                  <div className="h-3 bg-gray-200 rounded w-3/4"></div>
                </div>
              ))}
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
              {saleProducts.content.map((product) => (
                <ProductCard key={product.id} product={product} showPrice />
              ))}
            </div>
          )}
        </div>
      )}

      {/* Categories Section */}
      <div className="container mx-auto px-4 max-w-7xl">
        <h2 className="text-3xl font-bold mb-6 text-gray-900">Danh mục sản phẩm</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {[
            { name: 'Sách Kỹ Năng', icon: Book, color: 'text-blue-600' },
            { name: 'Sách Kinh Doanh', icon: Book, color: 'text-green-600' },
            { name: 'Sách Tâm Lý', icon: Book, color: 'text-purple-600' },
            { name: 'Sách Văn Học', icon: Book, color: 'text-red-600' },
          ].map((category) => (
            <Link
              key={category.name}
              to="/products"
              search={{ category: category.name.toLowerCase().replace(/\s+/g, '-'), keyword: undefined }}
              className="bg-white rounded-lg shadow-md hover:shadow-xl transition-all duration-300 p-6 text-center group"
            >
              <div className={`text-4xl mb-3 ${category.color} group-hover:scale-110 transition-all duration-300 inline-block`}>
                <category.icon className="h-10 w-10" />
              </div>
              <h3 className="font-semibold text-lg text-gray-900 group-hover:text-primary transition-colors">
                {category.name}
              </h3>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}
