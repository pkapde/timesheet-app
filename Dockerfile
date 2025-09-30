FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/timesheet-app-1.0.0.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
