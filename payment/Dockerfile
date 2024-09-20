FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/*.jar /app/order.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/order.jar"]