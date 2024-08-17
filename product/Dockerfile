FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/*.jar /app/product.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/product.jar"]