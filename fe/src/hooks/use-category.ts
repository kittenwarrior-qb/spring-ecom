import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { categoryApi } from '@/api/category.api'
import type { CategoryRequest, CategoryResponse } from '@/types/api'

// Query keys
export const categoryKeys = {
  all: ['categories'] as const,
  lists: () => [...categoryKeys.all, 'list'] as const,
  list: (filters: Record<string, unknown>) => [...categoryKeys.lists(), filters] as const,
  details: () => [...categoryKeys.all, 'detail'] as const,
  detail: (id: number) => [...categoryKeys.details(), id] as const,
  bySlug: (slug: string) => [...categoryKeys.all, 'slug', slug] as const,
  byParent: (parentId: number) => [...categoryKeys.all, 'parent', parentId] as const,
}

// Get all categories
export function useCategories() {
  return useQuery({
    queryKey: categoryKeys.lists(),
    queryFn: () => categoryApi.getAll(),
  })
}

// Get category by ID
export function useCategory(id: number) {
  return useQuery({
    queryKey: categoryKeys.detail(id),
    queryFn: () => categoryApi.getById(id),
    enabled: !!id,
  })
}

// Get category by slug
export function useCategoryBySlug(slug: string) {
  return useQuery({
    queryKey: categoryKeys.bySlug(slug),
    queryFn: () => categoryApi.getBySlug(slug),
    enabled: !!slug,
  })
}

// Get categories by parent ID
export function useCategoriesByParent(parentId: number) {
  return useQuery({
    queryKey: categoryKeys.byParent(parentId),
    queryFn: () => categoryApi.getByParentId(parentId),
    enabled: !!parentId,
  })
}

// Create category
export function useCreateCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CategoryRequest) => categoryApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.lists() })
    },
  })
}

// Update category
export function useUpdateCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: CategoryRequest }) =>
      categoryApi.update(id, data),
    onSuccess: (updatedCategory: CategoryResponse) => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.lists() })
      queryClient.setQueryData(categoryKeys.detail(updatedCategory.id), updatedCategory)
    },
  })
}

// Delete category
export function useDeleteCategory() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => categoryApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: categoryKeys.lists() })
    },
  })
}
