import { usePurchaseOrder } from '@/hooks/use-purchase-order'
import { usePurchaseOrdersContext } from './purchase-orders-provider'
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Badge } from '@/components/ui/badge'
import { statusLabels } from './purchase-orders-columns'
import { Loader2 } from 'lucide-react'

export function PurchaseOrderDetailDialog() {
  const { selectedPO, isDetailOpen, setIsDetailOpen, setSelectedPO } = usePurchaseOrdersContext()
  const { data: poDetail, isLoading } = usePurchaseOrder(selectedPO?.id ?? 0, { enabled: isDetailOpen && !!selectedPO })

  const handleClose = () => {
    setIsDetailOpen(false)
    setSelectedPO(null)
  }

  if (!selectedPO) return null

  const displayPO = poDetail || selectedPO
  const status = statusLabels[displayPO.status]

  return (
    <Dialog open={isDetailOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[700px]">
        <DialogHeader>
          <DialogTitle>Chi tiết đơn nhập #{displayPO.poNumber}</DialogTitle>
          <DialogDescription>
            Thông tin đơn nhập hàng
          </DialogDescription>
        </DialogHeader>

        {isLoading ? (
          <div className="flex justify-center p-8">
            <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
          </div>
        ) : (
        <div className="space-y-6">
          {/* Header Info */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-muted-foreground">Nhà cung cấp</p>
              <p className="font-medium">{displayPO.supplierName ?? `NCC #${displayPO.supplierId}`}</p>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Trạng thái</p>
              <Badge variant={status.variant}>{status.label}</Badge>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Ngày tạo</p>
              <p>{new Date(displayPO.createdAt).toLocaleDateString('vi-VN')}</p>
            </div>
            {displayPO.expectedDate && (
              <div>
                <p className="text-sm text-muted-foreground">Ngày dự kiến</p>
                <p>{new Date(displayPO.expectedDate).toLocaleDateString('vi-VN')}</p>
              </div>
            )}
            {displayPO.receivedDate && (
              <div>
                <p className="text-sm text-muted-foreground">Ngày nhận</p>
                <p>{new Date(displayPO.receivedDate).toLocaleDateString('vi-VN')}</p>
              </div>
            )}
          </div>

          {/* Note */}
          {displayPO.note && (
            <div>
              <p className="text-sm text-muted-foreground">Ghi chú</p>
              <p className="text-sm">{displayPO.note}</p>
            </div>
          )}

          {/* Items Table */}
          <div>
            <h4 className="font-medium mb-2">Danh sach san pham</h4>
            <div className="rounded-md border">
              <table className="w-full text-sm">
                <thead className="bg-muted">
                  <tr>
                    <th className="p-2 text-left">Sản phẩm</th>
                    <th className="p-2 text-right">Dat</th>
                    <th className="p-2 text-right">Nhan</th>
                    <th className="p-2 text-right">Đơn giá</th>
                    <th className="p-2 text-right">Thành tiền</th>
                  </tr>
                </thead>
                <tbody>
                  {displayPO.items?.map((item) => (
                    <tr key={item.id} className="border-t">
                      <td className="p-2">{item.productTitle ?? `Sàn phâm #${item.productId}`}</td>
                      <td className="p-2 text-right">{item.quantity}</td>
                      <td className="p-2 text-right">-</td>
                      <td className="p-2 text-right">{item.unitPrice?.toLocaleString('vi-VN') ?? '-'}d</td>
                      <td className="p-2 text-right font-medium">{item.totalPrice?.toLocaleString('vi-VN') ?? '-'}d</td>
                    </tr>
                  ))}
                  {!displayPO.items?.length && (
                    <tr>
                      <td colSpan={5} className="p-4 text-center text-muted-foreground">
                        Chưa có sản phẩm nào
                      </td>
                    </tr>
                  )}
                </tbody>
                <tfoot className="bg-muted font-medium">
                  <tr>
                    <td colSpan={4} className="p-2 text-right">Tổng cộng:</td>
                    <td className="p-2 text-right">{displayPO.totalAmount?.toLocaleString('vi-VN')}d</td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </div>
        )}
      </DialogContent>
    </Dialog>
  )
}
