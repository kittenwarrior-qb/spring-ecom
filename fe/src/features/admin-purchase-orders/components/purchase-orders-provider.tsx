import { createContext, useContext, useState, type ReactNode } from 'react'
import type { PurchaseOrderResponse } from '@/types/api'

interface PurchaseOrdersContextType {
  selectedPO: PurchaseOrderResponse | null
  setSelectedPO: (po: PurchaseOrderResponse | null) => void
  isDetailOpen: boolean
  setIsDetailOpen: (open: boolean) => void
  isEditOpen: boolean
  setIsEditOpen: (open: boolean) => void
  isReceiveOpen: boolean
  setIsReceiveOpen: (open: boolean) => void
}

const PurchaseOrdersContext = createContext<PurchaseOrdersContextType | undefined>(undefined)

export function PurchaseOrdersProvider({ children }: { children: ReactNode }) {
  const [selectedPO, setSelectedPO] = useState<PurchaseOrderResponse | null>(null)
  const [isDetailOpen, setIsDetailOpen] = useState(false)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const [isReceiveOpen, setIsReceiveOpen] = useState(false)

  return (
    <PurchaseOrdersContext.Provider
      value={{
        selectedPO,
        setSelectedPO,
        isDetailOpen,
        setIsDetailOpen,
        isEditOpen,
        setIsEditOpen,
        isReceiveOpen,
        setIsReceiveOpen,
      }}
    >
      {children}
    </PurchaseOrdersContext.Provider>
  )
}

export function usePurchaseOrdersContext() {
  const context = useContext(PurchaseOrdersContext)
  if (!context) {
    throw new Error('usePurchaseOrdersContext must be used within a PurchaseOrdersProvider')
  }
  return context
}
