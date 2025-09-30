Write-Host "========================================" -ForegroundColor Green
Write-Host "Timesheet App - Quick Deploy to Heroku" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green

Write-Host ""
Write-Host "Step 1: Building the application..." -ForegroundColor Yellow
try {
    & mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build failed! Please fix any compilation errors." -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Host "✅ Build successful!" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven not found or build failed!" -ForegroundColor Red
    Write-Host "Please ensure Maven is installed and in your PATH." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Step 2: Checking if Heroku CLI is installed..." -ForegroundColor Yellow
try {
    & heroku --version | Out-Null
    Write-Host "✅ Heroku CLI found!" -ForegroundColor Green
} catch {
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host "HEROKU CLI NOT FOUND!" -ForegroundColor Red
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "The Heroku CLI is required to deploy your application."
    Write-Host "Please install it using one of these methods:"
    Write-Host ""
    Write-Host "Method 1 - Download Installer (Recommended):" -ForegroundColor Cyan
    Write-Host "1. Visit: https://devcenter.heroku.com/articles/heroku-cli"
    Write-Host "2. Download the Windows 64-bit installer"
    Write-Host "3. Run the installer and follow the setup wizard"
    Write-Host "4. Restart PowerShell"
    Write-Host "5. Run this script again"
    Write-Host ""
    Write-Host "Method 2 - Using npm (if you have Node.js):" -ForegroundColor Cyan
    Write-Host "   npm install -g heroku"
    Write-Host ""
    Write-Host "Method 3 - Using Chocolatey (if you have it):" -ForegroundColor Cyan
    Write-Host "   choco install heroku-cli"
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host ""
    $choice = Read-Host "Do you want me to open the download page? (y/n)"
    if ($choice -eq "y" -or $choice -eq "Y") {
        Start-Process "https://devcenter.heroku.com/articles/heroku-cli"
        Write-Host "Download page opened in your browser." -ForegroundColor Green
        Write-Host "After installation, restart PowerShell and run this script again."
    }
    Write-Host ""
    Write-Host "Alternative: Try Railway deployment (easier, no CLI needed)" -ForegroundColor Yellow
    Write-Host "Run: .\deploy-railway.bat" -ForegroundColor Cyan
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Step 3: Login to Heroku (browser will open)..." -ForegroundColor Yellow
& heroku login

Write-Host ""
Write-Host "Step 4: Creating Heroku application..." -ForegroundColor Yellow
$appName = Read-Host "Enter your app name (e.g., my-timesheet-app)"

if ([string]::IsNullOrWhiteSpace($appName)) {
    Write-Host "❌ App name cannot be empty!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

try {
    & heroku create $appName
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Failed to create Heroku app. App name might already exist." -ForegroundColor Red
        Write-Host "Try a different name or destroy the existing one if you own it:" -ForegroundColor Yellow
        Write-Host "heroku apps:destroy $appName" -ForegroundColor Cyan
        Read-Host "Press Enter to exit"
        exit 1
    }
    Write-Host "✅ Heroku app created successfully!" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to create Heroku app!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Step 5: Adding PostgreSQL database..." -ForegroundColor Yellow
Write-Host "Adding PostgreSQL database addon (this may take a moment)..." -ForegroundColor Cyan

try {
    & heroku addons:create heroku-postgresql:essential-0 --app $appName
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Warning: Failed to add PostgreSQL addon. Trying alternative..." -ForegroundColor Yellow
        & heroku addons:create heroku-postgresql:mini --app $appName
    }
    Write-Host "✅ PostgreSQL database added!" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed to add PostgreSQL database!" -ForegroundColor Red
}

Write-Host ""
Write-Host "Step 6: Setting environment variables..." -ForegroundColor Yellow
$randomSuffix = Get-Random
& heroku config:set SPRING_PROFILES_ACTIVE=prod --app $appName
& heroku config:set JWT_SECRET="TimeSheet2024SuperSecretKey$randomSuffix" --app $appName
& heroku config:set CONTEXT_PATH=/ --app $appName
Write-Host "✅ Environment variables set!" -ForegroundColor Green

Write-Host ""
Write-Host "Step 7: Initializing Git repository..." -ForegroundColor Yellow
if (-not (Test-Path ".git")) {
    & git init
    & git branch -M main
}

Write-Host "Adding files to git..." -ForegroundColor Cyan
& git add .
& git commit -m "Deploy timesheet app to Heroku with PostgreSQL support"

Write-Host ""
Write-Host "Step 8: Adding Heroku remote and deploying..." -ForegroundColor Yellow
& heroku git:remote -a $appName

Write-Host "Starting deployment... (this may take several minutes)" -ForegroundColor Cyan
Write-Host "Please wait..." -ForegroundColor Yellow

try {
    & git push heroku main
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ Deployment failed! Check the error messages above." -ForegroundColor Red
        Write-Host "You can view detailed logs with: heroku logs --tail --app $appName" -ForegroundColor Yellow
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "❌ Deployment failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""
Write-Host "Step 9: Ensuring app is running..." -ForegroundColor Yellow
& heroku ps:scale web=1 --app $appName

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "🎉 Deployment Complete! 🎉" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Your timesheet app is now available at:" -ForegroundColor Cyan
Write-Host "https://$appName.herokuapp.com" -ForegroundColor Yellow
Write-Host ""
Write-Host "Database: PostgreSQL (automatically configured)" -ForegroundColor Green
Write-Host "Profile: Production (PostgreSQL enabled, H2 disabled)" -ForegroundColor Green
Write-Host ""
Write-Host "Useful commands:" -ForegroundColor Cyan
Write-Host "- View logs: heroku logs --tail --app $appName"
Write-Host "- Open app: heroku open --app $appName"
Write-Host "- Check status: heroku ps --app $appName"
Write-Host "- View config: heroku config --app $appName"
Write-Host ""

$openChoice = Read-Host "Do you want to open the app in your browser now? (y/n)"
if ($openChoice -eq "y" -or $openChoice -eq "Y") {
    & heroku open --app $appName
}

Read-Host "Press Enter to exit"
