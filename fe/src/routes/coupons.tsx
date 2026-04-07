import { createFileRoute } from '@tanstack/react-router'
import { useState, useMemo } from 'react'
import { useQuery } from '@tanstack/react-query'
import { format, isAfter, isBefore, addDays } from 'date-fns'
import { vi } from 'date-fns/locale'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Copy, Tag, Calendar, Percent, Clock, CheckCircle, AlertCircle, Sparkles } from 'lucide-react'
import { toast } from 'sonner'
import { couponApi } from '@/api/coupon.api'
import type { CouponResponse } from '@/types/api'
import { useAuth } from '@/stores/auth-store'

// Simple format function
const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(amount)
}

// Coupon status enum
type CouponStatus = 'available' | 'expiring-soon' | 'used' | 'expired'

// Get coupon status
const getCouponStatus = (coupon: CouponResponse): CouponStatus => {
  const now = new Date()
  const endDate = new Date(coupon.endDate)
  const startDate = new Date(coupon.startDate)

  // Check if expired
  if (isBefore(endDate, now)) {
    return 'expired'
  }

  // Check if expiring soon (within 3 days)
  const threeDaysFromNow = addDays(now, 3)
  if (isBefore(endDate, threeDaysFromNow)) {
    return 'expiring-soon'
  }

  // Check if not yet active
  if (isAfter(startDate, now)) {
    return 'expired' // Treat as not available yet
  }

  // Check if used (would need user data)
  // For now, assume available
  return 'available'
}

// Check if coupon is relevant to user
const isCouponRelevant = (coupon: CouponResponse, _userTotalSpent: number = 0): boolean => {
  const now = new Date()
  const endDate = new Date(coupon.endDate)
  const startDate = new Date(coupon.startDate)

  // Must be active
  if (!coupon.isActive) return false

  // Must be within valid date range
  if (isBefore(endDate, now) || isAfter(startDate, now)) return false

  // Check usage limit
  if (coupon.usageLimit && coupon.usedCount >= coupon.usageLimit) return false

  // Check minimum order amount (if user has order history)
  // For now, show all coupons regardless of min order amount
  // User can see the requirement and decide

  return true
}

