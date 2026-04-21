import React from 'react'
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
import type { OrderStatus } from '@/types/api'
import { useUpdateOrderStatus } from '@/hooks/use-order'
import { useOrders } from './orders-provider'

const formSchema = z.object({
    status: z.string().min(1, 'Trạng thái là bắt buộc'),
})

// Define allowed next statuses based on current status (same as backend)
const ALLOWED_NEXT_STATUSES: Record<OrderStatus, OrderStatus[]> = {
    PENDING: ['CONFIRMED', 'CANCELLED'],
    PENDING_STOCK: ['STOCK_RESERVED', 'STOCK_FAILED', 'CANCELLED'],
    STOCK_RESERVED: ['CONFIRMED', 'CANCELLED'],
    STOCK_FAILED: ['CANCELLED'],
    CONFIRMED: ['SHIPPED', 'CANCELLED', 'PARTIALLY_CANCELLED'],
    SHIPPED: ['DELIVERED', 'PARTIALLY_CANCELLED'],
    DELIVERED: ['CANCELLED'],
    CANCELLED: [],
    PARTIALLY_CANCELLED: ['CANCELLED', 'SHIPPED', 'DELIVERED'],
}

const STATUS_LABELS: Record<OrderStatus, string> = {
    PENDING: 'Chờ xử lý',
    PENDING_STOCK: 'Chờ reserve stock',
    STOCK_RESERVED: 'Đã reserve stock',
    STOCK_FAILED: 'Thất bại (hết hàng)',
    CONFIRMED: 'Đã xác nhận',
    SHIPPED: 'Đang giao',
    DELIVERED: 'Đã giao',
    CANCELLED: 'Đã hủy',
    PARTIALLY_CANCELLED: 'Đã hủy 1 phần',
}

export function OrderStatusDialog() {
    const { open, setOpen, currentRow } = useOrders()
    const updateStatus = useUpdateOrderStatus()

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        values: {
            status: currentRow?.status || '',
        },
    })

    // Get allowed next statuses for current order
    const currentStatus = currentRow?.status as OrderStatus | undefined
    const allowedNextStatuses = currentStatus ? ALLOWED_NEXT_STATUSES[currentStatus] || [] : []

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
            const message = error instanceof Error ? error.message : 'Không thể cập nhật trạng thái đơn hàng'
            toast.error(message)
        }
    }

    return (
        <Dialog open={open === 'status'} onOpenChange={() => setOpen(null)}>
            <DialogContent className='sm:max-w-[425px]'>
                <DialogHeader>
                    <DialogTitle>Cập Nhật Trạng Thái Đơn Hàng</DialogTitle>
                    <DialogDescription>
                        Thay đổi trạng thái cho đơn hàng #{currentRow?.orderNumber}
                        <br />
                        <span className='text-muted-foreground'>
                            Hiện tại: {currentStatus ? STATUS_LABELS[currentStatus] : 'N/A'}
                        </span>
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className='space-y-4'>
                        <FormField
                            control={form.control}
                            name='status'
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Trạng thái mới</FormLabel>
                                    <Select
                                        onValueChange={field.onChange}
                                        value={field.value}
                                    >
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder='Chọn trạng thái' />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            {currentStatus && (
                                                <SelectItem value={currentStatus} disabled>
                                                    {STATUS_LABELS[currentStatus]} (hiện tại)
                                                </SelectItem>
                                            )}
                                            {allowedNextStatuses.map((status) => (
                                                <SelectItem key={status} value={status}>
                                                    {STATUS_LABELS[status]}
                                                </SelectItem>
                                            ))}
                                            {allowedNextStatuses.length === 0 && !currentStatus && (
                                                <SelectItem value='_none' disabled>
                                                    Không có trạng thái hợp lệ
                                                </SelectItem>
                                            )}
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <DialogFooter>
                            <Button type='button' variant='outline' onClick={() => setOpen(null)}>
                                Hủy
                            </Button>
                            <Button type='submit' disabled={updateStatus.isPending || allowedNextStatuses.length === 0}>
                                {updateStatus.isPending ? 'Đang lưu...' : 'Lưu thay đổi'}
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    )
}
