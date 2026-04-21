import { AxiosError } from 'axios'
import { toast } from 'sonner'
import type { ApiResponse } from '@/types/api'
import { isNetworkError, getNetworkErrorMessage } from './request-utils'

export interface ServerValidationErrors {
  [key: string]: string
}

export function handleServerError(error: unknown, onValidationErrors?: (errors: ServerValidationErrors) => void) {
  // eslint-disable-next-line no-console
  console.log(error)

  // Handle network/timeout errors first
  if (isNetworkError(error)) {
    toast.error(getNetworkErrorMessage(error))
    return
  }

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

    // Handle validation errors from backend
    if (apiResponse?.data && typeof apiResponse.data === 'object') {
      const validationErrors = apiResponse.data as ServerValidationErrors
      if (onValidationErrors && Object.keys(validationErrors).length > 0) {
        onValidationErrors(validationErrors)
        return // Don't show toast, form will show field errors
      }
    }
  }

  toast.error(errMsg)
}
