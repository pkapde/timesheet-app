@echo off
echo ========================================
echo Timesheet App - Deploy to Vercel (FREE)
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
echo Step 3: Preparing Git repository...
if not exist .git (
    git init
    git branch -M main
)

git add .
git commit -m "Deploy timesheet app to Vercel"

echo.
echo ========================================
echo VERCEL DEPLOYMENT (100%% FREE!)
echo ========================================
echo.
echo Your application is ready for FREE Vercel deployment!
echo.
echo STEP-BY-STEP INSTRUCTIONS:
echo.
echo 1. PUSH TO GITHUB (Free):
echo    - Go to https://github.com/new
echo    - Create repository: 'timesheet-app'
echo    - Run these commands:
echo.
echo    git remote add origin https://github.com/YOUR_USERNAME/timesheet-app.git
echo    git push -u origin main
echo.
echo 2. GET FREE DATABASE (Aiven - PostgreSQL):
echo    - Go to: https://aiven.io/free-postgresql
echo    - Sign up for FREE account
echo    - Create free PostgreSQL database (1 month free, then $19/month)
echo    OR
echo    - Go to: https://supabase.com
echo    - Sign up for FREE account (Forever free tier!)
echo    - Create new project -^> Get database URL
echo.
echo 3. DEPLOY TO VERCEL (Free):
echo    - Go to: https://vercel.com
echo    - Sign up with GitHub (FREE forever!)
echo    - Click "New Project" -^> Import from GitHub
echo    - Select your 'timesheet-app' repository
echo    - Vercel will auto-detect Spring Boot
echo.
echo 4. SET ENVIRONMENT VARIABLES in Vercel:
echo    - In Vercel dashboard -^> Settings -^> Environment Variables
echo    - Add: SPRING_PROFILES_ACTIVE = prod
echo    - Add: DATABASE_URL = (from step 2)
echo    - Add: JWT_SECRET = TimeSheet2024SecureKey
echo.
echo 5. DEPLOY:
echo    - Vercel deploys automatically!
echo    - Your app will be live at: https://your-app.vercel.app
echo.
echo ========================================
echo FREE TIER COMPARISON:
echo ========================================
echo.
echo VERCEL (Recommended):
echo ✓ Completely FREE forever
echo ✓ No credit card needed
echo ✓ Unlimited personal projects
echo ✓ Global CDN + HTTPS
echo ✓ 100GB bandwidth/month
echo.
echo SUPABASE DATABASE:
echo ✓ FREE PostgreSQL forever
echo ✓ 500MB storage
echo ✓ No credit card needed
echo.
echo ALTERNATIVE - RAILWAY:
echo ✓ $5 free credit monthly
echo ✓ Built-in database
echo ~ Eventually needs payment
echo.
echo ========================================
echo.
echo Your app is ready! Follow steps 1-5 above.
echo All services are FREE for personal use!
echo.
pause
