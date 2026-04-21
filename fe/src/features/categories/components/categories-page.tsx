import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { ConfigDrawer } from '@/components/config-drawer'
import { CategoriesProvider } from './categories-provider'
import { CategoriesTable } from './categories-table'
import { CategoriesPrimaryButtons } from './categories-primary-buttons'
import { CategoriesDialogs } from './categories-dialogs'

export function CategoriesPage() {
  return (
    <CategoriesProvider>
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
            <h2 className='text-2xl font-bold tracking-tight'>Quản Lý Danh Mục</h2>
            <p className='text-muted-foreground'>
              Quản lý danh mục sản phẩm của bạn tại đây.
            </p>
          </div>
          <CategoriesPrimaryButtons />
        </div>
        <CategoriesTable />
      </Main>

      <CategoriesDialogs />
    </CategoriesProvider>
  )
}
