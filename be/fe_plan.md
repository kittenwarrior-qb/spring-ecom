# Frontend Plan: Inventory Management & Profit Analytics

Kê hoach xây dng giao dien qun tri cho Inventory Management & Profit Analytics, tuan theo cau truc Frontend hien tai (TanStack Router, React Query, shadcn/ui).

---

## Cau truc Frontend hien tai

```
fe/src/
  api/                    -> API layer (direct Core calls port 8081)
  hooks/                  -> React Query hooks
  types/api.ts            -> TypeScript interfaces
  features/               -> Feature modules
    admin-orders/
    admin-coupons/
    products/
    ...
  routes/admin/           -> TanStack Router file-based
  components/layout/
    data/sidebar-data.ts  -> Sidebar menu with permission filtering
```

**Pattern:**
- API: `api/*.api.ts` goi truc tiep Core (8081)
- Hooks: `hooks/use-*.ts` voi React Query
- Features: `features/<name>/index.tsx` + components/
- Sidebar: permission filtering qua `item.permission`

---

## Phase 1 - Supplier Management UI

### 1.1 Types (`types/api.ts`)
Them interfaces:
```ts
export interface SupplierResponse {
  id: number
  name: string
  code: string
  contactPerson: string | null
  email: string | null
  phone: string | null
  address: string | null
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface SupplierRequest {
  name: string
  code: string
  contactPerson?: string
  email?: string
  phone?: string
  address?: string
  isActive?: boolean
}
```

### 1.2 API Layer (`api/supplier.api.ts`)
```ts
const ADMIN_SUPPLIER_URL = 'http://localhost:8081/v1/api/admin/suppliers'

export const supplierApi = {
  getAll: (page, size, keyword?, isActive?) -> PageResponse<SupplierResponse>
  getById: (id) -> SupplierResponse
  create: (data: SupplierRequest) -> SupplierResponse
  update: (id, data: SupplierRequest) -> SupplierResponse
  delete: (id) -> void
}
```

### 1.3 Hooks (`hooks/use-supplier.ts`)
```ts
export const supplierKeys = {
  all: ['suppliers'],
  list: (page, size, filters) => [...supplierKeys.all, 'list', { page, size, filters }],
  detail: (id) => [...supplierKeys.all, 'detail', id],
}

export function useSuppliers(params, options?)
export function useSupplier(id)
export function useCreateSupplier()
export function useUpdateSupplier()
export function useDeleteSupplier()
```

### 1.4 Feature (`features/admin-suppliers/`)
```
admin-suppliers/
  index.tsx              -> Main page
  components/
    suppliers-table.tsx  -> DataTable
    supplier-form.tsx    -> Dialog/Drawer form
    supplier-columns.tsx -> Table columns
```

### 1.5 Route (`routes/admin/suppliers/index.tsx`)
```ts
export const Route = createFileRoute('/admin/suppliers/')({
  component: AdminSuppliers,
})
```

### 1.6 Sidebar (`sidebar-data.ts`)
Them item:
```ts
{
  title: 'Nha cung cap',
  url: '/admin/suppliers/',
  icon: Truck,
  permission: 'SUPPLIER_VIEW',
}
```

---

## Phase 2 - Purchase Order Management UI

### 2.1 Types
```ts
export type PurchaseOrderStatus = 'DRAFT' | 'CONFIRMED' | 'RECEIVED' | 'PARTIALLY_RECEIVED' | 'CANCELLED'

export interface PurchaseOrderItemResponse {
  id: number
  productId: number
  productTitle: string
  quantityOrdered: number
  quantityReceived: number
  costPrice: number
  subtotal: number
}

export interface PurchaseOrderResponse {
  id: number
  poNumber: string
  supplierId: number
  supplierName: string
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
  quantityOrdered: number
  costPrice: number
}

export interface ReceiveGoodsRequest {
  items: { productId: number; quantityReceived: number }[]
}
```

### 2.2 API Layer (`api/purchase-order.api.ts`)
```ts
const ADMIN_PO_URL = 'http://localhost:8081/v1/api/admin/inventory/purchase-orders'

export const purchaseOrderApi = {
  getAll: (page, size, status?, supplierId?) -> PageResponse<PurchaseOrderResponse>
  getById: (id) -> PurchaseOrderResponse
  create: (data) -> PurchaseOrderResponse
  update: (id, data) -> PurchaseOrderResponse
  confirm: (id) -> PurchaseOrderResponse
  receive: (id, data: ReceiveGoodsRequest) -> PurchaseOrderResponse
  cancel: (id) -> void
}
```

