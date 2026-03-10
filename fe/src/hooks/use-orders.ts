import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { orderApi } from '@/api/order.api'
import type { PageRequest, OrderStatus } from '@/types/api'

// Query keys
export const orderKeys = {
  all: ['orders'] as const,
  lists: () => [...orderKeys.all, 'list'] as const,
  list: (params: PageRequest) => [...orderKeys.lists(), params] as const,
  details: () => [...orderKeys.all, 'detail'] as const,
  detail: (id: number) => [...orderKeys.details(), id] as const,
  adminLists: () => [...orderKeys.all, 'admin-list'] as const,
  adminList: (params: PageRequest) => [...orderKeys.adminLists(), params] as const,
}

// Get all orders (Admin)
export function useAdminOrders(params?: PageRequest) {
  return useQuery({
    queryKey: orderKeys.adminList(params || {}),
    queryFn: () => orderApi.getAllOrders(params?.page, params?.size),
  })
}

// Get order detail
export function useOrderDetail(id: number) {
  return useQuery({
    queryKey: orderKeys.detail(id),
    queryFn: () => orderApi.getOrderDetail(id),
    enabled: !!id,
  })
}

// Update order status (Admin)
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
