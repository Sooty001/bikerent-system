FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S javauser && adduser -S javauser -G javauser
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN chown javauser:javauser /app/app.jar
USER javauser
# Открываем и WEB порт (8083) и gRPC порт (9090)
EXPOSE 8083 9090
ENTRYPOINT ["java", "-jar", "app.jar"]