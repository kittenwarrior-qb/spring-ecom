import {
  Monitor,
  Bell,
  Palette,
  Wrench,
  UserCog,
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
    {
      title: 'Cài Đặt',
      items: [
        {
          title: 'Hồ Sơ',
          url: '/settings',
          icon: UserCog,
        },
        {
          title: 'Tài Khoản',
          url: '/settings/account',
          icon: Wrench,
        },
        {
          title: 'Giao Diện',
          url: '/settings/appearance',
          icon: Palette,
        },
        {
          title: 'Thông Báo',
          url: '/settings/notifications',
          icon: Bell,
        },
        {
          title: 'Hiển Thị',
          url: '/settings/display',
          icon: Monitor,
        },
      ],
    },
  ],
})


