import { createFileRoute } from '@tanstack/react-router'
import { AdminGuard } from '@/components/auth/auth-guard'
import { ProductsPage } from '@/features/products'

export const Route = createFileRoute('/_authenticated/products/')({
  component: () => (
    <AdminGuard>
      <ProductsPage />
    </AdminGuard>
  ),
})
