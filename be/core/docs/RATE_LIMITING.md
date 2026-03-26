# Rate Limiting Documentation

## Tổng quan

Hệ thống rate limiting được thiết kế để bảo vệ API khỏi việc lạm dụng và tấn công DDoS. Hệ thống hỗ trợ fallback mechanism khi Redis không khả dụng.

## Cách sử dụng

### 1. Sử dụng Annotation

```java
@RestController
public class MyController {
    
    // Rate limit theo IP - 10 requests per minute
    @GetMapping("/api/data")
    @RateLimit(type = RateLimitType.IP, limit = 10, duration = 1, unit = ChronoUnit.MINUTES)
    public ApiResponse<String> getData() {
        return ApiResponse.Success.of(ResponseCode.OK, "Data", "Some data");
    }
    
    // Rate limit theo User - 100 requests per hour
    @GetMapping("/api/user-data")
    @RateLimit(type = RateLimitType.USER, limit = 100, duration = 1, unit = ChronoUnit.HOURS)
    public ApiResponse<String> getUserData() {
        return ApiResponse.Success.of(ResponseCode.OK, "User data", "User specific data");
    }
    
    // Rate limit global cho endpoint - 1000 requests per hour
    @GetMapping("/api/public")
    @RateLimit(type = RateLimitType.ENDPOINT, limit = 1000, duration = 1, unit = ChronoUnit.HOURS)
    public ApiResponse<String> getPublicData() {
        return ApiResponse.Success.of(ResponseCode.OK, "Public data", "Public data");
    }
}
```

### 2. Sử dụng Service trực tiếp

```java
@Service
public class MyService {
    
    @Autowired
    private RateLimitService rateLimitService;
    
    public void checkRateLimit() {
        boolean allowed = rateLimitService.checkRateLimit(
            RateLimitType.IP, 
            "192.168.1.1", 
            "custom-endpoint", 
            10, 
            Duration.ofMinutes(1)
        );
        
        if (!allowed) {
            throw new BaseException(ResponseCode.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
    }
}
```

## Các loại Rate Limit

### 1. IP-based Rate Limiting
- Giới hạn theo địa chỉ IP
- Phù hợp cho các endpoint public
- Tự động fallback sang in-memory khi Redis down

### 2. User-based Rate Limiting  
- Giới hạn theo user đã authenticate
- Fallback sang IP nếu user chưa đăng nhập
- Phù hợp cho các endpoint cần authentication

### 3. Endpoint-based Rate Limiting
- Giới hạn global cho toàn bộ endpoint
- Áp dụng cho tất cả requests đến endpoint đó
- Phù hợp để bảo vệ tài nguyên hệ thống

## Configuration

### Annotation Parameters

- `type`: Loại rate limit (IP, USER, ENDPOINT)
- `limit`: Số requests tối đa
- `duration`: Thời gian window
- `unit`: Đơn vị thời gian (SECONDS, MINUTES, HOURS, DAYS)
- `message`: Custom message khi bị rate limit

### Default Configurations

```java
// Login endpoint
- Type: IP
- Limit: 5 requests
- Window: 15 minutes

// Register endpoint  
- Type: IP
- Limit: 3 requests
- Window: 30 minutes

// API endpoints
- Type: USER
- Limit: 100 requests
- Window: 1 minute
```

## Response Headers

Khi request bị rate limit, response sẽ có các headers:

```
X-RateLimit-Limit: 10
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1640995200000
```

## Admin Management

### API Endpoints

```bash
# Lấy thông tin rate limit
GET /api/admin/rate-limit/info?type=IP&identifier=192.168.1.1&endpoint=login

# Reset rate limit cụ thể
DELETE /api/admin/rate-limit/reset?type=IP&identifier=192.168.1.1&endpoint=login

# Reset tất cả rate limit của identifier
DELETE /api/admin/rate-limit/reset-all?identifier=192.168.1.1

# Lấy thông tin login rate limit
GET /api/admin/rate-limit/login-info?ipAddress=192.168.1.1

# Reset login rate limit
DELETE /api/admin/rate-limit/reset-login?ipAddress=192.168.1.1
```

## Fallback Mechanism

Khi Redis không khả dụng:

1. **Automatic Fallback**: Hệ thống tự động chuyển sang in-memory cache
2. **Transparent Operation**: API hoạt động bình thường, không có downtime
3. **Memory Management**: Tự động cleanup expired entries
4. **Logging**: Log chi tiết khi fallback xảy ra

## Monitoring

### Logs

```
2024-01-01 10:00:00 WARN  - Rate limit exceeded for IP 192.168.1.1 on endpoint login
2024-01-01 10:00:00 INFO  - Redis not available, using in-memory fallback
2024-01-01 10:00:00 INFO  - Admin reset rate limit for IP 192.168.1.1
```

### Metrics

- Total requests
- Blocked requests  
- Active rate limits
- Block rate percentage

## Best Practices

1. **Endpoint-specific Limits**: Đặt limit phù hợp cho từng endpoint
2. **User vs IP**: Sử dụng USER cho authenticated endpoints, IP cho public
3. **Graceful Degradation**: Luôn có fallback plan khi Redis down
4. **Monitoring**: Theo dõi rate limit metrics thường xuyên
5. **Admin Tools**: Sử dụng admin endpoints để quản lý khi cần thiết

## Troubleshooting

### Redis Connection Issues
- Hệ thống tự động fallback sang in-memory
- Check Redis connection trong logs
- Restart Redis service nếu cần

### False Positives
- Sử dụng admin endpoints để reset rate limit
- Kiểm tra IP detection logic
- Adjust rate limit parameters nếu cần

### Performance Issues
- Monitor memory usage khi sử dụng fallback
- Cleanup expired entries thường xuyên
- Consider Redis cluster cho high traffic