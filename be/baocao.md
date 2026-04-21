









BÁO CÁO DỰ ÁN

MỤC LỤC
MỤC LỤC	2
I. Nội dung đề tài:	3
II. Kiến trúc tổng quan:	3
III. Công nghệ sử dụng và tổ chức code	3
3.1. Công nghệ chính	3
3.2. Các thư viện sử dụng:	4
3.3. Cách tổ chức code:	4
IV. Bảng dữ liệu và API	5
4.1. Các bảng chính:	5
4.2. Các mối quan hệ:	5
V. Các API chính	6
6.1. Xác thực (/v1/api/auth)	6
6.2. Người dùng (/v1/api/user)	6
6.3. Sản phẩm (/v1/api/products)	6
6.4. Danh mục (/v1/api/categories)	7
6.5. Giỏ hàng (/v1/api/cart)	7
6.6. Đơn hàng (/v1/api/orders)	7
6.7. Thông báo (/v1/api/notifications)	7
6.8. File (/v1/api/files)	7
6.9. Admin — Sản phẩm (/v1/api/admin/products)	8
6.10. Admin — Đơn hàng (/v1/api/admin/orders)	8
6.11. Admin — Nhà cung cấp (/v1/api/admin/suppliers)	8
6.12. Admin — Đơn nhập hàng & Kho (/v1/api/admin/inventory)	8
6.13. Admin — Thống kê (/v1/api/admin/statistics)	8
VI. Các luồng nghiệp vụ trong dự án	9
5.1. Xác thực và phân quyền	9
5.2. Luồng thêm vào giỏ hàng và tạo đơn hàng	9
5.3. Luồng đồng bộ kho qua Kafka (Stock)	9
5.4. Admin quản lý	9
5.5. Gửi Notification MQTT	10
5.6. Upload file hình ảnh Minio	10
5.7. Luồng nhập hàng (Supplier -> Purchase Order -> Nhập kho)	10
5.8. Luồng tính doanh thu - giá vốn - lợi nhuận	10
5.9. Xử lý race condition khi 2 người cùng mua 1 sản phẩm	10

I. Nội dung đề tài:
- Trong quá trình tìm hiểu ngôn ngữ và công cụ mới, em đã thực hành một dự án e-commerce bán sách được xây dựng theo mô hình multi-module, sử dụng Gradle với Java 17 và Spring Boot theo yêu cầu và thực hiện các tính năng sau:

Quản lý phiên đăng nhập và giới hạn truy cập với Redis.
Quản lý Database Migration với Flyway
Xác thực và phân quyền người dùng Spring Security
Giao tiếp giữa service bằng gRPC
Truyền sự kiện bằng Kafka
Gửi thông báo realtime qua MQTT
Quản lý file hình ảnh với Minio
Quản lý nhà cung cấp, đơn nhập hàng
Theo dõi giá vốn theo lô nhập và thống kê doanh thu lợi nhuận
II. Kiến trúc tổng quan:
Backend được tổ chức theo mô hình multi-module Gradle, trong đó settings.gradle khai báo 6 module:
landing
core
grpc-proto
emqx-proto
kafka-proto
Migration-postgres

Hệ thống xoay quanh 2 service chính:

landing: Là service cung cấp API cho phía client/user. Là gRPC Client khi cần gọi sang service core, chạy HTTP ở 8080 và gRPC ở 9090.
core: Là service xử lý nghiệp vụ chính. Cung cấp API cho admin là gRPC Server để nhận request từ landing, chạy HTTP ở 8081 và gRPC ở 9091.

III. Công nghệ sử dụng và tổ chức code
3.1. Công nghệ chính

