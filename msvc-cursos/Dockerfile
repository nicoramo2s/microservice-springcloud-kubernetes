FROM eclipse-temurin:21.0.10_7-jdk-alpine

WORKDIR /app

COPY ./target/msvc-cursos-0.0.1-SNAPSHOT.jar .

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "msvc-cursos-0.0.1-SNAPSHOT.jar"]