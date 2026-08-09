# ============================================================
# 1. СБОРКА (Amazon Corretto 25 + Maven)
# ============================================================
FROM amazoncorretto:25-alpine AS build

# Устанавливаем Maven
RUN apk add --no-cache maven

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ============================================================
# 2. ЗАПУСК (JRE) — Java 25
# ============================================================
FROM amazoncorretto:25-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]