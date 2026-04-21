import { useNavigate, useSearch } from '@tanstack/react-router'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { PermissionDenied } from '@/components/permission-denied'
import { PurchaseOrdersTable } from './components/purchase-orders-table'
import { PurchaseOrderCreateDialog } from './components/purchase-order-create-dialog'
import { PurchaseOrderDetailDialog } from './components/purchase-order-detail-dialog'
import { ReceiveGoodsDialog } from './components/receive-goods-dialog'
import { PurchaseOrdersProvider } from './components/purchase-orders-provider'
import { type NavigateFn } from '@/hooks/use-table-url-state'
import { usePermissions } from '@/hooks/use-permissions'

export function AdminPurchaseOrders() {
  const navigate = useNavigate()
  const search = useSearch({ strict: false })
  const { hasPermission } = usePermissions()

  if (!hasPermission('PRODUCT_VIEW')) {
    return (
      <>
        <Header>
          <div className='ms-auto flex items-center space-x-4 px-4'>
            <Search />
            <ProfileDropdown />
          </div>
        </Header>
        <Main>
          <PermissionDenied permission="PRODUCT_VIEW" />
        </Main>
      </>
    )
  }

  return (
    <PurchaseOrdersProvider>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className='text-2xl font-bold tracking-tight'>Quản Lý Đơn Nhập Hàng</h2>
          {hasPermission('PRODUCT_CREATE') && <PurchaseOrderCreateDialog />}
        </div>

        <div className='flex-1 copy_scrollbar -mr-2 pr-2'>
          <PurchaseOrdersTable search={search} navigate={navigate as NavigateFn} />
        </div>
      </Main>

      <PurchaseOrderDetailDialog />
      <ReceiveGoodsDialog />
    </PurchaseOrdersProvider>
  )
}
