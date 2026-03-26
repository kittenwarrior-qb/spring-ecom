import { Link } from '@tanstack/react-router'
import { ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'

export function HeroSection() {
  return (
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
          <Link to="/products">
            <Button variant="outline" size="lg">
              View Bestsellers
            </Button>
          </Link>
        </div>
      </div>
    </section>
  )
}
