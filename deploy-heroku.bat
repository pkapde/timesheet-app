@echo off
echo ========================================
echo Timesheet App - Quick Deploy to Heroku
echo ========================================

echo.
echo Step 1: Building the application...
call mvn clean package -DskipTests

if %ERRORLEVEL% neq 0 (
    echo Build failed! Please fix any compilation errors.
    pause
    exit /b 1
)

echo.
echo Step 2: Checking if Heroku CLI is installed...
heroku --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo.
    echo ============================================================
    echo HEROKU CLI NOT FOUND!
    echo ============================================================
    echo.
    echo The Heroku CLI is required to deploy your application.
    echo Please install it using one of these methods:
    echo.
    echo Method 1 - Download Installer (Recommended):
    echo 1. Visit: https://devcenter.heroku.com/articles/heroku-cli
    echo 2. Download the Windows 64-bit installer
    echo 3. Run the installer and follow the setup wizard
    echo 4. Restart this command prompt
    echo 5. Run this script again
    echo.
    echo Method 2 - Using npm (if you have Node.js):
    echo    npm install -g heroku
    echo.
    echo Method 3 - Using Chocolatey (if you have it):
    echo    choco install heroku-cli
    echo.
    echo ============================================================
    echo.
    set /p INSTALL_CHOICE="Do you want me to open the download page? (y/n): "
    if /i "%INSTALL_CHOICE%"=="y" (
        start https://devcenter.heroku.com/articles/heroku-cli
        echo Download page opened in your browser.
        echo After installation, restart command prompt and run this script again.
    )
    echo.
    echo Alternative: Try Railway deployment (easier, no CLI needed)
    echo See the DEPLOYMENT.md file for Railway instructions.
    pause
    exit /b 1
)

echo Heroku CLI found! Continuing with deployment...

echo.
echo Step 3: Login to Heroku (browser will open)...
call heroku login

echo.
echo Step 4: Creating Heroku application...
set /p APP_NAME="Enter your app name (e.g., my-timesheet-app): "
call heroku create %APP_NAME%

if %ERRORLEVEL% neq 0 (
    echo Failed to create Heroku app. App name might already exist.
    echo Try a different name or use: heroku apps:destroy %APP_NAME% (if you own it)
    pause
    exit /b 1
)

echo.
echo Step 5: Adding PostgreSQL database...
echo Adding PostgreSQL database addon (this may take a moment)...
call heroku addons:create heroku-postgresql:essential-0 --app %APP_NAME%

if %ERRORLEVEL% neq 0 (
    echo Warning: Failed to add PostgreSQL addon. Trying alternative...
    call heroku addons:create heroku-postgresql:mini --app %APP_NAME%
)

echo.
echo Step 6: Setting environment variables...
call heroku config:set SPRING_PROFILES_ACTIVE=prod --app %APP_NAME%
call heroku config:set JWT_SECRET=TimeSheet2024SuperSecretKey%RANDOM% --app %APP_NAME%
call heroku config:set CONTEXT_PATH=/ --app %APP_NAME%

echo.
echo Step 7: Initializing Git repository...
if not exist .git (
    git init
    git branch -M main
)

echo Adding files to git...
git add .
git commit -m "Deploy timesheet app to Heroku with PostgreSQL support"

echo.
echo Step 8: Adding Heroku remote and deploying...
call heroku git:remote -a %APP_NAME%

echo Starting deployment... (this may take several minutes)
git push heroku main

if %ERRORLEVEL% neq 0 (
    echo Deployment failed! Check the error messages above.
    echo You can view detailed logs with: heroku logs --tail --app %APP_NAME%
    pause
    exit /b 1
)

echo.
echo Step 9: Ensuring app is running...
call heroku ps:scale web=1 --app %APP_NAME%

echo.
echo ========================================
echo Deployment Complete!
echo ========================================
echo.
echo Your timesheet app is now available at:
echo https://%APP_NAME%.herokuapp.com
echo.
echo Database: PostgreSQL (automatically configured)
echo Profile: Production (PostgreSQL enabled, H2 disabled)
echo.
echo Useful commands:
echo - View logs: heroku logs --tail --app %APP_NAME%
echo - Open app: heroku open --app %APP_NAME%
echo - Check status: heroku ps --app %APP_NAME%
echo - View config: heroku config --app %APP_NAME%
echo.
pause
