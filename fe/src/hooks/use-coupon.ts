import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { couponApi } from '@/api/coupon.api'
import type { CouponRequest, CouponValidationRequest } from '@/types/api'

// Query keys
export const couponKeys = {
  all: ['coupons'] as const,
  lists: () => [...couponKeys.all, 'list'] as const,
  list: (filters: { page: number; size: number }) => [...couponKeys.lists(), filters] as const,
  details: () => [...couponKeys.all, 'detail'] as const,
  detail: (id: number) => [...couponKeys.details(), id] as const,
  byCode: (code: string) => [...couponKeys.all, 'code', code] as const,
  active: (filters: { page: number; size: number }) => [...couponKeys.all, 'active', filters] as const,
}

// Public hooks
export function useActiveCoupons(params: { page: number; size: number }) {
  return useQuery({
    queryKey: couponKeys.active(params),
    queryFn: () => couponApi.getActiveCoupons(params.page, params.size),
  })
}

export function useCouponByCode(code: string, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: couponKeys.byCode(code),
    queryFn: () => couponApi.getCouponByCode(code),
    enabled: options?.enabled ?? !!code,
  })
}

export function useValidateCoupon() {
  return useMutation({
    mutationFn: (request: CouponValidationRequest) => couponApi.validateCoupon(request),
  })
}

// Admin hooks
export function useAdminCoupons(params: { page: number; size: number }) {
  return useQuery({
    queryKey: couponKeys.list(params),
    queryFn: () => couponApi.getAllCoupons(params.page, params.size),
  })
}

export function useAdminCoupon(id: number, options?: { enabled?: boolean }) {
  return useQuery({
    queryKey: couponKeys.detail(id),
    queryFn: () => couponApi.getCouponById(id),
    enabled: options?.enabled ?? true,
  })
}

export function useCreateCoupon() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (request: CouponRequest) => couponApi.createCoupon(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: couponKeys.lists() })
    },
  })
}

export function useUpdateCoupon() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, request }: { id: number; request: CouponRequest }) => 
      couponApi.updateCoupon(id, request),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: couponKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: couponKeys.lists() })
    },
  })
}

export function useDeleteCoupon() {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (id: number) => couponApi.deleteCoupon(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: couponKeys.lists() })
    },
  })
}
