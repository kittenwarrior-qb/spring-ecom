import { createContext, useContext, useState, type ReactNode } from 'react'
import type { ProductResponse } from '@/types/api'

interface ProductsContextType {
  // Selected product for edit/delete
  selectedProduct: ProductResponse | null
  setSelectedProduct: (product: ProductResponse | null) => void
  // Dialog states
  isCreateOpen: boolean
  setIsCreateOpen: (open: boolean) => void
  isEditOpen: boolean
  setIsEditOpen: (open: boolean) => void
  isDeleteOpen: boolean
  setIsDeleteOpen: (open: boolean) => void
}

const ProductsContext = createContext<ProductsContextType | undefined>(undefined)

export function ProductsProvider({ children }: { children: ReactNode }) {
  const [selectedProduct, setSelectedProduct] = useState<ProductResponse | null>(null)
  const [isCreateOpen, setIsCreateOpen] = useState(false)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const [isDeleteOpen, setIsDeleteOpen] = useState(false)

  return (
    <ProductsContext.Provider
      value={{
        selectedProduct,
        setSelectedProduct,
        isCreateOpen,
        setIsCreateOpen,
        isEditOpen,
        setIsEditOpen,
        isDeleteOpen,
        setIsDeleteOpen,
      }}
    >
      {children}
    </ProductsContext.Provider>
  )
}

export function useProductsContext() {
  const context = useContext(ProductsContext)
  if (!context) {
    throw new Error('useProductsContext must be used within ProductsProvider')
  }
  return context
}
