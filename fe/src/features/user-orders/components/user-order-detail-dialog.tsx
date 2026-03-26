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
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { usePartialCancelOrder, useOrderDetail } from '@/hooks/use-order'
import { useUserOrders } from './user-orders-provider'
import { statusStyles } from './user-orders-columns'
import { format } from 'date-fns'
import { Package, X, AlertTriangle, Minus, Loader2, RotateCcw, CreditCard, Tag } from 'lucide-react'
import { toast } from 'sonner'
import { useState } from 'react'
import { getErrorMessage } from '@/lib/error-utils'

export function UserOrderDetailDialog() {
    const { open, setOpen, currentRow } = useUserOrders()
    const partialCancelOrder = usePartialCancelOrder()
    const [cancellingItems, setCancellingItems] = useState<Set<number>>(new Set())

    // Use fresh data from API instead of cached row data
    const { data: orderDetail, isLoading } = useOrderDetail(currentRow?.id || 0)
    const order = orderDetail || currentRow

    // Check if order has any cancelled items
    const hasCancelledItems = order?.items.some(item => item.cancelledQuantity > 0)
    const cancelledItems = order?.items.filter(item => item.cancelledQuantity > 0) || []
    const canPartialCancel = order && !['CANCELLED', 'DELIVERED', 'SHIPPED'].includes(order.status)
    
    // Check if order needs payment
    const needsPayment = order && 
        order.paymentMethod !== 'COD' && 
        (!order.paymentStatus || ['UNPAID', 'PENDING'].includes(order.paymentStatus))
    
    const handlePayment = () => {
        if (!order) return
        window.location.href = `/payment-success?orderNumber=${encodeURIComponent(order.orderNumber)}`
    }

    const handlePartialCancel = async (itemId: number, maxQuantity: number) => {
        if (!order) return
        
        const quantityToCancel = prompt(`Nhập số lượng muốn hủy (tối đa ${maxQuantity}):`)
        if (!quantityToCancel) return
        
        const quantity = parseInt(quantityToCancel)
        if (isNaN(quantity) || quantity <= 0 || quantity > maxQuantity) {
            toast.error('Số lượng không hợp lệ')
            return
        }

        setCancellingItems(prev => new Set(prev).add(itemId))
        
        try {
            await partialCancelOrder.mutateAsync({
                id: order.id,
                request: {
                    items: [{ orderItemId: itemId, quantityToCancel: quantity }]
                }
            })
            toast.success(`Đã hủy ${quantity} sản phẩm thành công`)
            
            // The hook will automatically invalidate queries and refresh data
        } catch (error) {
            const errorMessage = getErrorMessage(error)
            toast.error(errorMessage)
        } finally {
            setCancellingItems(prev => {
                const newSet = new Set(prev)
                newSet.delete(itemId)
                return newSet
            })
        }
    }

    return (
        <Dialog open={open === 'detail'} onOpenChange={(isOpen) => !isOpen && setOpen(null)}>
            <DialogContent className='max-w-[95vw] sm:max-w-2xl lg:max-w-4xl max-h-[90vh] overflow-y-auto'>
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <Package className="h-5 w-5" />
                        Chi tiết đơn hàng: #{order?.orderNumber}
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

                {order ? (
                    <div className='grid gap-6'>
                        {/* Loading indicator when refreshing data */}
                        {isLoading && (
                            <div className="flex items-center justify-center py-4">
                                <Loader2 className="h-6 w-6 animate-spin mr-2" />
                                <span className="text-sm text-muted-foreground">Đang tải dữ liệu mới...</span>
                            </div>
                        )}
                        
                        {/* Order Information */}
                        <div className='grid grid-cols-1 md:grid-cols-2 gap-4 text-sm'>
                            <div className='space-y-1'>
                                <p className='text-muted-foreground font-medium'>Thông tin giao hàng</p>
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
                                        {canPartialCancel && <TableHead className='text-right min-w-[100px]'>Hành động</TableHead>}
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items.map((item) => {
                                        const remainingQuantity = item.quantity - item.cancelledQuantity
                                        const canCancelThisItem = remainingQuantity > 0
                                        
                                        return (
                                            <TableRow key={item.id}>
                                                <TableCell>
                                                    <div className="flex items-center gap-2 sm:gap-3">
                                                        {item.productImage && (
                                                            <img 
                                                                src={item.productImage} 
                                                                alt={item.productTitle}
                                                                className="w-8 h-8 sm:w-10 sm:h-10 object-cover rounded-md shrink-0"
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
                                                        {remainingQuantity}
                                                    </span>
                                                </TableCell>
                                                <TableCell className='text-right font-medium'>
                                                    {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(item.subtotal)}
                                                </TableCell>
                                                {canPartialCancel && (
                                                    <TableCell className='text-right'>
                                                        {canCancelThisItem && (
                                                            <Button
                                                                variant="outline"
                                                                size="sm"
                                                                onClick={() => handlePartialCancel(item.id, remainingQuantity)}
                                                                disabled={cancellingItems.has(item.id) || partialCancelOrder.isPending}
                                                            >
                                                                {cancellingItems.has(item.id) ? (
                                                                    <>
                                                                        <Loader2 className="h-3 w-3 mr-1 animate-spin" />
                                                                        Đang hủy...
                                                                    </>
                                                                ) : (
                                                                    <>
                                                                        <Minus className="h-3 w-3 mr-1" />
                                                                        Hủy
                                                                    </>
                                                                )}
                                                            </Button>
                                                        )}
                                                    </TableCell>
                                                )}
                                            </TableRow>
                                        )
                                    })}
                                    <TableRow>
                                        <TableCell colSpan={canPartialCancel ? 6 : 5} className='text-right font-medium'>Tạm tính</TableCell>
                                        <TableCell className='text-right'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.subtotal)}
                                        </TableCell>
                                    </TableRow>
                                    <TableRow>
                                        <TableCell colSpan={canPartialCancel ? 6 : 5} className='text-right font-medium'>Phí vận chuyển</TableCell>
                                        <TableCell className='text-right'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.shippingFee)}
                                        </TableCell>
                                    </TableRow>
                                    {order.discount > 0 && (
                                        <TableRow>
                                            <TableCell colSpan={canPartialCancel ? 6 : 5} className='text-right font-medium'>
                                                <div className="flex items-center justify-end gap-1">
                                                    <Tag className="h-4 w-4" />
                                                    Giảm giá
                                                    {order.couponCode && (
                                                        <span className="text-green-600 font-mono text-xs">({order.couponCode})</span>
                                                    )}
                                                </div>
                                            </TableCell>
                                            <TableCell className='text-right text-green-600'>
                                                -{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.discount)}
                                            </TableCell>
                                        </TableRow>
                                    )}
                                    {order.refundedAmount > 0 && (
                                        <TableRow className='bg-green-50 dark:bg-green-950'>
                                            <TableCell colSpan={canPartialCancel ? 6 : 5} className='text-right font-bold text-green-600'>
                                                <RotateCcw className="h-4 w-4 inline mr-1" />
                                                Đã hoàn tiền
                                            </TableCell>
                                            <TableCell className='text-right font-bold text-green-600'>
                                                {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.refundedAmount)}
                                            </TableCell>
                                        </TableRow>
                                    )}
                                    <TableRow className='bg-muted/50'>
                                        <TableCell colSpan={canPartialCancel ? 6 : 5} className='text-right text-lg font-bold'>Tổng cộng</TableCell>
                                        <TableCell className='text-right text-lg font-bold text-primary'>
                                            {new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(order.total)}
                                        </TableCell>
                                    </TableRow>
                                </TableBody>
                            </Table>
                        </div>

                        {/* Payment Button */}
                        {needsPayment && (
                            <div className="flex justify-end pt-4 border-t">
                                <Button onClick={handlePayment} className="bg-green-600 hover:bg-green-700">
                                    <CreditCard className="h-4 w-4 mr-2" />
                                    Thanh toán đơn hàng
                                </Button>
                            </div>
                        )}
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
