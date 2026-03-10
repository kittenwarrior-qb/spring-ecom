import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { cartApi } from '@/api/cart.api'
import type { AddToCartRequest, UpdateCartItemRequest, CartItemResponse } from '@/types/api'
import { useAuthStore } from '@/stores/auth-store'
import { useCartStore } from '@/stores/cart-store'

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
        return cartApi.getCartItems()
      }
      
      // Map local items to CartItemResponse format for UI consistency
      return localItems.map(item => ({
        id: item.productId, // Use productId as id for local
        productId: item.productId,
        quantity: item.quantity,
        price: item.price,
        cartId: 0,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString()
      })) as CartItemResponse[]
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
    onSuccess: () => {
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
