# BÁO CÁO KẾT QUẢ HỌC TẬP

## 1. Nội dung báo cáo

Trong quá trình tìm hiểu và làm việc với dự án backend `spring-ecom-v2`, em đã nghiên cứu một hệ thống thương mại điện tử được xây dựng theo mô hình **multi-module Gradle** với **Java 17** và **Spring Boot**. Báo cáo này tập trung vào 7 nội dung chính:

1. Mô tả phạm vi và nội dung đã học được từ dự án.
2. Phân tích kiến trúc tổng quan của hệ thống backend.
3. Tổng hợp các công nghệ, thư viện và hạ tầng đang được sử dụng.
4. Trình bày các kiến thức chuyên môn rút ra trong quá trình đọc hiểu codebase.
5. Mô tả các luồng xử lý chính đang vận hành trong dự án.
6. Liệt kê các bảng dữ liệu chính đang được quản lý trong hệ thống.
7. Đánh giá hiện trạng và định hướng phát triển mở rộng cho khối admin, nhập hàng sách, lợi nhuận/thống kê.

Qua việc đọc cấu trúc module, cấu hình ứng dụng, các lớp controller/service/repository/domain và một số tài liệu kỹ thuật đi kèm, em rút ra được rằng đây là một hệ thống backend tương đối hoàn chỉnh, không chỉ xử lý CRUD cơ bản mà còn triển khai nhiều bài toán thực tế như:

- xác thực và phân quyền người dùng,
- giao tiếp giữa service bằng gRPC,
- truyền sự kiện bằng Kafka,
- gửi thông báo thời gian thực qua MQTT,
- xử lý webhook thanh toán,
- quản lý giới hạn truy cập API bằng rate limiting,
- quản lý database migration bằng Flyway,
- và tự động hóa một số xử lý nền bằng scheduler.

Nội dung báo cáo không chỉ dừng ở mức liệt kê công nghệ, mà còn hướng đến việc giải thích **vì sao hệ thống được thiết kế như vậy**, **các thành phần phối hợp với nhau ra sao**, và **những kinh nghiệm chuyên môn có thể áp dụng cho các dự án backend thực tế**.

---

## 2. Kiến trúc tổng quan

### 2.1. Mô hình tổng thể

Backend được tổ chức theo mô hình **multi-module Gradle**, trong đó `settings.gradle` khai báo 6 module chính:

- `landing`
- `core`
- `grpc-proto`
- `migration-postgres`
- `kafka-service`
- `emqx-proto`

Về mặt kiến trúc runtime, hệ thống xoay quanh **2 service chính**:

#### a. `landing`
- Là service hướng người dùng cuối.
- Chạy REST API cho phía client/user.
- Đồng thời đóng vai trò **gRPC client** khi cần gọi sang `core`.
- Ngoài ra còn có **gRPC server** cho một số luồng nhận thông báo hoặc trao đổi nội bộ.
- Theo cấu hình hiện tại, service này chạy HTTP ở **port 8080** và gRPC ở **port 9090**.

#### b. `core`
- Là service trung tâm xử lý nghiệp vụ chính.
- Cung cấp các API phục vụ quản trị/admin và các nghiệp vụ lõi của hệ thống.
- Đóng vai trò **gRPC server** để nhận request từ `landing`.
- Đồng thời cũng có **gRPC client** cho một số luồng callback/thông báo ngược về `landing`.
- Theo cấu hình hiện tại, service này chạy HTTP ở **port 8081** và gRPC ở **port 9091**.

Điểm đáng chú ý là `core` có bật `@EnableScheduling`, cho thấy ngoài xử lý request đồng bộ, hệ thống còn có các tác vụ nền định kỳ, ví dụ như giải phóng giữ chỗ tồn kho hết hạn.

### 2.2. Vai trò của các module hỗ trợ

#### `grpc-proto`
- Chứa định nghĩa **Protocol Buffers** và sinh mã Java cho giao tiếp gRPC.
- Đây là module giúp chuẩn hóa contract giữa các service.
- Nhờ cách làm này, các service giao tiếp với nhau theo hướng **contract-first**, giúp giảm lỗi sai khác dữ liệu và dễ mở rộng.

#### `kafka-service`
- Chứa các model sự kiện và contract dùng chung cho Kafka.
- Được dùng để truyền các event nghiệp vụ như tạo đơn hàng, hủy đơn, xác nhận thanh toán, giao hàng thành công...
- Giúp các service trao đổi theo mô hình bất đồng bộ, giảm phụ thuộc trực tiếp.

#### `emqx-proto`
- Chứa các model/payload chuẩn hóa cho MQTT.
- Phục vụ các luồng gửi thông báo thời gian thực đến người dùng thông qua broker MQTT/EMQX.

