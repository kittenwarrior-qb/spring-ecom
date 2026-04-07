import { createFileRoute } from '@tanstack/react-router'
import { OrderDetailPage } from '@/features/user-orders/components/order-detail-page'

export const Route = createFileRoute('/orders/$id')({
  component: OrderDetailPage,
})
