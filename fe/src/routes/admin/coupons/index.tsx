import { createFileRoute } from '@tanstack/react-router'
import { AdminCoupons } from '@/features/admin-coupons'
import { PermissionGuard } from '@/components/auth/auth-guard'

export const Route = createFileRoute('/admin/coupons/')({
  component: () => (
    <PermissionGuard anyPermission={['COUPON_VIEW']}>
      <AdminCoupons />
    </PermissionGuard>
  ),
})