#### `migration-postgres`
- Quản lý thay đổi schema database bằng **Flyway**.
- Giúp kiểm soát version của database, đảm bảo việc triển khai ở các môi trường nhất quán.

### 2.3. Tổ chức code bên trong service

Bên trong các service, cấu trúc code được chia lớp khá rõ ràng:

- `controller`: tiếp nhận request REST hoặc gRPC.
- `service`: chứa use case, command/query và logic nghiệp vụ.
- `repository`: làm việc với database, Redis, Kafka, gRPC, MQTT...
- `domain`: mô hình nghiệp vụ trung tâm.
- `config`: cấu hình bảo mật, Kafka, Redis, MQTT, CORS, JWT, MapStruct...
- `scheduler`: xử lý các tác vụ nền theo lịch.

Cách tổ chức này giúp dự án đạt được một số lợi ích:

- tách biệt rõ phần giao tiếp bên ngoài và phần nghiệp vụ,
- dễ kiểm thử và bảo trì,
- dễ mở rộng thêm nguồn dữ liệu hoặc giao thức tích hợp,
- tránh việc business logic dồn hết vào controller.

### 2.4. Đặc điểm kiến trúc nổi bật

Từ codebase hiện có, có thể thấy hệ thống đang kết hợp nhiều phong cách kiến trúc:

1. **Layered Architecture**: controller → service → repository → database/integration.
2. **Microservice-oriented communication**: dùng gRPC giữa `landing` và `core`.
3. **Event-driven Architecture**: dùng Kafka để xử lý các sự kiện đơn hàng và đồng bộ trạng thái.
4. **Real-time Notification Architecture**: dùng MQTT/EMQX để đẩy thông báo đến client.
5. **Security-first Design**: xác thực JWT, phân quyền theo authority/role, rate limiting, Redis session.
6. **Operational Architecture**: quản lý migration, scheduler, logging, OpenAPI/Swagger.

Nhìn chung, đây là một kiến trúc backend hiện đại, phù hợp với hệ thống thương mại điện tử có nhiều luồng tương tác, yêu cầu hiệu năng và khả năng mở rộng cao hơn mô hình monolith CRUD cơ bản.

---

## 3. Công nghệ sử dụng và tổ chức code

Dựa trên `build.gradle`, `settings.gradle` và cấu trúc mã nguồn, các công nghệ chính và vai trò của chúng trong dự án như sau:

### 3.1. Nhóm web API và bảo mật
- **Spring Web MVC**: xây dựng REST API, routing request/response, xử lý controller theo mô hình MVC.
- **Spring Validation**: kiểm tra dữ liệu đầu vào (request body, query param) trước khi vào business logic.
- **Spring Security**: quản lý xác thực (authentication) và phân quyền (authorization) cho toàn hệ thống.
- **JWT (jjwt)**: phát hành và xác minh access token/refresh token cho cơ chế đăng nhập stateless.
- **BCryptPasswordEncoder**: băm mật khẩu an toàn trước khi lưu database.
- **Method Security / `@PreAuthorize`**: kiểm tra quyền chi tiết ở cấp method API/service.
- **Redis (session/token info)**: lưu thông tin phiên/token để giảm truy vấn DB lặp lại và hỗ trợ logout/invalidate token.

### 3.2. Nhóm dữ liệu và persistence
- **PostgreSQL**: cơ sở dữ liệu chính của hệ thống.
- **Spring Data JPA**: abstraction truy vấn dữ liệu qua repository, giảm code SQL lặp.
- **Hibernate**: ORM engine ánh xạ entity <-> bảng dữ liệu.
- **HikariCP**: connection pool giúp tái sử dụng kết nối DB và tăng hiệu năng.
- **Flyway**: quản lý phiên bản schema database bằng migration script.
- **H2 (test)**: database in-memory phục vụ unit/integration test.

### 3.3. Nhóm cache và chống lạm dụng
- **Redis (cache, rate limit, session)**: lưu cache dữ liệu nóng, trạng thái rate limit và session xác thực.
- **Bucket4j (rate limiting)**: giới hạn số lượng request theo IP/User/Endpoint để chống spam, brute-force.
- **Fallback in-memory khi Redis không khả dụng**: đảm bảo hệ thống vẫn chạy được ở mức suy giảm thay vì dừng hoàn toàn.

### 3.4. Nhóm giao tiếp service nội bộ
- **gRPC**: giao tiếp hiệu năng cao giữa `landing` và `core` theo mô hình client/server.
- **Protocol Buffers**: định nghĩa contract dữ liệu/service và generate mã Java đồng bộ giữa các module.

