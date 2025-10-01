# 1. Этап сборки
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Копируем весь проект, чтобы Maven видел common-dto и остальные модули
COPY . .

# Собираем именно модуль notification + его зависимости (в т.ч. common-dto)
RUN mvn clean package -DskipTests -pl services/notification -am

# 2. Этап запуска
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем собранный jar notification
COPY --from=builder /app/services/notification/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]