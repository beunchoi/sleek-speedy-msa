FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/payment-0.0.1-SNAPSHOT.jar /app/payment.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/payment.jar"]