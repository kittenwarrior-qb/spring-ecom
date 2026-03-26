import { AlertTriangle, ShoppingCart } from 'lucide-react'
import { Alert, AlertDescription } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { useNavigate } from '@tanstack/react-router'

interface StockErrorAlertProps {
  productId?: string
  message: string
  onDismiss?: () => void
}

export function StockErrorAlert({ message, onDismiss }: StockErrorAlertProps) {
  const navigate = useNavigate()

  const handleGoToCart = () => {
    navigate({ to: '/cart' })
    onDismiss?.()
  }

  return (
    <Alert className="border-orange-200 bg-orange-50 text-orange-800">
      <AlertTriangle className="h-4 w-4 text-orange-600" />
      <AlertDescription className="flex items-center justify-between">
        <div className="flex-1">
          <p className="font-medium mb-1">Không đủ hàng trong kho</p>
          <p className="text-sm">{message}</p>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={handleGoToCart}
          className="ml-4 border-orange-300 text-orange-700 hover:bg-orange-100"
        >
          <ShoppingCart className="h-4 w-4 mr-2" />
          Xem giỏ hàng
        </Button>
      </AlertDescription>
    </Alert>
  )
}