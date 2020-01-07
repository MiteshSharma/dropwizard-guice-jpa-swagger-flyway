FROM openjdk:8-jre-alpine

WORKDIR /var/mobileServer

ADD target/MobileServer-2.0.0.jar /var/mobileServer/MobileServer.jar
ADD config.yml /var/mobileServer/config.yml

EXPOSE 8080 8080

ENTRYPOINT ["java", "-jar", "MobileServer.jar", "server", "config.yml"]