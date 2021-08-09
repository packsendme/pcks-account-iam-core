
FROM openjdk:8-jdk-alpine
EXPOSE 9093
COPY /target/pcks-account-iam-core-0.0.1-SNAPSHOT.jar pcks-account-iam-core-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/pcks-account-iam-core-0.0.1-SNAPSHOT.jar"]