| Loại | Công cụ | Tại sao sử dụng | Điểm mạnh | Điểm yếu |
|------|---------|-----------------|------------|-----------|
| Ngôn ngữ | Java 17 | Là bản LTS ổn định, hệ sinh thái Spring Boot đầy đủ, hỗ trợ tốt cho dự án backend quy mô vừa/lớn, dễ bảo trì | Strongly-typed giúp phát hiện lỗi sớm lúc compile; hệ sinh thái thư viện rất lớn; hỗ trợ multi-threading tốt; cộng đồng đông đảo, tài liệu phong phú; JVM tối ưu hiệu năng tự động (JIT, GC) | Cú pháp dài dòng (verbose) hơn so với Kotlin, Python; thời gian khởi động ứng dụng chậm hơn Node.js; tiêu tốn bộ nhớ nhiều hơn các ngôn ngữ nhẹ |
| CSDL | PostgreSQL | Hỗ trợ transaction ACID tốt, phù hợp bài toán đơn hàng - tồn kho cần tính chính xác | Hỗ trợ ACID transaction đầy đủ, đảm bảo tính toàn vẹn dữ liệu; hỗ trợ row-level locking (SELECT FOR UPDATE) rất tốt cho bài toán race condition; hỗ trợ nhiều kiểu dữ liệu (JSON, Array, UUID); hiệu năng cao với dữ liệu lớn; mã nguồn mở, miễn phí | Cấu hình tối ưu phức tạp hơn MySQL; tiêu tốn tài nguyên RAM nhiều hơn ở cấu hình mặc định; replication và clustering phức tạp hơn một số NoSQL |
| Frontend | ReactJS, Vite | Xây dựng giao diện người dùng nhanh, component-based dễ tái sử dụng | React có cộng đồng lớn nhất, nhiều thư viện hỗ trợ; Vite build cực nhanh nhờ ESBuild, Hot Module Replacement (HMR) gần như tức thì; component-based dễ chia nhỏ và bảo trì | Chỉ là thư viện UI, cần thêm nhiều thư viện bổ sung (routing, state management); learning curve với hooks và state management phức tạp |
| Build Tool | Gradle (Multi-module) | Quản lý nhiều module trong một project, chia sẻ dependency và cấu hình chung | Hỗ trợ multi-module project tốt, mỗi module build độc lập; incremental build nhanh hơn Maven; cú pháp Groovy/Kotlin DSL linh hoạt | Cú pháp phức tạp hơn Maven (pom.xml); debug build script khó hơn; thời gian học ban đầu cao hơn |
| Container | Docker & Docker Compose | Đóng gói và khởi chạy toàn bộ hạ tầng (PostgreSQL, Redis, Kafka, EMQX, Minio) bằng một lệnh duy nhất | Môi trường đồng nhất giữa dev/staging/production; dễ dàng scale từng service; dọn dẹp sạch sẽ khi không cần | Tốn tài nguyên hơn chạy native; networking giữa các container đôi khi phức tạp; cần kiến thức thêm về Docker |
| Message Broker | Apache Kafka | Xử lý sự kiện bất đồng bộ giữa các nghiệp vụ (đơn hàng, tồn kho) mà không blocking user | Throughput cực cao (hàng triệu message/giây); message được lưu bền vững (durable), không mất khi consumer chưa xử lý; đảm bảo thứ tự trong cùng partition; hỗ trợ consumer group để scale; replay message khi cần | Hạ tầng phức tạp (cần Zookeeper/KRaft, broker); learning curve cao; không phù hợp cho hệ thống message ít và đơn giản; cần quản lý offset, partition cẩn thận |
| Realtime | MQTT (EMQX Broker) | Gửi thông báo realtime đến frontend mà không cần client polling liên tục, tiết kiệm tài nguyên | Giao thức nhẹ, tiêu tốn ít bandwidth; hỗ trợ QoS đảm bảo tin nhắn được gửi đến; mô hình pub/sub linh hoạt; EMQX hỗ trợ hàng triệu kết nối đồng thời | Cần duy trì broker riêng; debug khó hơn REST API; không có cơ chế request-response như HTTP; client mất kết nối thì mất message (trừ khi dùng persistent session) |
| Giao tiếp service | gRPC (Protocol Buffers) | Giao tiếp nhanh giữa landing và core, đảm bảo contract giữa 2 service luôn đồng bộ | Dùng binary format, nhanh hơn JSON/REST 2-10 lần; contract-first với file .proto, 2 service luôn khớp nhau; tự sinh code client/server; hỗ trợ streaming (bidirectional) | Khó debug hơn REST (binary format); không hỗ trợ trực tiếp từ browser (cần gRPC-Web); cần quản lý file .proto chung; learning curve cao hơn REST |





3.2. Các thư viện sử dụng:

**Spring Web MVC**: xây dựng REST API, xử lý request/response theo mô hình MVC.
- *Điểm mạnh*: Mô hình MVC rõ ràng, dễ tổ chức code; tích hợp sẵn với toàn bộ hệ sinh thái Spring; hỗ trợ đầy đủ RESTful convention; cộng đồng lớn, tài liệu phong phú.
- *Điểm yếu*: Khởi động chậm hơn framework nhẹ (Micronaut, Quarkus); tiêu tốn bộ nhớ nhiều hơn; blocking I/O (cần chuyển sang WebFlux nếu muốn non-blocking).
- *Tại sao dùng*: Là framework chuẩn công nghiệp cho Java backend, phù hợp xây dựng REST API có cấu trúc rõ ràng và dễ mở rộng.

**Spring Validation**: kiểm tra dữ liệu (request body, query param) trước khi xử lý.
- *Điểm mạnh*: Validate tự động bằng annotation (@NotNull, @Size, @Email...); tích hợp tốt với Spring MVC, chỉ cần thêm @Valid; custom validator dễ dàng.
- *Điểm yếu*: Thông báo lỗi mặc định bằng tiếng Anh, cần custom lại; validation logic phức tạp thì annotation không đủ, phải viết custom validator.
- *Tại sao dùng*: Giúp kiểm tra dữ liệu đầu vào ngay tại controller, tránh dữ liệu sai đi sâu vào business logic, giảm bug.

**Spring Data JPA**: truy vấn dữ liệu qua repository, giảm code SQL thủ công.
- *Điểm mạnh*: Tự sinh query từ tên method (findByEmail, findByStatus...); hỗ trợ phân trang (Pageable) và sắp xếp sẵn; giảm đáng kể code boilerplate cho CRUD.
- *Điểm yếu*: Query phức tạp (join nhiều bảng, subquery) phải viết @Query hoặc native SQL; dễ gặp vấn đề N+1 query nếu không hiểu rõ lazy/eager loading; khó tối ưu performance cho truy vấn đặc thù.
- *Tại sao dùng*: Giảm thời gian viết code truy vấn, tập trung vào nghiệp vụ thay vì viết SQL thủ công.

