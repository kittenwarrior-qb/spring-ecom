import { createFileRoute, Link } from '@tanstack/react-router'
import { ArrowLeft, Star, ShoppingCart, Heart, Share2, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { useProductBySlug } from '@/hooks/use-product'

export const Route = createFileRoute('/products/$slug')({
  component: ProductDetailPage,
})

function ProductDetailPage() {
  const { slug } = Route.useParams()
  const { data: product, isLoading, error } = useProductBySlug(slug)

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
        <p className="text-lg text-muted-foreground">Product not found</p>
        <Link to="/">
          <Button variant="outline">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Home
          </Button>
        </Link>
      </div>
    )
  }

  const displayPrice = product.discountPrice ?? product.price
  const hasDiscount = product.discountPrice !== null && product.discountPrice < product.price
  const discountPercent = hasDiscount
    ? Math.round(((product.price - product.discountPrice!) / product.price) * 100)
    : 0

  return (
    <div className="space-y-8">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <Link to="/" className="hover:text-foreground">
          Home
        </Link>
        <span>/</span>
        <Link to="/products" className="hover:text-foreground">
          Products
        </Link>
        <span>/</span>
        <span className="text-foreground">{product.title}</span>
      </div>

      {/* Back Button */}
      <Link to="/products">
        <Button variant="ghost" size="sm">
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Products
        </Button>
      </Link>

      {/* Product Details */}
      <div className="grid gap-8 lg:grid-cols-2">
        {/* Product Image */}
        <div className="space-y-4">
          <Card className="overflow-hidden">
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
                <Badge className="absolute left-4 top-4">Bestseller</Badge>
              )}
              {hasDiscount && (
                <Badge variant="destructive" className="absolute right-4 top-4">
                  -{discountPercent}%
                </Badge>
              )}
            </div>
          </Card>
        </div>

        {/* Product Info */}
        <div className="space-y-6">
          <div>
            <h1 className="text-3xl font-bold">{product.title}</h1>
            {product.author && (
              <p className="mt-2 text-lg text-muted-foreground">by {product.author}</p>
            )}
          </div>

          {/* Rating */}
          <div className="flex items-center gap-2">
            <div className="flex items-center">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={i}
                  className={`h-5 w-5 ${
                    i < Math.floor(product.ratingAverage)
                      ? 'fill-yellow-400 text-yellow-400'
                      : 'text-muted-foreground'
                  }`}
                />
              ))}
            </div>
            <span className="text-sm text-muted-foreground">
              {product.ratingAverage.toFixed(1)} ({product.ratingCount} reviews)
            </span>
          </div>

          {/* Price */}
          <div className="flex items-baseline gap-3">
            <span className="text-3xl font-bold">${displayPrice.toFixed(2)}</span>
            {hasDiscount && (
              <span className="text-lg text-muted-foreground line-through">
                ${product.price.toFixed(2)}
              </span>
            )}
          </div>

          {/* Stock */}
          <div className="flex items-center gap-2">
            <span className="text-sm">
              {product.stockQuantity > 0 ? (
                <>
                  <span className="text-green-600">In Stock</span>
                  <span className="text-muted-foreground"> ({product.stockQuantity} available)</span>
                </>
              ) : (
                <span className="text-red-600">Out of Stock</span>
              )}
            </span>
          </div>

          {/* Actions */}
          <div className="flex flex-wrap gap-3">
            <Button size="lg" disabled={product.stockQuantity === 0}>
              <ShoppingCart className="mr-2 h-5 w-5" />
              Add to Cart
            </Button>
            <Button variant="outline" size="lg">
              <Heart className="mr-2 h-5 w-5" />
              Wishlist
            </Button>
            <Button variant="ghost" size="lg">
              <Share2 className="mr-2 h-5 w-5" />
              Share
            </Button>
          </div>

          <Separator />

          {/* Book Details */}
          <Card>
            <CardContent className="p-6">
              <h3 className="mb-4 font-semibold">Book Details</h3>
              <dl className="grid grid-cols-2 gap-4 text-sm">
                {product.publisher && (
                  <>
                    <dt className="text-muted-foreground">Publisher</dt>
                    <dd>{product.publisher}</dd>
                  </>
                )}
                {product.publicationYear && (
                  <>
                    <dt className="text-muted-foreground">Publication Year</dt>
                    <dd>{product.publicationYear}</dd>
                  </>
                )}
                {product.language && (
                  <>
                    <dt className="text-muted-foreground">Language</dt>
                    <dd>{product.language}</dd>
                  </>
                )}
                {product.pages && (
                  <>
                    <dt className="text-muted-foreground">Pages</dt>
                    <dd>{product.pages}</dd>
                  </>
                )}
                {product.format && (
                  <>
                    <dt className="text-muted-foreground">Format</dt>
                    <dd>{product.format}</dd>
                  </>
                )}
                <dt className="text-muted-foreground">Sold</dt>
                <dd>{product.soldCount}</dd>
                <dt className="text-muted-foreground">Views</dt>
                <dd>{product.viewCount}</dd>
              </dl>
            </CardContent>
          </Card>

          {/* Description */}
          {product.description && (
            <Card>
              <CardContent className="p-6">
                <h3 className="mb-4 font-semibold">Description</h3>
                <p className="text-sm text-muted-foreground whitespace-pre-line">
                  {product.description}
                </p>
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  )
}
