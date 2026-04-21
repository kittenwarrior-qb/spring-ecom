import apiClient from '@/lib/api-client'
import type {
  ApiResponse,
  PageResponse,
  PurchaseOrderResponse,
  CreatePurchaseOrderRequest,
  PurchaseOrderStatus,
} from '@/types/api'

// Purchase Order API calls Core service directly on port 8081
const ADMIN_PO_URL = 'http://localhost:8081/v1/api/admin/inventory/purchase-orders'

export const purchaseOrderApi = {
  // Get all purchase orders with pagination and filters
  getAll: async (
    page = 0,
    size = 10,
    status?: PurchaseOrderStatus,
    supplierId?: number
  ): Promise<PageResponse<PurchaseOrderResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<PurchaseOrderResponse>>>(
      ADMIN_PO_URL,
      {
        params: {
          page,
          size,
          sort: 'id,desc',
          ...(status && { status }),
          ...(supplierId && { supplierId }),
        },
      }
    )
    return response.data.data
  },

  // Get purchase order by ID
  getById: async (id: number): Promise<PurchaseOrderResponse> => {
    const response = await apiClient.get<ApiResponse<PurchaseOrderResponse>>(
      `${ADMIN_PO_URL}/${id}`
    )
    return response.data.data
  },

  // Create new purchase order
  create: async (data: CreatePurchaseOrderRequest): Promise<PurchaseOrderResponse> => {
    const response = await apiClient.post<ApiResponse<PurchaseOrderResponse>>(
      ADMIN_PO_URL,
      data
    )
    return response.data.data
  },

  // Update purchase order (only DRAFT status)
  update: async (id: number, data: CreatePurchaseOrderRequest): Promise<PurchaseOrderResponse> => {
    const response = await apiClient.put<ApiResponse<PurchaseOrderResponse>>(
      `${ADMIN_PO_URL}/${id}`,
      data
    )
    return response.data.data
  },

  // Confirm purchase order (DRAFT -> CONFIRMED)
  confirm: async (id: number): Promise<PurchaseOrderResponse> => {
    const response = await apiClient.post<ApiResponse<PurchaseOrderResponse>>(
      `${ADMIN_PO_URL}/${id}/confirm`
    )
    return response.data.data
  },

  // Receive goods (CONFIRMED -> RECEIVED, stock updated automatically)
  receive: async (id: number): Promise<PurchaseOrderResponse> => {
    const response = await apiClient.post<ApiResponse<PurchaseOrderResponse>>(
      `${ADMIN_PO_URL}/${id}/receive`
    )
    return response.data.data
  },

  // Cancel purchase order (DRAFT/CONFIRMED -> CANCELLED)
  cancel: async (id: number): Promise<void> => {
    await apiClient.post<ApiResponse<void>>(`${ADMIN_PO_URL}/${id}/cancel`)
  },
}
