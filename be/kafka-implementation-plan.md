# KAFKA IMPLEMENTATION PLAN

## 📋 Tổng quan
Dự án đã có:
- ✅ Kafka & Zookeeper trong docker-compose
- ✅ Module kafka-service (shared library)
- ✅ Cấu hình Kafka trong core service
- ✅ Dependencies cơ bản

Cần implement:
- ❌ Event models & DTOs
- ❌ Kafka configuration với retry mechanism
- ❌ Producer service
- ❌ Consumer listeners
- ❌ Integration với business logic

---

## 🎯 PHASE 1: Setup Kafka Service Module (Shared Library)

### 1.1. Tạo Event Models
**File**: `kafka-service/src/main/java/com/example/kafka/event/`

#### OrderEvent.java
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long userId;
    private OrderEventType eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}

public enum OrderEventType {
    ORDER_CREATED,
    ORDER_PAID,
    ORDER_CANCELLED,
    ORDER_DELIVERED,
    ORDER_STATUS_CHANGED
}
```

#### UserEvent.java
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {
    private Long userId;
    private String email;
    private String username;
    private UserEventType eventType;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}

public enum UserEventType {
    USER_REGISTERED,
    USER_VERIFIED,
    USER_PASSWORD_RESET
}
```

### 1.2. Tạo Kafka Topics Constants
**File**: `kafka-service/src/main/java/com/example/kafka/constant/KafkaTopics.java`

```java
public class KafkaTopics {
    public static final String ORDER_EVENTS = "order-events";
    public static final String USER_EVENTS = "user-events";
    
    // Dead Letter Queue topics
    public static final String ORDER_EVENTS_DLQ = "order-events-dlq";
    public static final String USER_EVENTS_DLQ = "user-events-dlq";
}
```

---

## 🎯 PHASE 2: Core Service - Kafka Configuration

### 2.1. Kafka Producer Configuration
**File**: `core/src/main/java/com/example/core/config/KafkaProducerConfig.java`

Cấu hình:
- JsonSerializer cho value
- StringSerializer cho key
- Retry mechanism (3 lần)
- Idempotence enabled
- Acks = all

### 2.2. Kafka Consumer Configuration
**File**: `core/src/main/java/com/example/core/config/KafkaConsumerConfig.java`

Cấu hình:
- JsonDeserializer với trusted packages
- Manual commit mode (AckMode.MANUAL)
- Concurrency = 3
- Error handler với retry

### 2.3. Kafka Error Handler
**File**: `core/src/main/java/com/example/core/config/KafkaErrorHandler.java`

Implement:
- DefaultErrorHandler với BackOff
- Retry 3 lần với delay tăng dần (1s, 2s, 4s)
- Sau khi fail hết retry → gửi vào DLQ
- Log chi tiết lỗi

---

## 🎯 PHASE 3: Core Service - Producer Implementation

### 3.1. Kafka Producer Service
**File**: `core/src/main/java/com/example/core/kafka/producer/KafkaProducerService.java`

Methods:
```java
public void sendOrderEvent(OrderEvent event)
public void sendUserEvent(UserEvent event)
private void handleSendSuccess(SendResult result)
private void handleSendFailure(Throwable ex)
```

---

## 🎯 PHASE 4: Core Service - Consumer Implementation

### 4.1. Order Event Consumer
**File**: `core/src/main/java/com/example/core/kafka/consumer/OrderEventConsumer.java`

```java
@KafkaListener(
    topics = KafkaTopics.ORDER_EVENTS,
    groupId = "core-order-consumer-group",
    containerFactory = "kafkaListenerContainerFactory"
)
public void consumeOrderEvent(
    @Payload OrderEvent event,
    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
    @Header(KafkaHeaders.OFFSET) long offset,
    Acknowledgment acknowledgment
)
```

Logic xử lý theo eventType:
- **ORDER_CREATED**: 
  - Gọi `inventoryService.reserveStock(orderId)`
  - Gọi `notificationService.sendOrderCreatedNotification(userId, orderId)`
  
