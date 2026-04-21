import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { orderApi, type CreateOrderRequest } from '@/api/order.api'
import type { OrderStatus } from '@/types/api'

// Query keys
export const orderKeys = {
  all: ['orders'] as const,
  myOrders: (page: number, size: number) => [...orderKeys.all, 'my', { page, size }] as const,
  myOrdersByStatus: (status?: OrderStatus, page?: number, size?: number) => [...orderKeys.all, 'my', status, { page, size }] as const,
  detail: (id: number) => [...orderKeys.all, 'detail', id] as const,
}

// Create order from cart
export function useCreateOrder() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (request: CreateOrderRequest) => orderApi.createOrder(request),
    onSuccess: () => {
      // Clear cart after successful order
      queryClient.invalidateQueries({ queryKey: ['cart'] })
      queryClient.invalidateQueries({ queryKey: orderKeys.all })
    },
    onError: (error: any) => {
      console.error('Create order error:', error)
      // Let the component handle the error display
    },
  })
}

// Get my orders
export function useMyOrders(page = 0, size = 10) {
  return useQuery({
    queryKey: orderKeys.myOrders(page, size),
    queryFn: () => orderApi.getMyOrders(page, size),
  })
}

// Get my orders with items
export function useMyOrdersWithItems(params: { page: number; size: number }) {
  return useQuery({
    queryKey: [...orderKeys.all, 'my', 'withItems', params],
    queryFn: () => orderApi.getMyOrdersWithItems(params.page, params.size),
  })
}

// Get my orders by status
export function useMyOrdersByStatus(status: OrderStatus | undefined, page = 0, size = 10) {
  return useQuery({
    queryKey: orderKeys.myOrdersByStatus(status, page, size),
    queryFn: () => status ? orderApi.getMyOrdersByStatusWithItems(status, page, size) : orderApi.getMyOrdersWithItems(page, size),
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

// Partial cancel order
export function usePartialCancelOrder() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: orderApi.PartialCancelRequest }) => 
      orderApi.partialCancelOrder(id, request),
    onSuccess: (data, variables) => {
      // Invalidate all order queries to refresh lists
      queryClient.invalidateQueries({ queryKey: orderKeys.all })
      
      // Specifically invalidate the order detail for immediate refresh
      queryClient.invalidateQueries({ queryKey: orderKeys.detail(variables.id) })
      queryClient.invalidateQueries({ queryKey: [...orderKeys.detail(variables.id), 'items'] })
    },
  })
}

// Admin hooks
export function useAdminOrders(
  params: { 
    page: number
    size: number
    dateFrom?: string
    dateTo?: string
    status?: string
    search?: string
  }, 
  options?: { enabled?: boolean }
) {
  return useQuery({
    queryKey: [...orderKeys.all, 'admin', params],
    queryFn: () => orderApi.getAllOrders(params.page, params.size, {
      dateFrom: params.dateFrom,
      dateTo: params.dateTo,
      status: params.status,
      search: params.search,
    }),
    enabled: options?.enabled ?? true,
  })
}

export function useUpdateOrderStatus() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: OrderStatus }) => 
      orderApi.updateOrderStatus(id, status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: orderKeys.all })
    },
  })
}
