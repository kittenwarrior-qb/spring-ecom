import {
  Command,
  GalleryVerticalEnd,
  FolderOpen,
  ShoppingBag,
  LayoutDashboard,
  Users,
} from 'lucide-react'

export const getStaticSidebarData = () => ({
  teams: [
    {
      name: 'Spring Ecom',
      logo: Command,
      plan: 'Bảng Quản Trị',
    },
    {
      name: 'Acme Inc',
      logo: GalleryVerticalEnd,
      plan: 'Doanh Nghiệp',
    },
  ],
  navGroups: [
    {
      title: 'Quản Trị',
      items: [
        {
          title: 'Đơn Hàng',
          url: '/admin/',
          icon: LayoutDashboard,
        },
        {
          title: 'Sản Phẩm',
          url: '/admin/products/',
          icon: ShoppingBag,
        },
        {
          title: 'Danh Mục',
          url: '/admin/categories/',
          icon: FolderOpen,
        },
        {
          title: 'Người Dùng',
          url: '/admin/users/',
          icon: Users,
        },
      ],
    },
  ],
})


