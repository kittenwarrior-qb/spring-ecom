import apiClient from '@/lib/api-client'
import type { ApiResponse, PageResponse, ProductRequest, ProductResponse } from '@/types/api'

// All product API calls Core service directly on port 8081
const PRODUCT_BASE_URL = 'http://localhost:8081/v1/api/products'
const ADMIN_PRODUCT_URL = 'http://localhost:8081/v1/api/admin/products'

export const productApi = {
  // Get all products with pagination
  getAll: async (page = 0, size = 10, sort = 'id,desc'): Promise<PageResponse<ProductResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ProductResponse>>>(
      PRODUCT_BASE_URL,
      { params: { page, size, sort } }
    )
    return response.data.data
  },

  // Get product by ID
  getById: async (id: number): Promise<ProductResponse> => {
    const response = await apiClient.get<ApiResponse<ProductResponse>>(`${PRODUCT_BASE_URL}/${id}`)
    return response.data.data
  },

  // Get product by slug
  getBySlug: async (slug: string): Promise<ProductResponse> => {
    const response = await apiClient.get<ApiResponse<ProductResponse>>(`${PRODUCT_BASE_URL}/slug/${slug}`)
    return response.data.data
  },

  // Search products by keyword
  search: async (keyword: string, page = 0, size = 10): Promise<PageResponse<ProductResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ProductResponse>>>(
      `${PRODUCT_BASE_URL}/search`,
      { params: { keyword, page, size } }
    )
    return response.data.data
  },

  // Get bestseller products
  getBestsellers: async (page = 0, size = 10): Promise<PageResponse<ProductResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ProductResponse>>>(
      `${PRODUCT_BASE_URL}/bestseller`,
      { params: { page, size } }
    )
    return response.data.data
  },

  // Get products by category slug
  getByCategory: async (categorySlug: string, page = 0, size = 10, sort = 'id,desc'): Promise<PageResponse<ProductResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<ProductResponse>>>(
      `${PRODUCT_BASE_URL}/category/${categorySlug}`,
      { params: { page, size, sort } }
    )
    return response.data.data
  },

  // Admin: Create new product (calls Landing 8080 -> gRPC -> Core)
  create: async (data: ProductRequest): Promise<ProductResponse> => {
    const response = await apiClient.post<ApiResponse<ProductResponse>>(ADMIN_PRODUCT_URL, data)
    return response.data.data
  },

  // Admin: Update product (calls Landing 8080 -> gRPC -> Core)
  update: async (id: number, data: ProductRequest): Promise<ProductResponse> => {
    const response = await apiClient.put<ApiResponse<ProductResponse>>(`${ADMIN_PRODUCT_URL}/${id}`, data)
    return response.data.data
  },

  // Admin: Delete product (calls Landing 8080 -> gRPC -> Core)
  delete: async (id: number): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${ADMIN_PRODUCT_URL}/${id}`)
  },
}
