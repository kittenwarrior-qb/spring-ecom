// Generic API Response wrapper from backend
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// User role enum matching backend
export type UserRole = 'ADMIN' | 'USER' | 'SELLER'

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
  role?: UserRole
  roleName?: string
  roleId?: number
  roles?: string[]
  permissions: string[]
}

export interface AuthResponse {
  accessToken: string | null
  refreshToken: string | null
  message?: string
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
  roles: string[]
  permissions: string[]
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
  // User info fields (from user_info table)
  firstName: string | null
  lastName: string | null
  phoneNumber: string | null
  dateOfBirth: string | null
  avatarUrl: string | null
  address: string | null
  ward: string | null
  district: string | null
  city: string | null
  postalCode: string | null
}

export interface AssignUserRoleRequest {
  roleId: number
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
  costPrice?: number
  stockQuantity?: number
  coverImageUrl?: string
  isBestseller?: boolean
  isActive?: boolean
  categoryId?: number
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
  costPrice: number | null  // Gia von - admin only
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
  categoryId: number | null
  categoryName: string | null
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
  address: string | null
  ward: string | null
  district: string | null
  city: string | null
  postalCode: string | null
  roles: string[]
  permissions: string[]
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
  address?: string
  ward?: string
  district?: string
  city?: string
  postalCode?: string
}

