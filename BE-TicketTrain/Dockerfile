# Build stage - sử dụng JDK 20
FROM eclipse-temurin:20-jdk-jammy as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build ứng dụng và tạo file jar
RUN ./mvnw clean package -DskipTests

# Runtime stage - sử dụng JRE 20 (đồng bộ với JDK build)
FROM eclipse-temurin:20-jre-jammy

WORKDIR /app

# Copy file jar từ build stage
COPY --from=builder /app/target/*.jar app.jar

# Mở port ứng dụng Spring Boot (mặc định 8080)
EXPOSE 8080

# Câu lệnh khởi chạy
ENTRYPOINT ["java", "-jar", "app.jar"]