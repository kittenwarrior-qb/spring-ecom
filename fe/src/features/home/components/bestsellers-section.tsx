import { Link } from '@tanstack/react-router'
import { ArrowRight, Star, Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useBestsellerProducts } from '@/hooks/use-product'
import type { ProductResponse } from '@/types/api'

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

export function BestsellersSection() {
  const { data, isLoading, error } = useBestsellerProducts(0, 4)

  if (isLoading) {
    return (
      <section>
        <div className="flex items-center justify-between">
          <h2 className="text-2xl font-bold">Bestsellers</h2>
        </div>
        <div className="mt-6 flex justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </section>
    )
  }

  if (error || !data?.content?.length) {
    return null
  }

  return (
    <section>
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Bestsellers</h2>
        <Link to="/products">
          <Button variant="ghost">
            View All
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </Link>
      </div>
      <div className="mt-6 grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {data.content.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </section>
  )
}