### 2.3 Hooks (`hooks/use-purchase-order.ts`)
```ts
export const poKeys = { all: ['purchase-orders'], ... }

export function usePurchaseOrders(params, options?)
export function usePurchaseOrder(id)
export function useCreatePurchaseOrder()
export function useUpdatePurchaseOrder()
export function useConfirmPurchaseOrder()
export function useReceiveGoods()
export function useCancelPurchaseOrder()
```

### 2.4 Feature (`features/admin-purchase-orders/`)
```
admin-purchase-orders/
  index.tsx                    -> List page
  create.tsx                   -> Create page
  detail.tsx                   -> Detail page
  components/
    purchase-orders-table.tsx
    purchase-order-form.tsx    -> Dynamic items form
    po-items-table.tsx
    receive-goods-dialog.tsx
    po-columns.tsx
```

### 2.5 Routes
```
routes/admin/purchase-orders/
  index.tsx    -> List
  create.tsx   -> Create
  $id.tsx      -> Detail
  $id.edit.tsx -> Edit
```

### 2.6 Sidebar
```ts
{
  title: 'Don nhap hang',
  url: '/admin/purchase-orders/',
  icon: ClipboardList,
  permission: 'PURCHASE_ORDER_VIEW',
}
```

---

## Phase 3 - Inventory Movement UI

### 3.1 Types
```ts
export type MovementType = 'PURCHASE_IN' | 'SALE_OUT' | 'RETURN_IN' | 'ADJUSTMENT' | 'RESERVATION' | 'RESERVATION_RELEASE'

export interface InventoryTransactionResponse {
  id: number
  productId: number
  productName: string
  movementType: MovementType
  quantity: number  // positive = in, negative = out
  costPrice: number | null
  referenceType: 'PURCHASE_ORDER' | 'ORDER' | 'MANUAL' | null
  referenceId: number | null
  stockBefore: number
  stockAfter: number
  note: string | null
  createdBy: number | null
  createdAt: string
}
```

### 3.2 API Layer (`api/inventory.api.ts`)
```ts
const ADMIN_INV_URL = 'http://localhost:8081/v1/api/admin/inventory'

export const inventoryApi = {
  getMovements: (page, size, productId?, movementType?) -> PageResponse<InventoryTransactionResponse>
  getProductHistory: (productId, page, size) -> PageResponse<InventoryTransactionResponse>
  adjustStock: (productId, quantity, note) -> InventoryTransactionResponse
}
```

### 3.3 Hooks (`hooks/use-inventory.ts`)
```ts
export const invKeys = { all: ['inventory'], ... }

export function useInventoryMovements(params)
export function useProductInventoryHistory(productId, params)
export function useAdjustStock()
```

### 3.4 Feature (`features/admin-inventory/`)
```
admin-inventory/
  index.tsx                    -> Movements list
  components/
    movements-table.tsx
    movement-columns.tsx       -> With reference links
    stock-adjust-dialog.tsx
```

### 3.5 Route
```
routes/admin/inventory/
  index.tsx -> Movements list
```

### 3.6 Sidebar
```ts
{
  title: 'Lich su kho',
  url: '/admin/inventory/',
  icon: History,
  permission: 'INVENTORY_VIEW',
}
```

---

## Phase 4 - Cost & Profit UI Updates

### 4.1 Update Product Form
Them field `costPrice` vao ProductForm (chi admin thay, read-only hoac editable).

### 4.2 Update Order Detail
Them cot `costPrice` vao OrderItemsTable (chi admin thay).

### 4.3 Types
```ts
// Them vao ProductResponse
costPrice: number | null

// Them vao OrderItemResponse
costPrice: number | null
profit: number | null  // (price - costPrice) * quantity
```

---

## Phase 5 - Statistics Dashboard UI

### 5.1 Types
```ts
export interface DashboardSummary {
  totalRevenue: number
  totalCogs: number
  grossProfit: number
  profitMargin: number
  orderCount: number
  avgOrderValue: number
  topProducts: TopProduct[]
  revenueByCategory: RevenueByCategory[]
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
```

