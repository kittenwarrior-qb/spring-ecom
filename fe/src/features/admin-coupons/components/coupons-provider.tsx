import { createContext, useContext, useState, type ReactNode } from 'react'
import type { CouponResponse } from '@/types/api'

interface CouponsContextType {
  selectedCoupon: CouponResponse | null
  setSelectedCoupon: (coupon: CouponResponse | null) => void
  isEditDialogOpen: boolean
  setIsEditDialogOpen: (open: boolean) => void
}

const CouponsContext = createContext<CouponsContextType | null>(null)

export function CouponsProvider({ children }: { children: ReactNode }) {
  const [selectedCoupon, setSelectedCoupon] = useState<CouponResponse | null>(null)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)

  return (
    <CouponsContext.Provider value={{ 
      selectedCoupon, 
      setSelectedCoupon, 
      isEditDialogOpen, 
      setIsEditDialogOpen 
    }}>
      {children}
    </CouponsContext.Provider>
  )
}

export function useCouponsContext() {
  const context = useContext(CouponsContext)
  if (!context) {
    throw new Error('useCouponsContext must be used within a CouponsProvider')
  }
  return context
}
