# Timesheet Application - Cloud Deployment Guide

## Prerequisites
- Git installed and configured
- Your application JAR file built (`mvn clean package`)
- Account on chosen cloud platform

## Option 1: Heroku (Easiest - Free Tier Available)

### Steps:
1. **Install Heroku CLI**
   - Download from: https://devcenter.heroku.com/articles/heroku-cli
   - Or use: `npm install -g heroku`

2. **Prepare and Deploy**
   ```bash
   # Login to Heroku
   heroku login
   
   # Create new Heroku app
   heroku create your-timesheet-app
   
   # Add PostgreSQL database
   heroku addons:create heroku-postgresql:mini
   
   # Set environment variables
   heroku config:set SPRING_PROFILES_ACTIVE=prod
   heroku config:set JWT_SECRET=your-super-secret-jwt-key-here
   
   # Initialize git repository (if not already)
   git init
   git add .
   git commit -m "Initial commit for deployment"
   
   # Deploy to Heroku
   git push heroku main
   ```

3. **Your app will be available at**: `https://your-timesheet-app.herokuapp.com`

## Option 2: Railway (Modern, Easy, Free Tier)

### Steps:
1. **Visit**: https://railway.app
2. **Sign up** with GitHub
3. **Deploy from GitHub**:
   - Connect your GitHub account
   - Create new repository and push your code
   - Select "Deploy from GitHub repo"
   - Choose your repository
   - Railway will auto-detect Spring Boot and deploy

4. **Add Database**:
   - In Railway dashboard, click "New" → "Database" → "PostgreSQL"
   - Railway will automatically set DATABASE_URL environment variable

5. **Set Environment Variables**:
   - SPRING_PROFILES_ACTIVE=prod
   - JWT_SECRET=your-super-secret-jwt-key

## Option 3: AWS Elastic Beanstalk

### Steps:
1. **Install AWS CLI and EB CLI**
2. **Package your application**:
   ```bash
   mvn clean package
   ```
3. **Initialize and Deploy**:
   ```bash
   eb init
   eb create timesheet-env
   eb deploy
   ```

## Option 4: Google Cloud Platform (App Engine)

### Steps:
1. **Create app.yaml**:
   ```yaml
   runtime: java11
   env_variables:
     SPRING_PROFILES_ACTIVE: prod
     JWT_SECRET: your-secret-key
   ```
2. **Deploy**:
   ```bash
   gcloud app deploy
   ```

## Option 5: Docker + Any Cloud Provider

### Build Docker Image:
```bash
mvn clean package
docker build -t timesheet-app .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod timesheet-app
```

### Deploy to:
- **Digital Ocean App Platform**
- **Azure Container Instances** 
- **AWS ECS/Fargate**

## Database Considerations

### For Production:
- **Heroku**: Heroku Postgres (free tier: 10MB, paid plans available)
- **Railway**: Built-in PostgreSQL (generous free tier)
- **AWS**: RDS PostgreSQL
- **GCP**: Cloud SQL PostgreSQL

### Environment Variables Needed:
- `DATABASE_URL` (auto-set by most platforms)
- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET` (generate a strong secret)
- `PORT` (auto-set by platforms)

## Security Notes:
1. Change default JWT secret
2. Use strong database passwords
3. Enable HTTPS (most platforms do this automatically)
4. Consider adding CORS configuration for production

## Monitoring:
- Most platforms provide built-in logging and monitoring
- Check application logs: `heroku logs --tail` (Heroku) or platform dashboard

## Cost Estimates:
- **Heroku**: Free tier available, paid plans from $7/month
- **Railway**: Generous free tier, paid from $5/month
- **AWS**: Pay-as-you-go, typically $10-20/month for small apps
- **GCP**: Similar to AWS pricing
- **Digital Ocean**: $5-12/month for basic droplets