export interface UpdateAvatarRequest {
  avatarUrl: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

// User Session types
export interface UserSessionResponse {
  userId: number
  email: string
  role: UserRole
  roleName?: string
  roleId?: number
  firstName: string | null
  lastName: string | null
  phoneNumber: string | null
  address: string | null
  city: string | null
  district: string | null
  ward: string | null
}

// Order types
export type OrderStatus = 
  | 'PENDING' 
  | 'PENDING_STOCK' 
  | 'STOCK_RESERVED' 
  | 'STOCK_FAILED' 
  | 'CONFIRMED' 
  | 'SHIPPED' 
  | 'DELIVERED' 
  | 'CANCELLED' 
  | 'PARTIALLY_CANCELLED'
export type PaymentMethod = 'COD' | 'PAYOS' | 'BANK_TRANSFER'
export type PaymentStatus = 'UNPAID' | 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED'

export interface OrderResponse {
  id: number
  orderNumber: string
  userId: number
  userEmail: string
  status: OrderStatus
  paymentStatus: PaymentStatus
  subtotal: number
  shippingFee: number
  discount: number
  total: number
  refundedAmount: number
  paymentMethod: PaymentMethod
  shippingAddress: string
  shippingCity: string
  shippingDistrict: string
  shippingWard: string
  recipientName: string
  recipientPhone: string
  note: string | null
  couponCode: string | null
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
  cancelledQuantity: number
  price: number
  costPrice: number | null  // Gia von - admin only
  subtotal: number
  profit: number | null  // Loi nhuan - admin only
  status: string
  createdAt: string
  cancelledAt: string | null
}

export interface OrderDetailResponse extends OrderResponse {
  items: OrderItemResponse[]
}

// Order Statistics
export interface OrderStatistics {
  totalOrders: number
  totalRevenue: number
  ordersByStatus: Record<string, number>
  revenueByPeriod: Record<string, number>
}

// Role & Permission types
export interface PermissionResponse {
  id: number
  name: string
}

export interface RoleResponse {
  id: number
  name: string
  permissions: string[]
}

export interface CreateRoleRequest {
  name: string
  permissionIds: number[]
}

export interface AssignPermissionsRequest {
  permissionIds: number[]
}

export interface CreatePermissionRequest {
  name: string
}

// Coupon types
export type DiscountType = 'PERCENTAGE' | 'FIXED_AMOUNT'
export type NotificationType = 'NONE' | 'BROADCAST' | 'TARGETED'

export interface CouponRequest {
  code: string
  description?: string
  discountType: DiscountType
  discountValue: number
  minOrderValue?: number
  maxDiscount?: number
  usageLimit?: number
  startDate: string
  endDate: string
  isActive?: boolean
  notificationType?: NotificationType
  targetUserIds?: number[]
}

export interface CouponResponse {
  id: number
  code: string
  description: string | null
  discountType: DiscountType
  discountValue: number
  minOrderValue: number
  maxDiscount: number | null
  usageLimit: number | null
  usedCount: number
  remainingUses: number | null
  startDate: string
  endDate: string
  isActive: boolean
  isValid: boolean
  createdAt: string
  updatedAt: string
}

export interface CouponValidationRequest {
  code: string
  orderTotal: number
}

export interface CouponValidationResponse {
  valid: boolean
  message: string
  coupon: CouponResponse | null
  discountAmount: number
}

// File types
export interface FileUploadResponse {
  filename: string
  url: string
}

export interface FileItem {
  filename: string
  url: string
}

export interface FileListResponse {
  files: FileItem[]
  count: number
}

// Supplier types
export interface SupplierResponse {
  id: number
  name: string
  contactName: string | null
  email: string | null
  phone: string | null
  address: string | null
  note: string | null
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface SupplierRequest {
  name: string
  contactName?: string
  email?: string
  phone?: string
  address?: string
  note?: string
  isActive?: boolean
}

// Purchase Order types
export type PurchaseOrderStatus = 'DRAFT' | 'CONFIRMED' | 'RECEIVED' | 'PARTIALLY_RECEIVED' | 'CANCELLED'

export interface PurchaseOrderItemResponse {
  id: number
  productId: number
  productTitle: string | null
  quantity: number
  unitPrice: number
  totalPrice: number
}

export interface PurchaseOrderResponse {
  id: number
  poNumber: string
  supplierId: number
  supplierName: string | null
  status: PurchaseOrderStatus
  totalAmount: number
  note: string | null
  expectedDate: string | null
  receivedDate: string | null
  createdBy: number | null
  createdAt: string
  updatedAt: string
  items: PurchaseOrderItemResponse[]
}

export interface CreatePurchaseOrderRequest {
  supplierId: number
  note?: string
  expectedDate?: string
  items: PurchaseOrderItemRequest[]
}

export interface PurchaseOrderItemRequest {
  productId: number
  quantity: number
  unitPrice: number
}

// Inventory Transaction types
export type MovementType = 'IMPORT' | 'EXPORT' | 'SALE_OUT' | 'RETURN' | 'ADJUSTMENT' | 'RESERVATION' | 'RESERVATION_RELEASE'

export interface InventoryTransactionResponse {
  id: number
  productId: number
  productName: string | null
  productImage: string | null
  movementType: MovementType
  quantity: number
  costPrice: number | null
  stockBefore: number | null
  stockAfter: number | null
  referenceType: string | null
  referenceId: number | null
  note: string | null
  createdBy: number | null
  createdAt: string
}

// Statistics types
export interface DashboardSummary {
  totalOrders: number
  totalRevenue: number
  totalCost: number
  totalProfit: number
  profitMargin: number
  todayRevenue: number
  averageOrderValue: number
  pendingOrders: number
  confirmedOrders: number
  shippedOrders: number
  deliveredOrders: number
  cancelledOrders: number
  partiallyCancelledOrders: number
  totalProducts: number
  lowStockProducts: number
  outOfStockProducts: number
  totalSuppliers: number
  pendingPurchaseOrders: number
  inventoryValuation: number
}

export interface TopProduct {
  productId: number
  productTitle: string
  quantitySold: number
  revenue: number
  profit: number
}

export interface RevenueByCategory {
  categoryId: number
  categoryName: string
  revenue: number
  percentage: number
}

export interface PeriodStatistics {
  date: string
  revenue: number
  cogs: number
  profit: number
  orderCount: number
}

export interface InventoryValuation {
  totalValue: number
  productCount: number
  lowStockCount: number
}

// Notification types
export interface NotificationResponse {
  id: number
  userId: number | null  // null for broadcast notifications
  type: string
  title: string
  message: string
  referenceId: number | null
  referenceType: string | null
  imageUrl: string | null
  actionUrl: string | null
  isRead: boolean
  isBroadcast: boolean  // true if this is a broadcast notification
  createdAt: string
}
