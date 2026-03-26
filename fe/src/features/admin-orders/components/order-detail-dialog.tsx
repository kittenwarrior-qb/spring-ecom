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
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useOrderDetail } from '@/hooks/use-orders'
import { useOrders } from './orders-provider'
import { statusStyles } from './orders-columns'
import { format } from 'date-fns'
import { Loader2, Package, X, AlertTriangle } from 'lucide-react'

export function OrderDetailDialog() {
    const { open, setOpen, currentRow } = useOrders()
    const { data: order, isLoading } = useOrderDetail(currentRow?.id || 0)

    // Check if order has any cancelled items
    const hasCancelledItems = order?.items.some(item => item.cancelledQuantity > 0)
    const cancelledItems = order?.items.filter(item => item.cancelledQuantity > 0) || []

    return (
        <Dialog open={open === 'detail'} onOpenChange={() => setOpen(null)}>
            <DialogContent className='max-w-[95vw] sm:max-w-2xl lg:max-w-4xl max-h-[90vh] overflow-y-auto'>
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <Package className="h-5 w-5" />
                        Chi tiết đơn hàng: #{order?.orderNumber || currentRow?.orderNumber}
                        {hasCancelledItems && (
                            <Badge variant="outline" className="ml-2">
                                <AlertTriangle className="h-3 w-3 mr-1" />
                                Có sản phẩm bị hủy
                            </Badge>
                        )}
                    </DialogTitle>
                    <DialogDescription>
                        Thông tin chi tiết về đơn hàng và các sản phẩm trong đơn.
                    </DialogDescription>
                </DialogHeader>

                {isLoading ? (
                    <div className='flex h-40 items-center justify-center'>
                        <Loader2 className='h-8 w-8 animate-spin text-primary' />
                    </div>
                ) : order ? (
                    <div className='grid gap-6'>
                        {/* Order Information */}
                        <div className='grid grid-cols-1 md:grid-cols-2 gap-4 text-sm'>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Thông tin khách hàng</p>
                                <p className="font-medium">{order.recipientName}</p>
                                <p>{order.recipientPhone}</p>
                                <p className='text-muted-foreground italic'>{order.note || 'Không có ghi chú'}</p>
                            </div>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Địa chỉ giao hàng</p>
                                <p>{order.shippingAddress}</p>
                                <p>{order.shippingWard && `${order.shippingWard}, `}{order.shippingDistrict}, {order.shippingCity}</p>
                            </div>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Thông tin đơn hàng</p>
                                <p>Ngày đặt: {format(new Date(order.createdAt), 'dd/MM/yyyy HH:mm')}</p>
                                <p>Thanh toán: {order.paymentMethod}</p>
                            </div>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Trạng thái</p>
                                <Badge variant={statusStyles[order.status]?.variant || 'outline'}>
                                    {statusStyles[order.status]?.label || order.status}
                                </Badge>
                            </div>
                        </div>

                        {/* Cancelled Items Alert */}
                        {hasCancelledItems && (
                            <Card>
                                <CardHeader className="pb-3">
                                    <CardTitle className="flex items-center gap-2">
                                        <X className="h-4 w-4" />
                                        Sản phẩm đã bị hủy ({cancelledItems.length} sản phẩm)
                                    </CardTitle>
                                </CardHeader>
                                <CardContent className="pt-0">
                                    <div className="space-y-3">
                                        {cancelledItems.map((item) => (
                                            <div key={item.id} className="flex items-center gap-3 p-3 bg-background rounded-lg border">
                                                {item.productImage && (
                                                    <img 
                                                        src={item.productImage} 
                                                        alt={item.productTitle}
                                                        className="w-12 h-12 object-cover rounded-md"
                                                    />
                                                )}
                                                <div className="flex-1">
                                                    <p className="font-medium">{item.productTitle}</p>
                                                    <p className="text-sm text-muted-foreground">
                                                        Số lượng bị hủy: <span className="font-medium">{item.cancelledQuantity}</span>
                                                        {item.cancelledAt && (
                                                            <span className="ml-2">
                                                                • Hủy lúc: {format(new Date(item.cancelledAt), 'dd/MM/yyyy HH:mm')}
                                                            </span>
                                                        )}
                                                    </p>
                                                    <p className="text-sm text-muted-foreground">
                                                        Còn lại: {item.quantity - item.cancelledQuantity} / {item.quantity}
                                                    </p>
                                                </div>
                                                <div className="text-right">
                                                    <p className="text-sm font-medium">
                                                        -{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.cancelledQuantity * item.price)}
                                                    </p>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>
                        )}

                        {/* Order Items Table */}
                        <div className='rounded-md border overflow-x-auto'>
                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead className="min-w-[200px]">Sản phẩm</TableHead>
                                        <TableHead className='text-right min-w-[100px]'>Giá</TableHead>
                                        <TableHead className='text-right min-w-[80px]'>SL đặt</TableHead>
                                        <TableHead className='text-right min-w-[80px]'>SL hủy</TableHead>
                                        <TableHead className='text-right min-w-[80px]'>SL còn lại</TableHead>
                                        <TableHead className='text-right min-w-[120px]'>Thành tiền</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items.map((item) => (
                                        <TableRow key={item.id}>
                                            <TableCell>
                                                <div className="flex items-center gap-2 sm:gap-3">
                                                    {item.productImage && (
                                                        <img 
                                                            src={item.productImage} 
                                                            alt={item.productTitle}
                                                            className="w-8 h-8 sm:w-10 sm:h-10 object-cover rounded-md flex-shrink-0"
                                                        />
                                                    )}
                                                    <div className="min-w-0 flex-1">
                                                        <p className="font-medium text-sm sm:text-base truncate">{item.productTitle}</p>
                                                        {item.cancelledQuantity > 0 && (
                                                            <Badge variant="outline" className="text-xs mt-1">
                                                                Một phần bị hủy
                                                            </Badge>
                                                        )}
                                                    </div>
                                                </div>
                                            </TableCell>
                                            <TableCell className='text-right'>
                                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.price)}
                                            </TableCell>
                                            <TableCell className='text-right'>{item.quantity}</TableCell>
                                            <TableCell className='text-right'>
                                                {item.cancelledQuantity > 0 ? (
                                                    <span className="font-medium">{item.cancelledQuantity}</span>
                                                ) : (
                                                    <span className="text-muted-foreground">0</span>
                                                )}
                                            </TableCell>
                                            <TableCell className='text-right'>
                                                <span className={item.cancelledQuantity > 0 ? 'font-medium' : ''}>
                                                    {item.quantity - item.cancelledQuantity}
                                                </span>
                                            </TableCell>
                                            <TableCell className='text-right font-medium'>
                                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.subtotal)}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    <TableRow>
                                        <TableCell colSpan={5} className='text-right font-medium'>Tạm tính</TableCell>
                                        <TableCell className='text-right'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.subtotal)}
                                        </TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell colSpan={5} className='text-right font-medium'>Phí vận chuyển</TableCell>
                                        <TableCell className='text-right'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.shippingFee)}
                                        </TableCell>
                                    </TableRow>
                                    {order.discount > 0 && (
                                        <TableRow>
                                            <TableCell colSpan={5} className='text-right font-medium'>Giảm giá</TableCell>
                                            <TableCell className='text-right'>
                                                -{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.discount)}
                                            </TableCell>
                                        </TableRow>
                                    )}
                                    <TableRow className='bg-muted/50'>
                                        <TableCell colSpan={5} className='text-right text-lg font-bold'>Tổng cộng</TableCell>
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
                        Không thể tải thông tin chi tiết đơn hàng.
                    </div>
                )}
            </DialogContent>
        </Dialog>
    )
}
