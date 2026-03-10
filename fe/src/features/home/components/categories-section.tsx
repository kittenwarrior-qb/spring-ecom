import { Link } from '@tanstack/react-router'
import { Loader2 } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { useCategories } from '@/hooks/use-category'

export function CategoriesSection() {
  const { data: categories, isLoading, error } = useCategories()

  if (isLoading) {
    return (
      <section>
        <h2 className="text-2xl font-bold">Browse by Category</h2>
        <div className="mt-6 flex justify-center">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      </section>
    )
  }

  if (error || !categories?.length) {
    return null
  }

  // Only show top-level categories (parentId is null)
  const topLevelCategories = categories.filter((cat) => cat.parentId === null)

  return (
    <section>
      <h2 className="text-2xl font-bold">Browse by Category</h2>
      <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {topLevelCategories.slice(0, 6).map((category) => (
          <Link key={category.id} to="/products" search={{ category: category.slug }}>
            <Card className="transition-shadow hover:shadow-lg">
              <CardContent className="flex items-center justify-between p-4">
                <span className="font-medium">{category.name}</span>
                <Badge variant="secondary">{category.description || 'Explore'}</Badge>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </section>
  )
}
