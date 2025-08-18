FROM bellsoft/liberica-openjdk-alpine:21

RUN apk add --no-cache bash netcat-openbsd

COPY target/*.jar /shop.jar

COPY wait-for.sh /wait-for.sh

RUN chmod +x /wait-for.sh

ENTRYPOINT ["/wait-for.sh", "keycloak", "8080", "java", "-jar", "/shop.jar"]
