import { Truck, Info } from 'lucide-react'
import { Alert, AlertDescription } from '@/components/ui/alert'

interface ShippingCalculatorProps {
  subtotal: number
}

export function ShippingCalculator({ subtotal }: ShippingCalculatorProps) {
  const freeShippingThreshold = 500000
  const shippingFee = subtotal >= freeShippingThreshold ? 0 : 30000
  const remainingForFreeShipping = freeShippingThreshold - subtotal

  return (
    <div className="space-y-3">
      <div className="flex justify-between text-sm">
        <span className="text-muted-foreground">Phí vận chuyển</span>
        <span className="flex items-center gap-1">
          <Truck className="h-4 w-4" />
          {shippingFee === 0 ? 'Miễn phí' : `${shippingFee.toLocaleString('vi-VN')}đ`}
        </span>
      </div>
      
      {remainingForFreeShipping > 0 && (
        <Alert>
          <Info className="h-4 w-4" />
          <AlertDescription className="text-sm">
            Mua thêm <strong>{remainingForFreeShipping.toLocaleString('vi-VN')}đ</strong> để được miễn phí vận chuyển
          </AlertDescription>
        </Alert>
      )}
      
      {shippingFee === 0 && (
        <Alert className="border-green-200 bg-green-50">
          <Truck className="h-4 w-4 text-green-600" />
          <AlertDescription className="text-sm text-green-700">
            🎉 Bạn được miễn phí vận chuyển!
          </AlertDescription>
        </Alert>
      )}
    </div>
  )
}