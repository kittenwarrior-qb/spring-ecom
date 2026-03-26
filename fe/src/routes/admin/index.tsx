import { createFileRoute } from '@tanstack/react-router'
import { AdminOrders } from '@/features/admin-orders'

export const Route = createFileRoute('/admin/')({
  component: AdminOrders,
})

