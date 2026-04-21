# Kiến trúc Code-Level Flow

## 1. gRPC Flow — Tiêu chuẩn chung (Landing → Core)

```mermaid
flowchart LR
    subgraph Frontend
        FE[User / Browser :5173]
    end

    subgraph "Landing Service :8080 / :9090"
        direction TB
        RC[REST Controller]
        UC_L[UseCase Interface]
        QS_L[QueryService / CommandService]
        GRPC_CLIENT[GrpcClient\n— BlockingStub]
    end

    subgraph "grpc-proto module"
        PROTO[".proto file\n— generated Stub + Message"]
    end

    subgraph "Core Service :8081 / :9091"
        direction TB
        GRPC_SVC[GrpcService\nextends *ImplBase]
        UC_C[UseCase Interface]
        QS_C[QueryService / CommandService]
        REPO[Repository\n— JPA / Database]
    end

    FE -->|"HTTP REST"| RC
    RC --> UC_L --> QS_L --> GRPC_CLIENT
    GRPC_CLIENT -->|"gRPC call\n(protobuf binary)"| PROTO
    PROTO -->|"generated stub"| GRPC_SVC
    GRPC_SVC --> UC_C --> QS_C --> REPO
    REPO -->|"response"| QS_C --> UC_C --> GRPC_SVC
    GRPC_SVC -->|"proto response"| GRPC_CLIENT
    GRPC_CLIENT --> QS_L --> UC_L --> RC
    RC -->|"JSON"| FE
```

---

## 2. Từng gRPC Service — Class cụ thể

```mermaid
flowchart TB
    subgraph "ProductService Flow"
        direction LR
        P1["ProductController\nAdminProductController"]
        P2["ProductUseCase\nAdminProductUseCase"]
        P3["ProductQueryService\nAdminProductQueryService\nAdminProductCommandService"]
        P4["ProductGrpcClient\n→ ProductServiceBlockingStub"]
        P5["ProductGrpcService\nextends ProductServiceImplBase"]
        P6["ProductUseCase\nCategoryUseCase"]
        P7["ProductQueryService\nProductCommandService"]
        P8["ProductRepository"]
    end
    P1 --> P2 --> P3 --> P4 -->|"gRPC"| P5 --> P6 --> P7 --> P8

    subgraph "OrderService Flow"
        direction LR
        O1["OrderController\nAdminOrderController"]
        O2["OrderUseCase"]
        O3["OrderQueryService\nOrderCommandService"]
        O4["OrderGrpcClient\n→ OrderServiceBlockingStub"]
        O5["OrderGrpcService\nextends OrderServiceImplBase"]
        O6["OrderUseCase"]
        O7["OrderQueryService\nOrderCommandService"]
        O8["OrderRepository"]
    end
    O1 --> O2 --> O3 --> O4 -->|"gRPC"| O5 --> O6 --> O7 --> O8

    subgraph "UserService Flow"
        direction LR
        U1["UserController\nAuthController"]
        U2["UserUseCase\nAuthUseCase"]
        U3["UserQueryService\nAuthCommandService"]
        U4["UserGrpcClient\n→ UserServiceBlockingStub"]
        U5["UserGrpcService\nextends UserServiceImplBase"]
        U6["UserUseCase"]
        U7["UserQueryService\nUserCommandService"]
        U8["UserRepository"]
    end
    U1 --> U2 --> U3 --> U4 -->|"gRPC"| U5 --> U6 --> U7 --> U8

    subgraph "CouponService Flow"
        direction LR
        C1["CouponController"]
        C2["CouponUseCase"]
        C3["CouponQueryService"]
        C4["CouponGrpcClient\n→ CouponServiceBlockingStub"]
        C5["CouponGrpcService\nextends CouponServiceImplBase"]
        C6["CouponUseCase"]
        C7["CouponQueryService\nCouponCommandService"]
        C8["CouponRepository"]
    end
    C1 --> C2 --> C3 --> C4 -->|"gRPC"| C5 --> C6 --> C7 --> C8
```

---

## 3. gRPC ngược — NotificationService (Core → Landing)

