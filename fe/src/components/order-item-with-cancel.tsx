import { useState } from 'react'
import { Minus, Plus, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import type { OrderItemResponse } from '@/types/api'

interface OrderItemWithCancelProps {
  item: OrderItemResponse
  onCancelQuantity?: (itemId: number, quantity: number) => void
  canCancel?: boolean
}

export function OrderItemWithCancel({ 
  item, 
  onCancelQuantity, 
  canCancel = false 
}: OrderItemWithCancelProps) {
  const [cancelQuantity, setCancelQuantity] = useState(1)
  
  const availableQuantity = item.quantity - item.cancelledQuantity
  const maxCancelQuantity = availableQuantity
  
  const handleCancelQuantity = () => {
    if (onCancelQuantity && cancelQuantity > 0) {
      const confirmMessage = `Bạn có chắc muốn hủy ${cancelQuantity} sản phẩm "${item.productTitle}"?`
      if (confirm(confirmMessage)) {
        onCancelQuantity(item.id, cancelQuantity)
        setCancelQuantity(1)
      }
    }
  }

  return (
    <Card>
      <CardContent className="p-4">
        <div className="flex gap-4">
          {/* Product Image */}
          <div className="h-20 w-16 rounded bg-gray-100 flex items-center justify-center overflow-hidden">
            {item.productImage ? (
              <img
                src={item.productImage}
                alt={item.productTitle}
                className="h-full w-full object-cover"
              />
            ) : (
              <span className="text-xs text-muted-foreground">No image</span>
            )}
          </div>

          {/* Product Info */}
          <div className="flex-1">
            <h4 className="font-semibold">{item.productTitle}</h4>
            <p className="text-sm text-muted-foreground">
              Giá: {item.price.toLocaleString('vi-VN')}đ
            </p>
            
            {/* Quantity Info */}
            <div className="mt-2 space-y-1">
              <div className="flex items-center gap-2">
                <span className="text-sm">Số lượng đặt: {item.quantity}</span>
                {item.cancelledQuantity > 0 && (
                  <Badge variant="destructive" className="text-xs">
                    Đã hủy: {item.cancelledQuantity}
                  </Badge>
                )}
              </div>
              
              {availableQuantity > 0 && (
                <div className="flex items-center gap-2">
                  <span className="text-sm font-medium text-green-600">
                    Còn lại: {availableQuantity}
                  </span>
                  <Badge variant="outline" className="text-xs">
                    {item.status}
                  </Badge>
                </div>
              )}
              
              {availableQuantity === 0 && (
                <Badge variant="destructive">Đã hủy hoàn toàn</Badge>
              )}
            </div>

            <p className="mt-2 font-semibold">
              Thành tiền: {(item.price * availableQuantity).toLocaleString('vi-VN')}đ
            </p>
          </div>

          {/* Cancel Controls */}
          {canCancel && availableQuantity > 0 && (
            <div className="flex flex-col gap-2 min-w-[120px]">
              <span className="text-xs text-muted-foreground">Hủy số lượng:</span>
              
              {/* Quantity Selector */}
              <div className="flex items-center gap-1">
                <Button
                  variant="outline"
                  size="icon"
                  className="h-6 w-6"
                  onClick={() => setCancelQuantity(Math.max(1, cancelQuantity - 1))}
                  disabled={cancelQuantity <= 1}
                >
                  <Minus className="h-3 w-3" />
                </Button>
                
                <span className="w-8 text-center text-sm font-medium">
                  {cancelQuantity}
                </span>
                
                <Button
                  variant="outline"
                  size="icon"
                  className="h-6 w-6"
                  onClick={() => setCancelQuantity(Math.min(maxCancelQuantity, cancelQuantity + 1))}
                  disabled={cancelQuantity >= maxCancelQuantity}
                >
                  <Plus className="h-3 w-3" />
                </Button>
              </div>

              {/* Cancel Button */}
              <Button
                variant="destructive"
                size="sm"
                className="text-xs"
                onClick={handleCancelQuantity}
                disabled={cancelQuantity <= 0 || cancelQuantity > maxCancelQuantity}
              >
                <X className="h-3 w-3 mr-1" />
                Hủy {cancelQuantity}
              </Button>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}