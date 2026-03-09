import { createFileRoute, Link } from '@tanstack/react-router'
import { Star, ShoppingCart, Minus, Plus, Heart, Share2, ArrowLeft } from 'lucide-react'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Card, CardContent } from '@/components/ui/card'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'

export const Route = createFileRoute('/_public/products/$productId')({
  component: ProductDetailPage,
})

const product = {
  id: 1,
  title: 'The Great Gatsby',
  author: 'F. Scott Fitzgerald',
  price: 12.99,
  discountPrice: 9.99,
  rating: 4.5,
  reviewCount: 128,
  category: 'Fiction',
  publisher: 'Scribner',
  publicationYear: 1925,
  language: 'English',
  pages: 180,
  format: 'Paperback',
  stock: 25,
  description: `The Great Gatsby is a 1925 novel by American writer F. Scott Fitzgerald. Set in the Jazz Age on Long Island, the novel depicts narrator Nick Carraway's interactions with mysterious millionaire Jay Gatsby and Gatsby's obsession with reuniting with his former lover, Daisy Buchanan.

The novel was inspired by a youthful romance Fitzgerald had with socialite Ginevra King, and the riotous parties he attended on Long Island's North Shore in 1922. Following a move to the French Riviera, he completed a rough draft in 1924. He submitted the draft to editor Maxwell Perkins, who persuaded Fitzgerald to revise the work through the winter of 1924–1925.`,
  reviews: [
    { id: 1, author: 'John D.', rating: 5, comment: 'A masterpiece of American literature!', date: '2024-01-15' },
    { id: 2, author: 'Sarah M.', rating: 4, comment: 'Beautiful prose, though the ending left me melancholic.', date: '2024-01-10' },
  ],
}

function ProductDetailPage() {
  const [quantity, setQuantity] = useState(1)
  const { productId } = Route.useParams()

  return (
    <div className="space-y-8">
      {/* Back Button */}
      <Link to="/products">
        <Button variant="ghost" size="sm">
          <ArrowLeft className="mr-2 h-4 w-4" />
          Back to Products
        </Button>
      </Link>

      {/* Product Details */}
      <div className="grid gap-8 lg:grid-cols-2">
        {/* Image */}
        <div className="aspect-3/4 rounded-lg bg-muted">
          <div className="flex h-full items-center justify-center text-muted-foreground">
            Book Cover
          </div>
        </div>

        {/* Info */}
        <div className="space-y-6">
          <div>
            <Badge variant="secondary">{product.category}</Badge>
            <h1 className="mt-2 text-3xl font-bold">{product.title}</h1>
            <p className="text-lg text-muted-foreground">by {product.author}</p>
          </div>

          <div className="flex items-center gap-4">
            <div className="flex items-center gap-1">
              <Star className="h-5 w-5 fill-yellow-400 text-yellow-400" />
              <span className="font-semibold">{product.rating}</span>
              <span className="text-muted-foreground">({product.reviewCount} reviews)</span>
            </div>
            <Badge variant="outline">In Stock: {product.stock}</Badge>
          </div>

          <div className="flex items-baseline gap-3">
            {product.discountPrice ? (
              <>
                <span className="text-3xl font-bold text-primary">${product.discountPrice}</span>
                <span className="text-xl line-through text-muted-foreground">${product.price}</span>
                <Badge variant="destructive">Sale</Badge>
              </>
            ) : (
              <span className="text-3xl font-bold">${product.price}</span>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-muted-foreground">Publisher:</span> {product.publisher}
            </div>
            <div>
              <span className="text-muted-foreground">Year:</span> {product.publicationYear}
            </div>
            <div>
              <span className="text-muted-foreground">Language:</span> {product.language}
            </div>
            <div>
              <span className="text-muted-foreground">Pages:</span> {product.pages}
            </div>
            <div>
              <span className="text-muted-foreground">Format:</span> {product.format}
            </div>
          </div>

          {/* Quantity */}
          <div className="flex items-center gap-4">
            <span className="font-medium">Quantity:</span>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="icon"
                onClick={() => setQuantity(Math.max(1, quantity - 1))}
              >
                <Minus className="h-4 w-4" />
              </Button>
              <span className="w-8 text-center">{quantity}</span>
              <Button
                variant="outline"
                size="icon"
                onClick={() => setQuantity(Math.min(product.stock, quantity + 1))}
              >
                <Plus className="h-4 w-4" />
              </Button>
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-3">
            <Button size="lg" className="flex-1">
              <ShoppingCart className="mr-2 h-5 w-5" />
              Add to Cart
            </Button>
            <Button variant="outline" size="icon">
              <Heart className="h-5 w-5" />
            </Button>
            <Button variant="outline" size="icon">
              <Share2 className="h-5 w-5" />
            </Button>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="description">
        <TabsList>
          <TabsTrigger value="description">Description</TabsTrigger>
          <TabsTrigger value="reviews">Reviews ({product.reviewCount})</TabsTrigger>
        </TabsList>
        <TabsContent value="description" className="mt-4">
          <Card>
            <CardContent className="prose prose-sm max-w-none p-6">
              {product.description.split('\n\n').map((p, i) => (
                <p key={i}>{p}</p>
              ))}
            </CardContent>
          </Card>
        </TabsContent>
        <TabsContent value="reviews" className="mt-4">
          <div className="space-y-4">
            {product.reviews.map((review) => (
              <Card key={review.id}>
                <CardContent className="p-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="font-semibold">{review.author}</span>
                      <div className="flex items-center gap-1">
                        {Array.from({ length: 5 }).map((_, i) => (
                          <Star
                            key={i}
                            className={`h-4 w-4 ${
                              i < review.rating
                                ? 'fill-yellow-400 text-yellow-400'
                                : 'text-muted-foreground'
                            }`}
                          />
                        ))}
                      </div>
                    </div>
                    <span className="text-sm text-muted-foreground">{review.date}</span>
                  </div>
                  <p className="mt-2 text-muted-foreground">{review.comment}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>
      </Tabs>
    </div>
  )
}
