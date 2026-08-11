# auth-service/Dockerfile.dev
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Step 1: Copy file pom.xml của common và build trước dependency (Tạo layer cache cho common)
COPY common/pom.xml ./common/pom.xml
COPY common/src ./common/src
# Bước này chỉ chạy lại NẾU code hoặc pom của folder common có thay đổi
RUN mvn -f common/pom.xml clean install -DskipTests

# Step 2: Copy code của service hiện tại (ví dụ: auth-service)
COPY auth-service/pom.xml ./auth-service/pom.xml
COPY auth-service/src ./auth-service/src
RUN mvn -f auth-service/pom.xml clean package -DskipTests

# Step 3: Run app
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]