**Hibernate**: ORM ánh xạ entity với bảng dữ liệu.
- *Điểm mạnh*: Ánh xạ tự động giữa Java object và bảng database; hỗ trợ caching (L1, L2) tăng hiệu năng; hỗ trợ Optimistic/Pessimistic Locking qua annotation; quản lý quan hệ (OneToMany, ManyToMany) tự động.
- *Điểm yếu*: Sinh ra SQL không tối ưu trong một số trường hợp; vấn đề N+1 query nếu cấu hình sai; debug khó hơn so với viết SQL thuần; learning curve cao khi gặp các tình huống nâng cao (lazy loading, detached entity).
- *Tại sao dùng*: Là ORM phổ biến nhất trong Java, hỗ trợ @Version cho optimistic locking và @Lock cho pessimistic locking — rất cần thiết cho bài toán race condition tồn kho.

**Spring Security**: quản lý xác thực và phân quyền.
- *Điểm mạnh*: Bảo mật toàn diện (authentication + authorization); hỗ trợ nhiều cơ chế xác thực (JWT, OAuth2, Session); phân quyền linh hoạt qua @PreAuthorize với SpEL expression; tích hợp sâu vào Spring ecosystem.
- *Điểm yếu*: Cấu hình phức tạp, khó debug khi filter chain gặp lỗi; learning curve cao; tài liệu đôi khi khó hiểu với người mới.
- *Tại sao dùng*: Là giải pháp bảo mật chuẩn cho Spring Boot, hỗ trợ phân quyền chi tiết theo permission (PRODUCT_CREATE, ORDER_UPDATE...) phù hợp với hệ thống phân quyền RBAC của dự án.

**JWT (jjwt)**: tạo và xác minh access token/refresh token cho đăng nhập.
- *Điểm mạnh*: Stateless — server không cần lưu session, dễ scale ngang; payload chứa thông tin user, giảm truy vấn database; hỗ trợ thuật toán mã hóa mạnh (HS256, RS256).
- *Điểm yếu*: Không thể thu hồi token ngay lập tức (phải dùng blacklist trên Redis); token bị lộ thì kẻ tấn công dùng được đến khi hết hạn; payload lớn hơn session ID.
- *Tại sao dùng*: Phù hợp cho kiến trúc multi-service (landing + core), không cần chia sẻ session giữa các service; kết hợp Redis blacklist để thu hồi token khi logout.

**BCryptPasswordEncoder**: mã hóa mật khẩu khi lưu vào database.
- *Điểm mạnh*: Thuật toán hash một chiều, không thể giải mã ngược; tự sinh salt ngẫu nhiên mỗi lần hash; điều chỉnh được độ phức tạp (cost factor) theo thời gian.
- *Điểm yếu*: Tốn tài nguyên CPU hơn MD5/SHA (nhưng đây chính là ưu điểm bảo mật); không phù hợp cho mã hóa dữ liệu cần giải mã ngược.
- *Tại sao dùng*: Là thuật toán hash mật khẩu được khuyến nghị bởi OWASP, chống brute-force tốt nhờ tốc độ hash chậm có chủ đích.

**Redis**: lưu dữ liệu tạm session, trạng thái rate limit.
- *Điểm mạnh*: Tốc độ đọc/ghi cực nhanh (in-memory); hỗ trợ TTL (Time To Live) tự động xóa dữ liệu hết hạn; hỗ trợ nhiều cấu trúc dữ liệu (String, Hash, Set, Sorted Set); phù hợp cho cache, session, rate limiting.
- *Điểm yếu*: Dữ liệu nằm trên RAM, tốn bộ nhớ; mất dữ liệu nếu server restart (cần cấu hình persistence RDB/AOF); không phù hợp lưu trữ dữ liệu lớn dài hạn.
- *Tại sao dùng*: Dùng để lưu JWT blacklist (logout), rate limit counter (giới hạn login/register), session management — đều cần tốc độ cao và tự hết hạn.

**Flyway**: quản lý database migration.
- *Điểm mạnh*: Version control cho database schema; migration tự động khi ứng dụng khởi động; đảm bảo schema đồng bộ giữa các môi trường (dev, staging, production); rollback khi migration lỗi.
- *Điểm yếu*: Migration đã chạy không được sửa; rollback phức tạp (phải viết migration mới để revert); cần đặt tên file đúng convention.
- *Tại sao dùng*: Đảm bảo thay đổi database được quản lý có trật tự, tránh tình trạng schema khác nhau giữa các môi trường, đặc biệt quan trọng khi dự án có nhiều bảng phức tạp.

**Spring Integration MQTT + Eclipse Paho MQTT Client**: tích hợp MQTT để gửi/nhận thông báo realtime.
- *Điểm mạnh*: Giao thức nhẹ, tiêu tốn ít bandwidth; hỗ trợ QoS (Quality of Service) đảm bảo tin nhắn được gửi đến; mô hình pub/sub linh hoạt; phù hợp cho push notification realtime; EMQX broker hỗ trợ hàng triệu kết nối đồng thời.
- *Điểm yếu*: Cần duy trì broker (EMQX/Mosquitto) riêng; debug khó hơn REST API; không có cơ chế request-response như HTTP; client mất kết nối thì mất message (trừ khi dùng persistent session).
- *Tại sao dùng*: Gửi thông báo realtime đến frontend (đơn hàng mới, cập nhật trạng thái...) mà không cần client polling liên tục, tiết kiệm tài nguyên server.

