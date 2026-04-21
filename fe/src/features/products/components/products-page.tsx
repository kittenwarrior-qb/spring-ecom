import { getRouteApi } from '@tanstack/react-router'
import { Header } from '@/components/layout/header'
import { Main } from '@/components/layout/main'
import { ProfileDropdown } from '@/components/profile-dropdown'
import { Search } from '@/components/search'
import { ConfigDrawer } from '@/components/config-drawer'
import { ProductsProvider } from './products-provider'
import { ProductsTable } from './products-table'
import { ProductsPrimaryButtons } from './products-primary-buttons'
import { ProductsDialogs } from './products-dialogs'

const route = getRouteApi('/admin/products/')

export function ProductsPage() {
  const search = route.useSearch()
  const navigate = route.useNavigate()

  return (
    <ProductsProvider>
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
            <h2 className='text-2xl font-bold tracking-tight'>Quản Lý Sản Phẩm</h2>
            <p className='text-muted-foreground'>
              Quản lý các sản phẩm của bạn tại đây.
            </p>
          </div>
          <ProductsPrimaryButtons />
        </div>
        <ProductsTable search={search} navigate={navigate} />
      </Main>

      <ProductsDialogs />
    </ProductsProvider>
  )
}
