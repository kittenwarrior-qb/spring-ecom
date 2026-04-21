import { useNavigate, useSearch } from '@tanstack/react-router'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { PermissionDenied } from '@/components/permission-denied'
import { SuppliersTable } from './components/suppliers-table'
import { SupplierDialog } from './components/supplier-dialog'
import { SuppliersProvider } from './components/suppliers-provider'
import { type NavigateFn } from '@/hooks/use-table-url-state'
import { usePermissions } from '@/hooks/use-permissions'

export function AdminSuppliers() {
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
    <SuppliersProvider>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className='text-2xl font-bold tracking-tight'>Quản lý nhà cung cấp</h2>
          {hasPermission('PRODUCT_CREATE') && <SupplierDialog mode="create" />}
        </div>

        <div className='flex-1 copy_scrollbar -mr-2 pr-2'>
          <SuppliersTable search={search} navigate={navigate as NavigateFn} />
        </div>
      </Main>

      <SupplierDialog mode="edit" />
    </SuppliersProvider>
  )
}
