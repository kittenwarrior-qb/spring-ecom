import { createContext, useContext, useState, type ReactNode } from 'react'
import type { CategoryResponse } from '@/types/api'

interface CategoriesContextType {
  // Selected category for edit/delete
  selectedCategory: CategoryResponse | null
  setSelectedCategory: (category: CategoryResponse | null) => void
  // Dialog states
  isCreateOpen: boolean
  setIsCreateOpen: (open: boolean) => void
  isEditOpen: boolean
  setIsEditOpen: (open: boolean) => void
  isDeleteOpen: boolean
  setIsDeleteOpen: (open: boolean) => void
}

const CategoriesContext = createContext<CategoriesContextType | undefined>(undefined)

export function CategoriesProvider({ children }: { children: ReactNode }) {
  const [selectedCategory, setSelectedCategory] = useState<CategoryResponse | null>(null)
  const [isCreateOpen, setIsCreateOpen] = useState(false)
  const [isEditOpen, setIsEditOpen] = useState(false)
  const [isDeleteOpen, setIsDeleteOpen] = useState(false)

  return (
    <CategoriesContext.Provider
      value={{
        selectedCategory,
        setSelectedCategory,
        isCreateOpen,
        setIsCreateOpen,
        isEditOpen,
        setIsEditOpen,
        isDeleteOpen,
        setIsDeleteOpen,
      }}
    >
      {children}
    </CategoriesContext.Provider>
  )
}

export function useCategoriesContext() {
  const context = useContext(CategoriesContext)
  if (!context) {
    throw new Error('useCategoriesContext must be used within CategoriesProvider')
  }
  return context
}