```mermaid
flowchart LR
    subgraph "Core Service"
        direction TB
        SVC["OrderEventService\n/ NotificationCommandService"]
        NUC["NotificationUseCase"]
        NGC["NotificationGrpcClient\n→ NotificationServiceBlockingStub"]
    end

    subgraph "grpc-proto"
        PROTO["notification_service.proto"]
    end

    subgraph "Landing Service"
        direction TB
        NGS["NotificationGrpcService\nextends NotificationServiceImplBase"]
        NUC_L["NotificationUseCase"]
        NCS_L["NotificationCommandService"]
        MQTT["NotificationMqttPublisherImpl\n→ MqttPahoMessageHandler"]
    end

    subgraph "EMQX :1883"
        BROKER["EMQX Broker"]
    end

    subgraph "Frontend"
        FE["Browser MQTT Client\nsubscribe notifications/#"]
    end

    SVC --> NUC --> NGC -->|"gRPC call"| PROTO --> NGS
    NGS --> NUC_L --> NCS_L --> MQTT
    MQTT -->|"MQTT publish\nnotifications/{userId}/{type}"| BROKER
    BROKER -->|"MQTT deliver"| FE
```

---

## 4. Kafka Flow — Tiêu chuẩn chung

```mermaid
flowchart LR
    subgraph "Landing Service (Producer)"
        direction TB
        OCS["OrderCommandService"]
        KP["OrderKafkaProducerImpl\nimplements OrderKafkaProducer"]
        KT["KafkaTemplate.send()"]
    end

    subgraph "Kafka :9092"
        TOPIC["Topic: order-events"]
    end

    subgraph "Core Service (Consumer)"
        direction TB
        KC["OrderKafkaConsumer\n@KafkaListener"]
        OES["OrderEventService"]
        PUC["ProductUseCase"]
        PCS["ProductCommandService"]
        PR["ProductRepository\nfindByIdWithLock()"]
    end

    OCS -->|"build OrderEvent"| KP --> KT -->|"produce"| TOPIC
    TOPIC -->|"consume"| KC -->|"switch eventType"| OES
    OES --> PUC --> PCS --> PR
```

---

## 5. Kafka — Từng Event Type cụ thể

```mermaid
flowchart TB
    subgraph "Landing produces"
        L_OCS["OrderCommandService"]
        L_KP["OrderKafkaProducerImpl.send()"]
    end

    L_OCS --> L_KP

    subgraph "Kafka Broker"
        T1["order-events"]
        T2["user-events"]
    end

    L_KP --> T1

    subgraph "Core consumes order-events"
        KC["OrderKafkaConsumer.consumeOrderEvent()"]
        
        E1["ORDER_CREATED\n→ OrderEventService.handleOrderCreated()\n→ ProductUseCase.reserveStock()\n→ ProductCommandService.reserveStock()\n→ ProductRepository.findByIdWithLock()"]
        
        E2["ORDER_PAID\n→ handleOrderPaid()\n→ ProductUseCase.deductReservedStock()\n→ ProductCommandService.deductReservedStock()"]
        
        E3["ORDER_CANCELLED\n→ handleOrderCancelled()\n→ ProductUseCase.releaseStock()\n→ ProductCommandService.releaseReservedStock()"]
        
        E4["ORDER_DELIVERED\n→ handleOrderDelivered()\n→ ProductUseCase.updateSoldCount()\n→ OrderUseCase.recordSaleOutTransactions()"]
        
        E5["ORDER_STATUS_CHANGED\n→ handleOrderStatusChanged()\n→ NotificationUseCase.createAndSend()\n→ NotificationGrpcClient → Landing MQTT"]
        
        E6["ORDER_PARTIAL_CANCELLED\n→ handleOrderPartialCancelled()\n→ ProductUseCase.releaseStock()"]
    end

    T1 --> KC
    KC --> E1
    KC --> E2
    KC --> E3
    KC --> E4
    KC --> E5
    KC --> E6

    subgraph "Landing produces user-events"
        L_AUTH["AuthCommandService"]
        L_UKP["UserKafkaProducer.send(REGISTERED)"]
    end

    L_AUTH --> L_UKP --> T2

    subgraph "Core consumes user-events"
        UKC["UserKafkaConsumer.consumeUserEvent()"]
        ECS["EmailCommandService.sendVerificationEmail()"]
    end

    T2 --> UKC --> ECS
```

