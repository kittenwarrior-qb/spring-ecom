import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { orderApi } from '@/api/order.api'
import type { OrderStatus } from '@/types/api'

// Query keys
export const orderKeys = {
  all: ['orders'] as const,
  myOrders: (page: number, size: number) => [...orderKeys.all, 'my', { page, size }] as const,
  myOrdersByStatus: (status?: OrderStatus, page?: number, size?: number) => [...orderKeys.all, 'my', status, { page, size }] as const,
  detail: (id: number) => [...orderKeys.all, 'detail', id] as const,
}

// Get my orders
export function useMyOrders(page = 0, size = 10) {
  return useQuery({
    queryKey: orderKeys.myOrders(page, size),
    queryFn: () => orderApi.getMyOrders(page, size),
  })
}

// Get my orders by status
export function useMyOrdersByStatus(status: OrderStatus | undefined, page = 0, size = 10) {
  return useQuery({
    queryKey: orderKeys.myOrdersByStatus(status, page, size),
    queryFn: () => status ? orderApi.getMyOrdersByStatus(status, page, size) : orderApi.getMyOrders(page, size),
    enabled: true,
  })
}

// Get order by ID
export function useOrder(id: number) {
  return useQuery({
    queryKey: orderKeys.detail(id),
    queryFn: () => orderApi.getOrderById(id),
    enabled: !!id,
  })
}

// Get order detail with items
export function useOrderDetail(id: number) {
  return useQuery({
    queryKey: [...orderKeys.detail(id), 'items'],
    queryFn: () => orderApi.getOrderDetail(id),
    enabled: !!id,
  })
}

// Cancel order
export function useCancelOrder() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (id: number) => orderApi.cancelOrder(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: orderKeys.all })
    },
  })
}
