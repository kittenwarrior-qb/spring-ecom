import { Truck, Shield, Headphones } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'

const features = [
  {
    icon: Truck,
    title: 'Free Shipping',
    description: 'Free shipping on orders over $50',
  },
  {
    icon: Shield,
    title: 'Secure Payment',
    description: '100% secure payment methods',
  },
  {
    icon: Headphones,
    title: '24/7 Support',
    description: 'Dedicated customer support',
  },
]

export function FeaturesSection() {
  return (
    <section>
      <div className="grid gap-6 md:grid-cols-3">
        {features.map((feature) => (
          <Card key={feature.title}>
            <CardContent className="flex items-start space-x-4 p-6">
              <div className="rounded-full bg-primary/10 p-3">
                <feature.icon className="h-6 w-6 text-primary" />
              </div>
              <div>
                <h3 className="font-semibold">{feature.title}</h3>
                <p className="text-sm text-muted-foreground">{feature.description}</p>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </section>
  )
}