---

## 6. MQTT Flow — Code-Level

```mermaid
flowchart LR
    subgraph "Core Service"
        C_SVC["OrderEventService\nhoặc NotificationCommandService"]
        C_NUC["NotificationUseCase.createAndSend()"]
        C_DB["NotificationRepository.save()"]
        C_GRPC["NotificationGrpcClient\n.sendToUser() / .broadcast()"]
    end

    subgraph "grpc-proto"
        PROTO["NotificationServiceBlockingStub"]
    end

    subgraph "Landing Service"
        L_GRPC["NotificationGrpcService\n.sendNotification()\n.broadcastNotification()"]
        L_NUC["NotificationUseCase\n.sendToUser() / .broadcast()"]
        L_MQTT["NotificationMqttPublisherImpl\n.publishToUser() / .broadcast()"]
        L_HANDLER["MqttPahoMessageHandler\n.handleMessage()"]
    end

    subgraph "EMQX Broker :1883"
        T_USER["notifications/{userId}/{type}"]
        T_BROADCAST["notifications/broadcast/{type}"]
    end

    subgraph "Frontend :5173"
        FE_SUB["MQTT Client\nsubscribe:\n• notifications/{myId}/#\n• notifications/broadcast/#"]
    end

    C_SVC --> C_NUC --> C_DB
    C_NUC --> C_GRPC -->|"gRPC"| PROTO --> L_GRPC
    L_GRPC --> L_NUC --> L_MQTT --> L_HANDLER
    L_HANDLER -->|"QoS=1"| T_USER
    L_HANDLER -->|"QoS=1"| T_BROADCAST
    T_USER -->|"deliver"| FE_SUB
    T_BROADCAST -->|"deliver"| FE_SUB
```

---

## 7. Tổng quan — 1 hình duy nhất

```mermaid
flowchart TB
    FE["🖥️ Frontend :5173\nReact + Vite"]

    subgraph LANDING["Landing Service :8080 / gRPC :9090"]
        direction TB
        L_REST["REST Controllers\nProductController, OrderController\nUserController, CartController\nAdminProductController, AdminOrderController"]
        L_UC["UseCases\nProductUseCase, OrderUseCase\nAuthUseCase, CartUseCase, CouponUseCase"]
        L_SVC["Services\nQueryService + CommandService"]
        L_GRPC_C["gRPC Clients\nProductGrpcClient, OrderGrpcClient\nUserGrpcClient, CouponGrpcClient"]
        L_GRPC_S["gRPC Server\nNotificationGrpcService"]
        L_KAFKA["Kafka Producer\nOrderKafkaProducerImpl"]
        L_MQTT["MQTT Publisher\nNotificationMqttPublisherImpl"]
        L_DB_L["JPA Repositories\nCartRepository, OrderRepository\nNotificationRepository"]
    end

    subgraph CORE["Core Service :8081 / gRPC :9091"]
        direction TB
        C_REST["REST Controllers\nAdminInventoryController\nAdminStatisticsController\nAdminSupplierController\nAdminDashboardController\nNotificationController"]
        C_GRPC_S["gRPC Servers\nProductGrpcService, OrderGrpcService\nUserGrpcService, CouponGrpcService"]
        C_UC["UseCases\nProductUseCase, OrderUseCase\nInventoryUseCase, StatisticsUseCase\nNotificationUseCase, CouponUseCase"]
        C_SVC["Services\nQueryService + CommandService"]
        C_GRPC_C["gRPC Client\nNotificationGrpcClient"]
        C_KAFKA["Kafka Consumers\nOrderKafkaConsumer → OrderEventService\nUserKafkaConsumer → EmailCommandService"]
        C_DB["JPA Repositories\nProductRepository, OrderRepository\nSupplierRepository, PurchaseOrderRepository\nInventoryMovementRepository\nProductCostBatchRepository"]
    end

    subgraph INFRA["Infrastructure (Docker)"]
        DB[("PostgreSQL :5432")]
        REDIS[("Redis :6379")]
        KAFKA_B["Kafka :9092"]
        EMQX["EMQX :1883"]
        MINIO["MinIO :9000"]
    end

    FE -->|"REST API"| L_REST
    L_REST --> L_UC --> L_SVC
    L_SVC --> L_GRPC_C
    L_SVC --> L_DB_L
    L_SVC --> L_KAFKA

    L_GRPC_C -->|"gRPC (proto)"| C_GRPC_S
    C_GRPC_S --> C_UC --> C_SVC --> C_DB

    C_REST --> C_UC
    C_SVC --> C_GRPC_C
    C_GRPC_C -->|"gRPC (proto)"| L_GRPC_S
    L_GRPC_S --> L_MQTT

    L_KAFKA -->|"produce"| KAFKA_B
    KAFKA_B -->|"consume"| C_KAFKA
    C_KAFKA --> C_UC

    L_MQTT -->|"publish"| EMQX
    EMQX -.->|"deliver"| FE

    L_DB_L --> DB
    C_DB --> DB
    L_SVC -.-> REDIS
    C_SVC -.-> REDIS
    C_SVC -.-> MINIO
```

