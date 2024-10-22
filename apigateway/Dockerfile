FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/apigateway-0.0.1-SNAPSHOT.jar /app/apigateway.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/apigateway.jar"]