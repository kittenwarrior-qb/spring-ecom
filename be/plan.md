Plan: Inventory Management & Profit Analytics for Spring Boot E-Commerce
This plan adds two major feature areas to the existing bookstore backend: (1) full inventory/stock management (suppliers, purchase orders, goods receipt, stock-in/out tracking, cost-price-per-batch, audit trail) and (2) profit & statistics (COGS calculation, real profit reporting, dashboard analytics). All work goes into the core module with Flyway migrations in migration-postgres, following existing patterns (domain records → JPA entities → MapStruct mappers → Query/Command services → UseCase interface → Admin API controller).
 
Phase 1 — Supplier Management (Foundation)
Flyway migration V26__create_suppliers_table.sql in migration-postgres/…/db/migration: create suppliers table (id, name, code UNIQUE, contact_person, email, phone, address, is_active, created_at, updated_at, deleted_at). Add SUPPLIER_VIEW, SUPPLIER_CREATE, SUPPLIER_UPDATE, SUPPLIER_DELETE permissions to permissions table and assign to ADMIN role.
Domain record Supplier in new package domain/supplier/ following the Product record pattern.
Entity + Repository + Mapper in repository/database/supplier/: create SupplierEntity (extends BaseAuditEntity), SupplierRepository (extends JpaRepository), SupplierEntityMapper (extends BaseEntityMapper), matching the existing product pattern.
Service layer in service/supplier/: SupplierUseCase interface, SupplierQueryService, SupplierCommandService, SupplierUseCaseService — CRUD + search + soft-delete, matching the product service pattern.
Admin REST API in controller/api/admin/supplier/: AdminSupplierAPI interface + AdminSupplierController, request/response DTOs. Endpoints:
GET /v1/api/admin/suppliers (paginated, search, filter)
GET /v1/api/admin/suppliers/{id}
POST /v1/api/admin/suppliers
PUT /v1/api/admin/suppliers/{id}
DELETE /v1/api/admin/suppliers/{id}
 
Phase 2 — Purchase Orders & Goods Receipt (Nhập hàng)
Flyway migration V27__create_purchase_orders_tables.sql: create two tables:
purchase_orders (id, po_number UNIQUE, supplier_id FK, status enum DRAFT/CONFIRMED/RECEIVED/PARTIALLY_RECEIVED/CANCELLED, total_amount, note, expected_date, received_date, created_by FK→users, created_at, updated_at)
purchase_order_items (id, purchase_order_id FK, product_id FK, quantity_ordered, quantity_received, cost_price DECIMAL, subtotal, created_at, updated_at) Add permissions PURCHASE_ORDER_VIEW/CREATE/UPDATE/DELETE and assign to ADMIN.
Domain records in domain/inventory/: PurchaseOrder, PurchaseOrderItem, PurchaseOrderStatus enum (DRAFT, CONFIRMED, RECEIVED, PARTIALLY_RECEIVED, CANCELLED).
Entity + Repository + Mapper in repository/database/inventory/: PurchaseOrderEntity, PurchaseOrderItemEntity, repositories, MapStruct mappers.
Service layer in service/inventory/: PurchaseOrderUseCase interface, PurchaseOrderCommandService, PurchaseOrderQueryService. Key business logic:
Create PO (DRAFT) → validate supplier exists, generate PO number
Confirm PO → status DRAFT→CONFIRMED
Receive goods → partial or full; for each item increment products.stock_quantity via ProductUseCase.updateProductStock(productId, +receivedQty) and create inventory_transactions record (Phase 3)
Cancel PO → only if DRAFT/CONFIRMED
Admin REST API in controller/api/admin/inventory/: AdminPurchaseOrderAPI + AdminPurchaseOrderController, DTOs. Endpoints:
GET /v1/api/admin/purchase-orders (paginated, filter by status/supplier/date)
GET /v1/api/admin/purchase-orders/{id}
POST /v1/api/admin/purchase-orders (create with items)
PUT /v1/api/admin/purchase-orders/{id} (update draft)
POST /v1/api/admin/purchase-orders/{id}/confirm
POST /v1/api/admin/purchase-orders/{id}/receive (body: items with received quantities)
POST /v1/api/admin/purchase-orders/{id}/cancel
 
Phase 3 — Stock-In/Out Tracking & Inventory Audit Trail
Flyway migration V28__create_inventory_transactions_table.sql: create inventory_transactions table (id, product_id FK, transaction_type enum PURCHASE_IN/SALE_OUT/RETURN_IN/ADJUSTMENT/RESERVATION/RESERVATION_RELEASE, quantity INTEGER — positive for in, negative for out, cost_price DECIMAL nullable, reference_type — PURCHASE_ORDER/ORDER/MANUAL, reference_id BIGINT, stock_before, stock_after, note, created_by FK→users nullable, created_at). Add INVENTORY_VIEW permission.
Domain & Entity in domain/inventory/InventoryTransaction (record) and repository/database/inventory/InventoryTransactionEntity + InventoryTransactionRepository with filtered queries by product_id, transaction_type, date range.
Service: InventoryTransactionService — a cross-cutting service called from:
PurchaseOrderCommandService.receiveGoods() → log PURCHASE_IN entries with cost_price
OrderCommandService.handleStatusChange(DELIVERED) → log SALE_OUT entries
OrderCommandService.cancel() / StockReservationScheduler → log RETURN_IN entries
New manual stock adjustment endpoint → log ADJUSTMENT entries
Modify existing AdminProductController PUT /{productId}/stock to also create an ADJUSTMENT inventory transaction (wrap existing logic, add note and createdBy params).
Admin API for inventory history:
GET /v1/api/admin/inventory/transactions — paginated, filter by product, type, date range
GET /v1/api/admin/inventory/products/{productId}/history — transaction history for one product
 