---

## 8. Luồng thêm vào giỏ hàng và tạo đơn hàng (Code-Level)

```mermaid
sequenceDiagram
    participant FE as Frontend :5173
    participant L_CTRL as OrderController<br/>(Landing)
    participant L_UC as OrderUseCase<br/>(Landing)
    participant L_CMD as OrderCommandService<br/>(Landing)
    participant L_CART as CartUseCase<br/>(Landing)
    participant L_PRODUCT as ProductGrpcClient<br/>(Landing → Core gRPC)
    participant L_COUPON as CouponGrpcClient<br/>(Landing → Core gRPC)
    participant L_ITEM as OrderItemUseCase<br/>(Landing)
    participant L_REPO as OrderRepository<br/>(Landing)
    participant KAFKA as Kafka Broker<br/>topic: order-events

    rect rgb(220, 240, 255)
        Note over FE,L_CTRL: Bước 1: User gửi request tạo đơn hàng
        FE->>L_CTRL: POST /v1/api/orders
        L_CTRL->>L_UC: createFromCart(userId, request)
        L_UC->>L_CMD: createFromCartDomain(request)
    end

    rect rgb(255, 245, 220)
        Note over L_CMD,L_CART: Bước 2: Lấy giỏ hàng + kiểm tra
        L_CMD->>L_CART: getCartItems(userId)
        L_CART-->>L_CMD: List〈CartItem〉
        Note over L_CMD: if cartItems.isEmpty()<br/>→ throw "Cart is empty"
    end

    rect rgb(220, 255, 220)
        Note over L_CMD,L_PRODUCT: Bước 3: Xác thực tồn kho từng sản phẩm
        L_CMD->>L_CMD: validateStockAvailability(cartItems)
        L_CMD->>L_PRODUCT: gRPC validateProductAvailability(productId, qty)
        L_PRODUCT-->>L_CMD: available = true/false
        Note over L_CMD: if not available<br/>→ throw "Insufficient stock"
    end

    rect rgb(255, 220, 255)
        Note over L_CMD,L_COUPON: Bước 4: Kiểm tra coupon (nếu có)
        L_CMD->>L_CMD: calculateOrderTotalsWithCoupon(cartItems, couponCode)
        L_CMD->>L_COUPON: gRPC validateCoupon(couponCode)
        L_COUPON-->>L_CMD: Coupon (discountType, discountValue)
        Note over L_CMD: Tính: subtotal, discount, total<br/>→ OrderCalculation(subtotal, discount, total, couponId)
    end

    rect rgb(240, 240, 255)
        Note over L_CMD,L_REPO: Bước 5: Tạo đơn hàng + order items
        L_CMD->>L_CMD: createOrderEntityFromCart(request, calculation)
        Note over L_CMD: Sinh orderNumber = "ORD-yyyyMMdd-HHmmss-xxxx"<br/>Status = PENDING, PaymentStatus = UNPAID
        L_CMD->>L_REPO: orderRepository.save(entity)
        L_REPO-->>L_CMD: OrderEntity saved (có id)
        
        L_CMD->>L_COUPON: gRPC incrementUsage(couponId)
        
        L_CMD->>L_ITEM: createOrderItems(savedEntity, cartItems)
        Note over L_ITEM: Với mỗi cartItem:<br/>→ gRPC getProductById(productId)<br/>→ tạo OrderItemEntity (price, qty, productTitle)<br/>→ orderItemRepository.saveAll()
        L_ITEM-->>L_CMD: List〈OrderItemEntity〉
    end

    rect rgb(255, 255, 200)
        Note over L_CMD,L_CART: Bước 6: Dọn giỏ hàng
        L_CMD->>L_CART: clearCart(userId)
    end

    rect rgb(255, 230, 220)
        Note over L_CMD,KAFKA: Bước 7: Publish event Kafka
        L_CMD->>L_CMD: publishOrderCreatedEvent(order, orderItems, cartItems)
        Note over L_CMD: Build OrderEvent:<br/>eventId = UUID<br/>eventType = "ORDER_CREATED"<br/>source = "client"<br/>orderId, orderNumber, userId<br/>items = [{productId, qty, price}]
        L_CMD->>KAFKA: OrderKafkaProducerImpl.send(event)
        Note over KAFKA: → Core consumer xử lý tiếp<br/>(giữ chỗ tồn kho, gửi notification)
    end

    rect rgb(220, 255, 240)
        Note over L_CMD,FE: Bước 8: Trả response
        L_CMD-->>L_UC: Optional〈Order〉
        L_UC-->>L_CTRL: Order domain
        L_CTRL-->>FE: JSON { code: 200, data: OrderResponse }
    end
```

