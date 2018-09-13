
FROM java:8
EXPOSE 9093
ADD /target/packsendme-iam-server-0.0.1-SNAPSHOT.jar packsendme-iam-server-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/packsendme-iam-server-0.0.1-SNAPSHOT.jar"]