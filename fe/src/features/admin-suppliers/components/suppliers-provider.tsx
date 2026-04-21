import { createContext, useContext, useState, type ReactNode } from 'react'
import type { SupplierResponse } from '@/types/api'

interface SuppliersContextType {
  selectedSupplier: SupplierResponse | null
  setSelectedSupplier: (supplier: SupplierResponse | null) => void
  isCreateDialogOpen: boolean
  setIsCreateDialogOpen: (open: boolean) => void
  isEditDialogOpen: boolean
  setIsEditDialogOpen: (open: boolean) => void
}

const SuppliersContext = createContext<SuppliersContextType | undefined>(undefined)

export function SuppliersProvider({ children }: { children: ReactNode }) {
  const [selectedSupplier, setSelectedSupplier] = useState<SupplierResponse | null>(null)
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)

  return (
    <SuppliersContext.Provider
      value={{
        selectedSupplier,
        setSelectedSupplier,
        isCreateDialogOpen,
        setIsCreateDialogOpen,
        isEditDialogOpen,
        setIsEditDialogOpen,
      }}
    >
      {children}
    </SuppliersContext.Provider>
  )
}

export function useSuppliersContext() {
  const context = useContext(SuppliersContext)
  if (!context) {
    throw new Error('useSuppliersContext must be used within a SuppliersProvider')
  }
  return context
}
