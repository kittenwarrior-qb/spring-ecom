import { useState } from 'react'
import { useForm, useFieldArray } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Plus, Loader2, Trash2 } from 'lucide-react'
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { useCreatePurchaseOrder } from '@/hooks/use-purchase-order'
import { useSuppliers } from '@/hooks/use-supplier'
import { useProducts } from '@/hooks/use-product'
import type { CreatePurchaseOrderRequest } from '@/types/api'
import { toast } from 'sonner'

const poItemSchema = z.object({
  productId: z.number().positive('Chọn sản phẩm'),
  quantityOrdered: z.number().positive('Số lượng phải lớn hơn 0'),
  costPrice: z.number().positive('Đơn giá phải lớn hơn 0'),
})

const poFormSchema = z.object({
  supplierId: z.number().positive('Chọn nhà cung cấp'),
  expectedDate: z.string().optional(),
  note: z.string().optional(),
  items: z.array(poItemSchema).min(1, 'Cần ít nhất 1 sản phẩm'),
})

type POFormValues = z.infer<typeof poFormSchema>

export function PurchaseOrderCreateDialog() {
  const [isOpen, setIsOpen] = useState(false)

  const createPO = useCreatePurchaseOrder()
  const { data: suppliersData } = useSuppliers({ page: 0, size: 100 })
  const { data: productsData } = useProducts(0, 100)

  const suppliers = suppliersData?.content ?? []
  const products = productsData?.content ?? []

  const form = useForm<POFormValues>({
    resolver: zodResolver(poFormSchema),
    defaultValues: {
      supplierId: undefined as unknown as number,
      expectedDate: '',
      note: '',
      items: [{ productId: undefined as unknown as number, quantityOrdered: undefined as unknown as number, costPrice: undefined as unknown as number }],
    },
  })

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'items',
  })

  const onSubmit = (values: POFormValues) => {
    const request: CreatePurchaseOrderRequest = {
      supplierId: values.supplierId,
      expectedDate: values.expectedDate || undefined,
      note: values.note || undefined,
      items: values.items.map((item) => ({
        productId: item.productId,
        quantity: item.quantityOrdered,
        unitPrice: item.costPrice,
      })),
    }

    createPO.mutate(request, {
      onSuccess: () => {
        toast.success('Đã tạo đơn nhập hàng thành công')
        setIsOpen(false)
        form.reset()
      },
      onError: (error) => {
        toast.error(`Lỗi tạo đơn nhập: ${error.message}`)
      },
    })
  }

  const calculateTotal = () => {
    const items = form.watch('items')
    return items.reduce((sum, item) => sum + (item.quantityOrdered * item.costPrice || 0), 0)
  }

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          Tạo đơn nhập
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[900px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Tạo đơn nhập hàng mới</DialogTitle>
          <DialogDescription>
            Nhập thông tin đơn nhập hàng từ nhà cung cấp
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <FormField
                control={form.control}
                name="supplierId"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Nhà cung cấp *</FormLabel>
                    <Select
                      onValueChange={(value) => field.onChange(Number(value))}
                      value={field.value ? String(field.value) : ''}
                    >
                      <FormControl>
                        <SelectTrigger>
                          <SelectValue placeholder="Chọn nhà cung cấp" />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {suppliers.map((s) => (
                          <SelectItem key={s.id} value={String(s.id)}>
                            {s.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <FormField
                control={form.control}
                name="expectedDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Ngày dự kiến</FormLabel>
                    <FormControl>
                      <Input type="date" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

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

            {/* Items */}
            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <FormLabel>Sản phẩm *</FormLabel>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={() => append({ productId: undefined as unknown as number, quantityOrdered: undefined as unknown as number, costPrice: undefined as unknown as number })}
                >
                  <Plus className="h-4 w-4 mr-1" /> Thêm sản phẩm
                </Button>
              </div>

              {fields.map((field, index) => (
                <div key={field.id} className="grid grid-cols-12 gap-2 items-start p-2 border rounded-md">
                  <FormField
                    control={form.control}
                    name={`items.${index}.productId`}
                    render={({ field }) => (
                      <FormItem className="col-span-4">
                        <Select
                          onValueChange={(value) => field.onChange(Number(value))}
                          value={field.value ? String(field.value) : ''}
                        >
                          <FormControl>
                            <SelectTrigger className="max-w-full">
                              <SelectValue placeholder="Chọn SP" className="truncate" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {products.map((p) => (
                              <SelectItem key={p.id} value={String(p.id)} className="max-w-full">
                                <span className="truncate block">{p.title}</span>
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name={`items.${index}.quantityOrdered`}
                    render={({ field }) => (
                      <FormItem className="col-span-2">
                        <FormControl>
                          <Input
                            type="number"
                            min={1}
                            placeholder="SL"
                            {...field}
                            value={field.value ?? ''}
                            onChange={(e) => field.onChange(e.target.value === '' ? undefined : Number(e.target.value))}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={form.control}
                    name={`items.${index}.costPrice`}
                    render={({ field }) => (
                      <FormItem className="col-span-3">
                        <FormControl>
                          <Input
                            type="number"
                            min={0}
                            placeholder="Đơn giá"
                            {...field}
                            value={field.value ?? ''}
                            onChange={(e) => field.onChange(e.target.value === '' ? undefined : Number(e.target.value))}
                          />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <div className="col-span-2 pt-2 text-right text-sm font-medium">
                    {((form.watch(`items.${index}.quantityOrdered`) || 0) * (form.watch(`items.${index}.costPrice`) || 0)).toLocaleString('vi-VN')}d
                  </div>
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="col-span-1"
                    onClick={() => fields.length > 1 && remove(index)}
                    disabled={fields.length <= 1}
                  >
                    <Trash2 className="h-4 w-4 text-destructive" />
                  </Button>
                </div>
              ))}
            </div>

            {/* Total */}
            <div className="flex justify-end text-lg font-bold">
              Tổng cộng: {calculateTotal().toLocaleString('vi-VN')}d
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
                Hủy
              </Button>
              <Button type="submit" disabled={createPO.isPending}>
                {createPO.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Tạo đơn nhập
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  )
}
