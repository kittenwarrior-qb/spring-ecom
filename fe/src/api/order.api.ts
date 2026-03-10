import apiClient from '@/lib/api-client'
import type { ApiResponse, PageResponse, OrderResponse, OrderDetailResponse, OrderStatus } from '@/types/api'

const ORDER_BASE_URL = '/v1/api/orders'

export const orderApi = {
  // Get my orders
  getMyOrders: async (page = 0, size = 10): Promise<PageResponse<OrderResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<OrderResponse>>>(`${ORDER_BASE_URL}/my-orders`, {
      params: { page, size }
    })
    return response.data.data
  },

  // Get my orders by status
  getMyOrdersByStatus: async (status: OrderStatus, page = 0, size = 10): Promise<PageResponse<OrderResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<OrderResponse>>>(
      `${ORDER_BASE_URL}/my-orders/status/${status}`,
      { params: { page, size } }
    )
    return response.data.data
  },

  // Get order by ID
  getOrderById: async (id: number): Promise<OrderResponse> => {
    const response = await apiClient.get<ApiResponse<OrderResponse>>(`${ORDER_BASE_URL}/${id}`)
    return response.data.data
  },

  // Get order detail with items
  getOrderDetail: async (id: number): Promise<OrderDetailResponse> => {
    const response = await apiClient.get<ApiResponse<OrderDetailResponse>>(`${ORDER_BASE_URL}/${id}/detail`)
    return response.data.data
  },

  // Get order by order number
  getOrderByNumber: async (orderNumber: string): Promise<OrderResponse> => {
    const response = await apiClient.get<ApiResponse<OrderResponse>>(`${ORDER_BASE_URL}/number/${orderNumber}`)
    return response.data.data
  },

  // Cancel order
  cancelOrder: async (id: number): Promise<void> => {
    await apiClient.post<ApiResponse<void>>(`${ORDER_BASE_URL}/${id}/cancel`)
  },

  // Admin: Get all orders
  getAllOrders: async (page = 0, size = 10): Promise<PageResponse<OrderResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<OrderResponse>>>(ORDER_BASE_URL, {
      params: { page, size }
    })
    return response.data.data
  },

  // Admin: Update order status
  updateOrderStatus: async (id: number, status: OrderStatus): Promise<OrderResponse> => {
    const response = await apiClient.put<ApiResponse<OrderResponse>>(`${ORDER_BASE_URL}/${id}/status`, {
      status
    })
    return response.data.data
  },
}

