import type { AxiosError } from 'axios'

export interface ApiErrorResponse {
  code: number
  message: string
  data: unknown
}

export function getErrorMessage(error: unknown): string {
  if (!error) return 'Có lỗi không xác định xảy ra'

  // Handle Axios errors
  if (typeof error === 'object' && error !== null && 'response' in error) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    
    if (axiosError.response?.data?.message) {
      const backendMessage = axiosError.response.data.message
      
      // Handle specific error types
      if (backendMessage.includes('Insufficient stock for product:')) {
        const productId = backendMessage.match(/product:\s*(\d+)/)?.[1]
        if (productId) {
          return `Sản phẩm ID ${productId} không đủ hàng trong kho. Vui lòng giảm số lượng hoặc chọn sản phẩm khác.`
        } else {
          return 'Một số sản phẩm trong giỏ hàng không đủ số lượng trong kho.'
        }
      }
      
      // Handle other specific errors
      if (backendMessage.includes('User not found')) {
        return 'Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.'
      }
      
      if (backendMessage.includes('Cart is empty')) {
        return 'Giỏ hàng trống. Vui lòng thêm sản phẩm vào giỏ hàng.'
      }
      
      if (backendMessage.includes('Product not found')) {
        return 'Không tìm thấy sản phẩm. Sản phẩm có thể đã bị xóa.'
      }
      
      // Return original message for other cases
      return backendMessage
    }
    
    // Handle HTTP status codes
    if (axiosError.response?.status === 401) {
      return 'Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.'
    }
    
    if (axiosError.response?.status === 403) {
      return 'Bạn không có quyền thực hiện hành động này.'
    }
    
    if (axiosError.response?.status === 404) {
      return 'Không tìm thấy tài nguyên yêu cầu.'
    }
    
    if (axiosError.response && axiosError.response.status >= 500) {
      return 'Lỗi máy chủ. Vui lòng thử lại sau.'
    }
  }
  
  // Handle network errors
  if (typeof error === 'object' && error !== null && 'message' in error) {
    const errorMessage = (error as Error).message
    if (errorMessage.includes('Network Error') || errorMessage.includes('timeout')) {
      return 'Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet và thử lại.'
    }
  }
  
  // Fallback
  return 'Có lỗi xảy ra. Vui lòng thử lại sau.'
}

export function isInsufficientStockError(error: unknown): { isStockError: boolean; productId?: string } {
  if (typeof error === 'object' && error !== null && 'response' in error) {
    const axiosError = error as AxiosError<ApiErrorResponse>
    const message = axiosError.response?.data?.message
    
    if (message?.includes('Insufficient stock for product:')) {
      const productId = message.match(/product:\s*(\d+)/)?.[1]
      return { isStockError: true, productId }
    }
  }
  
  return { isStockError: false }
}