- **ORDER_PAID**:
  - Gọi `inventoryService.confirmStock(orderId)`
  
- **ORDER_CANCELLED**:
  - Gọi `inventoryService.releaseStock(orderId)`
  - Gọi `notificationService.sendOrderCancelledNotification(userId, orderId)`
  
- **ORDER_DELIVERED**:
  - Gọi `productService.incrementSoldCount(orderId)`
  - Gọi `notificationService.sendOrderDeliveredNotification(userId, orderId)`
  
- **ORDER_STATUS_CHANGED**:
  - Gọi `notificationService.sendOrderStatusNotification(userId, orderId, status)`

### 4.2. User Event Consumer
**File**: `core/src/main/java/com/example/core/kafka/consumer/UserEventConsumer.java`

```java
@KafkaListener(
    topics = KafkaTopics.USER_EVENTS,
    groupId = "core-user-consumer-group",
    containerFactory = "kafkaListenerContainerFactory"
)
public void consumeUserEvent(
    @Payload UserEvent event,
    @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
    @Header(KafkaHeaders.OFFSET) long offset,
    Acknowledgment acknowledgment
)
```

Logic xử lý:
- **USER_REGISTERED**:
  - Gọi `emailService.sendVerificationEmail(userId, email)`
  
- **USER_VERIFIED**:
  - Gọi `emailService.sendWelcomeEmail(userId, email)`
  
- **USER_PASSWORD_RESET**:
  - Gọi `emailService.sendPasswordResetEmail(userId, email)`

---

## 🎯 PHASE 5: Business Logic Integration

### 5.1. Order Service Integration
**File**: `core/src/main/java/com/example/core/service/OrderService.java`

Thêm Kafka producer vào các methods:

```java
// Sau khi tạo order thành công
public Order createOrder(CreateOrderRequest request) {
    Order order = // ... logic tạo order
    
    // Publish event
    kafkaProducerService.sendOrderEvent(OrderEvent.builder()
        .orderId(order.getId())
        .userId(order.getUserId())
        .eventType(OrderEventType.ORDER_CREATED)
        .timestamp(LocalDateTime.now())
        .metadata(Map.of("items", order.getItems()))
        .build());
    
    return order;
}

// Tương tự cho các methods khác:
// - updateOrderStatus() → ORDER_STATUS_CHANGED
// - processPayment() → ORDER_PAID
// - cancelOrder() → ORDER_CANCELLED
// - deliverOrder() → ORDER_DELIVERED
```

### 5.2. User Service Integration
**File**: `core/src/main/java/com/example/core/service/UserService.java`

```java
public User registerUser(RegisterRequest request) {
    User user = // ... logic đăng ký
    
    // Publish event
    kafkaProducerService.sendUserEvent(UserEvent.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .username(user.getUsername())
        .eventType(UserEventType.USER_REGISTERED)
        .timestamp(LocalDateTime.now())
        .build());
    
    return user;
}
```

### 5.3. Inventory Service (New)
**File**: `core/src/main/java/com/example/core/service/InventoryService.java`

Methods cần implement:
```java
public void reserveStock(Long orderId)
public void confirmStock(Long orderId)
public void releaseStock(Long orderId)
```

### 5.4. Notification Service Enhancement
**File**: `core/src/main/java/com/example/core/service/NotificationService.java`

Thêm methods:
```java
public void sendOrderCreatedNotification(Long userId, Long orderId)
public void sendOrderCancelledNotification(Long userId, Long orderId)
public void sendOrderDeliveredNotification(Long userId, Long orderId)
public void sendOrderStatusNotification(Long userId, Long orderId, String status)
```

---

## 🎯 PHASE 6: Landing Service Integration (Optional)

Nếu landing service cũng cần consume events:

### 6.1. Add Kafka Dependencies
**File**: `landing/build.gradle`

```gradle
implementation project(':kafka-service')
implementation 'org.springframework.kafka:spring-kafka'
```

