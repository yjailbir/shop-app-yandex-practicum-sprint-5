FROM bellsoft/liberica-openjdk-alpine:21

COPY target/*.jar shop.jar

ENTRYPOINT ["java","-jar","/shop.jar"]