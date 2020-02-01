
FROM openjdk:8-jdk-alpine
EXPOSE 9093
COPY /target/packsendme-iam-server-0.0.1-SNAPSHOT.jar packsendme-iam-server-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/packsendme-iam-server-0.0.1-SNAPSHOT.jar"]