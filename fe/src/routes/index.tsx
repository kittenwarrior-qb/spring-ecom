import { createFileRoute, Link } from '@tanstack/react-router'
import { ArrowRight, Star, Truck, Shield, Headphones } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

export const Route = createFileRoute('/')({
  component: HomePage,
})

const features = [
  {
    icon: Truck,
    title: 'Free Shipping',
    description: 'Free shipping on orders over $50',
  },
  {
    icon: Shield,
    title: 'Secure Payment',
    description: '100% secure payment methods',
  },
  {
    icon: Headphones,
    title: '24/7 Support',
    description: 'Dedicated customer support',
  },
]

const bestsellers = [
  { id: 1, title: 'The Great Gatsby', author: 'F. Scott Fitzgerald', price: 12.99, rating: 4.5 },
  { id: 2, title: '1984', author: 'George Orwell', price: 14.99, rating: 4.8 },
  { id: 3, title: 'To Kill a Mockingbird', author: 'Harper Lee', price: 11.99, rating: 4.7 },
  { id: 4, title: 'Pride and Prejudice', author: 'Jane Austen', price: 10.99, rating: 4.6 },
]

const categories = [
  { name: 'Fiction', count: 245 },
  { name: 'Non-Fiction', count: 189 },
  { name: 'Science Fiction', count: 156 },
  { name: 'Mystery', count: 134 },
  { name: 'Romance', count: 98 },
  { name: 'Biography', count: 67 },
]

function HomePage() {
  return (
    <div className="space-y-12">
      {/* Hero Section */}
      <section className="rounded-lg bg-gradient-to-r from-primary/10 to-primary/5 p-8 md:p-12">
        <div className="mx-auto max-w-3xl text-center">
          <h1 className="text-4xl font-bold tracking-tight md:text-5xl">
            Discover Your Next Favorite Book
          </h1>
          <p className="mt-4 text-lg text-muted-foreground">
            Browse thousands of books across all genres. From bestsellers to hidden gems,
            find your perfect read today.
          </p>
          <div className="mt-8 flex flex-col gap-4 sm:flex-row sm:justify-center">
            <Link to="/products">
              <Button size="lg">
                Browse Books
                <ArrowRight className="ml-2 h-4 w-4" />
              </Button>
            </Link>
            <Button variant="outline" size="lg">
              View Bestsellers
            </Button>
          </div>
        </div>
      </section>

      {/* Features */}
      <section>
        <div className="grid gap-6 md:grid-cols-3">
          {features.map((feature) => (
            <Card key={feature.title}>
              <CardContent className="flex items-start space-x-4 p-6">
                <div className="rounded-full bg-primary/10 p-3">
                  <feature.icon className="h-6 w-6 text-primary" />
                </div>
                <div>
                  <h3 className="font-semibold">{feature.title}</h3>
                  <p className="text-sm text-muted-foreground">{feature.description}</p>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* Bestsellers */}
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
          {bestsellers.map((book) => (
            <Card key={book.id} className="group overflow-hidden transition-shadow hover:shadow-lg">
              <div className="aspect-3/4 bg-muted">
                <div className="flex h-full items-center justify-center text-muted-foreground">
                  Book Cover
                </div>
              </div>
              <CardContent className="p-4">
                <h3 className="font-semibold group-hover:text-primary">{book.title}</h3>
                <p className="text-sm text-muted-foreground">{book.author}</p>
                <div className="mt-2 flex items-center gap-1">
                  <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                  <span className="text-sm">{book.rating}</span>
                </div>
                <p className="mt-2 font-bold">${book.price}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </section>

      {/* Categories */}
      <section>
        <h2 className="text-2xl font-bold">Browse by Category</h2>
        <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {categories.map((category) => (
            <Link key={category.name} to="/products">
              <Card className="transition-shadow hover:shadow-lg">
                <CardContent className="flex items-center justify-between p-4">
                  <span className="font-medium">{category.name}</span>
                  <Badge variant="secondary">{category.count} books</Badge>
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      </section>
    </div>
  )
}