Phase 4 — Cost Price Tracking Per Batch
Flyway migration V29__add_cost_price_tracking.sql:
Add cost_price column to products table (latest/average cost — used as default for new POs)
Create product_cost_batches table (id, product_id FK, purchase_order_item_id FK, quantity_remaining, cost_price, received_at, created_at) for FIFO/weighted-average cost tracking.
Domain + Entity + Repository for ProductCostBatch in repository/database/inventory/.
Service logic in InventoryTransactionService or a new CostTrackingService:
On goods receipt: insert a product_cost_batches row per PO item.
On sale (order delivered): consume batches FIFO — decrement quantity_remaining from oldest batch and record the COGS per order item.
Add cost_price to the order_items table (migration) so each sold item stores its cost at time of sale.
Flyway migration V30__add_cost_price_to_order_items.sql: ALTER TABLE order_items ADD COLUMN cost_price DECIMAL(10,2). Update OrderItemEntity and OrderItem domain record.
 
Phase 5 — Profit Calculation & Dashboard Analytics
Flyway migration V31__create_statistics_views.sql: create materialized views or helper queries:
Revenue by period (reuse existing DELIVERED orders sum)
COGS by period (SUM(order_items.cost_price * quantity) for delivered orders)
Gross profit = revenue − COGS
Domain models in domain/statistics/: ProfitStatistics, DashboardSummary, ProductPerformance, RevenueByCategory, TopSellingProduct.
Service service/statistics/StatisticsService:
getDashboardSummary(period) → total revenue, COGS, gross profit, order counts, avg order value
getRevenueByPeriod(from, to, granularity) → daily/weekly/monthly breakdown
getProfitByPeriod(from, to, granularity) → revenue, COGS, profit per period
getTopSellingProducts(period, limit)
getRevenueByCategory(period)
getInventoryValuation() → total stock value based on cost batches
Replace the stubbed OrderUseCaseService.getOrderStatistics(period, dateFrom, dateTo) in OrderUseCaseService (currently returns zeros at lines 140–151) to delegate to StatisticsService.
Admin REST API in controller/api/admin/statistics/: AdminStatisticsAPI + AdminStatisticsController. Endpoints:
GET /v1/api/admin/statistics/dashboard?period=monthly — summary card data
GET /v1/api/admin/statistics/revenue?from=&to=&granularity=daily
GET /v1/api/admin/statistics/profit?from=&to=&granularity=daily
GET /v1/api/admin/statistics/top-products?period=monthly&limit=10
GET /v1/api/admin/statistics/revenue-by-category?period=monthly
GET /v1/api/admin/statistics/inventory-valuation
Add STATISTICS_VIEW permission in a migration and assign to ADMIN; protect all statistics endpoints with @PreAuthorize("hasAuthority('STATISTICS_VIEW')").
 
Further Considerations
COGS strategy: Use weighted average cost (simpler) or FIFO (more accurate)? Recommend weighted average for a bookstore since book prices per batch rarely fluctuate much.
Backfill existing data: Existing order_items have no cost_price. Either backfill using current products.price as an estimate, or leave NULL and calculate profit only for orders placed after the feature ships.
Performance for statistics: For large datasets, consider PostgreSQL materialized views refreshed via a scheduler (add to scheduler/ package) instead of computing on-the-fly. This can be deferred and added later if query performance degrades.

tuân thủ những yêu cầu sau:
Service:
- Tách thành 4 file gồm UseCase, UseCaseService, QueryService, CommandService, các file này phân bố rõ từng service và đặt @Transactional trên mọi method, nếu như có service con vd như order => orderItem thì cũng tương tự sẽ tạo 4 file orderItem trong chính file của order
- Các Command và Query Service nếu trong method có gọi tới các Service ngoài thì phải gọi từ UseCase. 
- Các service cần trả về response đã được map thành dto (domain) nhờ EntityMapper thường là toDomain hoặc update chứ không cần set value để đúng dto

Repository:
- Mỗi table/entity sẽ gồm 3 file Entity, Entity Mapper, Repository, đôi khi sẽ có thư mục dao nhằm trả thêm dữ liệu join bảng mà dto không có đủ để đáp ứng vd Category name,...
- Entity Mapper sẽ dung mapstruct và map mọi dữ lieu trong này để tối giản cho service, mapper đôi khi cũng map luôn dao từ repository
- Entity sẽ không chứa các relation như @OneToMany hay @ManyToMany @ManyToOne, mà sẽ để tất cả là @Column để tránh việc phải xử lý nhiều case hơn cho table
- Reposity trong này sẽ sử dụng @Join từ JPA Hypernates để lấy thêm dữ liệu cần trả về, thường response nếu có thêm dữ liệu sẽ lấy từ DAO, trong repository đều sẽ implement pages phân trang của jpa cho các bảng quản trị

Controller:
- Tách thành 2 file gồm API và Controller và 1 folder model, folder thường chứa 4 file RequestDto, ResponseDto,
RequestMapper, ResponseMapper dto là record còn mapper là interface giống như mapper của entity 
- Trong API sẽ là interface chứa phương thức và đường dẫn Mapping, Authorize và rate limit, Controller sẽ là nơi gọi method (UseCase) trong này sẽ không chứa logic gì cả ngoài việc gọi request, response mapper để map dữ liệu trước khi gửi.
- 2 cái Mapper sẽ có các method tương tự như của entitymapper, chứa đầy đủ định nghĩa và sẽ map từ dto nhận được từ service map thành response, không được chưa business logic trong controller