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
import { useUpdateOrderStatus } from '@/hooks/use-orders'
import { useOrders } from './orders-provider'

const formSchema = z.object({
    status: z.string().min(1, 'Status is required'),
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
            toast.success('Order status updated successfully')
            setOpen(null)
        } catch (error) {
            toast.error('Failed to update order status')
        }
    }

    return (
        <Dialog open={open === 'status'} onOpenChange={() => setOpen(null)}>
            <DialogContent className='sm:max-w-[425px]'>
                <DialogHeader>
                    <DialogTitle>Update Order Status</DialogTitle>
                    <DialogDescription>
                        Change the status for order #{currentRow?.orderNumber}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(onSubmit)} className='space-y-4'>
                        <FormField
                            control={form.control}
                            name='status'
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Status</FormLabel>
                                    <Select
                                        onValueChange={field.onChange}
                                        defaultValue={field.value}
                                    >
                                        <FormControl>
                                            <SelectTrigger>
                                                <SelectValue placeholder='Select a status' />
                                            </SelectTrigger>
                                        </FormControl>
                                        <SelectContent>
                                            <SelectItem value='PENDING'>Pending</SelectItem>
                                            <SelectItem value='CONFIRMED'>Confirmed</SelectItem>
                                            <SelectItem value='PROCESSING'>Processing</SelectItem>
                                            <SelectItem value='SHIPPED'>Shipped</SelectItem>
                                            <SelectItem value='DELIVERED'>Delivered</SelectItem>
                                            <SelectItem value='CANCELLED'>Cancelled</SelectItem>
                                        </SelectContent>
                                    </Select>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <DialogFooter>
                            <Button type='submit' disabled={updateStatus.isPending}>
                                Save changes
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    )
}
