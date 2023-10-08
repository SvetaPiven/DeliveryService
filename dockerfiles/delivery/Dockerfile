FROM openjdk:17-oracle
COPY dockerfiles/delivery/DeliveryService-0.0.1-SNAPSHOT.jar deliveryapp.jar
WORKDIR /
CMD ["java", "-jar",  "deliveryapp.jar"]