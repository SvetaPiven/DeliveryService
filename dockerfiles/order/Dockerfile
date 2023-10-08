FROM openjdk:17-oracle
COPY dockerfiles/order/OrderService-1.0-SNAPSHOT.jar orderapp.jar
WORKDIR /
CMD ["java", "-jar",  "orderapp.jar"]