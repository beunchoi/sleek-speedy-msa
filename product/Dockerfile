FROM amazoncorretto:21

WORKDIR /app

COPY build/libs/product-0.0.1-SNAPSHOT.jar /app/product.jar

ENV SPRING_PROFILES_ACTIVE=dev

ENTRYPOINT ["java", "-jar", "/app/product.jar"]