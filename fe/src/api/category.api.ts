import apiClient from '@/lib/api-client'
import adminApiClient from '@/lib/admin-api-client'
import type { ApiResponse, CategoryRequest, CategoryResponse } from '@/types/api'

const CATEGORY_BASE_URL = '/api/categories'
const ADMIN_CATEGORY_URL = '/api/admin/categories'

export const categoryApi = {
  getAll: async (): Promise<CategoryResponse[]> => {
    const response = await apiClient.get<ApiResponse<CategoryResponse[]>>(CATEGORY_BASE_URL)
    return response.data.data
  },

  getById: async (id: number): Promise<CategoryResponse> => {
    const response = await apiClient.get<ApiResponse<CategoryResponse>>(`${CATEGORY_BASE_URL}/${id}`)
    return response.data.data
  },

  getBySlug: async (slug: string): Promise<CategoryResponse> => {
    const response = await apiClient.get<ApiResponse<CategoryResponse>>(`${CATEGORY_BASE_URL}/slug/${slug}`)
    return response.data.data
  },

  getByParentId: async (parentId: number): Promise<CategoryResponse[]> => {
    const response = await apiClient.get<ApiResponse<CategoryResponse[]>>(`${CATEGORY_BASE_URL}/parent/${parentId}`)
    return response.data.data
  },

  // Admin: Create category (calls Server port 8081)
  create: async (data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await adminApiClient.post<ApiResponse<CategoryResponse>>(ADMIN_CATEGORY_URL, data)
    return response.data.data
  },

  // Admin: Update category (calls Server port 8081)
  update: async (id: number, data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await adminApiClient.put<ApiResponse<CategoryResponse>>(`${ADMIN_CATEGORY_URL}/${id}`, data)
    return response.data.data
  },

  // Admin: Delete category (calls Server port 8081)
  delete: async (id: number): Promise<void> => {
    await adminApiClient.delete<ApiResponse<void>>(`${ADMIN_CATEGORY_URL}/${id}`)
  },
}
