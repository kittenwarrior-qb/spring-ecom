import { ConfigDrawer } from '@/components/config-drawer'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { RolesTable } from './components/roles-table'
import { PermissionsTable } from './components/permissions-table'

export function RolesFeature() {
  return (
    <>
      <Header fixed>
        <Search />
        <div className='ms-auto flex items-center space-x-4'>
          <ConfigDrawer />
          <ProfileDropdown />
        </div>
      </Header>

      <Main className='flex flex-1 flex-col gap-4 sm:gap-6'>
        <div className='flex flex-wrap items-end justify-between gap-2'>
          <div>
            <h2 className='text-2xl font-bold tracking-tight'>Quản Lý Vai Trò & Quyền Hạn</h2>
            <p className='text-muted-foreground'>
              Xem, tạo mới và phân quyền cho các vai trò trong hệ thống.
            </p>
          </div>
        </div>
        
        <div className='flex flex-col gap-6'>
          <RolesTable />
          <PermissionsTable />
        </div>
      </Main>
    </>
  )
}
