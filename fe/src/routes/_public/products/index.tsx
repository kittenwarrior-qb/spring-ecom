import { createFileRoute } from '@tanstack/react-router'
import { Star, ShoppingCart, Filter, Grid, List } from 'lucide-react'
import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'
import { Checkbox } from '@/components/ui/checkbox'
import { Label } from '@/components/ui/label'
import { Slider } from '@/components/ui/slider'

export const Route = createFileRoute('/_public/products/')({
  component: ProductsPage,
})

const products = [
  { id: 1, title: 'The Great Gatsby', author: 'F. Scott Fitzgerald', price: 12.99, rating: 4.5, category: 'Fiction', image: '/books/book1.jpg' },
  { id: 2, title: '1984', author: 'George Orwell', price: 14.99, rating: 4.8, category: 'Science Fiction', image: '/books/book2.jpg' },
  { id: 3, title: 'To Kill a Mockingbird', author: 'Harper Lee', price: 11.99, rating: 4.7, category: 'Fiction', image: '/books/book3.jpg' },
  { id: 4, title: 'Pride and Prejudice', author: 'Jane Austen', price: 10.99, rating: 4.6, category: 'Romance', image: '/books/book4.jpg' },
  { id: 5, title: 'The Catcher in the Rye', author: 'J.D. Salinger', price: 13.99, rating: 4.3, category: 'Fiction', image: '/books/book5.jpg' },
  { id: 6, title: 'Brave New World', author: 'Aldous Huxley', price: 15.99, rating: 4.4, category: 'Science Fiction', image: '/books/book6.jpg' },
  { id: 7, title: 'The Hobbit', author: 'J.R.R. Tolkien', price: 16.99, rating: 4.9, category: 'Fantasy', image: '/books/book7.jpg' },
  { id: 8, title: 'Fahrenheit 451', author: 'Ray Bradbury', price: 12.99, rating: 4.5, category: 'Science Fiction', image: '/books/book8.jpg' },
]

const categories = ['All', 'Fiction', 'Non-Fiction', 'Science Fiction', 'Fantasy', 'Mystery', 'Romance', 'Biography']

function ProductsPage() {
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid')
  const [priceRange, setPriceRange] = useState([0, 50])

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold">All Books</h1>
          <p className="text-muted-foreground">Showing {products.length} books</p>
        </div>
        <div className="flex items-center gap-2">
          <Sheet>
            <SheetTrigger asChild>
              <Button variant="outline" size="sm">
                <Filter className="mr-2 h-4 w-4" />
                Filters
              </Button>
            </SheetTrigger>
            <SheetContent side="left">
              <SheetHeader>
                <SheetTitle>Filters</SheetTitle>
                <SheetDescription>
                  Narrow down your book search
                </SheetDescription>
              </SheetHeader>
              <div className="mt-6 space-y-6">
                <div className="space-y-4">
                  <h3 className="font-semibold">Categories</h3>
                  {categories.map((category) => (
                    <div key={category} className="flex items-center space-x-2">
                      <Checkbox id={category} />
                      <Label htmlFor={category} className="font-normal">
                        {category}
                      </Label>
                    </div>
                  ))}
                </div>
                <div className="space-y-4">
                  <h3 className="font-semibold">Price Range</h3>
                  <Slider
                    value={priceRange}
                    onValueChange={setPriceRange}
                    max={50}
                    step={1}
                  />
                  <div className="flex justify-between text-sm text-muted-foreground">
                    <span>${priceRange[0]}</span>
                    <span>${priceRange[1]}</span>
                  </div>
                </div>
              </div>
            </SheetContent>
          </Sheet>
          <Select defaultValue="newest">
            <SelectTrigger className="w-[180px]">
              <SelectValue placeholder="Sort by" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="newest">Newest</SelectItem>
              <SelectItem value="price-asc">Price: Low to High</SelectItem>
              <SelectItem value="price-desc">Price: High to Low</SelectItem>
              <SelectItem value="rating">Top Rated</SelectItem>
            </SelectContent>
          </Select>
          <div className="hidden items-center gap-1 sm:flex">
            <Button
              variant={viewMode === 'grid' ? 'default' : 'ghost'}
              size="icon"
              onClick={() => setViewMode('grid')}
            >
              <Grid className="h-4 w-4" />
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
      </div>

      {/* Products Grid */}
      <div className={viewMode === 'grid' 
        ? 'grid gap-6 sm:grid-cols-2 lg:grid-cols-4' 
        : 'space-y-4'
      }>
        {products.map((product) => (
          <Card key={product.id} className="group overflow-hidden transition-shadow hover:shadow-lg">
            {viewMode === 'grid' ? (
              <>
                <div className="aspect-3/4 bg-muted">
                  <div className="flex h-full items-center justify-center text-muted-foreground">
                    Book Cover
                  </div>
                </div>
                <CardContent className="p-4">
                  <Badge variant="secondary" className="mb-2">{product.category}</Badge>
                  <h3 className="font-semibold group-hover:text-primary">{product.title}</h3>
                  <p className="text-sm text-muted-foreground">{product.author}</p>
                  <div className="mt-2 flex items-center gap-1">
                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                    <span className="text-sm">{product.rating}</span>
                  </div>
                  <div className="mt-3 flex items-center justify-between">
                    <span className="font-bold">${product.price}</span>
                    <Button size="sm">
                      <ShoppingCart className="mr-1 h-4 w-4" />
                      Add
                    </Button>
                  </div>
                </CardContent>
              </>
            ) : (
              <CardContent className="flex gap-4 p-4">
                <div className="h-32 w-24 flex-shrink-0 bg-muted">
                  <div className="flex h-full items-center justify-center text-xs text-muted-foreground">
                    Cover
                  </div>
                </div>
                <div className="flex flex-1 flex-col justify-between">
                  <div>
                    <Badge variant="secondary" className="mb-1">{product.category}</Badge>
                    <h3 className="font-semibold">{product.title}</h3>
                    <p className="text-sm text-muted-foreground">{product.author}</p>
                    <div className="mt-1 flex items-center gap-1">
                      <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                      <span className="text-sm">{product.rating}</span>
                    </div>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="font-bold">${product.price}</span>
                    <Button size="sm">
                      <ShoppingCart className="mr-1 h-4 w-4" />
                      Add to Cart
                    </Button>
                  </div>
                </div>
              </CardContent>
            )}
          </Card>
        ))}
      </div>
    </div>
  )
}
