import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { purchaseOrderApi } from '@/api/purchase-order.api'
import type {
  CreatePurchaseOrderRequest,
  PurchaseOrderStatus,
} from '@/types/api'

// Query keys
export const poKeys = {
  all: ['purchaseOrders'] as const,
  list: (
    page: number,
    size: number,
    filters?: { status?: PurchaseOrderStatus; supplierId?: number }
  ) => [...poKeys.all, 'list', { page, size, ...filters }] as const,
  detail: (id: number) => [...poKeys.all, 'detail', id] as const,
}

// Get all purchase orders with pagination
export function usePurchaseOrders(
  params: {
    page?: number
    size?: number
    status?: PurchaseOrderStatus
    supplierId?: number
  },
  options?: { enabled?: boolean }
) {
  return useQuery({
    queryKey: poKeys.list(params.page ?? 0, params.size ?? 10, {
      status: params.status,
      supplierId: params.supplierId,
    }),
    queryFn: () => purchaseOrderApi.getAll(params.page, params.size, params.status, params.supplierId),
    enabled: options?.enabled ?? true,
  })
}

// Get purchase order by ID
export function usePurchaseOrder(id: number, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: poKeys.detail(id),
    queryFn: () => purchaseOrderApi.getById(id),
    enabled: options?.enabled ?? !!id,
  })
}

// Create purchase order
export function useCreatePurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreatePurchaseOrderRequest) => purchaseOrderApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: poKeys.all })
    },
  })
}

// Update purchase order
export function useUpdatePurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: CreatePurchaseOrderRequest }) =>
      purchaseOrderApi.update(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: poKeys.all })
      queryClient.invalidateQueries({ queryKey: poKeys.detail(variables.id) })
    },
  })
}

// Confirm purchase order
export function useConfirmPurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => purchaseOrderApi.confirm(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: poKeys.all })
      queryClient.invalidateQueries({ queryKey: poKeys.detail(id) })
    },
  })
}

// Receive goods (stock updated automatically by backend)
export function useReceiveGoods() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => purchaseOrderApi.receive(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: poKeys.all })
      queryClient.invalidateQueries({ queryKey: poKeys.detail(id) })
      // Also invalidate products since stock changed
      queryClient.invalidateQueries({ queryKey: ['products'] })
    },
  })
}

// Cancel purchase order
export function useCancelPurchaseOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => purchaseOrderApi.cancel(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: poKeys.all })
      queryClient.invalidateQueries({ queryKey: poKeys.detail(id) })
    },
  })
}
