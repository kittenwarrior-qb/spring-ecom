import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { Button } from '@/components/ui/button'
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form'
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select'
import { OrderStatus } from '@/types/api'
import { useUpdateOrderStatus } from '@/hooks/use-order'
import { useOrders } from './orders-provider'

const formSchema = z.object({
    status: z.string().min(1, 'Trạng thái là bắt buộc'),
})

export function OrderStatusDialog() {
    const { open, setOpen, currentRow } = useOrders()
    const updateStatus = useUpdateOrderStatus()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        values: {
            status: currentRow?.status || '',
        },
    })

    async function onSubmit(data: z.infer<typeof formSchema>) {
        if (!currentRow) return

        try {
            await updateStatus.mutateAsync({
                id: currentRow.id,
                status: data.status as OrderStatus,
            })
            toast.success('Trạng thái đơn hàng đã được cập nhật thành công')
            setOpen(null)
        } catch (error) {
            toast.error('Không thể cập nhật trạng thái đơn hàng')
        }
    }

    return (
        <Dialog open={open === 'status'} onOpenChange={() => setOpen(null)}>
            <DialogContent className='sm:max-w-[425px]'>
                <DialogHeader>
                    <DialogTitle>Cập Nhật Trạng Thái Đơn Hàng</DialogTitle>
                    <DialogDescription>
                        Thay đổi trạng thái cho đơn hàng #{currentRow?.orderNumber}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className='space-y-4'>
                        <FormField
                            control={form.control}
                            name='status'
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Trạng thái</FormLabel>
                                    <Select
                                        onValueChange={field.onChange}
                                        defaultValue={field.value}
                                    >
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder='Chọn trạng thái' />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            <SelectItem value='PENDING'>Chờ xử lý</SelectItem>
                                            <SelectItem value='CONFIRMED'>Đã xác nhận</SelectItem>
                                            <SelectItem value='PROCESSING'>Đang xử lý</SelectItem>
                                            <SelectItem value='DELIVERED'>Đã giao</SelectItem>
                                            <SelectItem value='CANCELLED'>Đã hủy</SelectItem>
                                            <SelectItem value='PARTIALLY_CANCELLED'>Đã hủy 1 phần</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <DialogFooter>
                            <Button type='submit' disabled={updateStatus.isPending}>
                                Lưu thay đổi
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    )
}
