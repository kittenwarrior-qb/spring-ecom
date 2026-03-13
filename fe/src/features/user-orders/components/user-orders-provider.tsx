import { createContext, useContext, useState, type ReactNode } from 'react'
import type { OrderDetailResponse } from '@/types/api'

type DialogType = 'detail' | 'cancel' | null

interface UserOrdersContextType {
  open: DialogType
  setOpen: (open: DialogType) => void
  currentRow: OrderDetailResponse | null
  setCurrentRow: (row: OrderDetailResponse | null) => void
}

const UserOrdersContext = createContext<UserOrdersContextType | undefined>(undefined)

export function UserOrdersProvider({ children }: { children: ReactNode }) {
  const [open, setOpen] = useState<DialogType>(null)
  const [currentRow, setCurrentRow] = useState<OrderDetailResponse | null>(null)

  return (
    <UserOrdersContext.Provider value={{ open, setOpen, currentRow, setCurrentRow }}>
      {children}
    </UserOrdersContext.Provider>
  )
}

export function useUserOrders() {
  const context = useContext(UserOrdersContext)
  if (!context) {
    throw new Error('useUserOrders must be used within UserOrdersProvider')
  }
  return context
}