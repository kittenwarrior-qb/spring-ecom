import { createFileRoute, Link } from '@tanstack/react-router'
import { Search, Grid3X3, List, Loader2, Star } from 'lucide-react'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useProducts, useSearchProducts } from '@/hooks/use-product'
import { useCategories } from '@/hooks/use-category'
import type { ProductResponse } from '@/types/api'

export const Route = createFileRoute('/products/')({
  component: ProductsListPage,
  validateSearch: (search: Record<string, unknown>) => ({
    category: search.category as string | undefined,
    keyword: search.keyword as string | undefined,
  }),
})

function ProductCard({ product }: { product: ProductResponse }) {
  const displayPrice = product.discountPrice ?? product.price
  const hasDiscount = product.discountPrice !== null && product.discountPrice < product.price

  return (
    <Link to="/products/$slug" params={{ slug: product.slug }}>
      <Card className="group overflow-hidden transition-shadow hover:shadow-lg">
        <div className="aspect-3/4 bg-muted relative">
          {product.coverImageUrl ? (
            <img
              src={product.coverImageUrl}
              alt={product.title}
              className="h-full w-full object-cover"
            />
          ) : (
            <div className="flex h-full items-center justify-center text-muted-foreground">
              Book Cover
            </div>
          )}
          {product.isBestseller && (
            <Badge className="absolute left-2 top-2">Bestseller</Badge>
          )}
          {hasDiscount && (
            <Badge variant="destructive" className="absolute right-2 top-2">
              Sale
            </Badge>
          )}
        </div>
        <CardContent className="p-4">
          <h3 className="font-semibold group-hover:text-primary line-clamp-1">
            {product.title}
          </h3>
          {product.author && (
            <p className="text-sm text-muted-foreground line-clamp-1">{product.author}</p>
          )}
          <div className="mt-2 flex items-center gap-1">
            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
            <span className="text-sm">
              {product.ratingAverage.toFixed(1)} ({product.ratingCount})
            </span>
          </div>
          <div className="mt-2 flex items-center gap-2">
            <span className="font-bold">${displayPrice.toFixed(2)}</span>
            {hasDiscount && (
              <span className="text-sm text-muted-foreground line-through">
                ${product.price.toFixed(2)}
              </span>
            )}
          </div>
        </CardContent>
      </Card>
    </Link>
  )
}

function ProductsListPage() {
  const search = Route.useSearch()
  const [keyword, setKeyword] = useState(search.keyword || '')
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid')
  const [pageSize, setPageSize] = useState(12)

  const { data: categories } = useCategories()
  const {
    data: allProductsData,
    isLoading: isLoadingAll,
    error: errorAll,
  } = useProducts(0, pageSize)
  const {
    data: searchProductsData,
    isLoading: isLoadingSearch,
    error: errorSearch,
  } = useSearchProducts(search.keyword || '', 0, pageSize)

  const productsData = search.keyword ? searchProductsData : allProductsData
  const isLoading = search.keyword ? isLoadingSearch : isLoadingAll
  const error = search.keyword ? errorSearch : errorAll

  const products = productsData?.content || []

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold">All Products</h1>
          <p className="text-muted-foreground">
            {productsData?.totalElements || 0} products found
          </p>
        </div>
        <div className="flex items-center gap-2">
          <Button
            variant={viewMode === 'grid' ? 'default' : 'ghost'}
            size="icon"
            onClick={() => setViewMode('grid')}
          >
            <Grid3X3 className="h-4 w-4" />
          </Button>
          <Button
            variant={viewMode === 'list' ? 'default' : 'ghost'}
            size="icon"
            onClick={() => setViewMode('list')}
          >
            <List className="h-4 w-4" />
          </Button>
        </div>
      </div>

      {/* Filters */}
      <div className="flex flex-col gap-4 sm:flex-row">
        <div className="relative flex-1">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search products..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            className="pl-9"
          />
        </div>
        <Select value={pageSize.toString()} onValueChange={(v) => setPageSize(Number(v))}>
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="Page size" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="12">12 per page</SelectItem>
            <SelectItem value="24">24 per page</SelectItem>
            <SelectItem value="48">48 per page</SelectItem>
          </SelectContent>
        </Select>
        {search.category && (
          <Link to="/products" search={{}}>
            <Button variant="outline">Clear Filter</Button>
          </Link>
        )}
      </div>

      {/* Categories Filter */}
      {categories && categories.filter((c) => c.parentId === null).length > 0 && (
        <div className="flex flex-wrap gap-2">
          <Link to="/products" search={{}}>
            <Button
              variant={!search.category ? 'default' : 'outline'}
              size="sm"
            >
              All
            </Button>
          </Link>
          {categories
            .filter((c) => c.parentId === null)
            .map((category) => (
              <Link
                key={category.id}
                to="/products"
                search={{ category: category.slug, keyword: undefined }}
              >
                <Button
                  variant={search.category === category.slug ? 'default' : 'outline'}
                  size="sm"
                >
                  {category.name}
                </Button>
              </Link>
            ))}
        </div>
      )}

      {/* Products Grid */}
      {isLoading ? (
        <div className="flex min-h-[400px] items-center justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      ) : error ? (
        <div className="flex min-h-[400px] items-center justify-center">
          <p className="text-muted-foreground">Failed to load products</p>
        </div>
      ) : products.length === 0 ? (
        <div className="flex min-h-[400px] items-center justify-center">
          <p className="text-muted-foreground">No products found</p>
        </div>
      ) : (
        <div
          className={
            viewMode === 'grid'
              ? 'grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4'
              : 'space-y-4'
          }
        >
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}

      {/* Pagination */}
      {productsData && productsData.totalPages > 1 && (
        <div className="flex justify-center gap-2">
          <Button variant="outline" disabled={productsData.first}>
            Previous
          </Button>
          <span className="flex items-center px-4">
            Page {productsData.number + 1} of {productsData.totalPages}
          </span>
          <Button variant="outline" disabled={productsData.last}>
            Next
          </Button>
        </div>
      )}
    </div>
  )
}
