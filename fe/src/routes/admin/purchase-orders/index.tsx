import { createFileRoute } from '@tanstack/react-router'
import { AdminPurchaseOrders } from '@/features/admin-purchase-orders'

export const Route = createFileRoute('/admin/purchase-orders/')({
  component: AdminPurchaseOrders,
  validateSearch: (search: Record<string, unknown>) => ({
    page: Number(search.page) || 0,
    size: Number(search.size) || 10,
    sort: (search.sort as string) || 'id,desc',
  }),
})
