# BÁO CÁO KẾT QUẢ HỌC TẬP TECH STACK BACKEND

**Dự án:** Spring E-commerce Backend  
**Người thực hiện:** (điền tên)  
**Thời gian:** 04/2026  
**Phạm vi:** Thư mục `be/` trong repository `spring-ecom-v2`

---

## MỤC LỤC

1. Mục tiêu báo cáo  
2. Phạm vi quét backend  
3. Tổng quan kiến trúc backend  
4. Tech stack đã học được  
5. Những kiến thức/chuyên môn đã học được  
6. Quy trình vận hành local  
7. Khó khăn, rủi ro và hướng cải thiện  
8. Kết luận

---

## 1) MỤC TIÊU BÁO CÁO

Báo cáo này tổng hợp các công nghệ backend em đã tiếp cận và áp dụng trong quá trình làm dự án. Nội dung tập trung vào:

- Nhận diện đúng stack kỹ thuật đang được sử dụng trong mã nguồn.
- Hệ thống hóa những bài học thực tế từ code và cấu hình.
- Rút ra kinh nghiệm để cải thiện chất lượng hệ thống cho giai đoạn tiếp theo.

---

## 2) PHẠM VI QUÉT BACKEND

Em đã quét các module và tài liệu backend sau:

- Cấu hình đa module: `be/settings.gradle`, `be/build.gradle`
- Service chính: `be/core`, `be/landing`
- Module chia sẻ giao tiếp: `be/grpc-proto`, `be/kafka-service`, `be/emqx-proto`
- Migration CSDL: `be/migration-postgres`
- Hạ tầng local: `be/docker-compose.yml`, `be/emqx.conf`
- Tài liệu kỹ thuật: `be/core/docs/RATE_LIMITING.md`
- Cấu hình runtime: `be/core/src/main/resources/application.yml`, `be/landing/src/main/resources/application.yml`

---

## 3) TỔNG QUAN KIẾN TRÚC BACKEND

Backend được tổ chức theo mô hình multi-module Gradle với Java 17, gồm 2 service chính:

- `landing`: xử lý API hướng người dùng, đóng vai trò gRPC client (gọi sang `core`) và có gRPC server cho một số luồng trao đổi.
- `core`: xử lý nghiệp vụ trung tâm, cung cấp REST cho admin, đóng vai trò gRPC server và có gRPC client cho notification.

Các module hỗ trợ:

- `grpc-proto`: định nghĩa schema protobuf và generate Java code cho gRPC.
- `kafka-service`: các model + contract dùng chung cho event Kafka.
- `emqx-proto`: model/chuẩn hóa payload cho MQTT.
- `migration-postgres`: quản lý schema database bằng Flyway.

Kiến trúc code bên trong service có xu hướng tách lớp rõ:

- `controller` (REST/gRPC)
- `service` (use case, command/query)
- `repository` (database, redis, kafka, grpc)
- `domain` (model nghiệp vụ)
- `config` (security, kafka, redis, mqtt, mapstruct...)

---

## 4) TECH STACK DA HOC DUOC

### 4.1 Nền tảng và công cụ chính

| Nhóm | Công nghệ |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot, Spring MVC |
| Build tool | Gradle (multi-module) |
| Dependency mgmt | Spring Dependency Management |
| Mapping | MapStruct |
| Boilerplate reduction | Lombok |

### 4.2 Data layer và persistence

| Nhóm | Công nghệ |
|---|---|
| ORM | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Cache/session/rate-limit state | Redis |
| Migration | Flyway (`migration-postgres`) |
| Test DB | H2 (test scope) |

### 4.3 Bảo mật và xác thực

| Nhóm | Công nghệ |
|---|---|
| Security framework | Spring Security |
| Auth token | JWT (`jjwt`) |
| Password hash | BCrypt |
| Authorization | Role/Authority + method security (`@PreAuthorize`) |
| Error handling security | Custom AuthenticationEntryPoint + AccessDeniedHandler |

### 4.4 Giao tiếp giữa service và event

| Nhóm | Công nghệ |
|---|---|
| Service-to-service RPC | gRPC (`net.devh grpc starter`) |
| Interface contract | Protocol Buffers (`grpc-proto`) |
| Event streaming | Apache Kafka + Spring Kafka |
| IoT/Realtime messaging | MQTT qua EMQX + Paho + Spring Integration MQTT |

### 4.5 Hỗ trợ API và tích hợp ngoài

| Nhóm | Công nghệ |
|---|---|
| API docs | OpenAPI/Swagger (`springdoc`) |
| File/object storage | MinIO |
| Media cloud | Cloudinary |
| Email service | Resend |
| Payment tích hợp | SePay webhook |
| Local infra orchestration | Docker Compose |

