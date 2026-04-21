# Kiến trúc hệ thống — Sơ đồ đơn giản

## 1. Tổng quan kiến trúc

```mermaid
flowchart TB
    subgraph "Frontend"
        FE_USER["Người dùng UI :5173"]
        FE_ADMIN["Quản trị UI :5173/admin"]
    end
    
    FE_USER -->|REST API| L["Landing :8080"]
    FE_ADMIN -->|REST API| C["Core :8081"]
    
    L -->|Internal Call| C
    C -->|Response| L
    
    L -->|Kafka produce| K["Kafka :9092"]
    K -->|Kafka consume| C
    L -->|MQTT publish| EMQX["EMQX :1883"]
    EMQX -.->|realtime| FE_USER
    
    L & C -->|JPA| DB[("PostgreSQL")]
    L & C -.->|cache| REDIS[("Redis")]
    C -.->|file| MINIO[("MinIO")]
```

**Landing (HTTP 8080):**
Service cung cấp API cho **user/client**. Bao gồm:
- Giỏ hàng (cart), đơn hàng (order)
- Sản phẩm công khai (product public), đánh giá (review)
- Thông tin người dùng (user profile), thanh toán (payment)
- Xác thực: đăng ký, đăng nhập, refresh token

**Core (HTTP 8081):**
Service cung cấp API cho **admin** và xử lý các nghiệp vụ chính:
- Quản lý sản phẩm, đơn hàng, thống kê, kho hàng, nhà cung cấp
- Xử lý nghiệp vụ: quản lý tồn kho, xử lý đơn hàng, định giá
- Xử lý sự kiện Kafka cho quy trình đơn hàng
- Xác thực: đăng nhập, refresh token

## 2. Phân công nghiệp vụ

| Service | Vai trò |
|---------|---------|
| Landing | Cung cấp API cho user/client |
| Core | Cung cấp API cho admin và xử lý nghiệp vụ |

**Khi Landing cần xử lý nghiệp vụ:** Gọi sang Core (Product, Order, User, Coupon)
**Khi Core cần gửi thông báo:** Gọi sang Landing để gửi MQTT realtime

## 3. Kafka: Xử lý đơn hàng bất đồng bộ

```mermaid
flowchart LR
    L["Landing\nOrderCommandService"] -->|produce| K["Kafka\norder-events"]
    K -->|consume| C["Core\nOrderKafkaConsumer"]
    C --> OES["OrderEventService"]
    OES --> R["ProductRepository\nfindByIdWithLock()"]
```

| Event | Xử lý |
|-------|-------|
| ORDER_CREATED | Giữ chỗ tồn kho (reserve) |
| ORDER_PAID | Trừ kho (reserved → sold) |
| ORDER_CANCELLED | Hoàn kho (release reserved) |
| ORDER_DELIVERED | Cập nhật sold + tính giá vốn FIFO |
| STATUS_CHANGED | Gửi notification |

## 4. MQTT: Thông báo realtime

```mermaid
flowchart LR
    C["Core\nNotificationUseCase"] -->|Internal Call| L["Landing\nNotificationService"]
    L --> M["MqttPublisherImpl"]
    M -->|publish| EMQX["EMQX"]
    EMQX -.->|deliver| FE["Frontend\nMQTT subscribe"]
```

**Topic cá nhân:** `notifications/{userId}/{type}`
**Topic broadcast:** `notifications/broadcast/{type}`

## 5. Luồng tạo đơn hàng

```mermaid
flowchart TB
    A["User đặt hàng"] --> B["Lấy giỏ hàng"]
    B --> C["Kiểm tra tồn kho\n(Core)"]
    C --> D["Kiểm tra coupon\n(Core)"]
    D --> E["Tạo Order + OrderItems"]
    E --> F["Xóa giỏ hàng"]
    F --> G["Kafka: ORDER_CREATED"]
    G --> H["Core: Giữ chỗ tồn kho\n(Pessimistic Lock)"]
    H --> I["Gửi notification\n(MQTT)"]
```

## 6. Vòng đời đơn hàng (Stock)

```mermaid
stateDiagram-v2
    [*] --> PENDING: User đặt hàng
    PENDING --> STOCK_RESERVED: Kafka ORDER_CREATED\n(reserve stock)
    STOCK_RESERVED --> CONFIRMED: Kafka ORDER_PAID\n(deduct reserved → sold)
    CONFIRMED --> DELIVERING: Admin cập nhật
    DELIVERING --> DELIVERED: Kafka ORDER_DELIVERED\n(update soldCount + FIFO cost)
    STOCK_RESERVED --> CANCELLED: Kafka ORDER_CANCELLED\n(release reserved stock)
    CONFIRMED --> CANCELLED: Kafka ORDER_CANCELLED
```

