import { createFileRoute } from '@tanstack/react-router'
import { CategoriesPage } from '@/features/categories'

export const Route = createFileRoute('/_admin/categories/')({
  component: CategoriesPage,
})