**MapStruct**: mapping code giữa entity, domain, req/res DTO và proto.
- *Điểm mạnh*: Sinh code mapping lúc compile-time, hiệu năng cao hơn reflection-based (ModelMapper, Dozer); type-safe, lỗi mapping phát hiện lúc compile; hỗ trợ custom mapping linh hoạt; không tốn tài nguyên runtime.
- *Điểm yếu*: Cần viết interface/abstract class cho mỗi mapper; cấu hình phức tạp khi mapping nested object hoặc nhiều source; phải rebuild khi thay đổi mapping.
- *Tại sao dùng*: Dự án có nhiều layer (Entity ↔ Domain ↔ DTO ↔ Proto), MapStruct giúp chuyển đổi dữ liệu giữa các layer nhanh và an toàn, tránh lỗi mapping runtime.

**Lombok**: giảm boilerplate code (getter/setter, constructor, builder...).
- *Điểm mạnh*: Giảm đáng kể code boilerplate; annotation đơn giản (@Data, @Builder, @RequiredArgsConstructor); code ngắn gọn, dễ đọc hơn.
- *Điểm yếu*: Code thực tế bị ẩn đi, debug khó hơn; phụ thuộc vào plugin IDE; một số annotation (@Data) có thể gây bug nếu dùng không đúng (equals/hashCode với entity).
- *Tại sao dùng*: Giảm lượng code lặp lại trong entity, DTO, service — giúp tập trung vào logic nghiệp vụ thay vì viết getter/setter thủ công.

**Apache Kafka**: xử lý sự kiện bất đồng bộ (đơn hàng, tồn kho).
- *Điểm mạnh*: Throughput cực cao, xử lý hàng triệu message/giây; đảm bảo thứ tự message trong cùng partition; lưu trữ message bền vững (durable), không mất khi consumer chưa xử lý; hỗ trợ consumer group để scale xử lý; replay message khi cần.
- *Điểm yếu*: Hạ tầng phức tạp (cần Zookeeper/KRaft, broker, topic management); learning curve cao; không phù hợp cho message ít và đơn giản; cần quản lý offset, partition cẩn thận.
- *Tại sao dùng*: Xử lý bất đồng bộ các event tồn kho (ORDER_CREATED, ORDER_PAID, ORDER_CANCELLED...) giúp tách biệt nghiệp vụ đặt hàng và nghiệp vụ kho, tránh blocking user khi đặt hàng; đảm bảo mỗi event chỉ xử lý đúng 1 lần.

**gRPC (Google Remote Procedure Call)**: giao tiếp nhanh giữa các service.
- *Điểm mạnh*: Dùng Protocol Buffers (binary), nhanh hơn JSON/REST 2-10 lần; hỗ trợ streaming (bidirectional); contract-first với file .proto, đảm bảo 2 service luôn đồng bộ; tự sinh code client/server từ proto file.
- *Điểm yếu*: Khó debug hơn REST (binary format); không hỗ trợ trực tiếp từ browser (cần gRPC-Web); cần quản lý file .proto chung giữa các service; learning curve cao hơn REST.
- *Tại sao dùng*: Service landing cần gọi sang core để lấy dữ liệu (sản phẩm, đơn hàng...), gRPC nhanh hơn REST HTTP đáng kể và đảm bảo contract giữa 2 service luôn khớp nhau qua file proto chung.

**Minio**: quản lý file hình ảnh.
- *Điểm mạnh*: Tương thích API Amazon S3, dễ chuyển đổi sang S3 khi cần; mã nguồn mở, self-hosted, không tốn phí cloud; hỗ trợ presigned URL để frontend truy cập trực tiếp; hỗ trợ bucket policy, versioning.
- *Điểm yếu*: Cần tự quản lý server và backup; không có CDN tích hợp sẵn như S3 + CloudFront; tốn dung lượng ổ cứng server.
- *Tại sao dùng*: Lưu trữ hình ảnh sản phẩm, avatar người dùng với chi phí thấp; presigned URL giúp frontend truy cập ảnh trực tiếp mà không cần đi qua backend, giảm tải server.

3.3. Cách tổ chức code:

**Controller**: tiếp nhận request REST/gRPC, map request/response thành Domain để gọi vào UseCase, không chứa business logic.
- *Điểm mạnh*: Tách biệt rõ ràng giữa tầng giao tiếp và nghiệp vụ; dễ thay đổi giao thức (REST → gRPC) mà không ảnh hưởng logic.
- *Điểm yếu*: Cần viết thêm code mapping giữa request/response và domain model.

