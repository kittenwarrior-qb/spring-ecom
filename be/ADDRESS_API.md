# Address Management API

## Endpoints

### 1. Get All Addresses
```http
GET /api/addresses
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

### 2. Get Address by ID
```http
GET /api/addresses/{id}
Authorization: Bearer {token}
```

### 3. Create New Address
```http
POST /api/addresses
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

**Validation:**
- `fullName`: Required, max 100 characters
- `phoneNumber`: Required, 10-11 digits
- `addressLine`: Required, max 255 characters
- `district`: Required, max 100 characters
- `city`: Required, max 100 characters
- `ward`: Optional, max 100 characters
- `postalCode`: Optional, max 20 characters
- `isDefault`: Optional, boolean

### 4. Update Address
```http
PUT /api/addresses/{id}
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

### 5. Delete Address
```http
DELETE /api/addresses/{id}
Authorization: Bearer {token}
```

**Note:** Nếu xóa địa chỉ mặc định, địa chỉ tiếp theo sẽ tự động được set làm mặc định.

### 6. Set Default Address
```http
PATCH /api/addresses/{id}/set-default
Authorization: Bearer {token}
```

### 7. Get Location Suggestion (Auto-fill based on IP)
```http
GET /api/addresses/location-suggestion
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
