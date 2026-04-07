import apiClient from '@/lib/api-client'
import adminApiClient from '@/lib/admin-api-client'
import type { 
  ApiResponse, 
  PageResponse, 
  CouponRequest, 
  CouponResponse, 
  CouponValidationRequest, 
  CouponValidationResponse 
} from '@/types/api'

const COUPON_BASE_URL = '/api/coupons'
const ADMIN_COUPON_URL = '/api/admin/coupons'

export const couponApi = {
  // Public: Get active coupons
  getActiveCoupons: async (page = 0, size = 10): Promise<PageResponse<CouponResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<CouponResponse>>>(COUPON_BASE_URL, {
      params: { page, size }
    })
    return response.data.data
  },

  // Public: Get all active coupons for coupons page
  getPublicCoupons: async (): Promise<CouponResponse[]> => {
    const response = await apiClient.get<ApiResponse<CouponResponse[]>>(`${COUPON_BASE_URL}/public`)
    return response.data.data
  },

  // Public: Get coupon by code
  getCouponByCode: async (code: string): Promise<CouponResponse> => {
    const response = await apiClient.get<ApiResponse<CouponResponse>>(`${COUPON_BASE_URL}/code/${code}`)
    return response.data.data
  },

  // Public: Validate coupon
  validateCoupon: async (request: CouponValidationRequest): Promise<CouponValidationResponse> => {
    const response = await apiClient.post<ApiResponse<CouponValidationResponse>>(
      `${COUPON_BASE_URL}/validate`,
      request
    )
    return response.data.data
  },

  // Admin: Get all coupons
  getAllCoupons: async (page = 0, size = 10): Promise<PageResponse<CouponResponse>> => {
    const response = await adminApiClient.get<ApiResponse<PageResponse<CouponResponse>>>(ADMIN_COUPON_URL, {
      params: { page, size }
    })
    return response.data.data
  },

  // Admin: Get coupon by ID
  getCouponById: async (id: number): Promise<CouponResponse> => {
    const response = await adminApiClient.get<ApiResponse<CouponResponse>>(`${ADMIN_COUPON_URL}/${id}`)
    return response.data.data
  },

  // Admin: Create coupon
  createCoupon: async (request: CouponRequest): Promise<CouponResponse> => {
    const response = await adminApiClient.post<ApiResponse<CouponResponse>>(ADMIN_COUPON_URL, request)
    return response.data.data
  },

  // Admin: Update coupon
  updateCoupon: async (id: number, request: CouponRequest): Promise<CouponResponse> => {
    const response = await adminApiClient.put<ApiResponse<CouponResponse>>(`${ADMIN_COUPON_URL}/${id}`, request)
    return response.data.data
  },

  // Admin: Delete coupon
  deleteCoupon: async (id: number): Promise<void> => {
    await adminApiClient.delete<ApiResponse<void>>(`${ADMIN_COUPON_URL}/${id}`)
  },
}
