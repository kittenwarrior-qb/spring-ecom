import {
  Command,
  GalleryVerticalEnd,
  FolderOpen,
  ShoppingBag,
  LayoutDashboard,
  Users,
  Shield,
  Ticket,
} from 'lucide-react'

export const getStaticSidebarData = () => ({
  teams: [
    {
      name: 'Fahaza',
      logo: Command,
      plan: 'Bảng Quản Trị',
    },
    {
      name: 'Fahaza',
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
          title: 'Mã Giảm Giá',
          url: '/admin/coupons/',
          icon: Ticket,
        },
        {
          title: 'Người Dùng',
          url: '/admin/users/',
          icon: Users,
        },
        {
          title: 'Phân Quyền',
          url: '/admin/roles/',
          icon: Shield,
          permission: 'ROLE_VIEW',
        },
      ],
    },
  ],
})