### 3.5. Nhóm event-driven và realtime
- **Apache Kafka**: truyền sự kiện nghiệp vụ bất đồng bộ (đơn hàng, thanh toán, cập nhật tồn kho).
- **MQTT / EMQX**: kênh truyền thông báo realtime đến client theo topic.
- **Spring Integration MQTT**: tích hợp MQTT vào Spring theo mô hình message channel.
- **Eclipse Paho MQTT Client**: client MQTT dùng để publish/subscribe với broker.

### 3.6. Nhóm hỗ trợ phát triển mã nguồn
- **MapStruct**: tự động sinh code mapper giữa entity/domain/dto/proto.
- **Lombok**: giảm boilerplate (getter/setter, constructor, builder, log annotation).
- **Jackson**: serialize/deserialize JSON cho API và lưu payload tích hợp.

### 3.7. Tổ chức code theo lớp trong dự án
- **Controller layer**: nhận request REST/gRPC, map request/response, không chứa business logic.
- **Service layer**: xử lý nghiệp vụ theo UseCase, tách `QueryService` và `CommandService`.
- **Repository layer**: truy cập DB, Redis, Kafka, MQTT, gRPC client.
- **Domain layer**: mô hình nghiệp vụ trung tâm, dùng làm dữ liệu trao đổi giữa các service nội bộ.
- **Config layer**: gom cấu hình kỹ thuật (security, redis, kafka, mqtt, mapper, cors, jwt...).

Nhờ tổ hợp công nghệ và cách tổ chức code này, hệ thống đáp ứng tốt cả 3 mục tiêu: **dễ mở rộng chức năng**, **dễ bảo trì mã nguồn**, và **đảm bảo hiệu năng/vận hành trong môi trường thực tế**.

---

## 4. Kiến thức chuyên môn

Qua dự án này, em rút ra được nhiều kiến thức chuyên môn quan trọng trong phát triển backend hiện đại.

### 4.1. Thiết kế hệ thống theo module và phân tách trách nhiệm

Bài học đầu tiên là cách chia dự án thành nhiều module độc lập nhưng vẫn liên kết chặt chẽ:

- module service để chạy nghiệp vụ,
- module contract để chia sẻ schema,
- module event để chia sẻ message,
- module migration để quản lý database.

Cách chia này giúp:
- dễ phát triển song song,
- giảm trùng lặp code,
- tăng khả năng tái sử dụng,
- dễ bảo trì khi hệ thống lớn dần.

Đây là tư duy rất quan trọng khi chuyển từ các dự án nhỏ sang hệ thống doanh nghiệp.

### 4.2. Xác thực và phân quyền thực tế

Dự án áp dụng **JWT + Spring Security + authority/role-based access control**. Từ đó em hiểu rõ hơn:

- cách phát hành access token/refresh token,
- cách đưa thông tin phiên đăng nhập vào Redis,
- cách filter request bằng `JwtAuthenticationFilter`,
- cách xây dựng `SecurityFilterChain`,
- cách dùng `@PreAuthorize` và authority để kiểm soát quyền admin.

Đặc biệt, code cho thấy hệ thống tối ưu bằng cách lưu `TokenInfo`/session trên Redis để tránh việc mỗi request đều phải query database. Đây là một kinh nghiệm thực tế rất đáng học khi cần cân bằng giữa hiệu năng và bảo mật.

### 4.3. Giao tiếp đồng bộ bằng gRPC

So với REST nội bộ, gRPC mang lại lợi ích về hiệu năng và contract rõ ràng. Từ dự án này em hiểu:

- cách định nghĩa message/service trong file `.proto`,
- cách generate Java code từ protobuf,
- cách cài đặt gRPC client/server trong Spring Boot,
- cách map domain model sang proto response,
- cách dùng gRPC để tách `landing` và `core` nhưng vẫn giữ giao tiếp chặt chẽ.

Đây là một kiến thức quan trọng đối với các hệ thống có nhiều service nội bộ và cần tốc độ trao đổi cao.

### 4.4. Event-driven và xử lý bất đồng bộ với Kafka

Dự án sử dụng Kafka cho các sự kiện đơn hàng như:
- tạo đơn hàng,
- hủy đơn,
- hủy một phần,
- thanh toán thành công,
- giao hàng thành công.

Qua đó em học được rằng không phải mọi xử lý đều nên làm đồng bộ trong request ban đầu. Việc tách một phần nghiệp vụ sang event mang lại lợi ích:

- giảm độ phụ thuộc giữa các service,
- tăng khả năng chịu tải,
- dễ mở rộng thêm consumer mới,
- phù hợp cho các bài toán đồng bộ kho, thông báo, thống kê.

Tuy nhiên, điều này cũng kéo theo yêu cầu hiểu về **event consistency**, **idempotency** và **xử lý trạng thái phân tán**.

### 4.5. Quản lý vòng đời đơn hàng và trạng thái nghiệp vụ

Một điểm rất đáng học trong dự án là logic trạng thái đơn hàng. `OrderCommandService` thể hiện rõ việc:

