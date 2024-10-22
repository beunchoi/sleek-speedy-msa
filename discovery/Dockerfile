FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/discovery-0.0.1-SNAPSHOT.jar /app/discovery.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/discovery.jar"]