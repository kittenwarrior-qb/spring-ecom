import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { productApi } from '@/api/product.api'
import type { ProductRequest, ProductResponse } from '@/types/api'

// Query keys
export const productKeys = {
  all: ['products'] as const,
  lists: () => [...productKeys.all, 'list'] as const,
  list: (params: { page?: number; size?: number; sort?: string }) =>
    [...productKeys.lists(), params] as const,
  details: () => [...productKeys.all, 'detail'] as const,
  detail: (id: number) => [...productKeys.details(), id] as const,
  bySlug: (slug: string) => [...productKeys.all, 'slug', slug] as const,
  search: (keyword: string, params: { page?: number; size?: number }) =>
    [...productKeys.all, 'search', keyword, params] as const,
  bestsellers: (params: { page?: number; size?: number }) =>
    [...productKeys.all, 'bestsellers', params] as const,
  byCategory: (categorySlug: string, params: { page?: number; size?: number; sort?: string }) =>
    [...productKeys.all, 'category', categorySlug, params] as const,
}

// Get all products with pagination
export function useProducts(page = 0, size = 10, sort = 'id,desc') {
  return useQuery({
    queryKey: productKeys.list({ page, size, sort }),
    queryFn: () => productApi.getAll(page, size, sort),
  })
}

// Get product by ID
export function useProduct(id: number) {
  return useQuery({
    queryKey: productKeys.detail(id),
    queryFn: () => productApi.getById(id),
    enabled: !!id,
  })
}

// Get product by slug
export function useProductBySlug(slug: string) {
  return useQuery({
    queryKey: productKeys.bySlug(slug),
    queryFn: () => productApi.getBySlug(slug),
    enabled: !!slug,
  })
}

// Search products
export function useSearchProducts(keyword: string, page = 0, size = 10) {
  return useQuery({
    queryKey: productKeys.search(keyword, { page, size }),
    queryFn: () => productApi.search(keyword, page, size),
    enabled: !!keyword && keyword.length > 0,
  })
}

// Get bestseller products
export function useBestsellerProducts(page = 0, size = 10) {
  return useQuery({
    queryKey: productKeys.bestsellers({ page, size }),
    queryFn: () => productApi.getBestsellers(page, size),
  })
}

// Get products by category
export function useProductsByCategory(categorySlug: string, page = 0, size = 10, sort = 'id,desc') {
  return useQuery({
    queryKey: productKeys.byCategory(categorySlug, { page, size, sort }),
    queryFn: () => productApi.getByCategory(categorySlug, page, size, sort),
    enabled: !!categorySlug,
  })
}

// Create product
export function useCreateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: ProductRequest) => productApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: productKeys.lists() })
    },
  })
}

// Update product
export function useUpdateProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: ProductRequest }) =>
      productApi.update(id, data),
    onSuccess: (updatedProduct: ProductResponse) => {
      queryClient.invalidateQueries({ queryKey: productKeys.lists() })
      queryClient.setQueryData(productKeys.detail(updatedProduct.id), updatedProduct)
    },
  })
}

// Delete product
export function useDeleteProduct() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => productApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: productKeys.lists() })
    },
  })
}
