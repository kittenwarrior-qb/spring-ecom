# Upload API Documentation

## Cấu hình Cloudinary

### 1. Đăng ký tài khoản Cloudinary
- Truy cập: https://cloudinary.com/
- Đăng ký tài khoản miễn phí
- Lấy thông tin từ Dashboard: Cloud Name, API Key, API Secret

### 2. Cấu hình Environment Variables

Tạo file `.env` hoặc set environment variables:

```bash
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

Hoặc cập nhật trực tiếp trong `application.properties`:

```properties
cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret
```

## API Endpoints

### Upload Image

**Endpoint:** `POST /api/upload/image`

**Authentication:** Required (Bearer Token)

**Content-Type:** `multipart/form-data`

**Request:**
```
file: [image file]
```

**Response:**
```json
{
  "code": "CREATED",
  "message": "Image uploaded successfully",
  "data": {
    "url": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/spring-ecom/abc123.jpg",
    "publicId": "spring-ecom/abc123",
    "format": "jpg",
    "size": 123456,
    "width": 1920,
    "height": 1080
  }
}
```

## Sử dụng với Postman

1. Chọn method: `POST`
2. URL: `http://localhost:8080/api/upload/image`
3. Headers:
   - `Authorization: Bearer {your-token}`
4. Body:
   - Chọn `form-data`
   - Key: `file` (type: File)
   - Value: Chọn file ảnh từ máy

## Sử dụng với cURL

```bash
curl -X POST http://localhost:8080/api/upload/image \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

## Giới hạn

- Max file size: 10MB
- Chỉ chấp nhận file ảnh (image/*)
- Ảnh sẽ được lưu trong folder `spring-ecom` trên Cloudinary

## Lưu ý

- URL trả về là secure URL (https)
- publicId có thể dùng để xóa ảnh sau này
- Free plan Cloudinary có giới hạn 25 credits/tháng
