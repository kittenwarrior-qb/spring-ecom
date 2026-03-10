import React, { useState } from 'react'
import useDialogState from '@/hooks/use-dialog-state'
import { OrderResponse } from '@/types/api'

type OrdersDialogType = 'status' | 'detail' | 'delete'

interface OrdersContextType {
    open: OrdersDialogType | null
    setOpen: (str: OrdersDialogType | null) => void
    currentRow: OrderResponse | null
    setCurrentRow: React.Dispatch<React.SetStateAction<OrderResponse | null>>
}

const OrdersContext = React.createContext<OrdersContextType | null>(null)

export function OrdersProvider({ children }: { children: React.ReactNode }) {
    const [open, setOpen] = useDialogState<OrdersDialogType>(null)
    const [currentRow, setCurrentRow] = useState<OrderResponse | null>(null)

    return (
        <OrdersContext.Provider value={{ open, setOpen, currentRow, setCurrentRow }}>
            {children}
        </OrdersContext.Provider>
    )
}

export const useOrders = () => {
    const context = React.useContext(OrdersContext)
    if (!context) {
        throw new Error('useOrders must be used within an OrdersProvider')
    }
    return context
}