### 6.2. Kafka Configuration
Tương tự core service nhưng với group-id khác

### 6.3. Consumer Implementation
Implement consumer cho các events cần thiết

---

## 🎯 PHASE 7: Testing & Monitoring

### 7.1. Kafka CLI Commands
**File**: `be/kafka-commands.md`

Document các commands:
```bash
# List topics
docker exec -it <kafka-container> kafka-topics --list --bootstrap-server localhost:9092

# Describe topic
docker exec -it <kafka-container> kafka-topics --describe --topic order-events --bootstrap-server localhost:9092

# View consumer groups
docker exec -it <kafka-container> kafka-consumer-groups --list --bootstrap-server localhost:9092

# Check consumer lag
docker exec -it <kafka-container> kafka-consumer-groups --describe --group core-order-consumer-group --bootstrap-server localhost:9092

# Consume messages from beginning
docker exec -it <kafka-container> kafka-console-consumer --topic order-events --from-beginning --bootstrap-server localhost:9092

# View DLQ messages
docker exec -it <kafka-container> kafka-console-consumer --topic order-events-dlq --from-beginning --bootstrap-server localhost:9092
```

### 7.2. Unit Tests
Tạo tests cho:
- Producer service
- Consumer logic
- Error handling
- Retry mechanism

### 7.3. Integration Tests
Test end-to-end flow:
- Tạo order → verify event published → verify consumer processed
- Test retry mechanism
- Test DLQ

---

## 📊 Implementation Checklist

### Phase 1: Kafka Service Module
- [ ] OrderEvent model
- [ ] UserEvent model
- [ ] Event type enums
- [ ] KafkaTopics constants

### Phase 2: Core Configuration
- [ ] KafkaProducerConfig
- [ ] KafkaConsumerConfig
- [ ] KafkaErrorHandler
- [ ] Update application.yml

### Phase 3: Producer
- [ ] KafkaProducerService
- [ ] Error handling
- [ ] Logging

### Phase 4: Consumers
- [ ] OrderEventConsumer
- [ ] UserEventConsumer
- [ ] Manual acknowledgment
- [ ] Error handling

### Phase 5: Business Integration
- [ ] OrderService integration
- [ ] UserService integration
- [ ] InventoryService implementation
- [ ] NotificationService enhancement

### Phase 6: Testing
- [ ] Unit tests
- [ ] Integration tests
- [ ] Manual testing với Kafka CLI

### Phase 7: Documentation
- [ ] Kafka commands reference
- [ ] Architecture diagram
- [ ] Troubleshooting guide

---

## 🚀 Execution Order

1. **Day 1**: Phase 1 + Phase 2 (Setup & Configuration)
2. **Day 2**: Phase 3 + Phase 4 (Producer & Consumer)
3. **Day 3**: Phase 5 (Business Integration)
4. **Day 4**: Phase 6 + Phase 7 (Testing & Documentation)

---

## 📝 Notes

### Retry Strategy
- Consumer retry: 3 lần với backoff (1s, 2s, 4s)
- Producer retry: 3 lần (built-in Kafka)
- Sau khi fail → DLQ

### Offset Management
- Manual commit mode
- Commit sau khi xử lý thành công
- Không commit nếu có exception → retry

### Partition Strategy
- Order events: partition by orderId
- User events: partition by userId
- Đảm bảo ordering trong cùng partition

### Monitoring Points
- Consumer lag
- DLQ message count
- Processing time
- Error rate

---

## 🔧 Configuration Summary

### application.yml additions needed:
```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: false  # Manual commit
      max-poll-records: 10
    listener:
      ack-mode: manual
      concurrency: 3
    
kafka:
  retry:
    max-attempts: 3
    backoff:
      initial-interval: 1000
      multiplier: 2
      max-interval: 4000
```

---

## 🎓 Learning Resources

Để hiểu rõ hơn về implementation:
1. Kafka offset management
2. Consumer group rebalancing
3. Idempotent producer
4. Dead letter queue pattern
5. Event-driven architecture best practices
