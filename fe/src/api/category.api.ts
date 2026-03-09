import apiClient from '@/lib/api-client'
import type { ApiResponse, CategoryRequest, CategoryResponse } from '@/types/api'

const CATEGORY_BASE_URL = '/v1/api/categories'

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

  create: async (data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await apiClient.post<ApiResponse<CategoryResponse>>(CATEGORY_BASE_URL, data)
    return response.data.data
  },

  update: async (id: number, data: CategoryRequest): Promise<CategoryResponse> => {
    const response = await apiClient.put<ApiResponse<CategoryResponse>>(`${CATEGORY_BASE_URL}/${id}`, data)
    return response.data.data
  },

  delete: async (id: number): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${CATEGORY_BASE_URL}/${id}`)
  },
}
