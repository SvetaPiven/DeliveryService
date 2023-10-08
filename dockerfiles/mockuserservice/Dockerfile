FROM openjdk:17-oracle
COPY dockerfiles/mockuserservice/TestUUID-0.0.1-SNAPSHOT.jar test.jar
WORKDIR /
CMD ["java", "-jar",  "test.jar"]