- kiểm tra tồn kho trước khi tạo đơn,
- tạo order number duy nhất,
- áp dụng coupon,
- làm sạch giỏ hàng sau khi tạo đơn,
- giới hạn chuyển trạng thái hợp lệ,
- xử lý hủy toàn phần/hủy một phần,
- cập nhật payment status và business status đồng bộ.

Điều này cho thấy khi xây dựng hệ thống thương mại điện tử, logic nghiệp vụ quan trọng hơn nhiều so với phần CRUD giao diện. Nếu không quản lý chặt state machine, rất dễ phát sinh lỗi như:

- trừ tồn kho sai,
- cho phép hủy đơn sai thời điểm,
- đánh dấu thanh toán sai,
- hoặc làm lệch dữ liệu giữa đơn hàng và kho.

### 4.6. Xử lý thanh toán webhook và tính idempotent

`SepayWebhookService` cho thấy một bài học rất thực tế:

- xác thực API key của webhook,
- kiểm tra giao dịch trùng lặp,
- lưu raw webhook data,
- đối chiếu mã thanh toán với order number,
- cập nhật trạng thái thanh toán,
- phát event tiếp theo nếu thanh toán thành công.

Đây là ví dụ điển hình về yêu cầu **idempotent** trong tích hợp thanh toán: cùng một webhook có thể được gửi lặp lại, nhưng hệ thống phải xử lý an toàn và không gây cập nhật trùng.

### 4.7. Rate limiting và bảo vệ hệ thống

Tài liệu `RATE_LIMITING.md` cho thấy hệ thống hỗ trợ rate limit theo:
- IP,
- user,
- endpoint.

Ngoài việc giới hạn số request, hệ thống còn có tư duy vận hành tốt:
- fallback sang in-memory khi Redis lỗi,
- có endpoint admin để kiểm tra và reset rate limit,
- có logging để giám sát.

Từ đây em hiểu rằng bảo vệ API không chỉ là xác thực, mà còn phải kiểm soát tần suất truy cập để chống brute force, spam và DDoS ở mức ứng dụng.

### 4.8. Scheduler và xử lý timeout nghiệp vụ

`StockReservationScheduler` là ví dụ rất rõ cho nghiệp vụ chạy nền:

- quét các reservation đã hết hạn,
- hoàn lại lượng hàng đang giữ chỗ,
- cập nhật trạng thái reservation,
- chuyển trạng thái order sang hủy nếu quá hạn thanh toán.

Qua đó em học được cách mô hình hóa các tiến trình không gắn trực tiếp với request của người dùng nhưng rất quan trọng để hệ thống luôn nhất quán dữ liệu.

### 4.9. Notification thời gian thực

Dự án có `NotificationGrpcService` ở `landing` và các thành phần MQTT publisher. Điều này cho thấy hệ thống có hướng tiếp cận:

- nhận notification event từ service khác qua gRPC,
- chuyển thành payload chuẩn,
- gửi tới người dùng qua MQTT/EMQX.

Từ đây em hiểu hơn về cách xây dựng **real-time backend** thay vì chỉ phản hồi request/response truyền thống.

### 4.10. Quản lý thay đổi database chuyên nghiệp

Module `migration-postgres` cho thấy việc thay đổi schema được quản lý bằng Flyway thay vì sửa tay trực tiếp trên database. Đây là thực hành quan trọng vì:

- dễ truy vết lịch sử thay đổi,
- đồng bộ giữa các môi trường,
- tránh lệch schema khi làm việc nhóm,
- giảm rủi ro khi deploy.

Tóm lại, kiến thức chuyên môn em học được từ dự án này không chỉ là cách viết API, mà còn là cách thiết kế một hệ thống backend có tính thực chiến, chú trọng bảo mật, tính nhất quán dữ liệu, khả năng mở rộng và khả năng vận hành lâu dài.

### 4.11. Quy ước tổ chức mã nguồn và triển khai theo lớp

Để đảm bảo codebase nhất quán, dễ bảo trì và dễ mở rộng, dự án áp dụng bộ quy ước triển khai theo từng nhóm thành phần như sau:

#### a) Service
- Mỗi domain/service được tách thành 4 file: `UseCase`, `UseCaseService`, `QueryService`, `CommandService`.
- Tất cả method trong các service đều đặt `@Transactional` theo đúng phạm vi xử lý dữ liệu.
- Nếu có service con (ví dụ `order` -> `orderItem`) thì service con cũng áp dụng cùng mô hình 4 file ngay trong domain đó.
- `CommandService` và `QueryService` không gọi trực tiếp service ngoài; khi cần tích hợp chéo phải đi qua `UseCase` để giữ luồng điều phối tập trung.
- Service trả về DTO domain đã map qua `EntityMapper` (thường qua `toDomain`, `update`), hạn chế set tay field trong service.

