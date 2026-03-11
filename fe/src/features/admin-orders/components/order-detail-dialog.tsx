import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'
import { Badge } from '@/components/ui/badge'
import { useOrderDetail } from '@/hooks/use-orders'
import { useOrders } from './orders-provider'
import { statusStyles } from './orders-columns'
import { format } from 'date-fns'
import { Loader2 } from 'lucide-react'

export function OrderDetailDialog() {
    const { open, setOpen, currentRow } = useOrders()
    const { data: order, isLoading } = useOrderDetail(currentRow?.id || 0)

    return (
        <Dialog open={open === 'detail'} onOpenChange={() => setOpen(null)}>
            <DialogContent className='max-w-3xl max-h-[90vh] overflow-y-auto'>
                <DialogHeader>
                    <DialogTitle>Order Details: #{order?.orderNumber || currentRow?.orderNumber}</DialogTitle>
                    <DialogDescription>
                        Detailed information about the order and its items.
                    </DialogDescription>
                </DialogHeader>

                {isLoading ? (
                    <div className='flex h-40 items-center justify-center'>
                        <Loader2 className='h-8 w-8 animate-spin text-primary' />
                    </div>
                ) : order ? (
                    <div className='grid gap-6'>
                        <div className='grid grid-cols-2 gap-4 text-sm'>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Customer Information</p>
                                <p>{order.recipientName}</p>
                                <p>{order.recipientPhone}</p>
                                <p className='text-muted-foreground italic'>{order.note || 'No notes provided'}</p>
                            </div>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Shipping Address</p>
                                <p>{order.shippingAddress}</p>
                                <p>{order.shippingDistrict}, {order.shippingCity}</p>
                            </div>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Order Info</p>
                                <p>Date: {format(new Date(order.createdAt), 'dd/MM/yyyy HH:mm')}</p>
                                <p>Payment: {order.paymentMethod}</p>
                            </div>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Status</p>
                                <Badge variant={statusStyles[order.status]?.variant || 'outline'}>
                                    {statusStyles[order.status]?.label || order.status}
                                </Badge>
                            </div>
                        </div>

                        <div className='rounded-md border'>
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Product</TableHead>
                                        <TableHead className='text-right'>Price</TableHead>
                                        <TableHead className='text-right'>Qty</TableHead>
                                        <TableHead className='text-right'>Subtotal</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items.map((item) => (
                                        <TableRow key={item.id}>
                                            <TableCell className='font-medium'>{item.productTitle}</TableCell>
                                            <TableCell className='text-right'>
                                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.price)}
                                            </TableCell>
                                            <TableCell className='text-right'>{item.quantity}</TableCell>
                                            <TableCell className='text-right font-medium'>
                                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.subtotal)}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    <TableRow>
                                        <TableCell colSpan={3} className='text-right font-medium'>Subtotal</TableCell>
                                        <TableCell className='text-right'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.subtotal)}
                                        </TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell colSpan={3} className='text-right font-medium'>Shipping Fee</TableCell>
                                        <TableCell className='text-right'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.shippingFee)}
                                        </TableCell>
                                    </TableRow>
                                    {order.discount > 0 && (
                                        <TableRow>
                                            <TableCell colSpan={3} className='text-right font-medium text-destructive'>Discount</TableCell>
                                            <TableCell className='text-right text-destructive'>
                                                -{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.discount)}
                                            </TableCell>
                                        </TableRow>
                                    )}
                                    <TableRow className='bg-muted/50'>
                                        <TableCell colSpan={3} className='text-right text-lg font-bold'>Total</TableCell>
                                        <TableCell className='text-right text-lg font-bold text-primary'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.total)}
                                        </TableCell>
                                    </TableRow>
                                </TableBody>
                            </Table>
                        </div>
                    </div>
                ) : (
                    <div className='p-4 text-center text-muted-foreground'>
                        Order details could not be loaded.
                    </div>
                )}
            </DialogContent>
        </Dialog>
    )
}
