import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Loader2, Plus } from 'lucide-react'
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
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Switch } from '@/components/ui/switch'
import { useCreateSupplier, useUpdateSupplier } from '@/hooks/use-supplier'
import { useSuppliersContext } from './suppliers-provider'
import type { SupplierRequest } from '@/types/api'
import { toast } from 'sonner'

const supplierFormSchema = z.object({
  name: z.string().min(1, 'Tên nhà cung cấp không được để trống').max(255, 'Tên tối đa 255 ký tự'),
  contactName: z.string().max(255, 'Tên người liên hệ tối đa 255 ký tự').optional().or(z.literal('')),
  email: z.string().email('Email không hợp lệ').max(255, 'Email tối đa 255 ký tự').optional().or(z.literal('')),
  phone: z.string().max(50, 'Số điện thoại tối đa 50 ký tự').optional().or(z.literal('')),
  address: z.string().optional().or(z.literal('')),
  note: z.string().optional().or(z.literal('')),
  isActive: z.boolean(),
})

type SupplierFormValues = z.infer<typeof supplierFormSchema>

interface SupplierDialogProps {
  mode: 'create' | 'edit'
}

export function SupplierDialog({ mode }: SupplierDialogProps) {
  const { selectedSupplier, isEditDialogOpen, setIsEditDialogOpen, setSelectedSupplier } = useSuppliersContext()

  // Local state for create mode dialog
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)

  const createSupplier = useCreateSupplier()
  const updateSupplier = useUpdateSupplier()

  const isOpen = mode === 'create' ? isCreateDialogOpen : isEditDialogOpen

  const form = useForm<SupplierFormValues>({
    resolver: zodResolver(supplierFormSchema),
    defaultValues: {
      name: '',
      contactName: '',
      email: '',
      phone: '',
      address: '',
      note: '',
      isActive: true,
    },
  })

  // Populate form when editing
  useEffect(() => {
    if (mode === 'edit' && selectedSupplier) {
      form.reset({
        name: selectedSupplier.name,
        contactName: selectedSupplier.contactName || '',
        email: selectedSupplier.email || '',
        phone: selectedSupplier.phone || '',
        address: selectedSupplier.address || '',
        note: selectedSupplier.note || '',
        isActive: selectedSupplier.isActive,
      })
    } else if (mode === 'create') {
      form.reset({
        name: '',
        contactName: '',
        email: '',
        phone: '',
        address: '',
        note: '',
        isActive: true,
      })
    }
  }, [mode, selectedSupplier, form])

  const onSubmit = (values: SupplierFormValues) => {
    const request: SupplierRequest = {
      name: values.name,
      contactName: values.contactName || undefined,
      email: values.email || undefined,
      phone: values.phone || undefined,
      address: values.address || undefined,
      note: values.note || undefined,
      isActive: values.isActive,
    }

    if (mode === 'create') {
      createSupplier.mutate(request, {
        onSuccess: () => {
          toast.success('Đã tạo nhà cung cấp thành công')
          setIsCreateDialogOpen(false)
          form.reset()
        },
        onError: (error) => {
          toast.error(`Lỗi tạo nhà cung cấp: ${error.message}`)
        },
      })
    } else if (selectedSupplier) {
      updateSupplier.mutate(
        { id: selectedSupplier.id, data: request },
        {
          onSuccess: () => {
            toast.success('Đã cập nhật nhà cung cấp thành công')
            setIsEditDialogOpen(false)
            setSelectedSupplier(null)
          },
          onError: (error) => {
            toast.error(`Lỗi cập nhật nhà cung cấp: ${error.message}`)
          },
        }
      )
    }
  }

  const handleClose = () => {
    if (mode === 'create') {
      setIsCreateDialogOpen(false)
      form.reset()
    } else {
      setIsEditDialogOpen(false)
      setSelectedSupplier(null)
    }
  }

  const isLoading = createSupplier.isPending || updateSupplier.isPending

  return (
    <Dialog open={isOpen} onOpenChange={mode === 'create' ? setIsCreateDialogOpen : setIsEditDialogOpen}>
      {mode === 'create' && (
        <DialogTrigger asChild>
          <Button>
            <Plus className="mr-2 h-4 w-4" />
            Thêm nhà cung cấp
          </Button>
        </DialogTrigger>
      )}
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>
            {mode === 'create' ? 'Thêm nhà cung cấp mới' : 'Chỉnh sửa nhà cung cấp'}
          </DialogTitle>
          <DialogDescription>
            {mode === 'create'
              ? 'Nhập thông tin nhà cung cấp mới'
              : 'Cập nhật thông tin nhà cung cấp'}
          </DialogDescription>
        </DialogHeader>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Tên NCC *</FormLabel>
                  <FormControl>
                    <Input placeholder="Nhà xuất bản..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="contactName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Người liên hệ</FormLabel>
                  <FormControl>
                    <Input placeholder="Nguyen Van A" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="phone"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Điện thoại</FormLabel>
                    <FormControl>
                      <Input placeholder="0901234567" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input type="email" placeholder="email@example.com" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            <FormField
              control={form.control}
              name="address"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Địa chỉ</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Địa chỉ đầy đủ..." className="resize-none" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="note"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Ghi chú</FormLabel>
                  <FormControl>
                    <Textarea placeholder="Ghi chú thêm..." className="resize-none" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="isActive"
              render={({ field }) => (
                <FormItem className="flex items-center justify-between rounded-lg border p-3">
                  <div className="space-y-0.5">
                    <FormLabel>Trạng thái</FormLabel>
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

            <DialogFooter>
              <Button type="button" variant="outline" onClick={handleClose}>
                Hủy
              </Button>
              <Button type="submit" disabled={isLoading}>
                {isLoading && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                {mode === 'create' ? 'Thêm mới' : 'Lưu thay đổi'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