---

## 5) NHỮNG KIẾN THỨC/CHUYÊN MÔN ĐÃ HỌC ĐƯỢC

### 5.1 Thiết kế hệ thống backend theo module

- Học cách chia dự án thành nhiều module có trách nhiệm rõ ràng để dễ mở rộng và dễ maintain.
- Học cách dùng module chung (`grpc-proto`, `kafka-service`, `emqx-proto`) để tránh duplicate contract.

### 5.2 Triển khai API và phân tách nghiệp vụ

- Từ REST controller đi vào use case/service thay vì dồn logic vào controller.
- Áp dụng tư duy Command/Query trong service layer, giúp code dễ đọc và test hơn.

### 5.3 Bảo mật thực tế trong Spring

- Cấu hình Stateless security chain với JWT filter.
- Kết hợp endpoint-level rule và method-level permission để control truy cập linh hoạt.
- Hiểu rõ luồng lỗi 401/403 với custom handler.

### 5.4 Giao tiếp liên service bằng gRPC

- Định nghĩa API contract bằng `.proto`, generate stub và sử dụng blocking stub trong client.
- Tách mapper cho gRPC model <-> domain model để giảm coupling.
- Thực hành phân bổ trách nhiệm: service A gọi service B qua gRPC cho nghiệp vụ cần đồng bộ.

### 5.5 Kiến trúc event-driven với Kafka

- Xuất bản event khi có thay đổi nghiệp vụ (ví dụ user/order event).
- Tiêu thụ event, xử lý theo `eventType`, và log quan sát để debug luồng bất đồng bộ.
- Nhận diện rõ lợi ích của loose coupling giữa các thành phần.

### 5.6 Rate limiting và khả năng chịu lỗi

- Áp dụng rate limiting với Bucket4j.
- Có cơ chế fallback khi Redis không khả dụng (theo tài liệu `RATE_LIMITING.md`) để hạn chế downtime.
- Học cách thêm monitoring headers và admin endpoint để vận hành.

### 5.7 Quản lý dữ liệu và migration

- Dùng Flyway để version hóa schema, giúp đồng bộ môi trường.
- Viết migration có trình tự, rollback strategy cần được tính trước.

### 5.8 Tích hợp hạ tầng và dịch vụ ngoài

- Khởi tạo local stack bằng Docker Compose: Kafka, Zookeeper, MinIO, EMQX.
- Kết nối cloud storage (Cloudinary), object storage (MinIO), email (Resend), webhook thanh toán.

---

## 6) QUY TRÌNH VẬN HÀNH LOCAL (TÓM TẮT)

1. Chạy hạ tầng local qua `be/docker-compose.yml` (Kafka, Zookeeper, MinIO, EMQX).  
2. Chạy migration CSDL bằng module `be/migration-postgres` (Flyway).  
3. Khởi động service `core` và `landing` bằng Gradle.  
4. Kiểm tra API qua Swagger UI và test luồng liên service (REST -> gRPC -> DB / event).

---

## 7) KHÓ KHĂN, RỦI RO VÀ HƯỚNG CẢI THIỆN

### 7.1 Khó khăn đã gặp

- Hệ thống tích hợp nhiều giao thức (REST, gRPC, Kafka, MQTT) nên dễ rơi vào tình trạng khó debug nếu không có quy trình quan sát rõ.
- Cấu hình môi trường local phụ thuộc nhiều service phụ trợ.

### 7.2 Rủi ro kỹ thuật cần lưu ý

- Một số cấu hình trong `application.yml` đang để giá trị mặc định nhạy cảm cho môi trường dev.
- EMQX config dev đang mở (anonymous/allow no-match), không phù hợp production.

### 7.3 Hướng cải thiện tiếp theo

- Tách profile `dev/staging/prod` rõ ràng hơn, đưa secrets về env/secret manager.
- Bổ sung test tự động (unit + integration + contract test cho gRPC/Kafka).
- Thêm observability đầy đủ: tracing, metrics dashboard, alert.
- Chuẩn hóa CI/CD để kiểm tra migration, security scan và smoke test trước deploy.

---

## 8) KẾT LUẬN

Qua quá trình làm backend trong dự án này, em đã học được không chỉ về framework Spring Boot mà còn về cách xây dựng hệ thống backend có tính mở rộng và tích hợp đa kênh. Các bài học lớn nhất là:

- Chia tách kiến trúc và contract rõ ràng giữa các service.
- Đặt security và migration thành quy trình bắt buộc, không để sau cùng.
- Ưu tiên observability và automation để hệ thống vận hành ổn định.

=> Nền tảng kiến thức hiện tại đã đủ để tiếp tục nâng cấp theo hướng production-ready, tập trung vào test, security hardening và vận hành.