#### b) Repository
- Mỗi bảng/entity gồm tối thiểu 3 file: `Entity`, `EntityMapper`, `Repository`.
- Khi response cần thêm dữ liệu join (ví dụ tên danh mục), có thể bổ sung thư mục `dao` để trả về model trung gian.
- `EntityMapper` sử dụng MapStruct để map tập trung, có thể map luôn từ DAO sang DTO domain khi cần.
- `Entity` ưu tiên dùng `@Column`, hạn chế relation kiểu `@OneToMany`, `@ManyToMany`, `@ManyToOne` để giảm độ phức tạp xử lý ngữ cảnh.
- Repository tận dụng truy vấn join bằng JPA/Hibernate và chuẩn hóa trả về phân trang (`Page`) cho các màn quản trị.

#### c) Controller
- Tách thành 2 file chính: `API` (interface) và `Controller` (implementation), kèm 1 thư mục `model`.
- Trong `model` thường có 4 file: `RequestDto`, `ResponseDto`, `RequestMapper`, `ResponseMapper`.
- DTO ưu tiên dùng `record`; mapper là interface theo phong cách tương tự entity mapper.
- `API` chứa định nghĩa endpoint, mapping, authorize, rate limit; `Controller` chỉ điều phối gọi `UseCase` và mapper.
- Controller không chứa business logic; mọi xử lý nghiệp vụ nằm ở service layer.

#### d) Kafka
- Producer đặt tại `repository/kafka/producer`.
- Việc tạo và phát event đặt trong `CommandService` để gắn chặt với luồng thay đổi trạng thái nghiệp vụ.
- Consumer đặt tại `repository/kafka/consumer`.
- Phần xử lý nghiệp vụ cho message đặt tại `repository/kafka/service`.

#### e) MQTT
- Publisher đặt tại `repository/mqtt/publisher`.
- Subscriber đặt tại `repository/mqtt/subcriber`.

#### f) gRPC
- Phía gửi (client) đặt tại `repository/grpc`, thường gồm `GrpcClient` và `Mapper` để định nghĩa stub và map dữ liệu.
- Phía nhận (server) đặt tại `controller/api/grpc`, gồm service gRPC và mapper tương ứng.
- Mapper gRPC thống nhất luồng map: `request -> dto/domain -> response`, tương tự các mapper khác trong dự án.

---

## 5. Các luồng chính trong dự án

Dưới đây là các luồng xử lý nổi bật mà em rút ra từ codebase.

### 5.1. Luồng xác thực người dùng và phân quyền

**Mục tiêu:** cho phép người dùng đăng nhập, nhận token và truy cập tài nguyên đúng quyền.

**Các bước chính:**
1. Người dùng gọi API đăng nhập/đăng ký ở `landing`.
2. Hệ thống kiểm tra thông tin tài khoản, mã hóa mật khẩu, phát hành JWT.
3. Thông tin phiên đăng nhập được lưu/phục vụ qua Redis.
4. Ở các request sau, `JwtAuthenticationFilter` đọc token và dựng lại `Authentication`.
5. `SecurityConfig` và `@PreAuthorize` kiểm tra role/authority trước khi cho phép truy cập API.

**Ý nghĩa:**
- tách rõ user thường và admin,
- giảm query database lặp lại,
- tăng tốc xử lý cho request đã xác thực.

### 5.2. Luồng duyệt sản phẩm và danh mục

**Mục tiêu:** người dùng xem sản phẩm, danh mục và dữ liệu public.

**Các bước chính:**
1. Client gọi các API public như `/v1/api/products/**`, `/v1/api/categories/**`.
2. `landing` tiếp nhận request qua controller.
3. Service nghiệp vụ truy vấn dữ liệu sản phẩm/danh mục.
4. Dữ liệu được mapping sang response trả về cho frontend.
5. Một số thành phần gRPC client/service đã được chuẩn bị sẵn để trao đổi giữa `landing` và `core` cho các luồng quản trị hoặc dùng chung.

**Ý nghĩa:**
- phục vụ phía người dùng với tốc độ nhanh,
- giữ các endpoint đọc public ở mức dễ truy cập,
- vẫn có khả năng mở rộng sang mô hình gọi `core` qua gRPC khi cần.

### 5.3. Luồng thêm vào giỏ hàng và tạo đơn hàng

**Mục tiêu:** chuyển dữ liệu từ giỏ hàng thành đơn hàng hợp lệ.

