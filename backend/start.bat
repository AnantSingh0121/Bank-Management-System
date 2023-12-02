@echo off
echo ========================================
echo Bank Management System - Backend Startup
echo ========================================
echo.

echo Checking Cassandra status...
docker ps | findstr cassandra
if errorlevel 1 (
    echo ERROR: Cassandra is not running!
    echo Please start Cassandra with: docker start cassandra
    pause
    exit /b 1
)

echo.
echo Cassandra is running!
echo.
echo Starting Spring Boot application...
echo.

mvn spring-boot:run

pause
