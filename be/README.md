# Spring E-commerce Backend

Backend service cho ứng dụng thương mại điện tử sử dụng Spring Boot với kiến trúc microservices.

## 🏗️ Kiến trúc

Hệ thống được chia thành 2 services chính giao tiếp qua gRPC:

### Client Service (Port 8080)
- **Mục đích**: REST APIs cho người dùng cuối (customers)
- **Vai trò**: gRPC Client
- **APIs**: User authentication, product browsing, cart, orders, payments

### Server Service (Port 8081)
- **Mục đích**: REST APIs cho admin + gRPC Server
- **Vai trò**: gRPC Server + Admin management
- **APIs**: Admin authentication, user management, product management, order management

### gRPC Proto
- **Mục đích**: Protocol Buffers definitions cho gRPC communication
- **Services**: UserService, ProductService, OrderService

## 📁 Cấu trúc dự án

```
be/
├── client/          # Client service (User APIs + gRPC Client)
│   ├── src/main/java/.../controller/
│   │   ├── api/              # REST Controllers cho users
│   │   │   ├── auth/         # User authentication
│   │   │   ├── product/      # Browse products
│   │   │   ├── category/     # Browse categories
│   │   │   ├── cart/         # Shopping cart
│   │   │   ├── order/        # User orders
│   │   │   ├── review/       # Product reviews
│   │   │   ├── user/         # User profile
│   │   │   ├── payment/      # Payments
│   │   │   └── upload/       # User uploads
│   └── client/grpc/          # gRPC Clients
│       ├── UserGrpcClient
│       ├── ProductGrpcClient
│       └── OrderGrpcClient
│
├── server/          # Server service (Admin APIs + gRPC Server)
│   ├── src/main/java/.../controller/
│   │   ├── api/
│   │   │   ├── admin/        # Admin REST Controllers
│   │   │   │   ├── AdminUserController
│   │   │   │   ├── AdminProductController
│   │   │   │   ├── AdminCategoryController
│   │   │   │   ├── AdminOrderController
│   │   │   │   └── AdminReviewController
│   │   │   ├── auth/         # Admin authentication
│   │   │   ├── email/        # Email management
│   │   │   └── upload/       # Admin uploads
│   │   └── grpc/             # gRPC Server Implementations
│   │       ├── UserGrpcService
│   │       ├── ProductGrpcService
│   │       └── OrderGrpcService
│
├── grpc-proto/      # gRPC Protocol Buffers definitions
│   └── src/main/proto/
│       ├── common/           # Common messages
│       ├── domain/           # Domain models
│       │   ├── user.proto
│       │   ├── product.proto
│       │   └── order.proto
│       └── services/         # Service definitions
│           ├── user_service.proto
│           ├── product_service.proto
│           └── order_service.proto
│
└── settings.gradle  # Gradle multi-module configuration
```

## 🔄 Luồng hoạt động

### User browsing products (Client → Server via gRPC)
```
1. User → Client REST API (/api/products)
2. Client ProductController → ProductGrpcClient
3. ProductGrpcClient → gRPC → Server ProductGrpcService
4. Server ProductGrpcService → Database
5. Response: Server → Client → User
```

### Admin managing products (Server direct)
```
1. Admin → Server REST API (/admin/products)
2. Server AdminProductController → ProductUseCase
3. ProductUseCase → Database
4. Response: Server → Admin
```

## 📋 API Endpoints

### Client Service (User APIs)
```
/api/auth/*           - User authentication (register, login, logout)
/api/products/*       - Browse products (public)
/api/categories/*     - Browse categories (public)
/api/user/*           - User profile management
/api/cart/*           - Shopping cart
/api/orders/*         - User orders
/api/reviews/*        - User reviews
/api/payments/*       - Payment processing
/api/upload/*         - User avatar upload
```

### Server Service (Admin APIs)
```
/admin/auth/*         - Admin authentication
/admin/users/*        - User management
/admin/products/*     - Product management
/admin/categories/*   - Category management
/admin/orders/*       - Order management
/admin/reviews/*      - Review management
/admin/upload/*       - Admin file upload
/admin/email/*        - Email management
```

## 🚀 Chạy ứng dụng

### Chạy Client Service
```bash
cd client
./gradlew bootRun
# hoặc
gradle bootRun
```
Client service sẽ chạy trên port 8080

### Chạy Server Service
```bash
cd server
./gradlew bootRun
# hoặc
gradle bootRun
```
Server service sẽ chạy trên port 8081

### Build tất cả modules
```bash
./gradlew build
```

## 🔧 Cấu hình

### Client Service
- Port: 8080
- gRPC Client: Kết nối đến server-service (localhost:9090)
- Database: Có thể truy cập trực tiếp hoặc qua gRPC

### Server Service
- Port: 8081
- gRPC Server: Lắng nghe trên port 9090
- Database: Truy cập trực tiếp

## 📚 Tech Stack

- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Build Tool**: Gradle
- **gRPC**: Spring Boot gRPC Starter
- **Database**: PostgreSQL
- **Cache**: Redis
- **Security**: Spring Security + JWT

**Kết quả:**
- Phân chia rõ ràng: Client = User APIs, Server = Admin APIs + gRPC
- Không còn controllers trùng lặp
- Xóa các test endpoints không cần thiết cho production
- gRPC infrastructure sẵn sàng sử dụng
