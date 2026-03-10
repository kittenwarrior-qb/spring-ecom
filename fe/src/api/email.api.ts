import apiClient from '@/lib/api-client'
import type { ApiResponse } from '@/types/api'

const EMAIL_BASE_URL = '/v1/api/email'

export const emailApi = {
  verifyEmail: async (token: string): Promise<string> => {
    const response = await apiClient.post<ApiResponse<string>>(`${EMAIL_BASE_URL}/verify`, null, {
      params: { token },
    })
    return response.data.message
  },

  resendVerification: async (email: string): Promise<string> => {
    const response = await apiClient.post<ApiResponse<string>>(`${EMAIL_BASE_URL}/resend-verification`, null, {
      params: { email },
    })
    return response.data.message
  },
}