**Các bước chính:**
1. Người dùng đăng nhập và thao tác với giỏ hàng ở `landing`.
2. Khi đặt hàng, `OrderCommandService` lấy các item trong cart.
3. Hệ thống kiểm tra giỏ hàng có rỗng không và xác thực tồn kho.
4. Nếu có coupon, service gọi `CouponGrpcClient` để kiểm tra và tính giảm giá.
5. Hệ thống tạo `OrderEntity`, sinh `orderNumber`, tạo các `OrderItem`.
6. Sau khi lưu đơn hàng, giỏ hàng được xóa.
7. Event `ORDER_CREATED` được đẩy lên Kafka.

**Ý nghĩa:**
- đảm bảo dữ liệu đơn hàng đầy đủ trước khi commit,
- tránh việc người dùng đặt đơn vượt quá tồn kho,
- mở đường cho các bước xử lý bất đồng bộ tiếp theo ở service trung tâm.

### 5.4. Luồng đồng bộ kho qua Kafka sau khi tạo/hủy/thanh toán đơn

**Mục tiêu:** cập nhật kho theo vòng đời đơn hàng mà không ghép cứng logic vào một request đồng bộ.

**Các tình huống chính:**
- `ORDER_CREATED`: phục vụ luồng giữ chỗ tồn kho.
- `ORDER_CANCELLED`: hoàn lại số lượng đã giữ.
- `ORDER_PARTIAL_CANCELLED`: hoàn lại một phần tồn kho.
- `ORDER_PAID`: chuyển lượng hàng từ trạng thái reserved sang sold.
- `ORDER_DELIVERED`: ghi nhận số lượng bán thành công.

**Ý nghĩa:**
- giảm coupling giữa module đơn hàng và module kho,
- cho phép mở rộng thêm consumer như analytics, notification, audit,
- phù hợp với nghiệp vụ thương mại điện tử có nhiều trạng thái trung gian.

### 5.5. Luồng thanh toán chuyển khoản qua SePay webhook

**Mục tiêu:** ghi nhận thanh toán chuyển khoản và cập nhật đơn hàng tự động.

**Các bước chính:**
1. Cổng thanh toán hoặc dịch vụ trung gian gửi webhook về hệ thống.
2. `PaymentWebhookController`/`SepayWebhookService` kiểm tra API key.
3. Hệ thống kiểm tra xem giao dịch đã được xử lý trước đó chưa.
4. Raw payload được chuyển thành JSON và lưu lại để đối soát/audit.
5. Service tìm đơn hàng theo mã thanh toán.
6. Nếu hợp lệ, hệ thống cập nhật `PaymentStatus = PAID`.
7. Nếu đơn đang ở trạng thái chờ xử lý kho hoặc đang giữ kho, hệ thống cập nhật sang `CONFIRMED`.
8. Đồng thời phát event `ORDER_PAID` để service trung tâm xử lý kho.

**Ý nghĩa:**
- tự động hóa quy trình xác nhận thanh toán,
- bảo đảm an toàn khi webhook gửi lặp,
- kết nối chặt giữa thanh toán và quản lý tồn kho.

### 5.6. Luồng quản trị sản phẩm, đơn hàng và coupon

**Mục tiêu:** cho admin quản lý dữ liệu lõi của hệ thống.

**Các bước chính:**
1. Admin truy cập các API ở `core`, ví dụ nhóm `/v1/api/admin/**`.
2. Spring Security xác thực token và kiểm tra authority tương ứng như `PRODUCT_VIEW`, `PRODUCT_CREATE`, `COUPON_CREATE`...
3. Controller admin gọi use case để CRUD sản phẩm, danh mục, coupon, đơn hàng.
4. Một số tác vụ có thể kích hoạt notification hoặc event đi kèm.

**Ý nghĩa:**
- phân tách rõ user flow và admin flow,
- đảm bảo quản trị dữ liệu trung tâm tập trung ở `core`,
- dễ kiểm soát phân quyền theo chức năng.

### 5.7. Luồng gửi Notification MQTT (chi tiết, có lưu database)

**Mục tiêu:** vừa đẩy thông báo gần thời gian thực cho frontend, vừa lưu lịch sử thông báo để truy vấn đọc/chưa đọc.

**Luồng xử lý chi tiết:**
1. Một service phát sinh sự kiện thông báo (thường từ `core`) và gọi gRPC client `NotificationGrpcClient`.
2. `landing` nhận request tại gRPC server `NotificationGrpcService` và map proto -> `NotificationEvent` qua `NotificationGrpcMapper`.
3. `NotificationUseCase` điều phối sang `NotificationCommandService` để xử lý command.
4. `NotificationCommandService` chuẩn hóa event (`eventId`, `timestamp`, `source`) nếu thiếu.
5. Event được map sang entity bằng `NotificationEntityMapper` và lưu vào bảng `notifications` qua `NotificationRepository`.
6. Sau khi lưu thành công, `notificationId` từ DB được gán ngược lại vào event để thống nhất dữ liệu trace/log.
7. `NotificationMqttPublisherImpl` serialize event thành JSON và publish lên EMQX/MQTT với QoS = 1.
8. Frontend đã subscribe topic phù hợp sẽ nhận thông báo gần thời gian thực.

