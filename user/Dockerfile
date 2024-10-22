FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/user-0.0.1-SNAPSHOT.jar /app/user.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/user.jar"]