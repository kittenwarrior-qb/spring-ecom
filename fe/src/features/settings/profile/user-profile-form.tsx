import { z } from 'zod'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { useState, useEffect } from 'react'
import { Button } from '@/components/ui/button'
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { DatePicker } from '@/components/date-picker'
import { AddressSelector } from '@/components/address-selector'
import { userApi } from '@/api/user.api'
import type { UpdateProfileRequest } from '@/types/api'
import type { Province, District, Ward } from '@/hooks/use-vietnam-address'

const profileFormSchema = z.object({
  firstName: z.string().optional(),
  lastName: z.string().optional(),
  phoneNumber: z.string().optional(),
  dateOfBirth: z.string().optional(),
  address: z.string().optional(),
  ward: z.string().optional(),
  district: z.string().optional(),
  city: z.string().optional(),
  postalCode: z.string().optional(),
})

type ProfileFormValues = z.infer<typeof profileFormSchema>

export function UserProfileForm() {
  const [isLoading, setIsLoading] = useState(false)
  const [selectedProvince, setSelectedProvince] = useState<Province | null>(null)
  const [selectedDistrict, setSelectedDistrict] = useState<District | null>(null)
  const [selectedWard, setSelectedWard] = useState<Ward | null>(null)
  
  const form = useForm<ProfileFormValues>({
    resolver: zodResolver(profileFormSchema),
    defaultValues: {
      firstName: '',
      lastName: '',
      phoneNumber: '',
      dateOfBirth: '',
      address: '',
      ward: '',
      district: '',
      city: '',
      postalCode: '',
    },
  })

  // Load current user profile
  useEffect(() => {
    const loadProfile = async () => {
      try {
        const profile = await userApi.getProfile()
        form.reset({
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          phoneNumber: profile.phoneNumber || '',
          dateOfBirth: profile.dateOfBirth || '',
          address: profile.address || '',
          ward: profile.ward || '',
          district: profile.district || '',
          city: profile.city || '',
          postalCode: profile.postalCode || '',
        })
      } catch (error) {
        console.error('Failed to load profile:', error)
      }
    }
    
    loadProfile()
  }, [form])

  // Update form when address selections change
  useEffect(() => {
    if (selectedProvince) {
      form.setValue('city', selectedProvince.name)
    }
  }, [selectedProvince, form])

  useEffect(() => {
    if (selectedDistrict) {
      form.setValue('district', selectedDistrict.name)
    }
  }, [selectedDistrict, form])

  useEffect(() => {
    if (selectedWard) {
      form.setValue('ward', selectedWard.name)
    }
  }, [selectedWard, form])

  const onSubmit = async (data: ProfileFormValues) => {
    try {
      setIsLoading(true)
      await userApi.updateProfile(data as UpdateProfileRequest)
      // Show success message
      alert('Cập nhật thông tin thành công!')
    } catch (error) {
      // Show error message
      
      alert('Có lỗi xảy ra khi cập nhật thông tin!')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className='space-y-6'>
        <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
          <FormField
            control={form.control}
            name='firstName'
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tên</FormLabel>
                <FormControl>
                  <Input placeholder='Nhập tên của bạn' {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          
          <FormField
            control={form.control}
            name='lastName'
            render={({ field }) => (
              <FormItem>
                <FormLabel>Họ</FormLabel>
                <FormControl>
                  <Input placeholder='Nhập họ của bạn' {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name='phoneNumber'
          render={({ field }) => (
            <FormItem>
              <FormLabel>Số điện thoại</FormLabel>
              <FormControl>
                <Input placeholder='Nhập số điện thoại' {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name='dateOfBirth'
          render={({ field }) => (
            <FormItem className='flex flex-col'>
              <FormLabel>Ngày sinh</FormLabel>
              <DatePicker 
                selected={field.value ? new Date(field.value) : undefined} 
                onSelect={(date) => field.onChange(date?.toISOString().split('T')[0])} 
              />
              <FormMessage />
            </FormItem>
          )}
        />

        <div className='space-y-4'>
          <h3 className='text-lg font-medium'>Địa chỉ</h3>
          
          <FormField
            control={form.control}
            name='address'
            render={({ field }) => (
              <FormItem>
                <FormLabel>Địa chỉ cụ thể</FormLabel>
                <FormControl>
                  <Input placeholder='Số nhà, tên đường' {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <AddressSelector
            selectedProvince={selectedProvince}
            selectedDistrict={selectedDistrict}
            selectedWard={selectedWard}
            onProvinceChange={setSelectedProvince}
            onDistrictChange={setSelectedDistrict}
            onWardChange={setSelectedWard}
          />

          <FormField
            control={form.control}
            name='postalCode'
            render={({ field }) => (
              <FormItem>
                <FormLabel>Mã bưu điện</FormLabel>
                <FormControl>
                  <Input placeholder='Nhập mã bưu điện' {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <Button type='submit' disabled={isLoading}>
          {isLoading ? 'Đang cập nhật...' : 'Cập nhật thông tin'}
        </Button>
      </form>
    </Form>
  )
}