function CouponsPage() {
  const [copiedCode, setCopiedCode] = useState<string | null>(null)
  const [activeTab, setActiveTab] = useState<string>('available')
  const auth = useAuth()

  const { data: coupons, isLoading } = useQuery({
    queryKey: ['public-coupons'],
    queryFn: () => couponApi.getPublicCoupons(),
  })

  // Filter coupons by status
  const filteredCoupons = useMemo(() => {
    if (!coupons) return { available: [], expiringSoon: [], expired: [] }

    const available = coupons.filter(c => {
      const status = getCouponStatus(c)
      return status === 'available' && isCouponRelevant(c)
    })

    const expiringSoon = coupons.filter(c => {
      const status = getCouponStatus(c)
      return status === 'expiring-soon' && isCouponRelevant(c)
    })

    const expired = coupons.filter(c => {
      const status = getCouponStatus(c)
      return status === 'expired' || !isCouponRelevant(c)
    })

    return { available, expiringSoon, expired }
  }, [coupons])

  // Get recommended coupons (top 3 with highest discount)
  const recommendedCoupons = useMemo(() => {
    if (!filteredCoupons.available.length) return []

    return [...filteredCoupons.available]
      .sort((a, b) => {
        // Sort by discount value
        const aValue = a.discountType === 'PERCENTAGE' ? a.discountValue : a.discountValue / 1000
        const bValue = b.discountType === 'PERCENTAGE' ? b.discountValue : b.discountValue / 1000
        return bValue - aValue
      })
      .slice(0, 3)
  }, [filteredCoupons.available])

  const copyToClipboard = async (code: string) => {
    try {
      await navigator.clipboard.writeText(code)
      setCopiedCode(code)
      toast.success('Đã sao chép mã giảm giá!', {
        description: 'Bạn có thể sử dụng mã này khi thanh toán',
        action: {
          label: 'Đi đến giỏ hàng',
          onClick: () => window.location.href = '/cart'
        }
      })
      setTimeout(() => setCopiedCode(null), 2000)
    } catch (_error) {
      toast.error('Không thể sao chép mã')
    }
  }

  const renderCouponCard = (coupon: CouponResponse, showStatus: boolean = true) => {
    const status = getCouponStatus(coupon)
    const isExpiringSoon = status === 'expiring-soon'
    const isExpired = status === 'expired'

    return (
      <Card 
        key={coupon.id} 
        className={`relative overflow-hidden transition-all hover:shadow-lg ${
          isExpired ? 'opacity-60' : ''
        }`}
      >
        {/* Expiring soon badge */}
        {isExpiringSoon && (
          <div className="absolute top-0 right-0 bg-orange-500 text-white text-xs px-3 py-1 rounded-bl-lg font-semibold">
            Sắp hết hạn!
          </div>
        )}

        {/* Expired badge */}
        {isExpired && (
          <div className="absolute top-0 right-0 bg-gray-500 text-white text-xs px-3 py-1 rounded-bl-lg font-semibold">
            Hết hạn
          </div>
        )}

        <CardHeader className="pb-3">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <CardTitle className="text-lg mb-1">
                {coupon.description || 'Giảm giá đặc biệt'}
              </CardTitle>
              <CardDescription className="text-sm flex items-center gap-1">
                <Calendar className="h-3 w-3" />
                HSD: {format(new Date(coupon.endDate), 'dd/MM/yyyy', { locale: vi })}
              </CardDescription>
            </div>
            {showStatus && !isExpired && (
              <Badge variant="default" className="bg-green-600">
                Khả dụng
              </Badge>
            )}
          </div>
        </CardHeader>

        <CardContent className="space-y-4">
          <div className="text-center py-4 bg-gradient-to-br from-primary/10 to-primary/5 rounded-lg">
            <div className="flex items-center justify-center gap-2 mb-2">
              <Percent className="h-5 w-5 text-primary" />
              <span className="text-3xl font-bold text-primary">
                {coupon.discountType === 'PERCENTAGE' 
                  ? `${coupon.discountValue}%`
                  : formatCurrency(coupon.discountValue)
                }
              </span>
            </div>
            <div className="text-sm text-muted-foreground space-y-1">
              {coupon.minOrderAmount && (
                <p className="flex items-center justify-center gap-1">
                  <AlertCircle className="h-3 w-3" />
                  Đơn tối thiểu: {formatCurrency(coupon.minOrderAmount)}
                </p>
              )}
              {coupon.maxDiscountAmount && (
                <p>Giảm tối đa: {formatCurrency(coupon.maxDiscountAmount)}</p>
              )}
            </div>
          </div>

          <Separator />

          <div className="space-y-2">
            <div className="flex items-center justify-between gap-2">
              <div className="flex items-center gap-2 text-sm flex-1">
                <Tag className="h-4 w-4 text-primary" />
                <span className="font-mono font-bold text-base bg-muted px-3 py-1 rounded border">
                  {coupon.code}
                </span>
              </div>
            </div>
            
            <Button 
              onClick={() => copyToClipboard(coupon.code)}
              className="w-full"
              variant={copiedCode === coupon.code ? 'secondary' : 'default'}
              disabled={isExpired}
            >
              {copiedCode === coupon.code ? (
                <>
                  <CheckCircle className="h-4 w-4 mr-2" />
                  Đã sao chép!
                </>
              ) : (
                <>
                  <Copy className="h-4 w-4 mr-2" />
                  Sao chép mã
                </>
              )}
            </Button>
          </div>

          {coupon.usageLimit && (
            <div className="text-xs text-muted-foreground text-center flex items-center justify-center gap-1">
              <Clock className="h-3 w-3" />
              Còn {coupon.usageLimit - coupon.usedCount} lượt sử dụng
            </div>
          )}
        </CardContent>
      </Card>
    )
  }

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="animate-pulse">
          <div className="h-8 bg-muted rounded mb-6 w-1/3"></div>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="h-80 bg-muted rounded"></div>
            ))}
          </div>
        </div>
      </div>
    )
  }

  const totalAvailable = filteredCoupons.available.length + filteredCoupons.expiringSoon.length

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Header */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">Mã giảm giá</h1>
        <p className="text-muted-foreground">
          {auth.accessToken 
            ? `${totalAvailable} mã giảm giá đang khả dụng cho bạn`
            : 'Đăng nhập để nhận mã giảm giá cá nhân hóa'
          }
        </p>
      </div>

      {/* Recommended Coupons (if logged in) */}
      {auth.accessToken && recommendedCoupons.length > 0 && (
        <div className="mb-8">
          <div className="flex items-center gap-2 mb-4">
            <Sparkles className="h-5 w-5 text-primary" />
            <h2 className="text-xl font-semibold">Được đề xuất cho bạn</h2>
          </div>
          <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {recommendedCoupons.map(coupon => renderCouponCard(coupon))}
          </div>
        </div>
      )}

      {/* Tabs */}
      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid w-full grid-cols-3 mb-6">
          <TabsTrigger value="available" className="flex items-center gap-2">
            <Tag className="h-4 w-4" />
            Khả dụng ({filteredCoupons.available.length})
          </TabsTrigger>
          <TabsTrigger value="expiring-soon" className="flex items-center gap-2">
            <Clock className="h-4 w-4" />
            Sắp hết hạn ({filteredCoupons.expiringSoon.length})
          </TabsTrigger>
          <TabsTrigger value="expired" className="flex items-center gap-2">
            <AlertCircle className="h-4 w-4" />
            Hết hạn ({filteredCoupons.expired.length})
          </TabsTrigger>
        </TabsList>

        {/* Available Coupons */}
        <TabsContent value="available">
          {filteredCoupons.available.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <Tag className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="text-lg font-semibold mb-2">Không có mã giảm giá</h3>
                <p className="text-muted-foreground text-center">
                  Hiện tại không có mã giảm giá nào khả dụng. Hãy quay lại sau nhé!
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {filteredCoupons.available.map(coupon => renderCouponCard(coupon))}
            </div>
          )}
        </TabsContent>

        {/* Expiring Soon */}
        <TabsContent value="expiring-soon">
          {filteredCoupons.expiringSoon.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <Clock className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="text-lg font-semibold mb-2">Không có mã sắp hết hạn</h3>
                <p className="text-muted-foreground text-center">
                  Tất cả mã giảm giá của bạn đều còn thời hạn sử dụng
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {filteredCoupons.expiringSoon.map(coupon => renderCouponCard(coupon))}
            </div>
          )}
        </TabsContent>

        {/* Expired */}
        <TabsContent value="expired">
          {filteredCoupons.expired.length === 0 ? (
            <Card>
              <CardContent className="flex flex-col items-center justify-center py-12">
                <AlertCircle className="h-12 w-12 text-muted-foreground mb-4" />
                <h3 className="text-lg font-semibold mb-2">Không có mã hết hạn</h3>
                <p className="text-muted-foreground text-center">
                  Tất cả mã giảm giá đều còn hiệu lực
                </p>
              </CardContent>
            </Card>
          ) : (
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
              {filteredCoupons.expired.map(coupon => renderCouponCard(coupon))}
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  )
}

export const Route = createFileRoute('/coupons')({
  component: CouponsPage,
})
