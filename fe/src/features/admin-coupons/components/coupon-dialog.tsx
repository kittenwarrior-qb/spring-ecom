import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { format } from 'date-fns'
import { CalendarIcon, Loader2, Plus } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'
import { Calendar } from '@/components/ui/calendar'
import { Switch } from '@/components/ui/switch'
import { cn } from '@/lib/utils'
import { useCreateCoupon, useUpdateCoupon } from '@/hooks/use-coupon'
import { useCouponsContext } from './coupons-provider'
import type { CouponRequest } from '@/types/api'
import { toast } from 'sonner'

const couponFormSchema = z.object({
  code: z.string().min(1, 'Mã coupon không được để trống').max(50, 'Mã coupon tối đa 50 ký tự'),
  description: z.string().max(255, 'Mô tả tối đa 255 ký tự').optional(),
  discountType: z.enum(['PERCENTAGE', 'FIXED_AMOUNT']),
  discountValue: z.number().positive('Giá trị giảm giá phải lớn hơn 0'),
  minOrderValue: z.number().min(0, 'Giá trị đơn hàng tối thiểu không được âm').optional(),
  maxDiscount: z.number().positive('Giảm giá tối đa phải lớn hơn 0').optional().nullable(),
  usageLimit: z.number().positive('Giới hạn sử dụng phải lớn hơn 0').optional().nullable(),
  startDate: z.date(),
  endDate: z.date(),
  isActive: z.boolean(),
  notificationType: z.enum(['NONE', 'BROADCAST', 'TARGETED']),
  targetUserIds: z.array(z.number()),
}).refine((data) => data.endDate > data.startDate, {
  message: 'Ngày kết thúc phải sau ngày bắt đầu',
  path: ['endDate'],
}).refine((data) => {
  if (data.discountType === 'PERCENTAGE' && data.discountValue > 100) {
    return false
  }
  return true
}, {
  message: 'Phần trăm giảm giá không được vượt quá 100%',
  path: ['discountValue'],
}).refine((data) => {
  if (data.notificationType === 'TARGETED' && data.targetUserIds.length === 0) {
    return false
  }
  return true
}, {
  message: 'Vui lòng chọn ít nhất một người dùng',
  path: ['targetUserIds'],
})

type CouponFormValues = z.infer<typeof couponFormSchema>

interface CouponDialogProps {
  mode: 'create' | 'edit'
}

