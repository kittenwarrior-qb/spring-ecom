import { createFileRoute, Link } from '@tanstack/react-router'
import { Package, Settings, CreditCard, MapPin } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback } from '@/components/ui/avatar'
import { useUserProfile, useUpdateAvatar } from '@/hooks/use-user'
import { useMyOrders } from '@/hooks/use-order'
import { Skeleton } from '@/components/ui/skeleton'

export const Route = createFileRoute('/profile/')({
  component: ProfilePage,
})

const statusColors: Record<string, string> = {
  DELIVERED: 'bg-green-100 text-green-800',
  PROCESSING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-cyan-100 text-cyan-800',
  PENDING: 'bg-gray-100 text-gray-800',
  CANCELLED: 'bg-red-100 text-red-800',
}

const statusLabels: Record<string, string> = {
  DELIVERED: 'Đã giao',
  PROCESSING: 'Đang xử lý',
  CONFIRMED: 'Đã xác nhận',
  PENDING: 'Chờ xác nhận',
  CANCELLED: 'Đã hủy',
}

function ProfilePage() {
  const { data: profile, isLoading: profileLoading } = useUserProfile()
  const { data: ordersData, isLoading: ordersLoading } = useMyOrders(0, 5)
  const updateAvatar = useUpdateAvatar()

  const isLoading = profileLoading || ordersLoading

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="p-6">
            <div className="flex flex-col items-center gap-4 sm:flex-row sm:items-start">
              <Skeleton className="h-24 w-24 rounded-full" />
              <div className="flex-1 space-y-2">
                <Skeleton className="h-8 w-48" />
                <Skeleton className="h-4 w-64" />
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  const user = profile
  const orders = ordersData?.content || []
  const totalOrders = ordersData?.totalElements || 0

  // Calculate total spent
  const totalSpent = orders.reduce((sum, order) => sum + order.total, 0)

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      // Upload to server and get URL
      const reader = new FileReader()
      reader.onload = () => {
        const avatarUrl = reader.result as string
        updateAvatar.mutate({ avatarUrl })
      }
      reader.readAsDataURL(file)
    }
  }

  return (
    <div className="space-y-6">
      {/* Profile Header */}
      <Card>
        <CardContent className="p-6">
          <div className="flex flex-col items-center gap-4 sm:flex-row sm:items-start">
            <div className="relative">
              <Avatar className="h-24 w-24">
                <AvatarFallback className="text-2xl bg-primary text-white">
                  {user?.username?.charAt(0).toUpperCase() || 'U'}
                </AvatarFallback>
              </Avatar>
              <label className="absolute bottom-0 right-0 bg-primary text-white rounded-full p-1 cursor-pointer hover:bg-primary/90">
                <input type="file" accept="image/*" className="hidden" onChange={handleAvatarChange} />
                <Settings className="h-4 w-4" />
              </label>
            </div>
            <div className="flex-1 text-center sm:text-left">
              <h1 className="text-2xl font-bold">
                {[user?.firstName, user?.lastName].filter(Boolean).join(' ') || user?.username || 'User'}
              </h1>
              <p className="text-muted-foreground">{user?.email}</p>
              <p className="text-sm text-muted-foreground">
                Thành viên từ {user?.createdAt ? new Date(user.createdAt).toLocaleDateString('vi-VN', { month: 'long', year: 'numeric' }) : ''}
              </p>
            </div>
            <div className="flex gap-4 text-center">
              <div>
                <p className="text-2xl font-bold">{totalOrders}</p>
                <p className="text-sm text-muted-foreground">Đơn hàng</p>
              </div>
              <div>
                <p className="text-2xl font-bold">{totalSpent.toLocaleString('vi-VN')}đ</p>
                <p className="text-sm text-muted-foreground">Đã chi</p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Quick Actions */}
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-2">
        <Link to="/profile/orders">
          <Card className="transition-shadow hover:shadow-md">
            <CardHeader className="flex flex-row items-center gap-4 pb-2">
              <Package className="h-5 w-5 text-primary" />
              <CardTitle className="text-lg">Đơn hàng của tôi</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription>Xem lịch sử đơn hàng và theo dõi giao hàng</CardDescription>
            </CardContent>
          </Card>
        </Link>

        <Link to="/profile/settings">
          <Card className="transition-shadow hover:shadow-md">
            <CardHeader className="flex flex-row items-center gap-4 pb-2">
              <Settings className="h-5 w-5 text-primary" />
              <CardTitle className="text-lg">Cài đặt</CardTitle>
            </CardHeader>
            <CardContent>
              <CardDescription>Quản lý cài đặt tài khoản</CardDescription>
            </CardContent>
          </Card>
        </Link>

      </div>

      {/* Recent Orders */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>Đơn hàng gần đây</CardTitle>
          <Link to="/profile/orders">
            <Button variant="ghost" size="sm">Xem tất cả</Button>
          </Link>
        </CardHeader>
        <CardContent>
          {orders.length > 0 ? (
            <div className="space-y-4">
              {orders.slice(0, 3).map((order) => (
                <div key={order.id} className="flex items-center justify-between rounded-lg border p-4">
                  <div>
                    <p className="font-medium">{order.orderNumber}</p>
                    <p className="text-sm text-muted-foreground">
                      {new Date(order.createdAt).toLocaleDateString('vi-VN')}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="font-medium">{order.total.toLocaleString('vi-VN')}đ</p>
                    <p className={`text-sm ${statusColors[order.status]?.split(' ')[1] || 'text-gray-600'}`}>
                      {statusLabels[order.status] || order.status}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8 text-muted-foreground">
              <Package className="h-12 w-12 mx-auto mb-4 opacity-50" />
              <p>Chưa có đơn hàng nào</p>
              <Link to="/products" search={{ category: undefined, keyword: undefined }}>
                <Button className="mt-4">Mua sắm ngay</Button>
              </Link>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