**Service (UseCase — tách Command/Query)**: xử lý nghiệp vụ chính của hệ thống theo từng use case. Tại đây áp dụng nguyên tắc tách biệt giữa Command và Query.
- *Điểm mạnh*: Command (ghi) và Query (đọc) tách riêng, code rõ ràng, dễ bảo trì; dễ tối ưu hiệu năng riêng cho đọc và ghi; mỗi use case là một class riêng, dễ test và mở rộng.
- *Điểm yếu*: Số lượng class nhiều hơn so với gom chung; một số nghiệp vụ đơn giản thì tách ra hơi thừa.
- *Tại sao dùng*: Áp dụng nguyên tắc CQRS (Command Query Responsibility Segregation) giúp dễ dàng scale và bảo trì khi hệ thống phức tạp lên.

**Repository**: chịu trách nhiệm truy cập vào database và tích hợp với các hệ thống bên ngoài như Redis, Kafka, MQTT và gRPC client.
- *Điểm mạnh*: Tách biệt logic truy cập dữ liệu khỏi business logic; dễ thay đổi nguồn dữ liệu (database, cache, API bên ngoài) mà không ảnh hưởng service.
- *Điểm yếu*: Cần viết adapter/wrapper cho mỗi nguồn dữ liệu bên ngoài.

**Domain**: chứa các mô hình nghiệp vụ cốt lõi (business model), được sử dụng làm chuẩn dữ liệu xuyên suốt giữa các layer, giúp đảm bảo tính nhất quán trong hệ thống.
- *Điểm mạnh*: Các layer giao tiếp qua domain model chung, tránh phụ thuộc vào entity hay DTO; thay đổi database schema không ảnh hưởng đến business logic.
- *Điểm yếu*: Cần mapper để chuyển đổi giữa domain ↔ entity ↔ DTO, tăng thêm code.

**Config**: quản lý cấu hình hệ thống (security, redis, kafka, mqtt, jwt, cors...).
- *Điểm mạnh*: Tập trung cấu hình một chỗ, dễ quản lý và thay đổi; tách biệt cấu hình khỏi logic nghiệp vụ.
- *Điểm yếu*: Khi hệ thống lớn, số lượng config class nhiều, cần tổ chức theo package rõ ràng.

IV. Bảng dữ liệu

Link Draw.io
V. Các API chính
6.1. Xác thực (/v1/api/auth)
POST /login — đăng nhập (rate limit 5 lần/phút)
POST /register — đăng ký (rate limit 3 lần/phút)
POST /refresh — làm mới access token
POST /logout — đăng xuất
POST /forgot-password — gửi email reset mật khẩu
POST /reset-password — đặt lại mật khẩu
6.2. Người dùng (/v1/api/user)
GET /me — xem thông tin cá nhân
PUT /me — cập nhật hồ sơ
PUT /me/avatar — cập nhật ảnh đại diện
PUT /me/password — đổi mật khẩu
6.3. Sản phẩm (/v1/api/products)
GET / — danh sách sản phẩm (phân trang)
GET /{id} — chi tiết sản phẩm theo ID
GET /slug/{slug} — chi tiết sản phẩm theo slug
GET /category/{slug} — sản phẩm theo danh mục
GET /search?keyword= — tìm kiếm sản phẩm
GET /bestseller — sản phẩm bán chạy
6.4. Danh mục (/v1/api/categories)
GET / — danh sách tất cả danh mục
GET /{id} — danh mục theo ID
GET /slug/{slug} — danh mục theo slug
6.5. Giỏ hàng (/v1/api/cart)
GET / — xem giỏ hàng
POST /items — thêm sản phẩm vào giỏ
PUT /items/{productId} — cập nhật số lượng
DELETE /items/{productId} — xóa sản phẩm khỏi giỏ
DELETE / — xóa toàn bộ giỏ hàng
POST /sync — đồng bộ giỏ hàng từ local
6.6. Đơn hàng (/v1/api/orders)
POST / — tạo đơn hàng từ giỏ
GET /my-orders — danh sách đơn hàng của user
GET /my-orders/status/{status} — đơn hàng theo trạng thái
GET /{id}/detail — chi tiết đơn hàng + sản phẩm
POST /{id}/cancel — hủy đơn hàng
POST /{id}/partial-cancel — hủy một phần đơn hàng
POST /{orderNumber}/create-payment — tạo thông tin thanh toán
GET /{orderNumber}/payment-status — kiểm tra thanh toán
6.7. Thông báo (/v1/api/notifications)
GET / — danh sách thông báo (phân trang)
GET /unread — thông báo chưa đọc
GET /unread/count — số lượng chưa đọc
PUT /read — đánh dấu đã đọc
PUT /read/all — đánh dấu tất cả đã đọc
6.8. File (/v1/api/files)
POST /upload — upload file (trả public URL)
POST /upload/presigned — upload file (trả presigned URL)
GET /presigned/{filename} — lấy presigned URL cho file có sẵn
GET /list — danh sách file trong bucket
GET /download/{filename} — tải file
DELETE /delete/{filename} — xóa file
6.9. Admin — Sản phẩm (/v1/api/admin/products)
GET / — danh sách sản phẩm (phân trang, search, filter)
GET /{productId} — chi tiết sản phẩm
POST / — tạo sản phẩm mới
PUT /{productId} — cập nhật sản phẩm
DELETE /{productId} — xóa sản phẩm (soft delete)
PUT /{productId}/stock — cập nhật tồn kho
6.10. Admin — Đơn hàng (/v1/api/admin/orders)
GET / — danh sách đơn hàng (filter status, ngày, search)
GET /{orderId} — chi tiết đơn hàng
PUT /{orderId}/status — cập nhật trạng thái đơn
PUT /{orderId}/payment-status — cập nhật trạng thái thanh toán
POST /{orderId}/cancel — hủy đơn hàng
GET /statistics — thống kê đơn hàng theo kỳ
6.11. Admin — Nhà cung cấp (/v1/api/admin/suppliers)
GET / — danh sách nhà cung cấp (phân trang, search)
GET /{id} — chi tiết nhà cung cấp
POST / — tạo nhà cung cấp
PUT /{id} — cập nhật nhà cung cấp
DELETE /{id} — xóa nhà cung cấp (soft delete)
6.12. Admin — Đơn nhập hàng & Kho (/v1/api/admin/inventory)
GET /purchase-orders — danh sách đơn nhập (filter status, supplier)
GET /purchase-orders/{id} — chi tiết đơn nhập + items
POST /purchase-orders — tạo đơn nhập hàng
PUT /purchase-orders/{id} — sửa đơn nhập (chỉ DRAFT)
POST /purchase-orders/{id}/confirm — xác nhận đơn nhập
POST /purchase-orders/{id}/receive — nhận hàng (cộng kho + tạo lô giá vốn)
POST /purchase-orders/{id}/cancel — hủy đơn nhập
GET /movements — lịch sử biến động kho (filter product, type)
6.13. Admin — Thống kê (/v1/api/admin/statistics)
GET /dashboard — tổng quan (doanh thu, giá vốn, lợi nhuận, đơn hàng, tồn kho)
GET /revenue — doanh thu theo kỳ (daily/weekly/monthly)
GET /profit — lợi nhuận theo kỳ
GET /top-products — sản phẩm bán chạy nhất
GET /revenue-by-category — doanh thu theo danh mục
GET /inventory-valuation — tổng giá trị tồn kho



