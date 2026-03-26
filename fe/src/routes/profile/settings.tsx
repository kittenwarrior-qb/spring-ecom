import { createFileRoute } from '@tanstack/react-router'
import { User, Lock, Bell, Shield, MapPin } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Separator } from '@/components/ui/separator'
import { Switch } from '@/components/ui/switch'
import { AddressSelector } from '@/components/address-selector'
import { DebugProfile } from '@/components/debug-profile'
import { useUserProfile, useUpdateProfile, useChangePassword } from '@/hooks/use-user'
import { toast } from 'sonner'
import { useState, useEffect } from 'react'
import type { UpdateProfileRequest, ChangePasswordRequest } from '@/types/api'
import type { Province, District, Ward } from '@/hooks/use-vietnam-address'

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

  // Address state
  const [selectedProvince, setSelectedProvince] = useState<Province | null>(null)
  const [selectedDistrict, setSelectedDistrict] = useState<District | null>(null)
  const [selectedWard, setSelectedWard] = useState<Ward | null>(null)

  // Initialize form with profile data when it loads
  useEffect(() => {
    if (profile) {
      setProfileForm({
        firstName: profile.firstName || '',
        lastName: profile.lastName || '',
        phoneNumber: profile.phoneNumber || '',
        dateOfBirth: profile.dateOfBirth || '',
        address: profile.address || '',
        city: profile.city || '',
        district: profile.district || '',
        ward: profile.ward || '',
        postalCode: profile.postalCode || '',
      })
    }
  }, [profile])

  const handleProfileChange = (field: keyof UpdateProfileRequest, value: string) => {
    setProfileForm(prev => ({ ...prev, [field]: value }))
  }

  const handlePasswordChange = (field: keyof ChangePasswordRequest, value: string) => {
    setPasswordForm(prev => ({ ...prev, [field]: value }))
  }

  const handleProvinceChange = (province: Province | null) => {
    setSelectedProvince(province)
    setSelectedDistrict(null)
    setSelectedWard(null)
    handleProfileChange('city', province?.name || '')
    handleProfileChange('district', '')
    handleProfileChange('ward', '')
  }

  const handleDistrictChange = (district: District | null) => {
    setSelectedDistrict(district)
    setSelectedWard(null)
    handleProfileChange('district', district?.name || '')
    handleProfileChange('ward', '')
  }

  const handleWardChange = (ward: Ward | null) => {
    setSelectedWard(ward)
    handleProfileChange('ward', ward?.name || '')
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
      <div>
        <h1 className="text-2xl font-bold">Cài đặt tài khoản</h1>
        <p className="text-muted-foreground">Quản lý cài đặt tài khoản của bạn</p>
      </div>

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
                value={profileForm.firstName || ''} 
                onChange={(e) => handleProfileChange('firstName', e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="lastName">Tên</Label>
              <Input 
                id="lastName" 
                value={profileForm.lastName || ''} 
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
                value={profileForm.phoneNumber || ''} 
                onChange={(e) => handleProfileChange('phoneNumber', e.target.value)}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="dateOfBirth">Ngày sinh</Label>
              <Input 
                id="dateOfBirth" 
                type="date" 
                value={profileForm.dateOfBirth || ''} 
                onChange={(e) => handleProfileChange('dateOfBirth', e.target.value)}
              />
            </div>
          </div>
          <Button onClick={handleSaveProfile} disabled={updateProfile.isPending}>
            {updateProfile.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <MapPin className="h-5 w-5" />
            <CardTitle>Địa chỉ</CardTitle>
          </div>
          <CardDescription>Cập nhật địa chỉ giao hàng mặc định</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="address">Địa chỉ cụ thể</Label>
              <Input 
                id="address" 
                placeholder="Số nhà, tên đường..."
                value={profileForm.address || ''} 
                onChange={(e) => handleProfileChange('address', e.target.value)}
              />
            </div>
            
            <AddressSelector
              selectedProvince={selectedProvince}
              selectedDistrict={selectedDistrict}
              selectedWard={selectedWard}
              defaultCity={profile?.city}
              defaultDistrict={profile?.district}
              defaultWard={profile?.ward}
              onProvinceChange={handleProvinceChange}
              onDistrictChange={handleDistrictChange}
              onWardChange={handleWardChange}
            />
            
            <div className="space-y-2">
              <Label htmlFor="postalCode">Mã bưu điện</Label>
              <Input 
                id="postalCode" 
                placeholder="700000"
                value={profileForm.postalCode || ''} 
                onChange={(e) => handleProfileChange('postalCode', e.target.value)}
              />
            </div>
          </div>
          <Button onClick={handleSaveProfile} disabled={updateProfile.isPending}>
            {updateProfile.isPending ? 'Đang lưu...' : 'Lưu địa chỉ'}
          </Button>
        </CardContent>
      </Card>

      <DebugProfile />
    </div>
  )
}
