import { createFileRoute } from '@tanstack/react-router'
import { AdminSuppliers } from '@/features/admin-suppliers'

export const Route = createFileRoute('/admin/suppliers/')({
  component: AdminSuppliers,
  validateSearch: (search: Record<string, unknown>) => ({
    page: Number(search.page) || 0,
    size: Number(search.size) || 10,
    sort: (search.sort as string) || 'id,desc',
  }),
})
