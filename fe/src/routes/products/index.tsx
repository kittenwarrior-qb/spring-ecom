import { createFileRoute, Link } from '@tanstack/react-router'
import { Search, Loader2 } from 'lucide-react'
import { useState, useEffect } from 'react'
import { useProducts, useSearchProducts, useProductsByCategory } from '@/hooks/use-product'
import { useCategories } from '@/hooks/use-category'
import { ProductCard } from '@/features/products/components/product-card'
import { Input } from '@/components/ui/input'

export const Route = createFileRoute('/products/')({
  component: ProductsListPage,
  validateSearch: (search: Record<string, unknown>) => ({
    category: search.category as string | undefined,
    keyword: search.keyword as string | undefined,
  }),
})

function ProductsListPage() {
  const search = Route.useSearch()
  const [keyword, setKeyword] = useState(search.keyword || '')
  const [debouncedKeyword, setDebouncedKeyword] = useState(search.keyword || '')

  // Debounce search
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedKeyword(keyword)
    }, 500)
    return () => clearTimeout(timer)
  }, [keyword])

  const { data: categories } = useCategories()
  const { data: allProducts, isLoading: isLoadingAll } = useProducts(0, 12)
  const { data: searchProducts, isLoading: isLoadingSearch } = useSearchProducts(debouncedKeyword, 0, 12)
  const { data: categoryProducts, isLoading: isLoadingCategory } = useProductsByCategory(search.category || '', 0, 12)

  // Determine which data to use
  const productsData = debouncedKeyword
    ? searchProducts
    : search.category
      ? categoryProducts
      : allProducts
  const isLoading = debouncedKeyword
    ? isLoadingSearch
    : search.category
      ? isLoadingCategory
      : isLoadingAll

  const products = productsData?.content || []

  return (
    <div className="container mx-auto px-4 pb-8 max-w-7xl">
      {/* Page Header */}
      <div className="mb-6">
        <h1 className="text-4xl font-bold mb-2 text-gray-900 text-center">Sách</h1>
        <p className="text-2xl text-gray-600 text-center">
          Nghe phiên bản sách nói có bản quyền của các tác phẩm bán chạy từ tác giả Việt Nam và quốc tế.
        </p>
      </div>

      {/* Search Bar */}
      <div className="mb-6 flex justify-center">
        <div className="max-w-2xl w-full">
          <div className="relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input
              type="text"
              placeholder="Tìm kiếm sách..."
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              className="w-full pl-12 pr-5 py-3 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-primary"
            />
          </div>
        </div>
      </div>

      {/* Category Pills */}
      {categories && categories.filter((c) => c.parentId === null).length > 0 && (
        <div className="mb-8 flex justify-center">
          <div className="flex flex-wrap gap-2 justify-center">
            <Link to="/products" search={{ category: undefined, keyword: undefined }}>
              <button
                className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                  !search.category
                    ? 'bg-[#1A3154] text-white'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                Mới nhất
              </button>
            </Link>
            {categories
              .filter((c) => c.parentId === null)
              .map((category) => (
                <Link
                  key={category.id}
                  to="/products"
                  search={{ category: category.slug, keyword: undefined }}
                >
                  <button
                    className={`px-4 py-2 rounded-full text-sm font-medium transition-colors ${
                      search.category === category.slug
                        ? 'bg-[#1A3154] text-white'
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    {category.name}
                  </button>
                </Link>
              ))}
          </div>
        </div>
      )}

      {/* Products Grid */}
      {isLoading ? (
        <div className="flex min-h-[400px] items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      ) : products.length === 0 ? (
        <div className="flex min-h-[400px] flex-col items-center justify-center">
          <div className="text-6xl mb-4">📚</div>
          <h3 className="text-xl font-semibold text-gray-700 mb-2">Không tìm thấy sản phẩm</h3>
          <p className="text-gray-500">Hãy thử tìm kiếm với từ khóa khác</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-3 lg:grid-cols-4 gap-8">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} showPrice />
          ))}
        </div>
      )}

      {/* Pagination Info */}
      {productsData && productsData.totalElements > 0 && (
        <div className="mt-8 text-center text-sm text-gray-500">
          Hiển thị {products.length} / {productsData.totalElements} sản phẩm
        </div>
      )}
    </div>
  )
}
