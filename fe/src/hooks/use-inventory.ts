import { useQuery } from '@tanstack/react-query'
import { inventoryApi } from '@/api/inventory.api'
import type { MovementType } from '@/types/api'

// Query keys
export const inventoryKeys = {
  all: ['inventory'] as const,
  movements: (
    page: number,
    size: number,
    filters?: { productId?: number; movementType?: MovementType }
  ) => [...inventoryKeys.all, 'movements', { page, size, ...filters }] as const,
}

// Get all inventory movements with pagination
export function useInventoryMovements(
  params: {
    page?: number
    size?: number
    productId?: number
    movementType?: MovementType
  },
  options?: { enabled?: boolean }
) {
  return useQuery({
    queryKey: inventoryKeys.movements(params.page ?? 0, params.size ?? 10, {
      productId: params.productId,
      movementType: params.movementType,
    }),
    queryFn: () => inventoryApi.getMovements(params.page, params.size, params.productId, params.movementType),
    enabled: options?.enabled ?? true,
  })
}
