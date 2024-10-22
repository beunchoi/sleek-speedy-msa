FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/order-0.0.1-SNAPSHOT.jar /app/order.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/order.jar"]