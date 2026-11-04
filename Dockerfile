# Шаг 1: Используем официальный образ Java 21 для сборки и запуска
FROM eclipse-temurin:21-jdk-jammy

# Рабочая директория внутри контейнера
WORKDIR /app

# Копируем файл сборки Maven и зависимости
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Копируем исходный код проекта
COPY src src

# Собираем проект (пропускаем тесты для скорости)
RUN ./mvnw clean package -DskipTests

# Указываем команду для запуска приложения
ENTRYPOINT ["java", "-jar", "target/demo-0.0.1-SNAPSHOT.jar"]