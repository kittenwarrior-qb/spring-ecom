// Generic API Response wrapper from backend
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// User role enum matching backend
export type UserRole = 'ADMIN' | 'USER'

// Auth types
export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  firstName?: string
  lastName?: string
  phoneNumber?: string
}

export interface UserInfo {
  id: number
  username: string
  email: string
  firstName: string | null
  lastName: string | null
  role: UserRole
}

export interface AuthResponse {
  accessToken: string
  expiresIn: number
  userInfo: UserInfo
}

// Category types
export interface CategoryRequest {
  name: string
  slug: string
  description?: string
  parentId?: number | null
  displayOrder?: number | null
  isActive?: boolean
}

export interface CategoryResponse {
  id: number
  name: string
  slug: string
  description: string | null
  parentId: number | null
  parentName: string | null
  displayOrder: number | null
  isActive: boolean
  createdAt: string
  updatedAt: string
}

// User types
export interface UserRequest {
  username: string
  email: string
  password: string
}

export interface UserResponse {
  id: number
  username: string
  email: string
  password: string
  firstName: string | null
  lastName: string | null
  phoneNumber: string | null
  dateOfBirth: string | null
  avatarUrl: string | null
  role: UserRole
  isEmailVerified: boolean
  emailVerificationToken: string | null
  emailVerificationTokenExpiry: string | null
  passwordResetToken: string | null
  passwordResetTokenExpiry: string | null
  lastLoginAt: string | null
  isActive: boolean
  createdAt: string
  updatedAt: string
  deletedAt: string | null
}

// Product types
export interface ProductRequest {
  title: string
  slug?: string
  author?: string
  publisher?: string
  publicationYear?: number
  language?: string
  pages?: number
  format?: string
  description?: string
  price: number
  discountPrice?: number
  stockQuantity?: number
  coverImageUrl?: string
  isBestseller?: boolean
  isActive?: boolean
}

export interface ProductResponse {
  id: number
  title: string
  slug: string
  author: string | null
  publisher: string | null
  publicationYear: number | null
  language: string | null
  pages: number | null
  format: string | null
  description: string | null
  price: number
  discountPrice: number | null
  stockQuantity: number
  coverImageUrl: string | null
  isBestseller: boolean
  isActive: boolean
  viewCount: number
  soldCount: number
  ratingAverage: number
  ratingCount: number
  createdAt: string
  updatedAt: string
}

// Cart types
export interface CartItemResponse {
  id: number
  cartId: number
  productId: number
  quantity: number
  price: number
  createdAt: string
  updatedAt: string
}

export interface AddToCartRequest {
  productId: number
  quantity: number
}

export interface UpdateCartItemRequest {
  quantity: number
}

// Pagination types
export interface PageRequest {
  page?: number
  size?: number
  sort?: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
  pageable: {
    pageNumber: number
    pageSize: number
    sort: {
      sorted: boolean
      unsorted: boolean
      empty: boolean
    }
  }
}

// User Profile types
export interface UserProfileResponse {
  id: number
  username: string
  email: string
  firstName: string | null
  lastName: string | null
  phoneNumber: string | null
  dateOfBirth: string | null
  avatarUrl: string | null
  role: UserRole
  isEmailVerified: boolean
  isActive: boolean
  lastLoginAt: string | null
  createdAt: string
  updatedAt: string
}

export interface UpdateProfileRequest {
  firstName?: string
  lastName?: string
  phoneNumber?: string
  dateOfBirth?: string
}

export interface UpdateAvatarRequest {
  avatarUrl: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

// Order types
export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED'
export type PaymentMethod = 'COD' | 'PAYOS' | 'BANK_TRANSFER'

export interface OrderResponse {
  id: number
  orderNumber: string
  userId: number
  userEmail: string
  status: OrderStatus
  subtotal: number
  shippingFee: number
  discount: number
  total: number
  paymentMethod: PaymentMethod
  shippingAddress: string
  shippingCity: string
  shippingDistrict: string
  shippingWard: string
  recipientName: string
  recipientPhone: string
  note: string | null
  items: OrderItemResponse[]
  createdAt: string
  updatedAt: string
  cancelledAt: string | null
}

export interface OrderItemResponse {
  id: number
  orderId: number
  productId: number
  productTitle: string
  productImage: string | null
  quantity: number
  price: number
  subtotal: number
  createdAt: string
}

export interface OrderDetailResponse extends OrderResponse {
  items: OrderItemResponse[]
}
