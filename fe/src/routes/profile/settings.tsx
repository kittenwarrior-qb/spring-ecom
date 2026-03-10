import { createFileRoute } from '@tanstack/react-router'
import { User, Lock, Bell, Shield } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { Switch } from '@/components/ui/switch'
import { useUserProfile, useUpdateProfile, useChangePassword } from '@/hooks/use-user'
import { toast } from 'sonner'
import { useState } from 'react'
import type { UpdateProfileRequest, ChangePasswordRequest } from '@/types/api'

export const Route = createFileRoute('/profile/settings')({
  component: SettingsPage,
})

function SettingsPage() {
  const { data: profile, isLoading } = useUserProfile()
  const updateProfile = useUpdateProfile()
  const changePassword = useChangePassword()

  const [profileForm, setProfileForm] = useState<UpdateProfileRequest>({})
  const [passwordForm, setPasswordForm] = useState<ChangePasswordRequest>({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })

  const handleProfileChange = (field: keyof UpdateProfileRequest, value: string) => {
    setProfileForm(prev => ({ ...prev, [field]: value }))
  }

  const handlePasswordChange = (field: keyof ChangePasswordRequest, value: string) => {
    setPasswordForm(prev => ({ ...prev, [field]: value }))
  }

  const handleSaveProfile = () => {
    updateProfile.mutate(profileForm, {
      onSuccess: () => {
        toast.success('Cập nhật thông tin thành công')
      },
      onError: () => {
        toast.error('Không thể cập nhật thông tin')
      }
    })
  }

  const handleChangePassword = () => {
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      toast.error('Mật khẩu xác nhận không khớp')
      return
    }
    if (passwordForm.newPassword.length < 6) {
      toast.error('Mật khẩu phải có ít nhất 6 ký tự')
      return
    }
    changePassword.mutate(passwordForm, {
      onSuccess: () => {
        toast.success('Đổi mật khẩu thành công')
        setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' })
      },
      onError: () => {
        toast.error('Không thể đổi mật khẩu. Kiểm tra mật khẩu hiện tại.')
      }
    })
  }

  if (isLoading) {
    return <div className="text-center py-8">Đang tải...</div>
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold">Cài đặt tài khoản</h1>
        <p className="text-muted-foreground">Quản lý cài đặt tài khoản của bạn</p>
      </div>

      {/* Profile Settings */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <User className="h-5 w-5" />
            <CardTitle>Thông tin cá nhân</CardTitle>
          </div>
          <CardDescription>Cập nhật thông tin cá nhân</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="firstName">Họ</Label>
              <Input 
                id="firstName" 
                defaultValue={profile?.firstName || ''} 
                onChange={(e) => handleProfileChange('firstName', e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="lastName">Tên</Label>
              <Input 
                id="lastName" 
                defaultValue={profile?.lastName || ''} 
                onChange={(e) => handleProfileChange('lastName', e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" value={profile?.email || ''} disabled />
            </div>
            <div className="space-y-2">
              <Label htmlFor="phone">Số điện thoại</Label>
              <Input 
                id="phone" 
                type="tel" 
                defaultValue={profile?.phoneNumber || ''} 
                onChange={(e) => handleProfileChange('phoneNumber', e.target.value)}
              />
            </div>
          </div>
          <Button onClick={handleSaveProfile} disabled={updateProfile.isPending}>
            {updateProfile.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
          </Button>
        </CardContent>
      </Card>

      {/* Password Settings */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Lock className="h-5 w-5" />
            <CardTitle>Mật khẩu</CardTitle>
          </div>
          <CardDescription>Đổi mật khẩu của bạn</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-4 sm:grid-cols-2">
            <div className="space-y-2">
              <Label htmlFor="current-password">Mật khẩu hiện tại</Label>
              <Input 
                id="current-password" 
                type="password" 
                value={passwordForm.currentPassword}
                onChange={(e) => handlePasswordChange('currentPassword', e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="new-password">Mật khẩu mới</Label>
              <Input 
                id="new-password" 
                type="password" 
                value={passwordForm.newPassword}
                onChange={(e) => handlePasswordChange('newPassword', e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="confirm-password">Xác nhận mật khẩu mới</Label>
              <Input 
                id="confirm-password" 
                type="password" 
                value={passwordForm.confirmPassword}
                onChange={(e) => handlePasswordChange('confirmPassword', e.target.value)}
              />
            </div>
          </div>
          <Button onClick={handleChangePassword} disabled={changePassword.isPending}>
            {changePassword.isPending ? 'Đang đổi...' : 'Đổi mật khẩu'}
          </Button>
        </CardContent>
      </Card>

      {/* Notification Settings */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Bell className="h-5 w-5" />
            <CardTitle>Thông báo</CardTitle>
          </div>
          <CardDescription>Quản lý cài đặt thông báo</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Thông báo email</Label>
              <p className="text-sm text-muted-foreground">
                Nhận email về đơn hàng của bạn
              </p>
            </div>
            <Switch defaultChecked />
          </div>
          <Separator />
          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Email khuyến mãi</Label>
              <p className="text-sm text-muted-foreground">
                Nhận email về sản phẩm mới và khuyến mãi
              </p>
            </div>
            <Switch />
          </div>
          <Separator />
          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Cập nhật đơn hàng</Label>
              <p className="text-sm text-muted-foreground">
                Nhận thông báo khi trạng thái đơn hàng thay đổi
              </p>
            </div>
            <Switch defaultChecked />
          </div>
        </CardContent>
      </Card>

      {/* Security Settings */}
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Shield className="h-5 w-5" />
            <CardTitle>Bảo mật</CardTitle>
          </div>
          <CardDescription>Quản lý cài đặt bảo mật</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Xác thực hai yếu tố</Label>
              <p className="text-sm text-muted-foreground">
                Thêm lớp bảo mật cho tài khoản
              </p>
            </div>
            <Button variant="outline">Bật</Button>
          </div>
          <Separator />
          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Phiên hoạt động</Label>
              <p className="text-sm text-muted-foreground">
                Quản lý phiên hoạt động trên các thiết bị
              </p>
            </div>
            <Button variant="outline">Quản lý</Button>
          </div>
        </CardContent>
      </Card>

      {/* Danger Zone */}
      <Card className="border-destructive">
        <CardHeader>
          <CardTitle className="text-destructive">Vùng nguy hiểm</CardTitle>
          <CardDescription>Các hành động không thể hoàn tác</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-between">
            <div className="space-y-0.5">
              <Label>Xóa tài khoản</Label>
              <p className="text-sm text-muted-foreground">
                Xóa vĩnh viễn tài khoản và tất cả dữ liệu
              </p>
            </div>
            <Button variant="destructive">Xóa tài khoản</Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
