import { createFileRoute } from '@tanstack/react-router'
import { AdminInventory } from '@/features/admin-inventory'

export const Route = createFileRoute('/admin/inventory/')({
  component: AdminInventory,
  validateSearch: (search: Record<string, unknown>) => ({
    page: Number(search.page) || 0,
    size: Number(search.size) || 10,
    sort: (search.sort as string) || 'id,desc',
  }),
})
