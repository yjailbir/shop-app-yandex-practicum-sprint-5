FROM bellsoft/liberica-openjdk-alpine:21

COPY target/*.jar payments-api.jar

ENTRYPOINT ["java","-jar","/payments-api.jar"]