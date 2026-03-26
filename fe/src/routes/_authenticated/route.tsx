import { createFileRoute, Outlet } from '@tanstack/react-router'
import { AuthGuard } from '@/components/auth/auth-guard'
import { AuthenticatedLayout } from '@/components/layout/authenticated-layout'

export const Route = createFileRoute('/_authenticated')({
  component: () => (
    <AuthGuard>
      <AuthenticatedLayout>
        <Outlet />
      </AuthenticatedLayout>
    </AuthGuard>
  ),
})
