# Giai đoạn 1: Build ứng dụng bằng Gradle
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
# Chạy lệnh build của Gradle và bỏ qua chạy thử nghiệm (tests)
RUN ./gradlew bootJar --no-daemon

# Giai đoạn 2: Chạy ứng dụng bằng Java 17
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# File .jar của Gradle sau khi build sẽ nằm trong thư mục build/libs
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
