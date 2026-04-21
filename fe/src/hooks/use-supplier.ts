import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { supplierApi } from '@/api/supplier.api'
import type { SupplierRequest } from '@/types/api'

// Query keys
export const supplierKeys = {
  all: ['suppliers'] as const,
  list: (page: number, size: number, filters?: { keyword?: string; isActive?: boolean }) =>
    [...supplierKeys.all, 'list', { page, size, ...filters }] as const,
  detail: (id: number) => [...supplierKeys.all, 'detail', id] as const,
}

// Get all suppliers with pagination
export function useSuppliers(
  params: {
    page?: number
    size?: number
    keyword?: string
    isActive?: boolean
  },
  options?: { enabled?: boolean }
) {
  return useQuery({
    queryKey: supplierKeys.list(params.page ?? 0, params.size ?? 10, {
      keyword: params.keyword,
      isActive: params.isActive,
    }),
    queryFn: () => supplierApi.getAll(params.page, params.size, params.keyword, params.isActive),
    enabled: options?.enabled ?? true,
  })
}

// Get supplier by ID
export function useSupplier(id: number, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: supplierKeys.detail(id),
    queryFn: () => supplierApi.getById(id),
    enabled: options?.enabled ?? !!id,
  })
}

// Create supplier
export function useCreateSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: SupplierRequest) => supplierApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supplierKeys.all })
    },
  })
}

// Update supplier
export function useUpdateSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: SupplierRequest }) =>
      supplierApi.update(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: supplierKeys.all })
      queryClient.invalidateQueries({ queryKey: supplierKeys.detail(variables.id) })
    },
  })
}

// Delete supplier
export function useDeleteSupplier() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => supplierApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: supplierKeys.all })
    },
  })
}
