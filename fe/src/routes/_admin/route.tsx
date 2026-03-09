import { createFileRoute, Outlet } from '@tanstack/react-router'
import { AdminGuard } from '@/components/auth/auth-guard'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'

export const Route = createFileRoute('/_admin')({
  component: () => (
    <AdminGuard>
      <AuthenticatedLayout>
        <Outlet />
      </AuthenticatedLayout>
    </AdminGuard>
  ),
})
