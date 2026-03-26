import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { cartApi } from '@/api/cart.api'
import type { AddToCartRequest, UpdateCartItemRequest, CartItemResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth-store'
import { useCartStore } from '@/stores/cart-store'
import { toast } from 'sonner'
import { getErrorMessage, isInsufficientStockError } from '@/lib/error-utils'

// Query keys
export const cartKeys = {
  all: ['cart'] as const,
  items: (isAuthenticated: boolean) => [...cartKeys.all, 'items', isAuthenticated] as const,
}

// Get cart items
export function useCartItems() {
  const isAuthenticated = useAuthStore((state) => !!state.auth.accessToken)
  const localItems = useCartStore((state) => state.items)

  return useQuery({
    queryKey: cartKeys.items(isAuthenticated),
    queryFn: async () => {
      if (isAuthenticated) {
        const items = await cartApi.getCartItems()
        // Sort by productId để đảm bảo thứ tự cố định
        return items.sort((a, b) => a.productId - b.productId)
      }
      
      // Map local items to CartItemResponse format for UI consistency
      const mappedItems = localItems.map(item => ({
        id: item.productId, // Use productId as id for local
        productId: item.productId,
        quantity: item.quantity,
        price: item.price,
        cartId: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      })) as CartItemResponse[]
      
      // Sort by productId để đảm bảo thứ tự cố định
      return mappedItems.sort((a, b) => a.productId - b.productId)
    },
    // Refresh when auth or local items change
    refetchOnWindowFocus: false,
  })
}

// Add to cart
export function useAddToCart() {
  const queryClient = useQueryClient()
  const isAuthenticated = useAuthStore((state) => !!state.auth.accessToken)
  const addLocalItem = useCartStore((state) => state.addItem)

  return useMutation({
    mutationFn: async (request: AddToCartRequest & { price?: number }) => {
      if (isAuthenticated) {
        return cartApi.addToCart(request)
      } else {
        addLocalItem({
          productId: request.productId,
          quantity: request.quantity,
          price: request.price || 0
        })
        return null
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: cartKeys.items(isAuthenticated) })
      toast.success('Đã thêm vào giỏ hàng')
    },
    onError: (error) => {
      const { isStockError } = isInsufficientStockError(error)
      const errorMessage = getErrorMessage(error)
      
      if (isStockError) {
        toast.error(errorMessage, { duration: 5000 })
      } else {
        toast.error(errorMessage)
      }
    },
  })
}

// Update cart item quantity
export function useUpdateCartItem() {
  const queryClient = useQueryClient()
  const isAuthenticated = useAuthStore((state) => !!state.auth.accessToken)
  const updateLocalQuantity = useCartStore((state) => state.updateQuantity)

  return useMutation({
    mutationFn: async ({ productId, request }: { productId: number; request: UpdateCartItemRequest }) => {
      if (isAuthenticated) {
        return cartApi.updateCartItem(productId, request)
      } else {
        updateLocalQuantity(productId, request.quantity)
        return null
      }
    },
    // Optimistic update để tránh UI jump
    onMutate: async ({ productId, request }) => {
      if (isAuthenticated) {
        // Cancel any outgoing refetches
        await queryClient.cancelQueries({ queryKey: cartKeys.items(isAuthenticated) })
        
        // Snapshot the previous value
        const previousCartItems = queryClient.getQueryData<CartItemResponse[]>(cartKeys.items(isAuthenticated))
        
        // Optimistically update to the new value
        if (previousCartItems) {
          const updatedItems = previousCartItems.map(item =>
            item.productId === productId 
              ? { ...item, quantity: request.quantity }
              : item
          )
          queryClient.setQueryData(cartKeys.items(isAuthenticated), updatedItems)
        }
        
        // Return a context object with the snapshotted value
        return { previousCartItems }
      }
    },
    // If the mutation fails, use the context returned from onMutate to roll back
    onError: (err, variables, context) => {
      if (isAuthenticated && context?.previousCartItems) {
        queryClient.setQueryData(cartKeys.items(isAuthenticated), context.previousCartItems)
      }
      
      // Show error message
      const { isStockError } = isInsufficientStockError(err)
      const errorMessage = getErrorMessage(err)
      
      if (isStockError) {
        toast.error(errorMessage, { duration: 5000 })
      } else {
        toast.error(errorMessage)
      }
    },
    // Always refetch after error or success to ensure consistency
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: cartKeys.items(isAuthenticated) })
    },
  })
}

// Remove cart item
export function useRemoveCartItem() {
  const queryClient = useQueryClient()
  const isAuthenticated = useAuthStore((state) => !!state.auth.accessToken)
  const removeLocalItem = useCartStore((state) => state.removeItem)

  return useMutation({
    mutationFn: async (productId: number) => {
      if (isAuthenticated) {
        return cartApi.removeCartItem(productId)
      } else {
        removeLocalItem(productId)
        return null
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: cartKeys.items(isAuthenticated) })
    },
  })
}

// Clear cart
export function useClearCart() {
  const queryClient = useQueryClient()
  const isAuthenticated = useAuthStore((state) => !!state.auth.accessToken)
  const clearLocalCart = useCartStore((state) => state.clearCart)

  return useMutation({
    mutationFn: async () => {
      if (isAuthenticated) {
        return cartApi.clearCart()
      } else {
        clearLocalCart()
        return null
      }
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: cartKeys.items(isAuthenticated) })
    },
  })
}

// Sync cart helper
export function useSyncCart() {
  const queryClient = useQueryClient()
  const clearLocalCart = useCartStore((state) => state.clearCart)

  return useMutation({
    mutationFn: (items: AddToCartRequest[]) => cartApi.syncCart(items),
    onSuccess: () => {
      clearLocalCart()
      // Invalidate both keys just in case, but usually we care about the transition to authenticated
      queryClient.invalidateQueries({ queryKey: cartKeys.all })
    },
  })
}

// Cart count helper
export function useCartCount() {
  const { data: cartItems } = useCartItems()
  return cartItems?.reduce((sum, item) => sum + item.quantity, 0) || 0
}
