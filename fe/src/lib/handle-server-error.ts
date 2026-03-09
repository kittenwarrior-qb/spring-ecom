import { AxiosError } from 'axios'
import { toast } from 'sonner'
import type { ApiResponse } from '@/types/api'

export function handleServerError(error: unknown) {
  // eslint-disable-next-line no-console
  console.log(error)

  let errMsg = 'Something went wrong!'

  if (
    error &&
    typeof error === 'object' &&
    'status' in error &&
    Number(error.status) === 204
  ) {
    errMsg = 'Content not found.'
  }

  if (error instanceof AxiosError) {
    const apiResponse = error.response?.data as ApiResponse<unknown> | undefined
    // Handle ApiResponse format from backend
    if (apiResponse?.message) {
      errMsg = apiResponse.message
    } else if (error.response?.data?.title) {
      errMsg = error.response.data.title
    } else if (error.response?.status === 401) {
      errMsg = 'Unauthorized. Please login again.'
    } else if (error.response?.status === 403) {
      errMsg = 'Access denied. You do not have permission.'
    } else if (error.response?.status === 404) {
      errMsg = 'Resource not found.'
    } else if (error.response?.status === 500) {
      errMsg = 'Server error. Please try again later.'
    }
  }

  toast.error(errMsg)
}
