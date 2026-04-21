# KAFKA IMPLEMENTATION - SIMPLE DEMO PLAN

## 🎯 Mục tiêu: Demo được các tính năng Kafka cơ bản, dễ debug, không phức tạp

---

## ✅ Dự án ĐANG CÓ:
- Kafka + Zookeeper trong docker-compose
- Module kafka-service (rỗng)
- Cấu hình Kafka trong application.yml
- Dependencies đã có

## ❌ Dự án ĐANG THIẾU (so với doc):

### 1. Event Models (DTOs)
- OrderEvent.java
- UserEvent.java
- Enums: OrderEventType, UserEventType

### 2. Kafka Configuration Classes
- KafkaProducerConfig (với retry)
- KafkaConsumerConfig (với error handler)

### 3. Producer Service
- KafkaProducerService để gửi events

### 4. Consumer Listeners
- OrderEventConsumer (xử lý ORDER_CREATED, ORDER_PAID, ORDER_CANCELLED, ORDER_DELIVERED)
- UserEventConsumer (xử lý USER_REGISTERED)

### 5. Business Logic Integration
- OrderService: gọi Kafka sau khi tạo/update order
- UserService: gọi Kafka sau khi register
- InventoryService: reserve/release stock
- NotificationService: gửi email/notification

### 6. Monitoring/Debug Tools
- Kafka CLI commands để check offset, consumer lag

---

## 🚀 PLAN ĐƠN GIẢN - CHIA NHỎ TỪNG BƯỚC

### BƯỚC 1: Event Models (5 phút)
**Tạo 2 files trong kafka-service:**

`OrderEvent.java` - chỉ cần 4 fields:
```java
orderId, userId, eventType, timestamp
```

`UserEvent.java` - chỉ cần 4 fields:
```java
userId, email, eventType, timestamp
```

**Không cần:** metadata, builder pattern phức tạp

---

### BƯỚC 2: Kafka Config (10 phút)
**Tạo 1 file config trong core:**

`KafkaConfig.java` - gộp cả producer + consumer + error handler
- Producer: JsonSerializer
- Consumer: JsonDeserializer + retry 3 lần
- Error handler: log lỗi ra console (dễ debug)

**Không cần:** DLQ, idempotence, acks phức tạp

---

### BƯỚC 3: Producer Service (5 phút)
**Tạo 1 file:**

`KafkaProducerService.java`
- Method: `sendOrderEvent(OrderEvent)`
- Method: `sendUserEvent(UserEvent)`
- Chỉ cần log success/failure ra console

**Không cần:** callback phức tạp, metrics

---

### BƯỚC 4: Consumer Listeners (15 phút)
**Tạo 2 files:**

`OrderEventConsumer.java`
- Listen topic "order-events"
- Switch-case theo eventType
- Mỗi case chỉ log ra console: "Processing ORDER_CREATED for orderId=123"
- **Không cần:** gọi service thật, chỉ log để demo

`UserEventConsumer.java`
- Listen topic "user-events"
- Log ra: "Sending verification email to user@email.com"
- **Không cần:** gửi email thật

---

### BƯỚC 5: Integration vào Business Logic (10 phút)
**Sửa 2 files có sẵn:**

`OrderService.java` - thêm 1 dòng sau khi tạo order:
```java
kafkaProducerService.sendOrderEvent(new OrderEvent(order.getId(), userId, ORDER_CREATED, now()));
```

`UserService.java` - thêm 1 dòng sau khi register:
```java
kafkaProducerService.sendUserEvent(new UserEvent(user.getId(), email, USER_REGISTERED, now()));
```

**Không cần:** implement InventoryService, NotificationService thật

---

### BƯỚC 6: Test & Demo (5 phút)
**Chạy thử:**
1. Start Kafka: `docker-compose up -d`
2. Start app
3. Tạo 1 order → check console log
4. Register 1 user → check console log

**Debug commands:**
```bash
# Check topic có message không
docker exec -it <kafka-container> kafka-console-consumer --topic order-events --from-beginning --bootstrap-server localhost:9092

# Check consumer group
docker exec -it <kafka-container> kafka-consumer-groups --describe --group core-order-consumer-group --bootstrap-server localhost:9092
```

