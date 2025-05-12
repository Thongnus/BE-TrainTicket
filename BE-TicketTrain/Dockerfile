# Build stage
FROM eclipse-temurin:24-jdk-jammy as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build ứng dụng và tạo file jar
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:24-jre-jammy

WORKDIR /app

# Copy file jar từ build stage
COPY --from=builder /app/target/*.jar app.jar

# Mở port ứng dụng Spring Boot (mặc định 8080)
EXPOSE 8080

# Câu lệnh khởi chạy
ENTRYPOINT ["java", "-jar", "app.jar"]