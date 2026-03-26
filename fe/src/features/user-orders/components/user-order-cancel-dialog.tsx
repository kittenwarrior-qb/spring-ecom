import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { useCancelOrder } from '@/hooks/use-order'
import { useUserOrders } from './user-orders-provider'
import { toast } from 'sonner'
import { AlertTriangle } from 'lucide-react'

export function UserOrderCancelDialog() {
    const { open, setOpen, currentRow } = useUserOrders()
    const cancelOrder = useCancelOrder()

    const handleCancel = async () => {
        if (!currentRow) return

        try {
            await cancelOrder.mutateAsync(currentRow.id)
            toast.success('Đã hủy đơn hàng thành công')
            setOpen(null)
        } catch (error) {
            toast.error('Không thể hủy đơn hàng')
        }
    }

    return (
        <Dialog open={open === 'cancel'} onOpenChange={() => setOpen(null)}>
            <DialogContent className='sm:max-w-md'>
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2">
                        <AlertTriangle className="h-5 w-5 text-orange-500" />
                        Xác nhận hủy đơn hàng
                    </DialogTitle>
                    <DialogDescription>
                        Bạn có chắc chắn muốn hủy đơn hàng #{currentRow?.orderNumber}?
                        <br />
                        <span className="text-sm text-muted-foreground mt-2 block">
                            Hành động này không thể hoàn tác.
                        </span>
                    </DialogDescription>
                </DialogHeader>
                <DialogFooter className="flex-col-reverse sm:flex-row gap-2">
                    <Button
                        variant="outline"
                        onClick={() => setOpen(null)}
                        disabled={cancelOrder.isPending}
                    >
                        Không, giữ lại
                    </Button>
                    <Button
                        variant="destructive"
                        onClick={handleCancel}
                        disabled={cancelOrder.isPending}
                    >
                        {cancelOrder.isPending ? 'Đang hủy...' : 'Có, hủy đơn hàng'}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    )
}