---

## 📋 CHECKLIST ĐƠN GIẢN

### Phase 1: Models (kafka-service)
- [ ] OrderEvent.java (4 fields)
- [ ] OrderEventType enum (4 values)
- [ ] UserEvent.java (4 fields)
- [ ] UserEventType enum (1 value: REGISTERED)

### Phase 2: Config (core)
- [ ] KafkaConfig.java (1 file gộp tất cả)

### Phase 3: Producer (core)
- [ ] KafkaProducerService.java (2 methods)

### Phase 4: Consumer (core)
- [ ] OrderEventConsumer.java (1 listener + switch-case + log)
- [ ] UserEventConsumer.java (1 listener + log)

### Phase 5: Integration (core)
- [ ] OrderService.java (thêm 1 dòng)
- [ ] UserService.java (thêm 1 dòng)

### Phase 6: Test
- [ ] Tạo order → check log
- [ ] Register user → check log
- [ ] Run Kafka CLI commands

---

## 🎓 DEMO ĐƯỢC GÌ?

### 1. Kafka Producer/Consumer
✅ Gửi event từ OrderService → Kafka
✅ Consumer nhận được và xử lý

### 2. Event Types
✅ ORDER_CREATED, ORDER_PAID, ORDER_CANCELLED, ORDER_DELIVERED
✅ USER_REGISTERED

### 3. Retry Mechanism
✅ Config retry 3 lần
✅ Log ra khi retry

### 4. Offset Management
✅ Consumer tự động commit offset
✅ Check offset bằng CLI

### 5. Partition
✅ Kafka tự động phân partition
✅ Check bằng CLI

---

## 🔧 CẤU HÌNH TỐI THIỂU

### application.yml (đã có, chỉ cần thêm):
```yaml
spring:
  kafka:
    consumer:
      enable-auto-commit: true  # Đơn giản hơn manual
    listener:
      ack-mode: batch
```

---

## ⚠️ KHÔNG CẦN LÀM (để đơn giản):

❌ Dead Letter Queue (DLQ)
❌ Manual offset commit
❌ Idempotent producer
❌ Transaction
❌ Implement InventoryService thật
❌ Implement NotificationService thật
❌ Gửi email thật
❌ Unit tests
❌ Integration tests
❌ Metrics/monitoring
❌ Partition strategy phức tạp

---

## 🐛 DỄ DEBUG VÌ:

1. **Tất cả đều log ra console** → nhìn thấy ngay
2. **Không có logic phức tạp** → ít bug
3. **Auto commit offset** → không lo offset
4. **Kafka CLI commands** → check được mọi thứ
5. **Chỉ 6 files mới** → dễ trace

---

## ⏱️ THỜI GIAN ƯỚC TÍNH

- Bước 1: 5 phút
- Bước 2: 10 phút
- Bước 3: 5 phút
- Bước 4: 15 phút
- Bước 5: 10 phút
- Bước 6: 5 phút

**TỔNG: ~50 phút** (nếu không gặp lỗi)

---

## 📝 TÓM TẮT THIẾU GÌ

| Component | Thiếu gì | Làm gì |
|-----------|----------|--------|
| **kafka-service** | Event models | Tạo 2 classes + 2 enums |
| **core/config** | Kafka config | Tạo 1 file config |
| **core/kafka/producer** | Producer service | Tạo 1 service |
| **core/kafka/consumer** | Consumer listeners | Tạo 2 listeners |
| **core/service** | Integration | Sửa 2 dòng code |

**TỔNG: 6 files mới + 2 files sửa**

---

## 🎯 KẾT QUẢ DEMO

Sau khi làm xong, bạn có thể:

1. **Show code:**
   - Event models
   - Producer gửi event
   - Consumer nhận event
   - Retry config

2. **Show console log:**
   - "Publishing OrderEvent: orderId=123"
   - "Received OrderEvent: orderId=123"
   - "Processing ORDER_CREATED for order 123"

3. **Show Kafka CLI:**
   - List topics
   - View messages
   - Check consumer offset

4. **Giải thích:**
   - Kafka hoạt động như thế nào
   - Tại sao dùng Kafka thay vì REST
   - Retry mechanism
   - Offset management
