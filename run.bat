@echo off
title Quiz Management System
echo ============================================================
echo           Starting Quiz Management System
echo ============================================================
echo.

:: Check if MySQL is running on port 3306
netstat -ano | findstr :3306 >nul
if %ERRORLEVEL% equ 0 (
    echo [OK] MySQL is active on port 3306.
) else (
    echo [INFO] MySQL is not detected on port 3306. Attempting to start XAMPP MySQL...
    if exist "C:\xampp\mysql\bin\mysqld.exe" (
        start /b "" "C:\xampp\mysql\bin\mysqld.exe" --defaults-file="C:\xampp\mysql\bin\my.ini" --standalone
        timeout /t 3 /nobreak >nul
        echo [OK] Started XAMPP MySQL server.
    ) else (
        echo [WARN] Could not automatically find XAMPP MySQL. Please ensure MySQL is running in XAMPP.
    )
)

echo.
echo Launching Application...
java -jar "%~dp0target\QuizManagementSystem-1.0-SNAPSHOT.jar"

if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited. Press any key to close...
    pause >nul
)
