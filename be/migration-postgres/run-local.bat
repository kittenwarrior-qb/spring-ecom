@echo off
echo Running PostgreSQL Migration locally (no Docker)...

echo Checking if local PostgreSQL is running...
pg_isready -h localhost -p 5432 -U postgres >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ PostgreSQL is not running on localhost:5432
    echo Please start your local PostgreSQL server first
    pause
    exit /b 1
)

echo ✅ PostgreSQL is running
echo.

echo Setting environment variables...
set FLYWAY_URL=jdbc:postgresql://localhost:5432/spring_ecom_db
set FLYWAY_USER=postgres
set FLYWAY_PASSWORD=quocbui26042005

echo Running migration repair (if needed)...
gradlew flywayRepair -Pflyway.url=%FLYWAY_URL% -Pflyway.user=%FLYWAY_USER% -Pflyway.password=%FLYWAY_PASSWORD%

echo.
echo Running migration with verbose output...
gradlew flywayMigrate -Pflyway.url=%FLYWAY_URL% -Pflyway.user=%FLYWAY_USER% -Pflyway.password=%FLYWAY_PASSWORD% --info

echo.
echo Showing migration info...
gradlew flywayInfo -Pflyway.url=%FLYWAY_URL% -Pflyway.user=%FLYWAY_USER% -Pflyway.password=%FLYWAY_PASSWORD%

echo.
echo ✅ Migration completed!
pause