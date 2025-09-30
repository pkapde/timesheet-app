@echo off
echo ========================================
echo Heroku Deployment Status Checker
echo ========================================

echo.
echo Checking if Heroku CLI is available...
heroku --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Heroku CLI not found! Please install it first.
    echo Visit: https://devcenter.heroku.com/articles/heroku-cli
    pause
    exit /b 1
)

echo.
set /p APP_NAME="Enter your Heroku app name: "

if "%APP_NAME%"=="" (
    echo App name cannot be empty!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Checking Deployment Status for: %APP_NAME%
echo ========================================

echo.
echo 1. Checking if app exists...
call heroku apps:info --app %APP_NAME% >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo ❌ App '%APP_NAME%' not found or you don't have access to it.
    echo Double-check the app name or your Heroku login status.
    pause
    exit /b 1
)
echo ✅ App exists and accessible

echo.
echo 2. Checking app status...
call heroku ps --app %APP_NAME%

echo.
echo 3. Getting app URL...
call heroku apps:info --app %APP_NAME% | findstr "Web URL"

echo.
echo 4. Checking recent deployments...
echo Recent releases:
call heroku releases --app %APP_NAME% --num 3

echo.
echo 5. Checking environment variables...
echo Configuration variables:
call heroku config --app %APP_NAME%

echo.
echo 6. Checking database status...
call heroku pg:info --app %APP_NAME%

echo.
echo 7. Getting recent logs (last 50 lines)...
echo Recent application logs:
call heroku logs --app %APP_NAME% --num 50

echo.
echo ========================================
echo Quick Actions
echo ========================================
echo.
set /p ACTION="Choose an action: [o]pen app, [l]ive logs, [r]estart app, [q]uit: "

if /i "%ACTION%"=="o" (
    echo Opening app in browser...
    call heroku open --app %APP_NAME%
)

if /i "%ACTION%"=="l" (
    echo Starting live log stream... (Press Ctrl+C to stop)
    call heroku logs --tail --app %APP_NAME%
)

if /i "%ACTION%"=="r" (
    echo Restarting application...
    call heroku restart --app %APP_NAME%
    echo App restarted successfully!
)

echo.
echo Done!
pause
