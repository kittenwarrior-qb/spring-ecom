# Address Management API

## Architecture

Project này sử dụng Clean Architecture pattern với các layer:

- **Controller Layer**: `AddressAPI` (interface) + `AddressController` (implementation)
- **Service Layer**: 
  - `AddressUseCase` (interface)
  - `AddressUseCaseService` (orchestration)
  - `AddressQueryService` (read operations)
  - `AddressCommandService` (write operations)
- **Domain Layer**: `Address` (domain model)
- **Repository Layer**: `AddressEntity` + `AddressRepository` + `AddressEntityMapper`

## Swagger Documentation

Swagger UI có sẵn tại: `http://localhost:8080/swagger-ui/index.html`

Address API được document đầy đủ với:
- ✅ Detailed operation descriptions
- ✅ Request/Response examples
- ✅ Parameter descriptions
- ✅ Validation rules
- ✅ HTTP status codes
- ✅ Security requirements (JWT Bearer token)

## Endpoints

Base URL: `/v1/api/addresses`

### 1. Get All Addresses
```http
GET /v1/api/addresses
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "fullName": "Nguyễn Văn A",
      "phoneNumber": "0901234567",
      "addressLine": "123 Đường ABC",
      "ward": "Phường 1",
      "district": "Quận 1",
      "city": "Hồ Chí Minh",
      "postalCode": "700000",
      "isDefault": true,
      "createdAt": "2026-03-10T10:00:00",
      "updatedAt": "2026-03-10T10:00:00"
    }
  ]
}
```

### 2. Get Default Address
```http
GET /v1/api/addresses/default
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0901234567",
    "addressLine": "123 Đường ABC",
    "ward": "Phường 1",
    "district": "Quận 1",
    "city": "Hồ Chí Minh",
    "postalCode": "700000",
    "isDefault": true,
    "createdAt": "2026-03-10T10:00:00",
    "updatedAt": "2026-03-10T10:00:00"
  }
}
```

**Use case:** Dùng cho checkout page để tự động điền địa chỉ giao hàng mặc định.

### 3. Get Address by ID
```http
GET /v1/api/addresses/{id}
Authorization: Bearer {token}
```

### 4. Create New Address
```http
POST /v1/api/addresses
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Nguyễn Văn A",
  "phoneNumber": "0901234567",
  "addressLine": "123 Đường ABC",
  "ward": "Phường 1",
  "district": "Quận 1",
  "city": "Hồ Chí Minh",
  "postalCode": "700000",
  "isDefault": true
}
```

**Note:** Address tự động gắn với user hiện tại (lấy từ JWT token). Không cần truyền userId.

**Validation:**
- `fullName`: Required, max 100 characters
- `phoneNumber`: Required, 10-11 digits
- `addressLine`: Required, max 255 characters
- `district`: Required, max 100 characters
- `city`: Required, max 100 characters
- `ward`: Optional, max 100 characters
- `postalCode`: Optional, max 20 characters
- `isDefault`: Optional, boolean

### 5. Update Address
```http
PUT /v1/api/addresses/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "Nguyễn Văn B",
  "phoneNumber": "0901234568",
  "addressLine": "456 Đường XYZ",
  "ward": "Phường 2",
  "district": "Quận 2",
  "city": "Hồ Chí Minh",
  "postalCode": "700000",
  "isDefault": false
}
```

### 6. Delete Address
```http
DELETE /v1/api/addresses/{id}
Authorization: Bearer {token}
```

**Note:** Nếu xóa địa chỉ mặc định, địa chỉ tiếp theo sẽ tự động được set làm mặc định.

### 7. Set Default Address
```http
PATCH /v1/api/addresses/{id}/set-default
Authorization: Bearer {token}
```

### 8. Get Location Suggestion (Auto-fill based on IP)
```http
GET /v1/api/addresses/location-suggestion
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "city": "Ho Chi Minh City",
    "region": "Ho Chi Minh",
    "country": "Vietnam",
    "countryCode": "VN",
    "timezone": "Asia/Ho_Chi_Minh"
  }
}
```

**Note:** API này sử dụng ip-api.com (free) để lấy thông tin địa lý dựa trên IP address. Nếu API fail, sẽ trả về thông tin mặc định của Việt Nam.

## Features

- ✅ CRUD operations cho địa chỉ
- ✅ Soft delete (không xóa vĩnh viễn)
- ✅ Tự động quản lý địa chỉ mặc định
- ✅ Gợi ý địa chỉ dựa trên IP (auto-fill city)
- ✅ Validation đầy đủ
- ✅ Chỉ user owner mới có thể thao tác với địa chỉ của mình

## Database Migration

Migration file đã được tạo tại: `be/src/main/resources/db/migration/V5__create_addresses_table.sql`

Chạy lại ứng dụng để Flyway tự động migrate database.