export function CouponDialog({ mode }: CouponDialogProps) {
  const { selectedCoupon, isEditDialogOpen, setIsEditDialogOpen, setSelectedCoupon } = useCouponsContext()
  
  // Local state for create mode dialog
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  
  const createCoupon = useCreateCoupon()
  const updateCoupon = useUpdateCoupon()

  const isOpen = mode === 'create' ? isCreateDialogOpen : isEditDialogOpen

  const form = useForm<CouponFormValues>({
    resolver: zodResolver(couponFormSchema),
    defaultValues: {
      code: '',
      description: '',
      discountType: 'PERCENTAGE',
      discountValue: 0,
      minOrderValue: 0,
      maxDiscount: null,
      usageLimit: null,
      startDate: new Date(),
      endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // 30 days from now
      isActive: true,
      notificationType: 'NONE',
      targetUserIds: [],
    },
  })

  // Populate form when editing
  useEffect(() => {
    if (mode === 'edit' && selectedCoupon) {
      form.reset({
        code: selectedCoupon.code,
        description: selectedCoupon.description || '',
        discountType: selectedCoupon.discountType,
        discountValue: selectedCoupon.discountValue,
        minOrderValue: selectedCoupon.minOrderValue,
        maxDiscount: selectedCoupon.maxDiscount,
        usageLimit: selectedCoupon.usageLimit,
        startDate: new Date(selectedCoupon.startDate),
        endDate: new Date(selectedCoupon.endDate),
        isActive: selectedCoupon.isActive,
        notificationType: 'NONE',
        targetUserIds: [],
      })
    } else if (mode === 'create') {
      form.reset({
        code: '',
        description: '',
        discountType: 'PERCENTAGE',
        discountValue: 0,
        minOrderValue: 0,
        maxDiscount: null,
        usageLimit: null,
        startDate: new Date(),
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
        isActive: true,
        notificationType: 'NONE',
        targetUserIds: [],
      })
    }
  }, [mode, selectedCoupon, form])

  const onSubmit = (values: CouponFormValues) => {
    const request: CouponRequest = {
      ...values,
      startDate: values.startDate.toISOString(),
      endDate: values.endDate.toISOString(),
      maxDiscount: values.maxDiscount || undefined,
      usageLimit: values.usageLimit || undefined,
      notificationType: values.notificationType,
      targetUserIds: values.notificationType === 'TARGETED' ? values.targetUserIds : undefined,
    }

    if (mode === 'create') {
      createCoupon.mutate(request, {
        onSuccess: () => {
          toast.success('Tạo coupon thành công')
          form.reset()
        },
        onError: (error) => {
          toast.error(`Lỗi tạo coupon: ${error.message}`)
        },
      })
    } else if (selectedCoupon) {
      updateCoupon.mutate(
        { id: selectedCoupon.id, request },
        {
          onSuccess: () => {
            toast.success('Cập nhật coupon thành công')
            setIsEditDialogOpen(false)
            setSelectedCoupon(null)
          },
          onError: (error) => {
            toast.error(`Lỗi cập nhật coupon: ${error.message}`)
          },
        }
      )
    }
  }

  const handleClose = () => {
    if (mode === 'edit') {
      setIsEditDialogOpen(false)
      setSelectedCoupon(null)
    } else {
      setIsCreateDialogOpen(false)
    }
  }

  const handleOpenChange = (open: boolean) => {
    if (!open) {
      handleClose()
    }
  }

  const discountType = form.watch('discountType')

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      {mode === 'create' && (
        <DialogTrigger asChild>
          <Button onClick={() => setIsCreateDialogOpen(true)}>
            <Plus className="mr-2 h-4 w-4" />
            Tạo Coupon
          </Button>
        </DialogTrigger>
      )}
      <DialogContent className="sm:max-w-[500px] max-h-[85vh] flex flex-col">
        <DialogHeader>
          <DialogTitle>
            {mode === 'create' ? 'Tạo coupon mới' : 'Chỉnh sửa coupon'}
          </DialogTitle>
          <DialogDescription>
            {mode === 'create' 
              ? 'Điền thông tin để tạo coupon giảm giá mới.' 
              : 'Cập nhật thông tin coupon.'}
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4 overflow-y-auto pr-2 -mr-2 flex-1">
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="code"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Mã coupon</FormLabel>
                    <FormControl>
                      <Input placeholder="SUMMER2024" {...field} className="uppercase" />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="discountType"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Loại giảm giá</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Chọn loại" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="PERCENTAGE">Phần trăm</SelectItem>
                        <SelectItem value="FIXED_AMOUNT">Số tiền cố định</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Mô tả</FormLabel>
                  <FormControl>
                    <Input placeholder="Giảm giá mùa hè..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="discountValue"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>
                      {discountType === 'PERCENTAGE' ? 'Phần trăm (%)' : 'Số tiền (VNĐ)'}
                    </FormLabel>
                    <FormControl>
                      <Input 
                        type="number" 
                        placeholder={discountType === 'PERCENTAGE' ? '10' : '50000'} 
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? Number(value) : 0)
                        }}
                      />
                    </FormControl>
                    {discountType === 'PERCENTAGE' && (
                      <FormDescription>Tối đa 100%</FormDescription>
                    )}
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="minOrderValue"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Đơn hàng tối thiểu (VNĐ)</FormLabel>
                    <FormControl>
                      <Input 
                        type="number" 
                        placeholder="0" 
                        {...field}
                        value={field.value ?? ''}
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? Number(value) : 0)
                        }}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="maxDiscount"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Giảm tối đa (VNĐ)</FormLabel>
                    <FormControl>
                      <Input 
                        type="number" 
                        placeholder="Không giới hạn" 
                        {...field} 
                        value={field.value ?? ''} 
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? Number(value) : null)
                        }}
                      />
                    </FormControl>
                    <FormDescription>Để trống nếu không giới hạn</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="usageLimit"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Giới hạn sử dụng</FormLabel>
                    <FormControl>
                      <Input 
                        type="number" 
                        placeholder="Không giới hạn" 
                        {...field}
                        value={field.value ?? ''} 
                        onChange={(e) => {
                          const value = e.target.value
                          field.onChange(value ? Number(value) : null)
                        }}
                      />
                    </FormControl>
                    <FormDescription>Để trống nếu không giới hạn</FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="startDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Ngày bắt đầu</FormLabel>
                    <Popover>
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button
                            variant="outline"
                            className={cn(
                              'w-full pl-3 text-left font-normal',
                              !field.value && 'text-muted-foreground'
                            )}
                          >
                            {field.value ? format(field.value, 'dd/MM/yyyy') : 'Chọn ngày'}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent className="w-auto p-0" align="start">
                        <Calendar
                          mode="single"
                          selected={field.value}
                          onSelect={field.onChange}
                          disabled={(date) => date < new Date('1900-01-01')}
                          initialFocus
                        />
                      </PopoverContent>
                    </Popover>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="endDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Ngày kết thúc</FormLabel>
                    <Popover>
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button
                            variant="outline"
                            className={cn(
                              'w-full pl-3 text-left font-normal',
                              !field.value && 'text-muted-foreground'
                            )}
                          >
                            {field.value ? format(field.value, 'dd/MM/yyyy') : 'Chọn ngày'}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent className="w-auto p-0" align="start">
                        <Calendar
                          mode="single"
                          selected={field.value}
                          onSelect={field.onChange}
                          disabled={(date) => date < new Date('1900-01-01')}
                          initialFocus
                        />
                      </PopoverContent>
                    </Popover>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="isActive"
              render={({ field }) => (
                <FormItem className="flex items-center justify-between rounded-lg border p-3">
                  <div className="space-y-0.5">
                    <FormLabel>Kích hoạt</FormLabel>
                    <FormDescription>
                      Coupon chỉ hoạt động khi được kích hoạt
                    </FormDescription>
                  </div>
                  <FormControl>
                    <Switch
                      checked={field.value}
                      onCheckedChange={field.onChange}
                    />
                  </FormControl>
                </FormItem>
              )}
            />

            {/* Notification Options */}
            <div className="space-y-4 rounded-lg border p-4">
              <h4 className="text-sm font-medium">Gửi thông báo</h4>
              
              <FormField
                control={form.control}
                name="notificationType"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Loại thông báo</FormLabel>
                    <Select onValueChange={field.onChange} value={field.value}>
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Chọn loại thông báo" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        <SelectItem value="NONE">Không gửi thông báo</SelectItem>
                        <SelectItem value="BROADCAST">Gửi cho tất cả người dùng</SelectItem>
                        <SelectItem value="TARGETED">Gửi cho người dùng cụ thể</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormDescription>
                      {field.value === 'BROADCAST' && 'Thông báo sẽ được gửi đến tất cả người dùng'}
                      {field.value === 'TARGETED' && 'Chọn người dùng cụ thể để gửi thông báo'}
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="targetUserIds"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Người dùng nhận thông báo</FormLabel>
                    <FormControl>
                      <Input
                        type="text"
                        placeholder="Nhập ID người dùng, cách nhau bằng dấu phẩy (vd: 1, 2, 3)"
                        value={field.value.join(', ')}
                        onChange={(e) => {
                          const value = e.target.value
                          const ids = value
                            .split(',')
                            .map((s) => Number(s.trim()))
                            .filter((n) => !isNaN(n) && n > 0)
                          field.onChange(ids)
                        }}
                        disabled={form.watch('notificationType') !== 'TARGETED'}
                      />
                    </FormControl>
                    <FormDescription>
                      Nhập ID của người dùng sẽ nhận thông báo coupon
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <DialogFooter className="pt-4 border-t mt-4 shrink-0">
              {mode === 'edit' && (
                <Button type="button" variant="outline" onClick={handleClose}>
                  Hủy
                </Button>
              )}
              <Button type="submit" disabled={createCoupon.isPending || updateCoupon.isPending}>
                {(createCoupon.isPending || updateCoupon.isPending) && (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                )}
                {mode === 'create' ? 'Tạo coupon' : 'Lưu thay đổi'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
