# 1. Этап сборки
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Копируем весь проект, чтобы Maven видел common-dto и остальные модули
COPY . .

# Собираем именно модуль auth + его зависимости (в т.ч. common-dto)
RUN mvn clean package -DskipTests -pl services/auth -am

# 2. Этап запуска
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем собранный jar auth
COPY --from=builder /app/services/auth/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]