**Bảng lưu dữ liệu:** `notifications`
- Lưu các cột chính: `id`, `user_id`, `type`, `title`, `message`, `reference_id`, `reference_type`, `image_url`, `action_url`, `is_read`, `created_at`.
- Notification cá nhân: `user_id` có giá trị.
- Notification broadcast: `user_id = null` (đã được migration cho phép) để biểu diễn thông báo toàn hệ thống.
- Có index cho các truy vấn quan trọng như theo user, unread, thời gian tạo để tối ưu màn hình danh sách thông báo.

**Topic MQTT đang dùng:**
- Cá nhân: `notifications/{userId}/{type}`
- Broadcast: `notifications/broadcast/{type}`

**Ý nghĩa:**
- đảm bảo thông báo không bị mất lịch sử do đã persist trước khi publish,
- hỗ trợ đầy đủ nghiệp vụ đọc/chưa đọc và thống kê unread,
- tách rõ pipeline: nhận sự kiện -> chuẩn hóa -> lưu DB -> đẩy realtime.

### 5.8. Luồng tự động hủy giữ chỗ tồn kho hết hạn

**Mục tiêu:** tránh việc tồn kho bị giữ quá lâu khi người dùng không hoàn tất thanh toán.

**Các bước chính:**
1. `StockReservationScheduler` chạy định kỳ mỗi phút.
2. Hệ thống tìm các reservation đã hết hạn.
3. Với từng reservation, service trừ `reservedQuantity` khỏi sản phẩm tương ứng.
4. Cập nhật trạng thái reservation sang `RELEASED`.
5. Nếu order liên quan đang ở `STOCK_RESERVED`, hệ thống chuyển sang `CANCELLED`.

**Ý nghĩa:**
- giải phóng hàng tồn bị giữ nhưng không thanh toán,
- giữ dữ liệu kho chính xác,
- mô hình hóa đúng bài toán timeout nghiệp vụ trong thương mại điện tử.

---

## 6. Bảng dữ liệu trong dự án

Dựa trên **code hiện tại** (Entity `@Table` trong `landing/core` và các repository SQL đang chạy), các bảng dữ liệu đang được hệ thống sử dụng gồm:

### 6.1. Nhóm người dùng, xác thực và phân quyền
- `users`: thông tin tài khoản người dùng/admin/seller.
- `user_info`: thông tin hồ sơ mở rộng của người dùng.
- `roles`: danh mục vai trò hệ thống.
- `permissions`: danh mục quyền chi tiết.
- `role_permissions`: bảng nối role - permission.
- `user_roles`: bảng nối user - role (many-to-many).

### 6.2. Nhóm catalog sản phẩm
- `categories`: danh mục sản phẩm.
- `products`: thông tin sản phẩm và tồn kho.
- `product_reviews`: đánh giá sản phẩm.
- `review_reactions`: like/dislike cho đánh giá.

### 6.3. Nhóm địa chỉ, giỏ hàng và đơn hàng
- `carts`: giỏ hàng theo user.
- `cart_items`: chi tiết sản phẩm trong giỏ hàng.
- `orders`: thông tin đơn hàng.
- `order_items`: chi tiết từng sản phẩm trong đơn.
- `stock_reservations`: giữ chỗ tồn kho theo TTL trước khi thanh toán hoàn tất.

### 6.4. Nhóm thanh toán, khuyến mãi và thông báo
- `sepay_transactions`: log giao dịch webhook SePay (kèm payload JSONB).
- `coupons`: mã giảm giá và điều kiện sử dụng.
- `notifications`: lịch sử thông báo cá nhân/broadcast, đồng bộ với luồng MQTT.

### 6.5. Ghi chú để tránh nhầm với migration cũ
- Danh sách trên ưu tiên theo **bảng đang được code tham chiếu trực tiếp** (qua `@Table` hoặc SQL query trong repository).
- Một số bảng từng xuất hiện ở migration cũ nhưng không còn thấy trong entity/repository hiện tại (ví dụ `product_categories`, `product_images`, `addresses`, `refresh_tokens`) nên không được xem là bảng active trong phạm vi báo cáo này.

---

## 7. Đánh giá hiện trạng và phát triển mở rộng

Mục này trả lời trực tiếp câu hỏi: **dự án hiện tại đã có gì cho admin, nhập hàng sách, lợi nhuận/thống kê**.

### 7.1. Khu vực Admin

**Trạng thái hiện tại: ĐÃ CÓ (khá đầy đủ phần quản trị cơ bản)**

