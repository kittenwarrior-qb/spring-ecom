import { AxiosError } from 'axios'

/**
 * Check if error is due to client-side timeout or network issues
 */
export function isNetworkError(error: unknown): boolean {
  if (error instanceof AxiosError) {
    // Network timeout or connection aborted
    if (error.code === 'ECONNABORTED' || error.code === 'TIMEOUT') {
      return true
    }
    
    // No response received (network error)
    if (!error.response) {
      return true
    }
    
    // Client cancelled request
    if (error.message?.includes('canceled')) {
      return true
    }
  }
  
  return false
}

/**
 * Check if error is due to server timeout
 */
export function isServerTimeout(error: unknown): boolean {
  if (error instanceof AxiosError) {
    return error.response?.status === 408 || error.response?.status === 504
  }
  return false
}

/**
 * Get user-friendly error message for network/timeout errors
 */
export function getNetworkErrorMessage(error: unknown): string {
  if (isNetworkError(error)) {
    return 'Kết nối mạng không ổn định. Vui lòng thử lại.'
  }
  
  if (isServerTimeout(error)) {
    return 'Máy chủ phản hồi chậm. Vui lòng thử lại sau.'
  }
  
  return 'Có lỗi xảy ra. Vui lòng thử lại.'
}