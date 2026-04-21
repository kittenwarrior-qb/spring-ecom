import { Loader2 } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { usePurchaseOrder, useReceiveGoods } from '@/hooks/use-purchase-order'
import { usePurchaseOrdersContext } from './purchase-orders-provider'
import { toast } from 'sonner'

export function ReceiveGoodsDialog() {
  const { selectedPO, isReceiveOpen, setIsReceiveOpen, setSelectedPO } = usePurchaseOrdersContext()
  const receiveGoods = useReceiveGoods()

  const { data: poDetail, isLoading } = usePurchaseOrder(selectedPO?.id ?? 0, { enabled: isReceiveOpen && !!selectedPO })

  const displayPO = poDetail || selectedPO

  const handleClose = () => {
    setIsReceiveOpen(false)
    setSelectedPO(null)
  }

  const handleConfirm = () => {
    if (!displayPO) return

    receiveGoods.mutate(displayPO.id, {
      onSuccess: () => {
        toast.success('Đã nhận hàng thành công, tồn kho đã được cập nhật')
        handleClose()
      },
      onError: (error) => {
        toast.error(`Lỗi nhận hàng: ${error.message}`)
      },
    })
  }

  if (!displayPO) return null

  return (
    <Dialog open={isReceiveOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Xác nhận nhận hàng - #{displayPO.poNumber}</DialogTitle>
          <DialogDescription>
            Xác nhận nhận hàng sẽ cập nhật trạng thái đơn và tăng tồn kho sản phẩm
          </DialogDescription>
        </DialogHeader>

        {isLoading ? (
          <div className="flex justify-center p-8">
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
        ) : (
          <div className="space-y-4">
            <div className="rounded-md bg-muted p-4">
              <p className="text-sm"><strong>Nhà cung cấp:</strong> {displayPO.supplierName}</p>
              <p className="text-sm"><strong>Tổng sản phẩm:</strong> {displayPO.items?.length ?? 0}</p>
              <p className="text-sm"><strong>Tổng tiền:</strong> {displayPO.totalAmount?.toLocaleString('vi-VN')}đ</p>
            </div>

            <DialogFooter>
              <Button type="button" variant="outline" onClick={handleClose}>
                Hủy
              </Button>
              <Button
                onClick={handleConfirm}
                disabled={receiveGoods.isPending}
              >
                {receiveGoods.isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                Xác nhận nhận hàng
              </Button>
            </DialogFooter>
          </div>
        )}
      </DialogContent>
    </Dialog>
  )
}
