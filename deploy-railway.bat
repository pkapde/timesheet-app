@echo off
echo ========================================
echo Timesheet App - Deploy to Railway (No CLI needed!)
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
echo Step 2: Checking if Git is available...
git --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo Git is required but not found. Please install Git from: https://git-scm.com/download/win
    pause
    exit /b 1
)

echo.
echo Step 3: Initializing Git repository...
if not exist .git (
    git init
    git branch -M main
)

echo.
echo Step 4: Adding files to Git...
git add .
git commit -m "Deploy timesheet app to Railway"

echo.
echo ========================================
echo RAILWAY DEPLOYMENT INSTRUCTIONS
echo ========================================
echo.
echo Your application is ready for Railway deployment!
echo Railway is easier than Heroku - no CLI installation needed.
echo.
echo Follow these steps:
echo.
echo 1. Create a GitHub repository:
echo    - Go to https://github.com/new
echo    - Create a new repository (e.g., 'timesheet-app')
echo    - Don't initialize with README (we already have files)
echo.
echo 2. Push your code to GitHub:
echo    - Copy these commands and run them:
echo.
echo    git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
echo    git push -u origin main
echo.
echo 3. Deploy to Railway:
echo    - Go to https://railway.app
echo    - Sign up/login with your GitHub account
echo    - Click "New Project" -^> "Deploy from GitHub repo"
echo    - Select your timesheet repository
echo    - Railway will auto-detect it's a Spring Boot app
echo.
echo 4. Add PostgreSQL database:
echo    - In your Railway project dashboard
echo    - Click "New" -^> "Database" -^> "Add PostgreSQL"
echo    - Railway automatically sets DATABASE_URL
echo.
echo 5. Set environment variables:
echo    - In Railway dashboard, go to your service
echo    - Click "Variables" tab
echo    - Add: SPRING_PROFILES_ACTIVE = prod
echo    - Add: JWT_SECRET = TimeSheet2024SecureKey
echo.
echo 6. Deploy:
echo    - Railway automatically deploys when you push to GitHub
echo    - Your app will be available at a Railway URL
echo.
echo ========================================
echo.
echo Benefits of Railway over Heroku:
echo ✓ No CLI installation needed
echo ✓ Free tier with generous limits
echo ✓ Automatic deployments from GitHub
echo ✓ Built-in PostgreSQL
echo ✓ Modern dashboard
echo.
echo Your app is ready! Just follow the steps above.
echo.
pause
