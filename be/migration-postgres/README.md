# PostgreSQL Migration với Docker (Gradle)

## 🐳 **Cách sử dụng:**

### **Yêu cầu:**
- PostgreSQL đã chạy local trên port 5432
- Database `spring_ecom_db` đã được tạo

### **Quick Start:**
```bash
cd be/migration-postgres

# Chạy migration với Docker + Gradle
docker compose run --rm migration gradle flywayMigrate --no-daemon
```

### **Hoặc dùng script Windows:**
```bash
./run-docker.bat
```

## 📁 **Cấu trúc đơn giản (Gradle-based):**

```
migration-postgres/
├── src/main/resources/db/migration/    # Migration files
├── build.gradle                       # Gradle config
├── settings.gradle                    # Gradle settings
├── gradle.properties                  # Gradle properties
├── Dockerfile                         # Docker với Gradle
├── docker-compose.yml                 # Migration service
└── run-docker.bat                     # Windows script
```

## 🔧 **Các lệnh migration:**

### **Docker + Gradle commands:**
```bash
# Chạy migration
docker compose run --rm migration gradle flywayMigrate --no-daemon

# Check status
docker compose run --rm migration gradle flywayInfo --no-daemon

# Validate
docker compose run --rm migration gradle flywayValidate --no-daemon

# Clean (⚠️ Cẩn thận)
docker compose run --rm migration gradle flywayClean --no-daemon
```

### **Local Gradle (alternative):**
```bash
./gradlew flywayMigrate
./gradlew flywayInfo
./gradlew flywayValidate
```

## 🔧 **Setup Environment:**

### **1. Tạo .env file:**
```bash
# Copy từ template
cp .env.example .env

# Edit với thông tin database của bạn
# .env
POSTGRES_PASSWORD=your_actual_password
FLYWAY_PASSWORD=your_actual_password
```

## 🔧 **2 cách chạy migration:**

### **Cách 1: Docker (với .env):**
```bash
cd be/migration-postgres

# Verbose logging
docker compose run --rm migration gradle flywayMigrate --no-daemon --info

# Xem migration info
docker compose run --rm migration gradle flywayInfo --no-daemon
```

### **Cách 2: Local Gradle (không cần Docker):**
```bash
cd be/migration-postgres

# Dùng script
./run-local.bat

# Hoặc manual
gradlew flywayMigrate -Pflyway.url=jdbc:postgresql://localhost:5432/spring_ecom_db -Pflyway.user=postgres -Pflyway.password=quocbui26042005 --info
```

## 🔒 **Security:**

- ✅ **`.env`** file chứa sensitive data (password)
- ✅ **`.env.example`** template cho team members
- ✅ **`.gitignore`** để không commit password lên Git
- ✅ **Environment variables** được load tự động

## 🚀 **Workflow Development:**

```bash
# 1. Đảm bảo PostgreSQL local đang chạy
# 2. Pull code mới
git pull

# 3. Chạy migration
docker compose run --rm migration gradle flywayMigrate --no-daemon

# 4. Start services
cd ../
./gradlew :core-services:user-service:bootRun
```

## ✅ **Ưu điểm:**

- ✅ **Clean structure**: Dockerfile ở root level
- ✅ **Consistent**: Cùng build tool với toàn project
- ✅ **Simple**: Ít thư mục con, dễ navigate
- ✅ **Fast**: Gradle caching và performance tốt