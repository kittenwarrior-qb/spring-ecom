import { useNavigate, useSearch } from '@tanstack/react-router'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { PermissionDenied } from '@/components/permission-denied'
import { MovementsTable } from './components/movements-table'
import { type NavigateFn } from '@/hooks/use-table-url-state'
import { usePermissions } from '@/hooks/use-permissions'

export function AdminInventory() {
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
    <>
      <Header>
        <div className='ms-auto flex items-center space-x-4 px-4'>
          <Search />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className="space-y-6">
        <div className="flex items-center justify-between">
          <h2 className='text-2xl font-bold tracking-tight'>Lịch Sử Kho</h2>
        </div>

        <div className='flex-1 copy_scrollbar -mr-2 pr-2'>
          <MovementsTable search={search} navigate={navigate as NavigateFn} />
        </div>
      </Main>
    </>
  )
}