VI. Các luồng nghiệp vụ trong dự án
5.1. Xác thực và phân quyền
Người dùng gọi API đăng nhập/đăng ký tại service landing.
Hệ thống kiểm tra thông tin tài khoản, mã hóa mật khẩu và tạo JWT (access token, refresh token).
Thông tin phiên đăng nhập (token/session) được lưu trữ thông qua Redis.
Với các request tiếp theo, JwtAuthenticationFilter sẽ đọc token
SecurityConfig kết hợp với annotation @PreAuthorize thực hiện kiểm tra role/authority trước khi cho phép truy cập API.
5.2. Luồng thêm vào giỏ hàng và tạo đơn hàng
Khi đặt hàng, OrderCommandService lấy danh sách sản phẩm từ giỏ hàng.
Hệ thống kiểm tra giỏ hàng và xác thực tồn kho của từng sản phẩm.
Nếu có áp dụng coupon, service sẽ kiểm tra và tính toán giảm giá.
Hệ thống tạo OrderEntity, sinh orderNumber và các OrderItem tương ứng.
Sau khi lưu đơn hàng thành công, giỏ hàng sẽ được làm sạch.
Event ORDER_CREATED được publish lên Kafka để xử lý các bước tiếp theo.

5.3. Luồng đồng bộ kho qua Kafka (Stock)
Cập nhật trạng thái đơn hàng dựa trên vòng đời của đơn hàng một cách bất đồng bộ.
ORDER_CREATED: giữ chỗ tồn kho.
ORDER_CANCELLED: hoàn lại tồn kho.
ORDER_PAID: chuyển trạng thái từ reserved sang sold.
ORDER_DELIVERED: xác nhận số lượng đã bán thành công.
5.4. Admin quản lý
Admin truy cập các API tại core (/v1/api/admin/**).
Spring Security xác thực token và kiểm tra authority tương ứng (PRODUCT_CREATE, COUPON_CREATE,...).
Controller gọi các UseCase để thực hiện CRUD dữ liệu (sản phẩm, danh mục, đơn hàng, coupon...).
5.5. Gửi Notification MQTT
Một service phát sinh sự kiện thông báo.
NotificationUseCase xử lý nội dung và lưu vào database Notification, sau đó đưa dữ liệu này cho MQTT publisher.
MQTT publisher gửi payload lên broker (EMQX/MQTT).
Client Frontend đã subscribe topic sẽ nhận được thông báo gần thời gian thực.
5.6. Upload file hình ảnh Minio
Khi nhận request từ client, FileCommandService kiểm tra Content-Type và kích thước (max 10MB).
FileCommandService upload file lên Minio bucket qua MinioClient, sau đó trả về FileUploadResponse chứa id, url, filename để lưu vào database.
FileQueryService tạo presigned URL có thời hạn để frontend truy cập hình ảnh.

5.7. Luồng nhập hàng (Supplier -> Purchase Order -> Nhập kho)
Admin tạo nhà cung cấp (supplier) và tạo đơn nhập hàng (purchase order) với danh sách sản phẩm, số lượng, đơn giá nhập.
Đơn nhập đi qua các trạng thái DRAFT -> CONFIRMED -> RECEIVED/CANCELLED. Khi xác nhận nhập hàng, hệ thống cộng tồn kho sản phẩm, ghi nhận lịch sử kho trong inventory_transactions. Đồng thời hệ thống tạo bản ghi lô giá vốn trong product_cost_batches để theo dõi giá vốn theo từng lần nhập.
5.8. Luồng tính doanh thu - giá vốn - lợi nhuận
Doanh thu lấy từ các đơn đã hoàn tất theo khoảng thời gian thống kê. Giá vốn (COGS) được tính dựa trên dữ liệu giá vốn đã chốt ở order_item hoặc từ cơ chế lô giá vốn. Lợi nhuận gộp = Doanh thu - Giá vốn. Các API thống kê admin trả về dashboard tổng quan, doanh thu theo kỳ, lợi nhuận theo kỳ, top sản phẩm.
5.9. Xử lý race condition khi 2 người cùng mua 1 sản phẩm
Khi nhiều người cùng mua 1 sản phẩm, hệ thống kết hợp nhiều kỹ thuật để tránh bán vượt tồn kho:

**Pessimistic Locking**: ProductRepository dùng @Lock(PESSIMISTIC_WRITE) với method findByIdWithLock(). Khi nhập hàng hoặc cập nhật tồn kho, database sẽ khóa dòng sản phẩm đó lại (SELECT ... FOR UPDATE), người khác phải chờ đến khi transaction hoàn tất mới được truy cập.
- *Điểm mạnh*: Đảm bảo tuyệt đối không có 2 transaction cùng sửa một dòng dữ liệu; đơn giản, dễ hiểu, dễ triển khai; phù hợp khi xung đột xảy ra thường xuyên (high contention).
- *Điểm yếu*: Giảm throughput vì các request phải xếp hàng chờ; có nguy cơ deadlock nếu lock nhiều bảng/dòng cùng lúc; giữ lock lâu ảnh hưởng hiệu năng toàn hệ thống.
- *Tại sao dùng*: Dùng cho các thao tác nhập hàng và cập nhật tồn kho — những nghiệp vụ yêu cầu tính chính xác tuyệt đối và tần suất thực hiện không quá cao (admin mới thao tác), nên chi phí lock chấp nhận được.

**Optimistic Locking**: ProductEntity có trường @Version. Mỗi lần cập nhật sản phẩm, Hibernate tự kiểm tra version có khớp không. Nếu 2 request cùng đọc version=5, người đầu tiên cập nhật thành version=6 thành công, người thứ hai sẽ bị lỗi vì version không còn là 5 nữa, buộc phải thử lại.
- *Điểm mạnh*: Không khóa dòng dữ liệu, throughput cao hơn pessimistic locking; không có deadlock; phù hợp khi xung đột ít xảy ra (low contention); Hibernate tự quản lý version, không cần code thêm.
- *Điểm yếu*: Khi xung đột xảy ra, request thất bại phải retry — tốn tài nguyên; nếu contention cao, tỷ lệ retry lớn dẫn đến trải nghiệm kém; cần xử lý exception OptimisticLockException đúng cách.
- *Tại sao dùng*: Dùng cho các thao tác cập nhật sản phẩm thông thường — phần lớn thời gian chỉ có 1 admin sửa, xung đột hiếm khi xảy ra, nên optimistic locking giữ hiệu năng tốt mà vẫn an toàn.

**Stock Reservation (giữ chỗ tồn kho)**: khi tạo đơn, hệ thống không trừ cứng tồn kho mà tạo bản ghi stock_reservations để giữ chỗ. Scheduler tự nhả giữ chỗ nếu quá hạn (expire time), trả lại số lượng cho hệ thống.
- *Điểm mạnh*: Không trừ tồn kho ngay, tránh mất hàng khi user đặt rồi không thanh toán; Scheduler tự dọn dẹp reservation hết hạn, trả lại tồn kho; tách biệt giữa "đang giữ" và "đã bán", dễ theo dõi trạng thái kho chính xác.
- *Điểm yếu*: Cần thêm bảng và logic quản lý reservation; Scheduler phải chạy đúng tần suất, nếu chậm thì tồn kho bị giữ lâu không cần thiết; tăng độ phức tạp của hệ thống.
- *Tại sao dùng*: Giải quyết bài toán "đặt hàng nhưng chưa thanh toán" — giữ chỗ tồn kho tạm thời cho user, nếu quá hạn thì tự động trả lại. Đây là pattern phổ biến trong e-commerce thực tế.

**Event-driven qua Kafka**: các thao tác trừ/hoàn kho được xử lý bất đồng bộ qua event Kafka (ORDER_PAID, ORDER_CANCELLED, ORDER_DELIVERED...), đảm bảo mỗi sự kiện chỉ xử lý đúng 1 lần, tránh trừ kho lặp.
- *Điểm mạnh*: Tách biệt nghiệp vụ đặt hàng và nghiệp vụ kho, giảm coupling; xử lý bất đồng bộ không blocking user; Kafka đảm bảo message không mất (durable); consumer group hỗ trợ scale khi lượng đơn hàng tăng; hỗ trợ replay event khi cần xử lý lại.
- *Điểm yếu*: Eventual consistency — dữ liệu kho có thể chưa cập nhật ngay lập tức; cần xử lý idempotency (tránh xử lý trùng event); debug khó hơn so với xử lý đồng bộ; cần hạ tầng Kafka riêng.
- *Tại sao dùng*: Khi tạo đơn hàng, user không cần chờ hệ thống cập nhật kho xong mới nhận response. Event Kafka đảm bảo các bước trừ kho, hoàn kho được xử lý đúng thứ tự và chỉ 1 lần, tránh tình trạng trừ kho lặp khi có lỗi mạng hoặc retry.


IV. Bảng dữ liệu
Dự án sử dụng PostgreSQL với các bảng nghiệp vụ chính:
users: lưu thông tin tài khoản (password, role).
user_info: lưu thông tin hồ sơ mở rộng của người dùng (phone, address). Liên kết với users qua user_id.
roles: danh sách các vai trò trong hệ thống.
permissions: danh sách các quyền chi tiết (READ, WRITE, UPDATE).
role_permissions: bảng liên kết giữa role và permission. (roles ← role_permissions → permissions)
user_roles: bảng liên kết giữa user và role. (users ← user_roles → roles)
categories: danh mục sản phẩm.
products: thông tin sản phẩm, tồn kho hiện tại và giá vốn. Liên kết với categories qua category_id.
carts: lưu thông tin giỏ hàng theo từng người dùng. Liên kết với users qua user_id.
cart_items: lưu danh sách sản phẩm trong giỏ hàng. Liên kết với carts qua cart_id và products qua product_id.
orders: lưu thông tin tổng quan của đơn hàng. Liên kết với users qua user_id, có thể liên kết với coupons qua coupon_id.
order_items: lưu chi tiết từng sản phẩm trong đơn hàng, giá vốn tại thời điểm bán. Liên kết với orders qua order_id và products qua product_id.
stock_reservations: lưu thông tin giữ chỗ tồn kho trước khi thanh toán hoàn tất. Liên kết với orders qua order_id và products qua product_id.
coupons: lưu thông tin mã giảm giá và các điều kiện áp dụng.
notifications: lưu lịch sử thông báo (cá nhân hoặc toàn bộ), đồng bộ với hệ thống gửi thông báo qua MQTT. Liên kết với users qua user_id (nullable cho broadcast).
notification_user_reads: lưu trạng thái đã đọc của từng user đối với thông báo broadcast. Liên kết với notifications qua notification_id và users qua user_id.
suppliers: lưu nhà cung cấp sách.
purchase_orders: lưu phiếu/đơn nhập hàng từ nhà cung cấp. Liên kết với suppliers qua supplier_id và users qua created_by.
purchase_order_items: lưu chi tiết từng sản phẩm trong đơn nhập. Liên kết với purchase_orders qua purchase_order_id và products qua product_id.
inventory_transactions: lưu nhật ký biến động kho. Liên kết với products qua product_id, tham chiếu tới purchase_orders hoặc orders qua reference_id.
product_cost_batches: lưu tồn kho theo từng lô giá vốn để phục vụ tính lợi nhuận chính xác. Liên kết với products qua product_id và purchase_order_items qua purchase_order_item_id.

Các mối quan hệ chính:
users — roles: Many-to-Many (qua user_roles). Một user có nhiều role, một role gán cho nhiều user.
roles — permissions: Many-to-Many (qua role_permissions). Một role có nhiều permission, một permission thuộc nhiều role.
users — user_info: One-to-One. Mỗi user có một bản ghi thông tin mở rộng.
categories — products: One-to-Many. Một danh mục chứa nhiều sản phẩm.
users — carts: One-to-One. Mỗi user có một giỏ hàng.
carts — cart_items: One-to-Many. Một giỏ hàng chứa nhiều sản phẩm.
products — cart_items: One-to-Many. Một sản phẩm có thể nằm trong nhiều giỏ hàng.
users — orders: One-to-Many. Một user có nhiều đơn hàng.
orders — order_items: One-to-Many. Một đơn hàng chứa nhiều sản phẩm.
products — order_items: One-to-Many. Một sản phẩm xuất hiện trong nhiều đơn hàng.
orders — stock_reservations: One-to-Many. Một đơn hàng giữ chỗ nhiều sản phẩm.
products — stock_reservations: One-to-Many. Một sản phẩm được giữ chỗ bởi nhiều đơn.
users — notifications: One-to-Many. Một user nhận nhiều thông báo.
notifications — notification_user_reads: One-to-Many. Một thông báo broadcast được nhiều user đánh dấu đã đọc.
suppliers — purchase_orders: One-to-Many. Một nhà cung cấp có nhiều đơn nhập hàng.
purchase_orders — purchase_order_items: One-to-Many. Một đơn nhập chứa nhiều sản phẩm.
products — purchase_order_items: One-to-Many. Một sản phẩm được nhập từ nhiều đơn nhập.
products — inventory_transactions: One-to-Many. Một sản phẩm có nhiều bản ghi biến động kho.
products — product_cost_batches: One-to-Many. Một sản phẩm có nhiều lô giá vốn khác nhau.
purchase_order_items — product_cost_batches: One-to-One. Mỗi dòng nhập hàng tạo ra một lô giá vốn tương ứng.
