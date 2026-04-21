import apiClient from '@/lib/api-client'
import type {
  ApiResponse,
  PageResponse,
  InventoryTransactionResponse,
  MovementType,
} from '@/types/api'

// Inventory API calls Core service directly on port 8081
const ADMIN_INV_URL = 'http://localhost:8081/v1/api/admin/inventory'

export const inventoryApi = {
  // Get all inventory movements with pagination and filters
  getMovements: async (
    page = 0,
    size = 10,
    productId?: number,
    movementType?: MovementType
  ): Promise<PageResponse<InventoryTransactionResponse>> => {
    const response = await apiClient.get<ApiResponse<PageResponse<InventoryTransactionResponse>>>(
      `${ADMIN_INV_URL}/movements`,
      {
        params: {
          page,
          size,
          sort: 'id,desc',
          ...(productId && { productId }),
          ...(movementType && { movementType }),
        },
      }
    )
    return response.data.data
  },
}
