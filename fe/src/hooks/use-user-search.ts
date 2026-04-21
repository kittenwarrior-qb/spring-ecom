import { useCallback } from 'react'
import adminApiClient from '@/lib/admin-api-client'
import type { User } from '@/components/user-search-select'

interface BackendUser {
  id: number
  email: string
  firstName: string | null
  lastName: string | null
  avatarUrl: string | null
}

interface SearchUsersResponse {
  data: {
    content: BackendUser[]
  }
}

export function useUserSearch() {
  const searchUsers = useCallback(async (query: string): Promise<User[]> => {
    if (!query.trim()) return []

    try {
      const response = await adminApiClient.get<SearchUsersResponse>(
        `/api/admin/users/search`,
        {
          params: {
            email: query,
            size: 10,
          },
        }
      )
      const users = response.data.data?.content || []
      // Transform backend response to match User interface
      return users.map(u => ({
        id: u.id,
        email: u.email,
        name: [u.firstName, u.lastName].filter(Boolean).join(' ') || undefined,
        avatar: u.avatarUrl || undefined,
      }))
    } catch (_error) {
      return []
    }
  }, [])

  return { searchUsers }
}
