import { createFileRoute } from '@tanstack/react-router'
import { ProductsPage } from '@/features/products'

export const Route = createFileRoute('/admin/products/')({
  component: ProductsPage,
  validateSearch: (search: Record<string, unknown>) => ({
    page: Number(search.page) || 0,
    size: Number(search.size) || 10,
    sort: (search.sort as string) || 'id,desc',
  }),
})
