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
  user: UserInfo
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
