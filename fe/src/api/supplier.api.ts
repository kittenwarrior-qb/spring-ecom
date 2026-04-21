import apiClient from '@/lib/api-client'
import type { ApiResponse, PageResponse, SupplierRequest, SupplierResponse } from '@/types/api'

// Supplier API calls Core service directly on port 8081
const ADMIN_SUPPLIER_URL = 'http://localhost:8081/v1/api/admin/suppliers'

export const supplierApi = {
  // Get all suppliers with pagination and filters
  getAll: async (
    page = 0,
    size = 10,
    keyword?: string,
    isActive?: boolean
  ): Promise<PageResponse<SupplierResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<SupplierResponse>>>(
      ADMIN_SUPPLIER_URL,
      {
        params: {
          page,
          size,
          sort: 'id,desc',
          ...(keyword && { keyword }),
          ...(isActive !== undefined && { isActive }),
        },
      }
    )
    return response.data.data
  },

  // Get supplier by ID
  getById: async (id: number): Promise<SupplierResponse> => {
    const response = await apiClient.get<ApiResponse<SupplierResponse>>(
      `${ADMIN_SUPPLIER_URL}/${id}`
    )
    return response.data.data
  },

  // Create new supplier
  create: async (data: SupplierRequest): Promise<SupplierResponse> => {
    const response = await apiClient.post<ApiResponse<SupplierResponse>>(
      ADMIN_SUPPLIER_URL,
      data
    )
    return response.data.data
  },

  // Update supplier
  update: async (id: number, data: SupplierRequest): Promise<SupplierResponse> => {
    const response = await apiClient.put<ApiResponse<SupplierResponse>>(
      `${ADMIN_SUPPLIER_URL}/${id}`,
      data
    )
    return response.data.data
  },

  // Delete supplier (soft delete)
  delete: async (id: number): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${ADMIN_SUPPLIER_URL}/${id}`)
  },
}
