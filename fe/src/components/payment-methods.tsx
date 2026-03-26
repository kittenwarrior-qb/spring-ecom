import { CreditCard, Truck, Building2 } from 'lucide-react'
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group'
import { Label } from '@/components/ui/label'

interface PaymentMethodsProps {
  value: string
  onValueChange: (value: string) => void
}

export function PaymentMethods({ value, onValueChange }: PaymentMethodsProps) {
  const paymentMethods = [
    {
      id: 'BANK_TRANSFER',
      name: 'Chuyển khoản ngân hàng',
      description: 'Chuyển khoản trực tiếp qua ngân hàng',
      icon: Building2,
      popular: true,
    },
    {
      id: 'COD',
      name: 'Thanh toán khi nhận hàng (COD)',
      description: 'Thanh toán bằng tiền mặt khi nhận hàng',
      icon: Truck,
      popular: false,
    },
  ]

  return (
    <RadioGroup value={value} onValueChange={onValueChange} className="space-y-3">
      {paymentMethods.map((method) => {
        const Icon = method.icon
        return (
          <div
            key={method.id}
            className={`relative flex items-center space-x-3 p-4 border rounded-lg transition-colors ${
              value === method.id
                ? 'border-primary bg-primary/5'
                : 'border-border hover:border-primary/50'
            }`}
          >
            <RadioGroupItem value={method.id} id={method.id} />
            <div className="flex items-center space-x-3 flex-1">
              <div className={`p-2 rounded-lg ${
                value === method.id ? 'bg-primary text-primary-foreground' : 'bg-muted'
              }`}>
                <Icon className="h-5 w-5" />
              </div>
              <Label htmlFor={method.id} className="flex-1 cursor-pointer">
                <div className="flex items-center gap-2">
                  <p className="font-medium">{method.name}</p>
                  {method.popular && (
                    <span className="px-2 py-1 text-xs bg-green-100 text-green-700 rounded-full">
                      Phổ biến
                    </span>
                  )}
                </div>
                <p className="text-sm text-muted-foreground mt-1">
                  {method.description}
                </p>
              </Label>
            </div>
          </div>
        )
      })}
    </RadioGroup>
  )
}