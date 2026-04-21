import { useNavigate, useSearch } from '@tanstack/react-router'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { CouponsTable } from './components/coupons-table'
import { CouponDialog } from './components/coupon-dialog'
import { CouponsProvider } from './components/coupons-provider'
import { type NavigateFn } from '@/hooks/use-table-url-state'
import { usePermissions } from '@/hooks/use-permissions'

export function AdminCoupons() {
  const navigate = useNavigate()
  const search = useSearch({ strict: false })
  const { hasPermission } = usePermissions()
  
  return (
    <CouponsProvider>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className='text-2xl font-bold tracking-tight'>Quản Lý Coupon</h2>
          {hasPermission('COUPON_CREATE') && <CouponDialog mode="create" />}
        </div>

        <div className='flex-1 copy-scrollbar -mr-2 pr-2'>
          <CouponsTable search={search} navigate={navigate as NavigateFn} />
        </div>
      </Main>

      <CouponDialog mode="edit" />
    </CouponsProvider>
  )
}