### 5.2 API Layer (`api/statistics.api.ts`)
```ts
const ADMIN_STATS_URL = 'http://localhost:8081/v1/api/admin/statistics'

export const statisticsApi = {
  getDashboard: (period: 'daily' | 'weekly' | 'monthly') -> DashboardSummary
  getRevenue: (from, to, granularity) -> PeriodStatistics[]
  getProfit: (from, to, granularity) -> PeriodStatistics[]
  getTopProducts: (period, limit) -> TopProduct[]
  getRevenueByCategory: (period) -> RevenueByCategory[]
  getInventoryValuation: () -> InventoryValuation
}
```

### 5.3 Hooks (`hooks/use-statistics.ts`)
```ts
export const statsKeys = { all: ['statistics'], ... }

export function useDashboard(period)
export function useRevenue(params)
export function useProfit(params)
export function useTopProducts(period, limit)
export function useRevenueByCategory(period)
export function useInventoryValuation()
```

### 5.4 Feature (`features/admin-statistics/`)
```
admin-statistics/
  index.tsx                    -> Dashboard page
  components/
    summary-cards.tsx          -> Revenue, COGS, Profit cards
    revenue-chart.tsx          -> Line/Area chart
    profit-chart.tsx           -> Combo chart
    top-products-table.tsx
    category-revenue-chart.tsx -> Pie/Bar chart
    inventory-valuation-card.tsx
    period-picker.tsx          -> Daily/Weekly/Monthly
```

### 5.5 Route
```
routes/admin/statistics/
  index.tsx -> Statistics dashboard
```

### 5.6 Sidebar
```ts
{
  title: 'Thong ke',
  url: '/admin/statistics/',
  icon: BarChart3,
  permission: 'STATISTICS_VIEW',
}
```

### 5.7 Update Admin Orders Page
- Thay `AnalyticsCards` hien tai (computed from orders) bang API statistics
- Hoac giu nguyen, them tab "Thong ke" rieng

---

## Sidebar Data Update

Cap nhat `sidebar-data.ts`:

```ts
navGroups: [
  {
    title: 'Quan Tri',
    items: [
      { title: 'Don Hang', url: '/admin/', icon: LayoutDashboard },
      { title: 'San Pham', url: '/admin/products/', icon: ShoppingBag },
      { title: 'Danh Muc', url: '/admin/categories/', icon: FolderOpen },
      { title: 'Ma Giam Gia', url: '/admin/coupons/', icon: Ticket },
      { title: 'Nguoi Dung', url: '/admin/users/', icon: Users },
      { title: 'Phan Quyen', url: '/admin/roles/', icon: Shield, permission: 'ROLE_VIEW' },
    ],
  },
  {
    title: 'Kho Hang',  // NEW GROUP
    items: [
      { title: 'Nha Cung Cap', url: '/admin/suppliers/', icon: Truck, permission: 'SUPPLIER_VIEW' },
      { title: 'Don Nhap Hang', url: '/admin/purchase-orders/', icon: ClipboardList, permission: 'PURCHASE_ORDER_VIEW' },
      { title: 'Lich Su Kho', url: '/admin/inventory/', icon: History, permission: 'INVENTORY_VIEW' },
    ],
  },
  {
    title: 'Bao Cao',  // NEW GROUP
    items: [
      { title: 'Thong Ke', url: '/admin/statistics/', icon: BarChart3, permission: 'STATISTICS_VIEW' },
    ],
  },
]
```

---

## Implementation Order

1. **Phase 1** - Supplier (don nhat, khoi dong nhanh)
2. **Phase 2** - Purchase Order (phuc tap nhat, can supplier)
3. **Phase 3** - Inventory Movement (read-only, nhanh)
4. **Phase 5** - Statistics (hap dan, charts)
5. **Phase 4** - Cost/Profit updates (cap nhat nho)

---

## Tech Stack

- **State**: React Query (TanStack Query)
- **Form**: React Hook Form + Zod
- **Table**: TanStack Table + DataTable
- **Charts**: Recharts
- **Date**: date-fns
- **Toast**: sonner
- **Format**: Intl.NumberFormat (VND)