---

## 9. Luồng đồng bộ kho qua Kafka — Vòng đời đơn hàng (Code-Level)

```mermaid
sequenceDiagram
    participant L as Landing<br/>OrderCommandService
    participant KP as OrderKafkaProducerImpl
    participant KAFKA as Kafka Broker<br/>topic: order-events
    participant KC as OrderKafkaConsumer<br/>(Core)
    participant OES as OrderEventService<br/>(Core)
    participant PUC as ProductUseCase<br/>(Core)
    participant PCS as ProductCommandService<br/>(Core)
    participant PR as ProductRepository<br/>(Core)
    participant SR as StockReservationRepository<br/>(Core)
    participant OUC as OrderUseCase<br/>(Core)
    participant NUC as NotificationUseCase<br/>(Core)
    participant INV as InventoryUseCase<br/>(Core)

    Note over L,NUC: ① ORDER_CREATED → Giữ chỗ tồn kho

    rect rgb(220, 240, 255)
        L->>KP: publishOrderCreatedEvent(order, items)
        KP->>KAFKA: send("order-events", OrderEvent)
        KAFKA->>KC: @KafkaListener consumeOrderEvent()
        KC->>OES: handleOrderCreated(event)
        
        OES->>OES: reserveStockForOrderAtomic(event)
        loop Mỗi item trong event.items
            OES->>PUC: reserveStock(productId, qty)
            PUC->>PCS: reserveStock(productId, qty)
            PCS->>PR: findByIdWithLock(productId)
            Note over PR: SELECT ... FOR UPDATE<br/>(Pessimistic Lock)
            PR-->>PCS: ProductEntity (locked)
            Note over PCS: Check: available = stock - reserved ≥ qty<br/>entity.reservedQuantity += qty
            PCS->>PR: save(entity)
            
            OES->>SR: save(StockReservation)<br/>status=ACTIVE, expireAt=now+15min
        end
        
        OES->>OUC: updateOrderStatusDirect(orderId, STOCK_RESERVED)
        OES->>NUC: createAndSend(userId, "Đặt hàng thành công")
        Note over NUC: → NotificationGrpcClient<br/>→ Landing MQTT → Frontend
    end

    Note over L,NUC: ② ORDER_PAID → Chuyển reserved sang sold

    rect rgb(255, 245, 220)
        L->>KP: sendOrderPaidEvent(order)
        KP->>KAFKA: send(ORDER_PAID)
        KAFKA->>KC: consumeOrderEvent()
        KC->>OES: handleOrderPaid(event)
        
        loop Mỗi item
            OES->>PUC: deductReservedStock(productId, qty)
            PUC->>PCS: deductReservedStock(productId, qty)
            PCS->>PR: findByIdWithLock(productId)
            Note over PCS: stock -= qty<br/>reserved -= qty<br/>sold += qty
            PCS->>PR: save(entity)
        end
        
        OES->>SR: updateStatusByOrderId(orderId, CONFIRMED)
    end

    Note over L,NUC: ③ ORDER_CANCELLED → Hoàn lại tồn kho

    rect rgb(255, 220, 220)
        L->>KP: publishOrderCancelledEvent(order, items)
        KP->>KAFKA: send(ORDER_CANCELLED)
        KAFKA->>KC: consumeOrderEvent()
        KC->>OES: handleOrderCancelled(event)
        
        loop Mỗi item
            OES->>PUC: releaseReservedStock(productId, qty)
            PUC->>PCS: releaseReservedStock(productId, qty)
            PCS->>PR: findByIdWithLock(productId)
            Note over PCS: reserved -= qty<br/>(stock trở lại available)
            PCS->>PR: save(entity)
        end
        
        OES->>SR: updateStatusByOrderId(orderId, CANCELLED)
    end

    Note over L,NUC: ④ ORDER_DELIVERED → Xác nhận bán + tính giá vốn FIFO

    rect rgb(220, 255, 220)
        L->>KP: publishOrderDeliveredEvent(order, items)
        KP->>KAFKA: send(ORDER_DELIVERED)
        KAFKA->>KC: consumeOrderEvent()
        KC->>OES: handleOrderDelivered(event)
        
        OES->>PUC: updateProductsSoldCount(soldCountMap)
        Note over PUC: products.sold_count += qty
        
        OES->>OUC: recordSaleOutTransactions(orderId)
        Note over OUC: OrderCommandService.recordSaleOutTransactions()
        
        loop Mỗi order_item
            OUC->>INV: consumeBatchesFIFO(productId, qty)
            Note over INV: Lấy lô cũ nhất (ORDER BY receivedAt ASC)<br/>Trừ quantityRemaining từng lô<br/>→ Tính giá vốn bình quân gia quyền
            INV-->>OUC: costPrice (BigDecimal)
            
            Note over OUC: orderItem.setCostPrice(costPrice)
            
            OUC->>INV: recordSaleOut(productId, qty, costPrice,<br/>stockBefore, stockAfter, orderId, orderNumber)
            Note over INV: Tạo InventoryMovement<br/>type=SALE_OUT
        end
    end

    Note over L,NUC: ⑤ ORDER_STATUS_CHANGED → Gửi notification

    rect rgb(240, 230, 255)
        L->>KP: publishOrderEvent(order, STATUS_CHANGED)
        KP->>KAFKA: send(ORDER_STATUS_CHANGED)
        KAFKA->>KC: consumeOrderEvent()
        KC->>OES: handleOrderStatusChanged(event)
        
        OES->>NUC: createAndSend(userId, type, title, message)
        Note over NUC: Save NotificationEntity to DB<br/>→ NotificationGrpcClient.sendToUser()<br/>→ Landing NotificationGrpcService<br/>→ NotificationMqttPublisherImpl<br/>→ EMQX → Frontend
    end
```

---

## Tóm tắt tiêu chuẩn chung

| Hướng | Chuỗi class |
|-------|-------------|
| **FE → Landing → Core (gRPC)** | `Controller` → `UseCase` → `QueryService/CommandService` → `GrpcClient (BlockingStub)` → **proto** → `GrpcService (ImplBase)` → `UseCase` → `QueryService/CommandService` → `Repository` |
| **Landing → Core (Kafka)** | `CommandService` → `KafkaProducerImpl.send()` → **Kafka Broker** → `KafkaConsumer.consume()` → `EventService.handle()` → `UseCase` → `CommandService` → `Repository` |
| **Core → Landing → FE (Notification)** | `EventService/CommandService` → `NotificationUseCase` → `NotificationGrpcClient (BlockingStub)` → **proto** → `NotificationGrpcService (ImplBase)` → `NotificationUseCase` → `MqttPublisherImpl` → **EMQX** → `Frontend MQTT Client` |
| **Admin trực tiếp Core** | `AdminController` → `UseCase` → `QueryService/CommandService` → `Repository` |
