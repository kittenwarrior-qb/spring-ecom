import apiClient from '@/lib/api-client'
import type { 
  ApiResponse, 
  CartItemResponse, 
  AddToCartRequest, 
  UpdateCartItemRequest 
} from '@/types/api'

const CART_BASE_URL = '/api/cart'

export const cartApi = {
  // Get all cart items
  getCartItems: async (): Promise<CartItemResponse[]> => {
    const response = await apiClient.get<ApiResponse<CartItemResponse[]>>(CART_BASE_URL)
    return response.data.data
  },

  // Add item to cart
  addToCart: async (request: AddToCartRequest): Promise<CartItemResponse> => {
    const response = await apiClient.post<ApiResponse<CartItemResponse>>(
      `${CART_BASE_URL}/items`,
      request
    )
    return response.data.data
  },

  // Update cart item quantity
  updateCartItem: async (productId: number, request: UpdateCartItemRequest): Promise<CartItemResponse> => {
    const response = await apiClient.put<ApiResponse<CartItemResponse>>(
      `${CART_BASE_URL}/items/${productId}`,
      request
    )
    return response.data.data
  },

  // Remove item from cart
  removeCartItem: async (productId: number): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(`${CART_BASE_URL}/items/${productId}`)
  },

  // Clear cart
  clearCart: async (): Promise<void> => {
    await apiClient.delete<ApiResponse<void>>(CART_BASE_URL)
  },

  // Sync local cart to server
  syncCart: async (items: AddToCartRequest[]): Promise<void> => {
    await apiClient.post<ApiResponse<void>>(`${CART_BASE_URL}/sync`, items)
  },
}