Trong code `core` đã có các nhóm API admin:
- `AdminUserController` / `AdminUserAPI`: quản lý người dùng và vai trò.
- `AdminRoleController` / `AdminRoleAPI`: quản lý role/permission.
- `AdminProductController` / `AdminProductAPI`: quản lý sản phẩm, cập nhật stock.
- `AdminCategoryController` / `AdminCategoryAPI`: quản lý danh mục.
- `AdminOrderController` / `AdminOrderAPI`: quản lý đơn hàng và trạng thái thanh toán.
- `AdminCouponController`: quản lý coupon.

**Nhận xét:** phần admin đã có nền tảng tốt cho vận hành thương mại điện tử, đặc biệt ở khối user/role/product/order/coupon.

### 7.2. Nhập hàng sách (inbound inventory)

**Trạng thái hiện tại: CHƯA CÓ ĐẦY ĐỦ (mới có cập nhật tồn trực tiếp)**

Hiện dự án có:
- Cột tồn kho trong `products` (`stock_quantity`, `reserved_quantity`, `sold_count`).
- API admin cập nhật stock trực tiếp: `PUT /v1/api/admin/products/{productId}/stock`.
- Cơ chế giữ chỗ tồn kho cho đơn hàng qua `stock_reservations`.

Hiện dự án **chưa có** luồng nhập hàng chuẩn nghiệp vụ kế toán/kho như:
- phiếu nhập hàng,
- nhà cung cấp,
- chi tiết từng lần nhập,
- lịch sử biến động kho theo chứng từ,
- duyệt/hủy phiếu nhập.

**Đề xuất mở rộng:**
- Thêm bảng: `suppliers`, `purchase_orders`, `purchase_order_items`, `inventory_movements`.
- Thêm API admin: tạo/duyệt phiếu nhập, nhập theo lô, xem lịch sử nhập theo sản phẩm.
- Áp dụng transaction + event để đồng bộ kho và báo cáo.

### 7.3. Lợi nhuận và thống kê

**Trạng thái hiện tại: CÓ MỘT PHẦN**

Đã có:
- Endpoint admin thống kê đơn: `GET /v1/api/admin/orders/statistics`.
- Query tổng hợp số đơn theo trạng thái và doanh thu giao thành công (`OrderRepository.getOrderStatistics`).
- Query doanh thu hôm nay (`getTodayRevenue`).

Chưa có/đang thiếu:
- Logic thống kê theo kỳ trong `OrderUseCaseService.getOrderStatistics(period, dateFrom, dateTo)` vẫn để `TODO` và trả dữ liệu mặc định.
- Chưa có giá vốn (`cost_price`/lịch sử giá nhập), nên **chưa tính được lợi nhuận thực** (gross/net profit).
- Chưa có dashboard tài chính đầy đủ (lãi/lỗ theo ngày-tháng, biên lợi nhuận theo ngành hàng/sản phẩm).

**Đề xuất mở rộng:**
- Bổ sung dữ liệu giá vốn: `cost_price` hoặc bảng `product_cost_history`.
- Thêm bảng snapshot/báo cáo: `profit_daily_snapshots` (tùy nhu cầu BI).
- Mở rộng API admin thống kê: doanh thu, giá vốn, lợi nhuận gộp, lợi nhuận ròng, top sản phẩm theo lợi nhuận.

### 7.4. Kết luận nhanh cho câu hỏi "đã có chưa?"

- **Admin:** đã có khá đầy đủ khung chức năng quản trị.
- **Nhập hàng sách:** chưa có module nhập hàng chuẩn, mới cập nhật tồn trực tiếp.
- **Lợi nhuận/thống kê:** mới có một phần (đơn và doanh thu cơ bản), chưa có lợi nhuận thực do thiếu giá vốn + logic thống kê nâng cao.

---

## Kết luận

Qua dự án `spring-ecom-v2`, em học được cách một hệ thống backend thương mại điện tử được xây dựng theo hướng hiện đại và thực tế hơn so với mô hình CRUD đơn giản. Dự án cho thấy sự kết hợp giữa:

- kiến trúc nhiều module,
- giao tiếp service bằng gRPC,
- xử lý event bằng Kafka,
- thông báo realtime bằng MQTT,
- bảo mật bằng JWT + Spring Security,
- chống lạm dụng bằng rate limiting,
- và quản lý dữ liệu bài bản bằng PostgreSQL + Flyway.

Không chỉ giúp em hiểu thêm về mặt kỹ thuật, dự án còn giúp em hình thành tư duy hệ thống: biết cách tách lớp, tách service, quản lý trạng thái nghiệp vụ, xử lý bất đồng bộ, và thiết kế backend theo hướng có thể mở rộng lâu dài. Đây là những kiến thức rất quan trọng để áp dụng vào quá trình học tập cũng như công việc phát triển phần mềm sau này.

